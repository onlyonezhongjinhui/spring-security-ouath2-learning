drop database if exists user_center;
create database user_center character set utf8mb4 collate utf8mb4_unicode_ci;
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

insert into oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types,
                                  web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity,
                                  additional_information, autoapprove)
values ('order', 'order', '{noop}secret', 'read,write',
        'authorization_code,client_credentials,password,implicit,refresh_token', 'www.baidu.com', 'ADMIN,USER', 2592000,
        15552000, null, true);