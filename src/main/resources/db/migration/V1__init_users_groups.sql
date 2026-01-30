create table users (
                       id bigserial primary key,
                       email varchar(255) not null unique,
                       password_hash varchar(255) not null,
                       full_name varchar(255) not null,
                       role varchar(50) not null,
                       created_at timestamp not null default now()
);

create table engineer_groups (
                                 id bigserial primary key,
                                 name varchar(255) not null unique
);

create table engineer_group_members (
                                        group_id bigint not null,
                                        user_id bigint not null,
                                        primary key (group_id, user_id),
                                        constraint fk_egm_group foreign key (group_id)
                                            references engineer_groups(id) on delete cascade,
                                        constraint fk_egm_user foreign key (user_id)
                                            references users(id) on delete cascade
);