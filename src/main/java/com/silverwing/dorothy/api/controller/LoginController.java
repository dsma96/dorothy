package com.silverwing.dorothy.api.controller;
import com.silverwing.dorothy.domain.service.user.DorothyUserService;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.api.dto.MemberDto;
import com.silverwing.dorothy.domain.security.JwtTokenManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@AllArgsConstructor
public class LoginController {

    @Autowired
    private DorothyUserService userService;

    @Autowired
    private JwtTokenManager tokenManager;

    @GetMapping("/relogin")
    public ResponseEntity<  ResponseData<MemberDto>> login(@AuthenticationPrincipal Member loginUser) {
        if( loginUser == null ){
            throw new CredentialsExpiredException("credentials expired");
        }

        userService.refreshUserLogin(loginUser);

        MemberDto resp = MemberDto.builder()
                .email(loginUser.getEmail())
                .name(loginUser.getUserName())
                .phone( loginUser.getPhone())
                .id(loginUser.getUserId())
                .isRootUser(loginUser.isRootUser())
                .build();
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),resp ),HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<  ResponseData<MemberDto>> login(@RequestBody MemberDto member, HttpServletResponse response) {
        try {

            if(  member.getPhone() == null || member.getPassword() == null){
                return new ResponseEntity<>(  new ResponseData<>( "invalid user id or password"), HttpStatus.UNAUTHORIZED);
            }

            Member loginUser  = userService.getMemberFromLogin(member.getPhone(), member.getPassword());

            tokenManager.persistToken( tokenManager.generateToken(member.getPhone()) ,response);
            MemberDto resp = MemberDto.builder()
                                .email(loginUser.getEmail())
                                .name(loginUser.getUserName())
                                .phone( loginUser.getPhone())
                                .id(loginUser.getUserId())
                                .isRootUser(loginUser.isRootUser())
                                .build();
            return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),resp ),HttpStatus.OK);

        }catch (AuthenticationException e) {
            return new ResponseEntity<>( new ResponseData<>(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseData<String>> logout( HttpServletResponse response) {
        tokenManager.eraseToken(response);
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),"OK" ),HttpStatus.OK);
    }
}
