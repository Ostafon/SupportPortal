insert into users (email, password_hash, first_name,last_name,username, role)
values (
           'admin@support.local',
           '$2a$10$7EqJtq98hPqEX7fNZaFWoOaW5m9GUGTjT6S3w0a7bYq9aYp6Qe8i', -- password: "password"
           'System',
        'Administrator',
        'sysadmin',
           'ADMIN'
       );

insert into engineer_groups (name)
values ('Default Support Team');