package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@IdClass(ServicePriceId.class)
@Table(name = "service_price")
@Getter
@Setter
@NoArgsConstructor
public class ServicePrice {

    public ServicePrice(int price){
        this.price = price;
    }

    @Id
    @Column(name = "svc_id")
    private int serviceId;

    @Id
    @Column(name= "start_date")
    Date startDate;

    @Column(name = "end_date")
    Date endDate;

    @Column(name="price")
    int price;
}
