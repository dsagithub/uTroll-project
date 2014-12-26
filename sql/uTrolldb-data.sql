source uTrolldb-schema.sql;

insert into groups values(0, 'Grupo Nulo', 0, 0, 0, 'none');
update groups set groupid=0 where groupid=1;
insert into groups values(1, 'Grupo de prueba', 5, 0, 0, 'open');
insert into groups values(2, 'Grupo de prueba 2', 5, 0, 0, 'open');

insert into users values('david', MD5('david'), 'David', 'david@mail.com', 19, 30, 30, 0);
insert into user_roles values ('david', 'registered');

insert into users values('angel', MD5('angel'), 'Angel', 'angel@mail.com', 22, 40, 40, 0);
insert into user_roles values ('angel', 'registered');

insert into friend_list values(NULL, 'angel', 'david', 'accepted');

insert into friend_list values(NULL, 'angel', 'albert', 'pending');

insert into friend_list values(NULL, 'david', 'albert', 'accepted');