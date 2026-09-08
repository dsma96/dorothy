package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.type.YesNoConverter;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name="services")
@Data
@Getter
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

    @Column(name="description")
    String description;

    @Column(name="guide")
    String guide;

    @OneToMany(mappedBy = "serviceId", fetch = FetchType.EAGER)
    private List<ServicePrice> servicePrices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HairServices svc = (HairServices) o;
        return Objects.equals(serviceId, svc.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }
}