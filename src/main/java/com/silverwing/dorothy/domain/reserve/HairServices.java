package com.silverwing.dorothy.domain.reserve;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.YesNoConverter;

@Entity
@Table(name="services")
@Data
public class HairServices {
    @Id
    @Column(name="svc_id")
    int serviceId;

    @Column(name="name")
    String name;

    @Column(name="mandatory")
    @Convert(converter = YesNoConverter.class)
    boolean mandatory;

    @Column(name="use")
    @Convert(converter = YesNoConverter.class)
    boolean isUse;

    @Column(name="idx")
    int idx;
}
