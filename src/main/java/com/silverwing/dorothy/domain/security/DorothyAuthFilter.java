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

        if (cookies != null) {
            java.util.Arrays.stream(cookies)
                    .filter(c -> DorothyApplication.COOKIE_NAME.equals(c.getName()))
                    .findFirst()
                    .ifPresent(cookie -> {
                        try {
                            String val = cookie.getValue();
                            String userPhone = jwtTokenManager.getPhone(val);
                            Member member = userService.getMember(userPhone);
                            if (member != null) {
                                SecurityContextHolder.getContext().setAuthentication(new DorothyAuthToken(member));
                                // Refresh token on every valid request
                                jwtTokenManager.persistToken(jwtTokenManager.generateToken(member.getPhone()), response);
                            }
                        } catch (Exception e) {
                            log.error("Authentication failed: {}", e.getMessage());
                        }
                    });
        }

        filterChain.doFilter(request, response);
    }
}
