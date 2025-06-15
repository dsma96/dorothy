package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.YesNoConverter;

@Entity
@Table(name = "service_options")
@Data
public class ServiceOptionMap {
    @Id
    @Column(name = "svc_opt_id")
    int svcOptId;

    @Column(name = "svc_id")
    int svcId;

    @Column(name = "opt_id")
    int optId;

    @Column(name="use")
    @Convert(converter = YesNoConverter.class)
    boolean isUse;

}
