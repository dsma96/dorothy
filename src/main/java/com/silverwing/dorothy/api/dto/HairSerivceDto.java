package com.silverwing.dorothy.api.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HairSerivceDto {

    int serviceId;
    String name;
    int idx;
    int serviceTime;
    int price;
    List<OptionDto> options;
}
