package com.silverwing.dorothy.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.silverwing.dorothy.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy/MM/dd", timezone = JsonFormat.DEFAULT_TIMEZONE)
    private Date createDate;


    public static MemberDto of(Member m){
        return MemberDto.builder()
                .email(m.getEmail())
                .name(m.getUserName())
                .phone( m.getPhone())
                .id(m.getUserId())
                .isRootUser(m.isRootUser())
                .memo( m.getMemo())
                .createDate(m.getCreateDate())
                .build();
    }
}
