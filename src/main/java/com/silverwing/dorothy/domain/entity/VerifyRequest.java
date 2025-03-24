package com.silverwing.dorothy.domain.entity;

import com.silverwing.dorothy.domain.type.VerifyChannel;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name="verify_request")
@Getter
@Setter
public class VerifyRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="verify_req_id")
    int verifyId;

    @Column(name="phone_no")
    String phoneNo;

    @Column(name="verify_channel")
    @Enumerated(EnumType.STRING)
    VerifyChannel verifyChannel;

    @Column(name="verify_type")
    @Enumerated(EnumType.STRING)
    VerifyType verifyType;

    @Column(name="verify_state")
    @Enumerated(EnumType.STRING)
    VerifyState verifyState;

    @Column(name="create_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column( name="expire_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date expireDate;

    @Column( name="verify_code")
    String  verifyCode;

    @Column( name="fail_cnt")
    int failCnt;

    @Column( name="max_try")
    int maxTry;

    @Column( name="verify_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date verifyDate;
}
