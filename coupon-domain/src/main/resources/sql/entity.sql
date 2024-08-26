# coupon 관련
create table coupon
(
    id        bigint auto_increment
        primary key,
    code      varchar(50) null,
    name      varchar(50) null,
    expiredAt datetime(6) null
);

create table coupon_history
(
    id          bigint auto_increment
        primary key,
    couponId    bigint       null,
    username    varchar(50)  null,
    message     varchar(255) null,
    createdAt   datetime(6)  null,
    historyType varchar(50)  null
);

create table coupon_inventory
(
    id            bigint auto_increment
        primary key,
    coupon_id     bigint null,
    issuedCoupons int    not null,
    totalCoupons  int    not null
);





