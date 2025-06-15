package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="reserve_options")
@Data
public class ReserveOptionMap {
    @Id
    @Column(name="reg_opt_id")
    int regOptId;

    @Column(name="reg_id")
    int regId;

    @Column(name="opt_id")
    int optId;

    @Column(name="price")
    int price;

    @ManyToOne
    @JoinColumn(name="opt_id",insertable=false,updatable=false)
    Options options;
}
