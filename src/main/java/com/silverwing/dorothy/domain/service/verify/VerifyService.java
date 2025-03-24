package com.silverwing.dorothy.domain.service.verify;

import com.silverwing.dorothy.domain.Exception.VerifyException;
import com.silverwing.dorothy.domain.dao.MemberRepository;
import com.silverwing.dorothy.domain.dao.VerifyRequestRepository;
import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.service.notification.NotificationService;
import com.silverwing.dorothy.domain.type.VerifyChannel;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class VerifyService {

    int SIGNUP_MAX_ATTEMPT = 12;
    int EXPIRE_TIME = 60 * 3  * 1000; // 3 minutes
    int MAX_TRY_ON_REQ = 15;

    private final VerifyRequestRepository verifyRequestRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    private Random random = new Random();
    public static final String TOO_MANY_REQUEST = "Too many attempts. Please try it later";
    public static final String TOO_MANY_REQUEST_ON_REQ = "Too many attempts. Try it again";

    public VerifyRequest createVerifyRequest(String phoneNo, VerifyType verifyType) throws VerifyException {

        if(memberRepository.findMemberByPhone(phoneNo).isPresent()) {
            throw new VerifyException("Phone number already in use ");
        }

        Optional<VerifyRequest> ongoingRequest =  verifyRequestRepository.findLiveVerify(phoneNo, verifyType);
        if( ongoingRequest.isPresent() ) {
            return ongoingRequest.get();
        }

        if( verifyRequestRepository.countDailyTry( phoneNo, VerifyType.SIGN_UP) > SIGNUP_MAX_ATTEMPT){
            throw new VerifyException( TOO_MANY_REQUEST );
        }

        int low = 1000;
        int max = 9999;

        Date now = new Date();
        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setPhoneNo(phoneNo);
        verifyRequest.setVerifyType(VerifyType.SIGN_UP);
        verifyRequest.setVerifyChannel(VerifyChannel.SMS);
        verifyRequest.setCreateDate(now);
        verifyRequest.setExpireDate( new Date(now.getTime()+EXPIRE_TIME));
        verifyRequest.setVerifyCode( (random.nextInt( max - low ) + low)+"");
        verifyRequest.setMaxTry( MAX_TRY_ON_REQ );
        verifyRequest.setFailCnt( 0 );
        verifyRequest.setVerifyState( VerifyState.CREATED );
        VerifyRequest result =  verifyRequestRepository.save(verifyRequest);
        notificationService.sendVerifyCode( verifyRequest );
        return result;

    }

    @Transactional
    public VerifyRequest verifyRequest(String phoneNo, String verifyCode) throws VerifyException {

        if(memberRepository.findMemberByPhone(phoneNo).isPresent()) {
            throw new VerifyException("Phone number already in use ");
        }

        VerifyRequest req =  verifyRequestRepository.findLiveVerify(phoneNo, VerifyType.SIGN_UP).orElseThrow( () -> new VerifyException("Can't find ongoing verification"));

        if( req.getFailCnt() >= req.getMaxTry() ){
            throw new VerifyException( TOO_MANY_REQUEST_ON_REQ );
        }

        if( req.getVerifyCode().equals(verifyCode.trim())){
            req.setVerifyState( VerifyState.VERIFIED );
            req.setVerifyDate(new Date());
        }else{
            req.setFailCnt( req.getFailCnt() + 1 );
            if( req.getFailCnt() >= MAX_TRY_ON_REQ ){
                req.setVerifyState( VerifyState.EXPIRED );
            }
        }
        return verifyRequestRepository.save(req);
    }
}
