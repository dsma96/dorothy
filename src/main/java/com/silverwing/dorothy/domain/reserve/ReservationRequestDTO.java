package com.silverwing.dorothy.domain.reserve;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ReservationRequestDTO {
    int userId;
    List<Integer> serviceIds;
    String startTime;
    int designer;
}
