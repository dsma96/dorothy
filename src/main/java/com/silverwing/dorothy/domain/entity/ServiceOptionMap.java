package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.YesNoConverter;

@Entity
@Table(name = "service_options")
@IdClass(ServiceOptionId.class)
@Data
public class ServiceOptionMap {

    @Id
    @Column(name = "svc_id")
    int serviceId;

    @Id
    @Column(name = "opt_id")
    int optionId;

    @Column(name="use")
    @Convert(converter = YesNoConverter.class)
    boolean isUse;

}
