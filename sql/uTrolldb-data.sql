source uTrolldb-schema.sql;

insert into groups values(0, 'Grupo Nulo', 0, 0, 0, 0, 'uTroll', 'No Troll', 'none');
update groups set groupid=0 where groupid=1;
insert into groups values(1, 'Grupo de prueba', 15, 0, 0, 0, 'david', 'No Troll', 'open');
insert into groups values(2, 'Grupo de prueba 2', 30, 0, 0, 0, 'david', 'No Troll', 'open');

insert into users values('david', MD5('david'), 'David', 'david@mail.com', 19, 30, 30, false, 0, 0, 'none');
insert into user_roles values ('david', 'registered');

insert into users values('angel', MD5('angel'), 'Angel', 'angel@mail.com', 22, 40, 40, false, 0, 0, 'none');
insert into user_roles values ('angel', 'registered');

insert into users values('albert', MD5('albert'), 'Albert', 'albert@mail.com', 25, 40, 40, false, 0, 0, 'none');
insert into user_roles values ('albert', 'registered');

insert into friend_list values(NULL, 'angel', 'david', 'accepted', true);
insert into friend_list values(NULL, 'david', 'angel', 'accepted', false);

insert into friend_list values(NULL, 'angel', 'albert', 'pending', true);
insert into friend_list values(NULL, 'albert', 'angel', 'pending', false);

insert into friend_list values(NULL, 'david', 'albert', 'accepted', true);
insert into friend_list values(NULL, 'albert', 'david', 'accepted', false);