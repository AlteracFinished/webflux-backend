create table backend_user
(
    id bigint auto_increment
        primary key,
    username varchar(200) not null,
    password varchar(200) null,
    description varchar(500) null comment 'user description',
    roles json null,
    constraint idx_username
        unique (username)
)
    comment 'login user';

INSERT INTO spring_backend.backend_user (username, password, description, roles) VALUES ('admin', '$2a$10$xhEGATZARVntAZmxH0Enuekymq5LMThMeOPDgLkuoax5NXUUsWvM6', null, '["ADMIN"]');