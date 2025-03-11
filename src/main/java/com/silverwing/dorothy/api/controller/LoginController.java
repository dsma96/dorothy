package com.silverwing.dorothy.api.controller;
import com.silverwing.dorothy.api.service.DorothyUserService;
import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.member.MemberDto;
import com.silverwing.dorothy.domain.security.JwtTokenManager;
import com.silverwing.dorothy.domain.type.UserStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@AllArgsConstructor
public class LoginController {

    @Autowired
    private DorothyUserService userService;

    @Autowired
    private JwtTokenManager tokenManager;

    @PostMapping("/login")
    public ResponseEntity<  ResponseData<MemberDto>> login(@RequestBody MemberDto member, HttpServletResponse response) {
        try {

            if(  member.getPhone() == null || member.getPassword() == null){
                return new ResponseEntity<>(  new ResponseData<>( "invalid user id and pwd"), HttpStatus.UNAUTHORIZED);
            }

            Member loginUser  = userService.getMember(member.getPhone(), member.getPassword());

            if(!UserStatus.ENABLED.equals( loginUser.getStatus() ) ){
                return new ResponseEntity<>(  new ResponseData<>("disabled user "), HttpStatus.UNAUTHORIZED);
            }

            tokenManager.persistToken( tokenManager.generateToken(member.getPhone()) ,response);
            MemberDto resp = MemberDto.builder()
                                .email(loginUser.getEmail())
                                .name(loginUser.getUsername())
                                .phone( loginUser.getPhone())
                                .id(loginUser.getUserId())
                                .isRootUser(loginUser.isRootUser())
                                .build();
            return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),resp ),HttpStatus.OK);

        }catch (AuthenticationException e) {
            return new ResponseEntity<>( new ResponseData<>(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(){
        return new ResponseEntity<>("OK",HttpStatus.OK);
    }


}
