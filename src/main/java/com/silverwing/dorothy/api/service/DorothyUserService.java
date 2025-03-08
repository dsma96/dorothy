package com.silverwing.dorothy.api.service;

import com.silverwing.dorothy.api.dao.MemberRepository;
import com.silverwing.dorothy.domain.member.Member;
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


}
