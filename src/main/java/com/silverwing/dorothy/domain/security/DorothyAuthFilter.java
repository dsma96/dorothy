package com.silverwing.dorothy.domain.security;

import com.silverwing.dorothy.DorothyApplication;
import com.silverwing.dorothy.domain.service.user.DorothyUserService;
import com.silverwing.dorothy.domain.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
public class DorothyAuthFilter extends OncePerRequestFilter {

    JwtTokenManager jwtTokenManager;
    DorothyUserService userService;
    public DorothyAuthFilter(JwtTokenManager tokenManager, DorothyUserService userService) {
        jwtTokenManager = tokenManager;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        for( Cookie cookie : cookies ) {
            if( cookie.getName().equals(DorothyApplication.COOKIE_NAME) ) {
                String val = cookie.getValue();
                try {
                    String userPhone = jwtTokenManager.getPhone(val);
                    Member member = userService.getMember(userPhone);
                    if( member != null ) {
                        SecurityContextHolder.getContext()
                                .setAuthentication( new DorothyAuthToken(member));
                        jwtTokenManager.persistToken(jwtTokenManager.generateToken(member.getPhone()), response);
                    }
                }catch(AuthenticationException e){
                  log.error("can't parse token:{}", val);
                }
            }

        }

        filterChain.doFilter(request, response);
    }
}
