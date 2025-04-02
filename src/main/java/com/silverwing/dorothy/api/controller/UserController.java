package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.domain.service.user.DorothyUserService;
import com.silverwing.dorothy.api.dto.ChangeMemberInfoDto;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.api.dto.MemberDto;
import com.silverwing.dorothy.domain.type.UserStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

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
    public ResponseEntity<  ResponseData<MemberDto>> updateUser(@AuthenticationPrincipal Member member, @RequestBody ChangeMemberInfoDto memberDto, @PathVariable int userId) {
        if (member == null) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        if( member.getUserId() != userId ) {
            throw new AuthorizationDeniedException("You can't change this user");
        }

        if( ! userService.isMatchedPassword( memberDto.getPassword(), member.getPassword() ) ) {
            throw new AuthorizationDeniedException("Please check your password");
        }


        Member loginUser  = userService.getMember(userId );

        if(!UserStatus.ENABLED.equals( loginUser.getStatus() ) ){
            return new ResponseEntity<>(  new ResponseData<>("disabled user "), HttpStatus.UNAUTHORIZED);
        }

        if( memberDto.getName()!= null ){
            loginUser.setUsername(memberDto.getName());
        }

        loginUser.setEmail(memberDto.getEmail());

        if( memberDto.getNewPassword() != null && memberDto.getNewPassword().length() > 5 ){
            loginUser.setPassword(userService.getEncryptedPassword( memberDto.getNewPassword() ));
        }

        userService.updateUser(loginUser );

        MemberDto resp = MemberDto.builder()
                .email(loginUser.getEmail())
                .name(loginUser.getUsername())
                .phone( loginUser.getPhone())
                .id(loginUser.getUserId())
                .isRootUser(loginUser.isRootUser())
                .build();
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),resp ),HttpStatus.OK);
    }

    /**
     * return available designers on the date.
     * @param dateStr
     * @return
     */
    @GetMapping("/{dateStr}/designers")
    public ResponseEntity<ResponseData<List<MemberDto>>> getDesigners(@AuthenticationPrincipal Member member, @PathVariable String dateStr) {
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        List<Member> designers=  userService.getAvailableDesigners(dateStr);
        if( designers == null || designers.size() == 0 ) {
            return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(), Collections.emptyList()),HttpStatus.OK);
        }

        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),
                designers.stream().map( m -> MemberDto.builder()
                        .name(m.getUsername())
                        .id(m.getUserId())
                        .build()).toList() ),HttpStatus.OK);
    }
}
