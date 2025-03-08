package com.silverwing.dorothy.domain.reserve;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Data;

@Entity
@Data
@Table(name="reserve_services")
public class ReserveServiceMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reg_svc_id")
    int regSvcId;

    @Column(name="reg_id")
    int regId;

    @Column(name="svc_id")
    int svcId;

    @ManyToOne
    @JoinColumn(name="svc_id",insertable=false,updatable=false)
    HairServices service;
}
