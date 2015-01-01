drop database if exists uTrolldb;
create database uTrolldb;

use uTrolldb;

create table groups (
	groupid 				int not null auto_increment primary key,
	groupname				varchar(100) not null,
	price					int not null,
	creation_timestamp		datetime not null default current_timestamp,
	ending_timestamp		timestamp not null,
	closing_timestamp		timestamp not null,
	creator					varchar(20) not null,
	troll					varchar(20) not null,
	state					varchar(50) not null
);

create table users (
	username	varchar(20) not null primary key,
	password	char(32) not null,
	name		varchar(70) not null,
	email		varchar(255) not null,
	age			int not null,
	points		int not null,
	points_max	int not null,
	isTroll		boolean not null,
	groupid		int not null,
	foreign key(groupid)	references groups(groupid)
);

create table user_roles (
	username			varchar(20) not null,
	rolename 			varchar(20) not null,
	foreign key(username) references users(username) on delete cascade,
	primary key (username, rolename)
);

create table friend_list (
	friendshipid			int not null auto_increment primary key,
	friend1 				varchar(20) not null,
	friend2					varchar(20) not null,
	state					varchar(50) not null,
	foreign key(friend1)	references users(username),
	foreign key(friend2)	references users(username) on delete cascade
);

create table comment (
	commentid			int not null auto_increment primary key,
	username			varchar(20) not null,
	creator				varchar(20) not null,
	content				varchar(500) not null,
	likes				int not null,
	dislikes			int not null,
	groupid				int not null,
	last_modified			timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	creation_timestamp		datetime not null default current_timestamp,
	foreign key(username) 	references users(username),
	foreign key(groupid)	references groups(groupid)
);

create table likes (
	likeid				int not null auto_increment primary key,
	commentid			int not null,
	username			varchar(20) not null,
	likeComment			boolean not null,
	dislikeComment		boolean not null,
	foreign key(username) 	references users(username),
	foreign key(commentid)	references comment(commentid)
);