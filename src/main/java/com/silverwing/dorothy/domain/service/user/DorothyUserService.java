package com.silverwing.dorothy.domain.service.user;

import com.silverwing.dorothy.domain.dao.MemberRepository;
import com.silverwing.dorothy.domain.Exception.UserException;
import com.silverwing.dorothy.domain.dao.OffDayRepository;
import com.silverwing.dorothy.domain.dao.VerifyRequestRepository;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.OffDay;
import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.type.UserRole;
import com.silverwing.dorothy.domain.type.UserStatus;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = "member")
@RequiredArgsConstructor
public class DorothyUserService  {

    private final MemberRepository memberRepository;
    private final VerifyRequestRepository verifyRequestRepository;
    private final OffDayRepository offDayRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder() ;
    private long expireTimeMs = 1000 * 60 * 10; // 10min
    private static int MAX_LOGIN_ATTEMPTS = 5;
    private static int BLOCK_TIME = 10 * 60 * 1000;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public boolean isMatchedPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String getEncryptedPassword(String password) {
        return passwordEncoder.encode(password);
    }


    public Member getMemberFromLogin( String phone, String pwd ) throws AuthenticationException {
        Member member = memberRepository.findMemberByPhone( phone ).orElseThrow( () -> new UsernameNotFoundException("Can't find "+phone) );
        Date now = new Date();

        if( member.getLoginFailCnt() >= MAX_LOGIN_ATTEMPTS ) {
            if( member.getLastLoginTry().getTime() +  BLOCK_TIME > now.getTime() ){
                throw new UserException("Too many attempts to login. please try again later");
            }else{
                member.setLoginFailCnt(0);
            }
        }
        member.setLastLoginTry(now);

        if( UserStatus.ENABLED.equals( member.getStatus()) ){
            if( passwordEncoder.matches( pwd, member.getPassword() ) ) {
                member.setLoginFailCnt(0);
                member.setLastLogin(now);
                updateUser(member);
                return member;
            }
            else {
                member.setLoginFailCnt(member.getLoginFailCnt()+1);
                updateUser(member);
                throw new UsernameNotFoundException("Invalid Password or Phone Number");
            }
        }else
            throw new UsernameNotFoundException("Disabled user "+phone);
    }

    @Cacheable(cacheNames = "member_phone", key = "#phone", unless="#result == null")
    public Member getMember( String phone ) throws AuthenticationException {
        return memberRepository.findMemberByPhone( phone ).orElseThrow( () -> new UsernameNotFoundException("Can't find "+phone) );
    }

    @Cacheable( cacheNames = "member" , key="#memberId")
    public Member getMember(int memberId)  {
        return memberRepository.findById( memberId ).orElseThrow();
    }


    @Transactional
    public Member createMember( String name, String telNo, String email, String password ) throws UserException {

        if( memberRepository.findMemberByPhone(telNo).isPresent() )
            throw new UserException("Phone already exists :"+telNo);

        VerifyRequest vr =  verifyRequestRepository.findVerify( telNo, VerifyType.SIGN_UP.name(), VerifyState.VERIFIED.name()).orElseThrow( ()->  new UserException("You should verify your phone number first"));
        vr.setVerifyState(VerifyState.PERSISTED);

        if( (email == null || !email.isEmpty()) && memberRepository.findMemberByEmail( email).isPresent() ){
            throw new UserException("Email already exists : "+email);
        }

        Member member = new Member( name, telNo, email, password );
        member.setRole(UserRole.CUSTOMER);
        member.setStatus(UserStatus.ENABLED);
        member.setPassword( passwordEncoder.encode(password) );
        member.setCreateDate( new Date() );

        try {
            verifyRequestRepository.save(vr);
            return memberRepository.save( member );
        } catch (Exception e) {
            throw new UserException( "Can't create user", e );
        }
    }

    @Transactional
    public Member resetPassword(String telNo, String newPassword) throws UserException {
        Member member = memberRepository.findMemberByPhone(telNo).orElseThrow( () -> new UserException("Can't find user with phone "+telNo) );
        VerifyRequest vr =  verifyRequestRepository.findVerify( telNo, VerifyType.PWD_RESET.name(), VerifyState.VERIFIED.name()).orElseThrow( ()->  new UserException("You should verify your phone number first"));
        vr.setVerifyState(VerifyState.PERSISTED);

        if( newPassword == null || newPassword.isEmpty() || newPassword.length() < 4) {
            throw new UserException("too short password, should be at least 4 characters");
        }

        member.setPassword( passwordEncoder.encode(newPassword) );
        member.setLastLoginTry(new Date());
        member.setLoginFailCnt(0);
        member.setLastLogin(new Date());

        try {
            verifyRequestRepository.save(vr);
            return memberRepository.save(member);
        } catch (Exception e) {
            throw new UserException( "Can't reset password", e );
        }
    }

    public Member refreshUserLogin( Member member) throws UserException{
        member.setLastLogin( new Date() );
        return memberRepository.save( member );
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "member", key = "#member.userId"),
            @CacheEvict(cacheNames = "member_phone", key = "#member.phone")
    })
    public Member updateUser( Member member) throws UserException{
        return memberRepository.save( member );
    }

    @Cacheable(cacheNames = "availableDesigners", key = "#dateStr")
    public List<Member> getAvailableDesigners( String dateStr ) {
        Date offDay = null;
        try {
            offDay = sdf.parse(dateStr);
            return memberRepository.findAvailableDesigners( offDay ).orElseGet(Collections::emptyList);
        }catch (ParseException e) {
            throw new UserException("Invalid date format "+dateStr);
        }
    }

    @Cacheable(cacheNames = "offday", key = "#StartDate.toString()+ #endDate.toString()")
    public List<OffDay> getOffDays( Date StartDate, Date endDate ) {
        return offDayRepository.getOffDays(StartDate, endDate).orElseGet(Collections::emptyList);
    }

    @CacheEvict(cacheNames="member", key="#userId")
    public void updateUserMemo( int userId, String memo) {
        Member member = memberRepository.findById(userId).orElseThrow();
        member.setMemo(memo);
        memberRepository.save(member);
    }

    public Page<Member> getAllUsers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
}
