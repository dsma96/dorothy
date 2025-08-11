package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.YesNoConverter;

import java.util.List;

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

    @Column(name="visible")
    @Convert(converter = YesNoConverter.class)
    boolean isVisible;

    @Column(name="default_val")
    @Convert(converter = YesNoConverter.class)
    boolean defaultValue;

    @Column(name="svc_time")
    int serviceTime;

    @Column(name="price")
    int price;

    @OneToMany(mappedBy = "serviceId", fetch = FetchType.EAGER)
    private List<ServicePrice> servicePrices;

    @ManyToMany
    @JoinTable(
            name = "service_options",
            joinColumns = @JoinColumn(name = "svc_id", referencedColumnName = "svc_id"),
            inverseJoinColumns = @JoinColumn(name = "opt_id", referencedColumnName = "opt_id")
    )
    private List<Options> options;
}