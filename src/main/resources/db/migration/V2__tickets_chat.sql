create table tickets (
                         id bigserial primary key,
                         title varchar(200) not null,
                         description text,
                         status varchar(50) not null,
                         priority varchar(50) not null,

                         requester_id bigint not null,
                         assignee_id bigint,
                         group_id bigint,

                         due_at timestamp,
                         created_at timestamp not null default now(),
                         updated_at timestamp not null default now(),
                         closed_at timestamp,

                         constraint fk_ticket_requester foreign key (requester_id)
                             references users(id),
                         constraint fk_ticket_assignee foreign key (assignee_id)
                             references users(id),
                         constraint fk_ticket_group foreign key (group_id)
                             references engineer_groups(id)
);

create index idx_tickets_status on tickets(status);
create index idx_tickets_priority on tickets(priority);
create index idx_tickets_requester on tickets(requester_id);
create index idx_tickets_assignee on tickets(assignee_id);

create table ticket_messages (
                                 id bigserial primary key,
                                 ticket_id bigint not null,
                                 author_id bigint not null,
                                 message text not null,
                                 created_at timestamp not null default now(),

                                 constraint fk_tm_ticket foreign key (ticket_id)
                                     references tickets(id) on delete cascade,
                                 constraint fk_tm_author foreign key (author_id)
                                     references users(id)
);

create index idx_ticket_messages_ticket on ticket_messages(ticket_id);

create table ticket_history (
                                id bigserial primary key,
                                ticket_id bigint not null,
                                changed_by bigint not null,
                                field varchar(100) not null,
                                old_value text,
                                new_value text,
                                created_at timestamp not null default now(),

                                constraint fk_th_ticket foreign key (ticket_id)
                                    references tickets(id) on delete cascade,
                                constraint fk_th_user foreign key (changed_by)
                                    references users(id)
);

create index idx_ticket_history_ticket on ticket_history(ticket_id);