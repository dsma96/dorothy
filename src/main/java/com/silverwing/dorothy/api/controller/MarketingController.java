package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.dto.ReservationDto;
import com.silverwing.dorothy.domain.entity.Marketing;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.Reservation;
import com.silverwing.dorothy.domain.service.MarketingService;
import com.silverwing.dorothy.domain.service.reserve.ReservationService;
import com.silverwing.dorothy.domain.task.CustomerRemindTask;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/marketing")
@RequiredArgsConstructor
public class MarketingController {
    private final ReservationService reservationService;
    private final MarketingService marketingService;
    private final CustomerRemindTask customerRemindTask;

    private SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");

    @GetMapping("/marketings")
    public ResponseEntity<ResponseData<List<Marketing>>> getMarketings(@AuthenticationPrincipal Member member) {
        if (member == null || !member.isRootUser()){
            throw new AuthenticationCredentialsNotFoundException("Permission Denied");
        }

        List<Marketing> marketings = marketingService.getAvailableMarketings();
        customerRemindTask.smsMarketingTask();
        return new ResponseEntity<>(new ResponseData<>("OK", HttpStatus.OK.value(), marketings), HttpStatus.OK);
    }


}
