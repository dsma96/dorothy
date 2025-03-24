package com.silverwing.dorothy.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.silverwing.dorothy.domain.type.VerifyChannel;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class VerifyRequestDto {
    private String phoneNo;
    private VerifyChannel channel;
    private VerifyType type;

    private int maxTry;
    private int failCnt;
    private String verifyCode;
    private VerifyState state;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HH:mm:ss")
    private Date createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HH:mm:ss")
    private Date expireDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HH:mm:ss")
    private Date verifyDate;

}
