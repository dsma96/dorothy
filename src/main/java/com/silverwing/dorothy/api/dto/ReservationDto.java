package com.silverwing.dorothy.api.dto;

import com.silverwing.dorothy.domain.entity.HairServices;
import com.silverwing.dorothy.domain.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private String endDate;
    private String createDate;
    private ReservationStatus status;
    private List<HairServices> services;
    private boolean isEditable;
    private String memo;
    private boolean isRequireSilence;
    private List<UploadFileDto> files;
    private int userId;
    private float tip;
}
