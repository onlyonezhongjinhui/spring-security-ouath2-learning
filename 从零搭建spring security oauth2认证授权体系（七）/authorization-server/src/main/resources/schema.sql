drop database if exists user_center;
create database user_center character set utf8mb4;
use user_center;

create table oauth_client_details
(
    client_id               varchar(256) not null primary key,
    resource_ids            varchar(256) null,
    client_secret           varchar(256) not null,
    scope                   varchar(256) not null,
    authorized_grant_types  varchar(256) not null,
    web_server_redirect_uri varchar(256) null,
    authorities             varchar(256) null,
    access_token_validity   int          not null,
    refresh_token_validity  int          not null,
    additional_information  varchar(4096),
    autoapprove             varchar(256)
);

create table oauth_client_token
(
    authentication_id varchar(256) primary key,
    token_id          varchar(256),
    token             blob,
    user_name         varchar(256),
    client_id         varchar(256)
);

create table oauth_access_token
(
    authentication_id varchar(256) primary key,
    token_id          varchar(256),
    token             blob,
    user_name         varchar(256),
    client_id         varchar(256),
    authentication    blob,
    refresh_token     varchar(256),
    key token_id (token_id),
    key user_name (user_name),
    key client_id (client_id),
    key refresh_token (refresh_token)
);

create table oauth_refresh_token
(
    token_id       varchar(256) primary key,
    token          blob,
    authentication blob
);

create table oauth_code
(
    code           varchar(256) primary key,
    authentication blob
);

create table `user`
(
    id       varchar(64)  not null primary key,
    username varchar(64)  not null,
    password varchar(256) not null,
    phone    varchar(11)  null,
    locked   tinyint(1)   not null,
    enabled  tinyint(1)   not null,
    open_id  varchar(64)  null,
    unique key username (`username`),
    unique key phone (`phone`),
    unique key open_id (`open_id`)
);

create table `role`
(
    `id`   varchar(64) not null primary key,
    `code` varchar(40) not null,
    `name` varchar(64) not null,
    unique key code (`code`),
    unique key name (`name`)
);

create table `user_role`
(
    `id`      varchar(64) not null primary key,
    `user_id` varchar(64) not null,
    `role_id` varchar(64) not null,
    key user_id (`user_id`),
    key role_id (`role_id`)
);

create table `permission`
(
    `id`            varchar(64) not null primary key,
    `parent_id`     varchar(64) not null,
    `code`          varchar(40) not null,
    `name`          varchar(64) not null,
    `resource_type` tinyint(1)  not null comment 'resource type eg 0:main menu 1:child menu 2:function button',
    `route_name`    varchar(64) not null comment 'route name',
    `route_url`     varchar(64) not null comment 'route url',
    `component`     varchar(32) not null comment 'component',
    `icon`          varchar(32) not null comment 'icon',
    unique key code (`code`),
    unique key name (`name`)
);

create table `role_permission`
(
    `id`            varchar(64) not null primary key,
    `permission_id` varchar(64) not null,
    `role_id`       varchar(64) not null,
    key permission_id (`permission_id`),
    key role_id (`role_id`)
);

insert into oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types,
                                  web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity,
                                  additional_information, autoapprove)
values ('order', 'order', 'secret', 'read,write',
        'authorization_code,client_credentials,password,implicit,refresh_token', 'http://www.baidu.com', '', 2592000,
        15552000, null, true);

insert into role (id, code, name)
VALUES ('1406151407442780162', 'user', '普通用户'),
       ('1406151407442780163', 'admin', '管理员');

insert into permission (id, parent_id, code, name, resource_type, route_name, route_url, component, icon)
values ('1406153943707496456', '', 'menu:order', '订单管理菜单', '0', '商品管理', '/goods', 'GoodsList', 'goods'),
       ('1406153943707496457', '', 'menu:goods', '商品管理菜单', '0', '商品管理', '/order', 'OrderList', 'order'),
       ('1406144185783369728', '1406153943707496456', 'order:add', '添加订单', '2', '', '', '', ''),
       ('1406144185783369729', '1406153943707496456', 'order:update', '更新订单', '2', '', '', '', ''),
       ('1406144185783369730', '1406153943707496456', 'order:delete', '删除订单', '2', '', '', '', ''),
       ('1406144185783369731', '1406153943707496456', 'order:query', '查询订单', '2', '', '', '', ''),
       ('1406144185783369732', '1406153943707496457', 'goods:add', '添加商品', '2', '', '', '', ''),
       ('1406144185783369733', '1406153943707496457', 'goods:update', '更新商品', '2', '', '', '', ''),
       ('1406144185783369734', '1406153943707496457', 'goods:delete', '删除商品', '2', '', '', '', ''),
       ('1406144185787564032', '1406153943707496457', 'goods:query', '查询商品', '2', '', '', '', '');

insert into role_permission (id, permission_id, role_id)
values ('1406151407442780167', '1406153943707496456', '1406151407442780162'),
       ('1406151407442780168', '1406153943707496457', '1406151407442780162'),
       ('1406151407442780169', '1406144185783369731', '1406151407442780162'),
       ('1406151407442780170', '1406144185787564032', '1406151407442780162'),

       ('1406151407442780171', '1406153943707496456', '1406151407442780163'),
       ('1406151407442780172', '1406153943707496457', '1406151407442780163'),
       ('1406151407442780173', '1406144185783369728', '1406151407442780163'),
       ('1406153943707496448', '1406144185783369729', '1406151407442780163'),
       ('1406153943707496449', '1406144185783369730', '1406151407442780163'),
       ('1406153943707496450', '1406144185783369731', '1406151407442780163'),
       ('1406153943707496451', '1406144185783369732', '1406151407442780163'),
       ('1406153943707496452', '1406144185783369733', '1406151407442780163'),
       ('1406153943707496453', '1406144185783369734', '1406151407442780163'),
       ('1406153943707496454', '1406144185787564032', '1406151407442780163');

insert into user (id, username, password, phone, locked, enabled, open_id)
VALUES ('1406151407442780160', 'user', 'password', null, 0, 1, null),
       ('1406151407442780161', 'admin', 'password', null, 0, 1, null);

insert into user_role (id, user_id, role_id)
VALUES ('1406151407442780164', '1406151407442780160', '1406151407442780162'),
       ('1406151407442780165', '1406151407442780161', '1406151407442780162'),
       ('1406151407442780166', '1406151407442780161', '1406151407442780163');

