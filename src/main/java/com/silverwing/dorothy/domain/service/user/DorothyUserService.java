package com.silverwing.dorothy.domain.service.user;

import com.silverwing.dorothy.domain.dao.MemberRepository;
import com.silverwing.dorothy.domain.Exception.UserException;
import com.silverwing.dorothy.domain.dao.VerifyRequestRepository;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.type.UserRole;
import com.silverwing.dorothy.domain.type.UserStatus;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable( key = "#phone", unless="#result == null")
    public Member getMember( String phone ) throws AuthenticationException {
        return memberRepository.findMemberByPhone( phone ).orElseThrow( () -> new UsernameNotFoundException("Can't find "+phone) );
    }

    @Cacheable
    public Member getMember(int memberId)  {
        return memberRepository.findById( memberId ).orElseThrow();
    }


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

    public Member refreshUserLogin( Member member) throws UserException{
        member.setLastLogin( new Date() );
        return memberRepository.save( member );
    }

    @CacheEvict(key="#member.phone")
    public Member updateUser( Member member) throws UserException{
        return memberRepository.save( member );
    }

    public List<Member> getAvailableDesigners( String dateStr ) {
        Date offDay = null;
        try {
            offDay = sdf.parse(dateStr);
            return memberRepository.findAvailableDesigners( offDay ).orElseGet(Collections::emptyList);
        }catch (ParseException e) {
            throw new UserException("Invalid date format "+dateStr);
        }


    }

}
