package com.silverwing.dorothy.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor

@NoArgsConstructor
public  class OffDayId {
    Date offDay;
    int designer;
}
