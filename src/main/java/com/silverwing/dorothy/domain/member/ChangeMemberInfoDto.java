package com.silverwing.dorothy.domain.member;

import lombok.Getter;

@Getter
public class ChangeMemberInfoDto {
    String password;
    String newPassword;
    String email;
    String name;
    int id;
}
