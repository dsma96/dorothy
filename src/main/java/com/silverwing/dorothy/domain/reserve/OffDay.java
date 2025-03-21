package com.silverwing.dorothy.domain.reserve;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
@IdClass(OffDayId.class)
@Entity(name="off_day")
@Setter
@Getter
public class OffDay {

    @Id
    @Column(name="off_day")
    Date offDay;

    @Id
    @Column(name="designer")
    int designer;

}
