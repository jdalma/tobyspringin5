
INSERT INTO SQLMAP(KEY_, SQL_) VALUES('userUpdate', 'update users set name = ?, password = ?, level = ?, login = ?,recommend = ?, email = ? where id = ?');
INSERT INTO SQLMAP(KEY_, SQL_) VALUES('userGetAll', 'select * from users order by id');
INSERT INTO SQLMAP(KEY_, SQL_) VALUES('userAdd', 'insert into users(id , name , password , level , login , recommend, email) values(?, ?, ?, ?, ?, ?, ?)');
INSERT INTO SQLMAP(KEY_, SQL_) VALUES('userDeleteAll', 'delete from users');
INSERT INTO SQLMAP(KEY_, SQL_) VALUES('userGet', 'select * from users where id = ?');
INSERT INTO SQLMAP(KEY_, SQL_) VALUES('userGetCount', 'select count(*) from users');
