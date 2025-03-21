package com.silverwing.dorothy.domain.reserve;

import jakarta.persistence.Embeddable;
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
