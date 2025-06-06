package com.silverwing.dorothy.domain.entity;

import com.silverwing.dorothy.domain.type.UserRole;
import com.silverwing.dorothy.domain.type.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.type.YesNoConverter;

import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
@Setter
@Table(name= "user")
public class Member {

    @Builder
    public Member(String username, String phone, String email, String password){
        this.userName = username;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private int userId;

    @Column(name="user_name")
    private String userName;

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

    @Column(name="last_login_try")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTry;


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
