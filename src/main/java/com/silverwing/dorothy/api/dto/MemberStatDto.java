package com.silverwing.dorothy.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class MemberStatDto {
    int id;
    String name;
    String memo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy/MM/dd", timezone = JsonFormat.DEFAULT_TIMEZONE)
    private Date createDate;
    private long reservationCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy/MM/dd", timezone = JsonFormat.DEFAULT_TIMEZONE)
    private Date lastDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy/MM/dd", timezone = JsonFormat.DEFAULT_TIMEZONE)
    private Date firstDate;

}

