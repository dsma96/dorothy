package com.silverwing.dorothy.api.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class hairServiceDto {

    int serviceId;
    String name;
    int idx;
    int serviceTime;
    int price;
    String description;
}
