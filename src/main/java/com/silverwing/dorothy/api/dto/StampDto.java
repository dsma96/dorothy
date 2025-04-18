package com.silverwing.dorothy.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.silverwing.dorothy.domain.entity.Reservation;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class StampDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd", timezone = JsonFormat.DEFAULT_TIMEZONE)
    Date serviceDate;
    int stampCount;
    boolean isConverted;
    int userId;

    public static StampDto of(Reservation r){
        return StampDto.builder()
                .serviceDate(r.getStartDate())
                .stampCount( r.getStampCount())
                .isConverted( r.isCouponConverted())
                .userId( r.getUserId())
                .build();
    }
}
