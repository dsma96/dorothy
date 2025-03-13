package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.service.DorothyUserService;
import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.member.MemberDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
}
