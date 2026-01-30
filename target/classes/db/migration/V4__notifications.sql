create table notifications (
                               id bigserial primary key,
                               user_id bigint not null,
                               channel varchar(50) not null,
                               title varchar(255) not null,
                               body text not null,
                               status varchar(50) not null,
                               created_at timestamp not null default now(),
                               sent_at timestamp,

                               constraint fk_notification_user foreign key (user_id)
                                   references users(id)
);

create index idx_notifications_user on notifications(user_id);
create index idx_notifications_status on notifications(status);