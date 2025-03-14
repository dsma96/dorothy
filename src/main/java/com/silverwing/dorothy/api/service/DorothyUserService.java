package com.silverwing.dorothy.api.service;

import com.silverwing.dorothy.api.dao.MemberRepository;
import com.silverwing.dorothy.domain.Exception.UserException;
import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.member.UserRole;
import com.silverwing.dorothy.domain.security.JwtTokenManager;
import com.silverwing.dorothy.domain.type.UserStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Service
@Slf4j
public class DorothyUserService  {
    @Autowired
    private  MemberRepository memberRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder() ;
    private long expireTimeMs = 1000 * 60 * 60; // 1시간

    public Member getMember( String phone, String pwd ) throws AuthenticationException {
        Member member = memberRepository.findMemberByPhone( phone ).orElseThrow( () -> new UsernameNotFoundException("Can't find "+phone) );
        if( UserStatus.ENABLED.equals( member.getStatus()) ){
            if( pwd == null || passwordEncoder.matches( pwd, member.getPassword() ) )
                return member;
            throw new UsernameNotFoundException("Invalid password :"+phone+ " "+pwd);
        }else
            throw new UsernameNotFoundException("Disabled user "+phone);
    }

    public Member getMember( String phone ) throws AuthenticationException {
        return getMember(phone, null);
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

}
