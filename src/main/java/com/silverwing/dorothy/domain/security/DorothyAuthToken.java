package com.silverwing.dorothy.domain.security;

import com.silverwing.dorothy.domain.member.Member;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public class DorothyAuthToken extends AbstractAuthenticationToken {
    public DorothyAuthToken(Member member){
        super( Set.of( new SimpleGrantedAuthority(member.getRole().name()) ) );
        setDetails(member);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return getDetails();
    }
}
