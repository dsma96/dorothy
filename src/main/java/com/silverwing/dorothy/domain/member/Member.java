package com.silverwing.dorothy.domain.member;

import com.silverwing.dorothy.domain.type.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.type.YesNoConverter;

import java.util.Date;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name= "user")
public class Member {

    @Builder
    public Member(String username, String phone, String email, String password){
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="user_id")
    private long user_id;

    @Column(name="user_name")
    private String username;

    @Column(name="phone")
    private String phone;

    @Column(name="email")
    private String email;

    @Column(name="user_pwd")
    private String password;

    @Column(name="root_user")
    @Convert(converter = YesNoConverter.class)
    private boolean isRootUser;

    @Column(name="login_fail_cnt")
    private int loginFailCnt;

    @Column(name="create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name="last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name="memo")
    String memo;
}
