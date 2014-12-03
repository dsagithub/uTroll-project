drop user 'uTroll'@'localhost';
create user 'uTroll'@'localhost' identified by 'uTroll';
grant all privileges on uTrolldb.* to 'uTroll'@'localhost';
flush privileges;