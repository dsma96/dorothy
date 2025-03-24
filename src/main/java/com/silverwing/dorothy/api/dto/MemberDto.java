package com.silverwing.dorothy.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    String phone;
    String password;
    String email;
    String name;
    int id;
    boolean isRootUser;

}
