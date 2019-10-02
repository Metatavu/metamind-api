create user if not exists sa identified by 'sa';
grant all on mmtest.* to sa; 
drop database if exists mmtest; 
create database mmtest default charset utf8;