package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.service.DorothyUserService;
import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.member.MemberDto;
import com.silverwing.dorothy.domain.type.UserStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final DorothyUserService userService;

    @PostMapping("/signup")
    public ResponseEntity<  ResponseData<MemberDto>> signup( @RequestBody MemberDto memberDto) {

        Member newMember =  userService.createMember(memberDto.getName(),memberDto.getPhone().trim(), memberDto.getEmail(), memberDto.getPassword());

        MemberDto resp = MemberDto.builder()
                .email(newMember.getEmail())
                .name(newMember.getUsername())
                .phone( newMember.getPhone())
                .id(newMember.getUserId())
                .isRootUser(newMember.isRootUser())
                .build();
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),resp ),HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<  ResponseData<MemberDto>> updateUser( @AuthenticationPrincipal Member member, @RequestBody MemberDto memberDto, @PathVariable int userId) {
        if (member == null) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        if( member.getUserId() != userId ) {
            throw new AuthorizationDeniedException("You can't change this user");
        }

        Member loginUser  = userService.getMember(userId );

        if(!UserStatus.ENABLED.equals( loginUser.getStatus() ) ){
            return new ResponseEntity<>(  new ResponseData<>("disabled user "), HttpStatus.UNAUTHORIZED);
        }

        if( memberDto.getName()!= null ){
            loginUser.setUsername(memberDto.getName());
        }

        loginUser.setEmail(memberDto.getEmail());

        if( memberDto.getPassword() != null && memberDto.getPassword().length() > 6 ){
            loginUser.setPassword( memberDto.getPassword() );
        }

        userService.updateUser(loginUser);

        MemberDto resp = MemberDto.builder()
                .email(loginUser.getEmail())
                .name(loginUser.getUsername())
                .phone( loginUser.getPhone())
                .id(loginUser.getUserId())
                .isRootUser(loginUser.isRootUser())
                .build();
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),resp ),HttpStatus.OK);
    }

}
