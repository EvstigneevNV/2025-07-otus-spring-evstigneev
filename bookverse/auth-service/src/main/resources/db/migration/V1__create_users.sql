create table if not exists users (
    id uuid primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    roles varchar(255) not null
);
