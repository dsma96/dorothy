package com.silverwing.dorothy.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.silverwing.dorothy.domain.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class StampDto {
    String serviceDate;
    int stampCount;
    int userId;

    public StampDto(long userId,  String serviceDate,long stampCount){
        this.userId = (int)userId;
        this.stampCount = (int)stampCount;
        this.serviceDate = serviceDate;
    }

}
