package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.type.YesNoConverter;

@Entity
@Table(name = "options")
@Getter
public class Options {

    @Id
    @Column(name="opt_id")
    int optionId;

    @Column(name="name")
    String name;

    @Column(name="use")
    @Convert(converter = YesNoConverter.class)
    boolean isUse;

    @Column(name="idx")
    int idx;

    @Column(name="visible")
    @Convert(converter = YesNoConverter.class)
    boolean isVisible;

    @Column(name="price")
    int price;
}
