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
    int id;
    boolean isRootUser;

    static MemberDto of(Member m){
        return MemberDto.builder()
                .email(m.getEmail())
                .name(m.getUsername())
                .phone( m.getPhone())
                .id(m.getUserId())
                .isRootUser(m.isRootUser())
                .build();
    }
}
