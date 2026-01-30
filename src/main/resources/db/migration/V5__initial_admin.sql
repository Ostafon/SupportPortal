insert into users (email, password_hash, full_name, role)
values (
           'admin@support.local',
           '$2a$10$7Qe1Q9Q7Q7Q7Q7Q7Q7Q7Qe', -- заглушка
           'System Administrator',
           'ADMIN'
       );

insert into engineer_groups (name)
values ('Default Support Team');