package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.dto.MemberDto;
import com.silverwing.dorothy.api.dto.StampDto;
import com.silverwing.dorothy.domain.Exception.CouponException;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.Reservation;
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

    @GetMapping("/{userId}/stamps")
    public ResponseEntity<  ResponseData<List<StampDto>>> getStamps(@AuthenticationPrincipal Member member , @PathVariable int userId) {
        if( member == null ){
            throw new CouponException("Login required");
        }
        if(!member.isRootUser() && member.getUserId() != userId){
            throw new CouponException("Permission error");
        }

        List<StampDto> rst =  couponService.getStamps(userId);

        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),rst ),HttpStatus.OK);
    }

    @PutMapping("/{userId}/coupon")
    public ResponseEntity<  ResponseData<Integer>> convertStamps(@AuthenticationPrincipal Member member, @PathVariable int userId, @RequestParam int regId) {
        if( member == null ){
            throw new CouponException("Login required");
        }
        if( !member.isRootUser()){
            throw new CouponException("Only Root User can use coupon");
        }

        int rst = couponService.convertCoupon(userId, regId, member);
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),rst ),HttpStatus.OK);
    }

    @GetMapping("/{userId}/stampAmount")
    public ResponseEntity <ResponseData<Integer>> getStampCount(@AuthenticationPrincipal Member caller, @PathVariable int userId){
        if( caller == null || !caller.isRootUser()){
            throw new CouponException(("Root permissio required"));
        }

        List<StampDto> rst =  couponService.getStamps(userId);
        return new ResponseEntity<>( new ResponseData<Integer>("OK", HttpStatus.OK.value(), rst.size()), HttpStatus.OK);
    }

}
