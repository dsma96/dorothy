package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.dto.MemberDto;
import com.silverwing.dorothy.api.dto.StampDto;
import com.silverwing.dorothy.domain.Exception.CouponException;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/api/coupon")
@Slf4j
public class CouponController {
    private final CouponService couponService;

    @GetMapping("/stamps")
    public ResponseEntity<  ResponseData<List<StampDto>>> getStamps(@AuthenticationPrincipal Member member) {
        if( member == null ){
            throw new CouponException("Login required");
        }

        List<StampDto> rst =  couponService.getStamps(member.getUserId()).stream().map( r-> StampDto.of( r)).toList();

        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),rst ),HttpStatus.OK);
    }

    @PutMapping("/{userid}/coupon")
    public ResponseEntity<  ResponseData<String>> convertStamps(@AuthenticationPrincipal Member member) {
        if( member == null ){
            throw new CouponException("Login required");
        }
        if( !member.isRootUser()){
            throw new CouponException("Only Root User can use coupon");
        }


        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),"OK" ),HttpStatus.OK);
    }

}
