package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.dto.MemberDto;
import com.silverwing.dorothy.api.dto.SaleStatDto;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.service.StatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/stat")
@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @GetMapping("/{year}/monthly")
    public ResponseEntity<ResponseData<List<SaleStatDto>>> getMonthlyStat(@AuthenticationPrincipal Member member, @PathVariable int year) {

        if (member == null || !member.isRootUser()) {
            throw new AuthenticationCredentialsNotFoundException("Not enough permission");
        }

        List <SaleStatDto> monthlyStats =  statService.getMonthlyStat(year);
        return new ResponseEntity<>(new ResponseData<>("OK", HttpStatus.OK.value(), monthlyStats), HttpStatus.OK);
    }
}
