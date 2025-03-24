package com.silverwing.dorothy.api.controller;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.silverwing.dorothy.api.dto.MemberDto;
import com.silverwing.dorothy.api.dto.VerifyRequestDto;
import com.silverwing.dorothy.domain.Exception.VerifyException;
import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.service.verify.VerifyService;
import com.silverwing.dorothy.domain.type.VerifyType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController()
@RequestMapping("/api/verify")
@RequiredArgsConstructor
@Slf4j
public class VerifiController {

    private Pattern telNoPattern = Pattern.compile("^[0-9]{10}$");

    private final VerifyService verifyService;

    @PostMapping("/request")
    public ResponseEntity <ResponseData<VerifyRequestDto>> requestVerify(@RequestBody VerifyRequestDto requestDto) {
        String phoneNo = requestDto.getPhoneNo();

        if( telNoPattern.matcher(phoneNo).find() ) {
            VerifyRequest req =  verifyService.createVerifyRequest(phoneNo, VerifyType.SIGN_UP);
            VerifyRequestDto res = VerifyRequestDto.builder()
                    .phoneNo( req.getPhoneNo() )
                    .expireDate( req.getExpireDate() )
                    .createDate( req.getCreateDate() )
                    .channel(req.getVerifyChannel())
                    .failCnt(req.getFailCnt())
                    .maxTry(req.getMaxTry())
                    .state( req.getVerifyState())
                    .type(req.getVerifyType())
                    .build();

            return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),res ),HttpStatus.OK);
        }
        else{
            throw new VerifyException("Invalid phone number:"+phoneNo);
        }
    }

    @PostMapping("/match")
    public ResponseEntity<ResponseData<VerifyRequestDto>> verify(@RequestBody VerifyRequestDto requestDto) {
        String phoneNo = requestDto.getPhoneNo();

        if( !telNoPattern.matcher(phoneNo).find() )
            throw new VerifyException("Invalid phone number:"+phoneNo);

        if( StringUtils.isBlank( requestDto.getVerifyCode())){
            throw new VerifyException("Invalid verify code");
        }
        try {
            VerifyRequest req = verifyService.verifyRequest(phoneNo, requestDto.getVerifyCode());

            VerifyRequestDto res = VerifyRequestDto.builder()
                    .phoneNo(req.getPhoneNo())
                    .expireDate(req.getExpireDate())
                    .createDate(req.getCreateDate())
                    .channel(req.getVerifyChannel())
                    .state(req.getVerifyState())
                    .type(req.getVerifyType())
                    .failCnt(req.getFailCnt())
                    .verifyDate(req.getVerifyDate())
                    .maxTry(req.getMaxTry())
                    .build();

            return new ResponseEntity<>(new ResponseData<>("OK", HttpStatus.OK.value(), res), HttpStatus.OK);
        }catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new ResponseData<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value(),  null), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
