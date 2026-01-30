create table knowledge_categories (
                                      id bigserial primary key,
                                      name varchar(255) not null unique
);

create table knowledge_articles (
                                    id bigserial primary key,
                                    title varchar(255) not null,
                                    content text not null,
                                    category_id bigint,
                                    author_id bigint not null,
                                    is_published boolean not null default true,
                                    created_at timestamp not null default now(),
                                    updated_at timestamp not null default now(),

                                    constraint fk_ka_category foreign key (category_id)
                                        references knowledge_categories(id),
                                    constraint fk_ka_author foreign key (author_id)
                                        references users(id)
);

create index idx_knowledge_articles_title on knowledge_articles(title);