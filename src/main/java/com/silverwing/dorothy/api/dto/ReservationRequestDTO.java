package com.silverwing.dorothy.api.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {
    int userId;
    List<Integer> serviceIds;
    String startTime;
    int designer;
    String memo;
    boolean requireSilence;
}
