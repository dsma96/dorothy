package com.silverwing.dorothy.domain.entity;

import com.silverwing.dorothy.domain.type.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.YesNoConverter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="reservation")
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reg_id")
    int regId;

    @Column(name="user_id")
    int userId;

    @ManyToOne
    @JoinColumn( insertable = false, updatable = false, name="user_id")
    Member user;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    ReservationStatus status;

    @Column(name="create_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(name="modify_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date modifyDate;

    @Column(name="designer")
    int designerId;

    @ManyToOne
    @JoinColumn( insertable = false, updatable = false, name="designer")
    Member Designer;

    @Column(name="memo")
    String memo;

    @Column(name="start_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date startDate;

    @Column(name="end_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    @Column(name="modifier")
    int modifier;

    @Column(name="req_silence")
    @Convert(converter = YesNoConverter.class)
    boolean requireSilence;

    @OneToMany(mappedBy = "regId")
    List<ReserveServiceMap> services;
}
