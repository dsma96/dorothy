package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.domain.member.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/hello")
    public ResponseEntity<String> helloUser(@AuthenticationPrincipal Member member){
        return new ResponseEntity<>("Hello User", HttpStatus.OK);
    }
}
