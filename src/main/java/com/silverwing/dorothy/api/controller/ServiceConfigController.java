package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.dto.SaleStatDto;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.ServiceConfig;
import com.silverwing.dorothy.domain.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configurations")
@RequiredArgsConstructor
public class ServiceConfigController {

    private final ConfigService configService;

    @GetMapping("/")
    public ResponseEntity<ResponseData<ServiceConfig>> getServiceConfig(@AuthenticationPrincipal Member member) {

        if (member == null ) {
            throw new AuthenticationCredentialsNotFoundException("Not enough permission");
        }

        ServiceConfig serviceConfig = configService.getAllConfiguration();
        return new ResponseEntity<>(new ResponseData<>("OK", HttpStatus.OK.value(), serviceConfig), HttpStatus.OK);
    }

}
