create table dorothy.off_day
(
    off_day  date          not null,
    designer int default 1 not null,
    primary key (off_day, designer)
);

create table dorothy.reservation
(
    reg_id      int auto_increment
        primary key,
    user_id     int                                      not null,
    status      varchar(12)  default 'CREATED'           null,
    create_date datetime     default current_timestamp() not null,
    modify_date datetime     default current_timestamp() null,
    start_date  datetime                                 not null,
    end_date    datetime                                 null,
    designer    int                                      null,
    memo        varchar(256) default ''                  null,
    modifier    int                                      null,
    req_silence char         default 'N'                 null comment 'User don''t want make any conversation.'
);

create index RESERVATION_start_date_index
    on dorothy.reservation (start_date, user_id);

create table dorothy.reserve_services
(
    reg_svc_id int auto_increment
        primary key,
    reg_id     int not null,
    svc_id     int not null
);

create index reserve_services_reg_id_index
    on dorothy.reserve_services (reg_id);

create table dorothy.services
(
    svc_id      int auto_increment
        primary key,
    name        varchar(64)      null,
    mandatory   char default 'N' not null,
    `use`       char default 'Y' not null,
    idx         int  default 999 not null,
    visible     char default 'Y' null,
    default_val char default 'N' null
);

create table dorothy.upload_file
(
    file_id        int auto_increment
        primary key,
    user_file_name varchar(128)                            not null,
    file_path      varchar(256)                            not null,
    file_status    varchar(16) default 'CREATED'           not null,
    create_date    datetime    default current_timestamp() not null,
    use_yn         char        default 'Y'                 null,
    user_id        int                                     not null,
    reg_id         int                                     not null
);

create index upload_file_reg_id_index
    on dorothy.upload_file (reg_id);

create table dorothy.user
(
    user_id        int auto_increment
        primary key,
    user_name      varchar(32)                             not null,
    phone          varchar(16)                             not null,
    email          varchar(64)                             null,
    user_pwd       varchar(128)                            not null,
    root_user      varchar(1)  default 'N'                 null,
    login_fail_cnt int         default 0                   null,
    create_date    datetime    default current_timestamp() null,
    last_login     datetime                                null,
    status         varchar(12) default 'ENABLED'           null,
    role           varchar(32) default 'USER'              not null,
    memo           varchar(256)                            null,
    last_login_try datetime                                null,
    constraint USER_PHONE_IDX
        unique (phone)
);
create table dorothy.USER_GROUP
(
    group_id   int auto_increment
        primary key,
    group_name varchar(32) not null,
    status     varchar(12) null
);

create table dorothy.verify_request
(
    phone_no       varchar(12)                             not null,
    verify_req_id  int auto_increment
        primary key,
    verify_type    varchar(12) default 'SIGN_UP'           not null,
    create_date    datetime    default current_timestamp() not null,
    expire_date    datetime                                not null,
    fail_cnt       int         default 0                   null,
    max_try        int         default 10                  not null,
    verify_channel varchar(12) default 'SMS'               not null,
    verify_state   varchar(16) default 'CREATED'           not null comment 'state',
    verify_code    varchar(16)                             not null,
    verify_date    datetime                                null
);


