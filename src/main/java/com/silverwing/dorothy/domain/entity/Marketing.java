package com.silverwing.dorothy.domain.entity;

import com.silverwing.dorothy.domain.type.MessageResourceId;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.YesNoConverter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="marketing")
@Data
public class Marketing {
    @Id
    @Column(name="id")
    int id;

    @Column(name="start_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date startDate;

    @Column(name="end_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date endDate;

    @Column(name="use_yn")
    @Convert(converter = YesNoConverter.class)
    boolean used;

    @Column(name="msg_id")
    @Enumerated(EnumType.STRING)
    private MessageResourceId messageId;


    @Column(name="days")
    int days;

    @OneToMany(mappedBy ="marketingId" )
    List<MarketingServiceMap> services;
}
