create database if not exists greenyp;
create database if not exists mygreenyp;
create database if not exists fusionauth;

grant all privileges on mysql.* to 'gypadmin'@'%';
grant all privileges on greenyp.* to 'gypadmin'@'%';
grant all privileges on mygreenyp.* to 'gypadmin'@'%';

grant all privileges on fusionauth.* to 'greenfusion'@'%';