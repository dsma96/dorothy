package com.silverwing.dorothy.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="marketing_service")
@Data
public class MarketingServiceMap {
    @Id
    @Column(name="id")
    int id;

    @Column(name="marketing_id")
    int marketingId;

    @JsonIgnore
    @Column(name="svc_id")
    int serviceId;

    @ManyToOne
    @JoinColumn(name="svc_id",insertable=false,updatable=false)
    HairServices service;
}
