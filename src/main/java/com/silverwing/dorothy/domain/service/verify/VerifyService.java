package com.silverwing.dorothy.domain.service.verify;

import com.silverwing.dorothy.domain.Exception.VerifyException;
import com.silverwing.dorothy.domain.dao.MemberRepository;
import com.silverwing.dorothy.domain.dao.VerifyRequestRepository;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.service.notification.NotificationService;
import com.silverwing.dorothy.domain.type.VerifyChannel;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private boolean isLocal = false;

    @PostConstruct
    public void init() {
        isLocal = activeProfile.equalsIgnoreCase("local");
    }

    @Transactional
    public VerifyRequest createVerifyRequest(String phoneNo, VerifyType verifyType) throws VerifyException {

        Optional<Member> existingMember =  memberRepository.findMemberByPhone(phoneNo);

        if( verifyType.equals( VerifyType.SIGN_UP) ) {
            if( existingMember.isPresent()){
                throw new VerifyException("Phone number already in use ");
            }
        }
        else if( verifyType.equals( VerifyType.PWD_RESET) ) {
            if( existingMember.isEmpty() )
                throw new VerifyException("Invalid phone number");

        }else {
            throw new VerifyException("Invalid verify type: " + verifyType.name());
        }

        Optional<VerifyRequest> ongoingRequest =  verifyRequestRepository.findLiveVerify(phoneNo, verifyType.name());
        if( ongoingRequest.isPresent() ) {
            return ongoingRequest.get();
        }

        Optional<VerifyRequest> verifiedRequest =  verifyRequestRepository.findVerify(phoneNo, verifyType.name(), VerifyState.VERIFIED.name());
        if( verifiedRequest.isPresent() ) {
            return verifiedRequest.get();
        }

        if( verifyRequestRepository.countDailyTry( phoneNo, verifyType.name()) > SIGNUP_MAX_ATTEMPT){
            throw new VerifyException( TOO_MANY_REQUEST );
        }

        // erase verified data but not persisted request.
        verifyRequestRepository.setVerifyState(phoneNo, VerifyState.VERIFIED, VerifyState.EXPIRED);

        int low = 1000;
        int max = 9999;

        Date now = new Date();
        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setPhoneNo(phoneNo);
        verifyRequest.setVerifyType(verifyType);
        verifyRequest.setVerifyChannel(VerifyChannel.SMS);
        verifyRequest.setCreateDate(now);
        verifyRequest.setExpireDate( new Date(now.getTime()+EXPIRE_TIME));
        verifyRequest.setVerifyCode( (random.nextInt( max - low ) + low)+"");
        verifyRequest.setMaxTry( MAX_TRY_ON_REQ );
        verifyRequest.setFailCnt( 0 );
        verifyRequest.setVerifyState( VerifyState.CREATED );
        VerifyRequest result =  verifyRequestRepository.save(verifyRequest);

        if( !isLocal )
            notificationService.sendVerifyCode( verifyRequest );

        return result;

    }

    @Transactional
    public VerifyRequest verifyRequest(String phoneNo, String verifyCode, VerifyType verifyType) throws VerifyException {


        VerifyRequest req =  verifyRequestRepository.findLiveVerify(phoneNo, verifyType.name()).orElseThrow( () -> new VerifyException("Can't find ongoing verification"));

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
