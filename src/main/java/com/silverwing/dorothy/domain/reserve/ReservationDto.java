package com.silverwing.dorothy.domain.reserve;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@Setter
public class ReservationDto {
    int reservationId;
    private String userName;
    private String phone;
    private String startDate;
    private String createDate;
    private ReservationStatus status;
    private List<HairServices> services;
    private boolean isEditable;
    private String memo;
    private boolean isRequireSilence;

}
