create table users (
                       id uuid primary key,
                       name varchar(120) not null,
                       email varchar(160) not null unique,
                       password_hash varchar(255) not null,
                       created_at timestamp with time zone not null,
                       updated_at timestamp with time zone not null
);