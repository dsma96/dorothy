package com.silverwing.dorothy.api.service;

import com.silverwing.dorothy.domain.dao.MemberRepository;
import com.silverwing.dorothy.domain.Exception.UserException;
import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.member.UserRole;
import com.silverwing.dorothy.domain.type.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@CacheConfig(cacheNames = "member")
public class DorothyUserService  {
    @Autowired
    private  MemberRepository memberRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder() ;
    private long expireTimeMs = 1000 * 60 * 10; // 10min
    private static int MAX_LOGIN_ATTEMPTS = 5;
    private static int BLOCK_TIME = 10 * 60 * 1000;

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

        if( (email == null || !email.isEmpty()) && memberRepository.findMemberByEmail( email).isPresent() ){
            throw new UserException("Email already exists : "+email);
        }

        Member member = new Member( name, telNo, email, password );
        member.setRole(UserRole.CUSTOMER);
        member.setStatus(UserStatus.ENABLED);
        member.setPassword( passwordEncoder.encode(password) );
        member.setCreateDate( new Date() );

        try {
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

}
