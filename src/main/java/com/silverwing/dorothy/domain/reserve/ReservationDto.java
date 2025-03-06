package com.silverwing.dorothy.domain.reserve;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
public class ReservationDto {
    int reservationId;
    private String userName;
    private String phone;
    private String startDate;
    private Date createDate;

    private List<HairServices> services;
}
