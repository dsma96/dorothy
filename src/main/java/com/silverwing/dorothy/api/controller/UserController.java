package com.silverwing.dorothy.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/hello")
    public ResponseEntity<String> helloUser(){
        return new ResponseEntity<>("Hello User", HttpStatus.OK);
    }
}
