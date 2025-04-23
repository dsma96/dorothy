package com.silverwing.dorothy.domain.entity;

import com.silverwing.dorothy.domain.type.CouponStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name="coupon")
@Data
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    int id;

    @Column(name="user_id")
    int userId;

    @Column(name="create_id")
    int createId;

    @Column(name="create_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(name="modify_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date modifyDate;

    @Column(name="stamp_count")
    int stampCount;

    @Column(name="reg_id")
    int usedRegId;

    //
    @Column(name="status")
    @Enumerated(EnumType.STRING)
    CouponStatus status;

}
