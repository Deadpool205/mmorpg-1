CREATE DATABASE IF NOT EXISTS mmorpg;
create table player ( id integer auto_increment primary key, userid varchar(32), name varchar(32), password varchar(32), mrole varchar(32), gender varchar(1), balance double, eckey varchar(64) )
create table inventory ( id integer, clazz varchar(64) )
