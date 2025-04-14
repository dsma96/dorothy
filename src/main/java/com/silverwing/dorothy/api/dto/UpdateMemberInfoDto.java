package com.silverwing.dorothy.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateMemberInfoDto {
    int id;
    String memo;
    String name;
}
