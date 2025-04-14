package com.silverwing.dorothy.api.dto;

import com.silverwing.dorothy.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    String phone;
    String password;
    String email;
    String name;
    String memo;
    int id;
    boolean isRootUser;

    public static MemberDto of(Member m){
        return MemberDto.builder()
                .email(m.getEmail())
                .name(m.getUsername())
                .phone( m.getPhone())
                .id(m.getUserId())
                .isRootUser(m.isRootUser())
                .memo( m.getMemo())
                .build();
    }
}
