package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="user_reservation_stat")
public class MemberReservationStat {
    @Column(name="user_id")
    @Id
    int userId;

    @Column(name="reserve_cnt")
    int reservationCount;

    @Column(name="last_visit_date")
    Date lastVisitDate;

    @Column(name="first_visit_date")
    Date firstVisitDate;

    @Column(name="update_dt")
    @Temporal(TemporalType.TIMESTAMP)
    Date updateDate;
}
