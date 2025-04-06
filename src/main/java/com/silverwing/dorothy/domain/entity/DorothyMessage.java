package com.silverwing.dorothy.domain.entity;

import com.silverwing.dorothy.domain.type.MessageResourceId;
import jakarta.persistence.*;
import lombok.Getter;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.type.YesNoConverter;

@Entity
@IdClass(DorothyMessageId.class)
@Table(name = "message")
@Getter
public class DorothyMessage {
    @Id
    @Column(name="msg_id")
    @Enumerated(EnumType.STRING)
    private MessageResourceId messageId;

    @Id
    @Column(name="language")
    private String language;

    @Column(name="msg")
    private String message;

    @Column(name="default_yn")
    @Convert(converter = YesNoConverter.class)
    private boolean isDefault;

    @Column(name="description")
    private String description;

}
