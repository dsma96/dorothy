package com.silverwing.dorothy.domain.security;
import com.silverwing.dorothy.DorothyApplication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialExpiredException;
import java.net.http.HttpResponse;
import java.util.Date;

@Service
public class JwtTokenManager {
    @Value("${jwt.token.secret}") // yml의 값을 가져올 수 있다.
    private String secretKey;

    private static final long expiredTime = 1000 * 60 * 60 * 60;
    //토큰 생성 메서드
    public  String generateToken(String phone) {
        Claims claims = Jwts.claims();
        claims.put("phone", phone);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    private  Claims extractClaims(String token) throws AuthenticationException {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        }catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token", e);
        }
    }

    public String getPhone(String token) throws AuthenticationException {
        try{
            if( isExpired(token))
                throw new AuthenticationCredentialsNotFoundException("Expired token");

            String phone =  extractClaims(token).get("phone").toString();
            if( phone == null )
                throw new AuthenticationCredentialsNotFoundException("Invalid token "+token );
            return phone;

        }catch(AuthenticationException e){
            throw e;
        }
    }

    //토큰 만료확인 메서드
    public  boolean isExpired(String token) throws AuthenticationException {

        Date expiredDate = extractClaims(token).getExpiration();
        if( expiredDate == null)
            throw new AuthenticationCredentialsNotFoundException("invalid token");

        return expiredDate.before(new Date());
    }

    public void persistToken(String token, HttpServletResponse response){
        Cookie cookie = new Cookie(DorothyApplication.COOKIE_NAME,  token);
        cookie.setSecure(true);
        cookie.setMaxAge(-1);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // global cookie accessible every where
        response.addCookie(cookie);
    }

    public void eraseToken(HttpServletResponse response){
        Cookie cookie = new Cookie(DorothyApplication.COOKIE_NAME,  null);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // global cookie accessible every where
        response.addCookie(cookie);
    }
}
