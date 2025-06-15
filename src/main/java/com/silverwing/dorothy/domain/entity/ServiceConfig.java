package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "config")
@Setter
@Getter
public class ServiceConfig {
    @Id
    @Column(name="address")
    private String address;
    @Column(name="shop_name")
    private String shopName;
    @Column(name="close_time")
    private String closeTime;
    @Column(name="max_reservation_date")
    private long maxReservationDate;
}
