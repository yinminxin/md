# Mysql主主配置

> Master1 : 172.27.1.70
>
> Master2 : 172.27.1.71

## 1.Master1设置

### 1.1在`my.cnf`文件的`[mysqld]`配置区域添加下面内容：

```shell
[root@mysql1 ~]# vim /etc/my.cnf

server-id = 1   
log-bin = mysql-bin
sync_binlog = 1
binlog_checksum = none
binlog_format = mixed
auto-increment-increment = 2
auto-increment-offset = 2
slave-skip-errors = all
```

### 1.2重启MySQL

```shell
service mysql restart
```

### 1.4创建一个复制用户

```shell
mysql> grant replication slave,replication client on *.* to repl@'172.27.1.71' identified by 'Wonders@300168';
Query OK, 0 rows affected, 1 warning (0.00 sec)
mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)
```

### 1.5锁表，待同步配置完成再解锁

```
mysql> flush tables with read lock;
Query OK, 0 rows affected (0.00 sec)
```

### 1.6查看当前的binlog以及数据所在位置（查看 master情况）

```
mysql> show master status;
```

## 2.Master2设置

### 2.1在`my.cnf`文件的`[mysqld]`配置区域添加下面内容：

```shell
[root@mysql1 ~]# vim /etc/my.cnf

server-id = 2  
log-bin = mysql-bin
sync_binlog = 1
binlog_checksum = none
binlog_format = mixed
auto-increment-increment = 2
auto-increment-offset = 2
slave-skip-errors = all
```

### 2.2重启MySQL

```shell
service mysql restart
```

### 2.4创建一个复制用户

```mysql
mysql> grant replication slave,replication client on *.* to repl@'172.27.1.70' identified by 'Wonders@300168';
Query OK, 0 rows affected, 1 warning (0.00 sec)
mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)
```

### 2.5锁表，待同步配置完成再解锁

```mysql
mysql> flush tables with read lock;
Query OK, 0 rows affected (0.00 sec)
```

### 2.6查看当前的binlog以及数据所在位置（查看 master情况）

```mysql
mysql> show master status;
```

## 3.分别开启同步对方

### 3.1Master1做同步操作

```mysql
mysql> unlock tables; //先解锁，将对方数据同步到自己的数据库中
mysql> stop slave;
mysql> change master to master_host='172.27.1.71',master_user='repl',master_password='Wonders@300168',master_log_file='mysql-bin.000001',master_log_pos=601;
Query OK, 0 rows affected, 2 warnings (0.01 sec)

mysql> start slave;
Query OK, 0 rows affected (0.01 sec)

//查看两个线程状态是否为YES
mysql> show slave status \G;
#Slave_IO_Running : YES
#Slave_SQL_Running : YES
```

### 3.2Master2做同步操作

```mysql
mysql> unlock tables; //先解锁，将对方数据同步到自己的数据库中
mysql> stop slave;
mysql> change master to master_host='172.27.1.70',master_user='repl',master_password='Wonders@300168',master_log_file='mysql-bin.000001',master_log_pos=601;
Query OK, 0 rows affected, 2 warnings (0.01 sec)

mysql> start slave;
Query OK, 0 rows affected (0.01 sec)

//查看两个线程状态是否为YES
mysql> show slave status \G;
#Slave_IO_Running : YES
#Slave_SQL_Running : YES
```

> ### 以上表明双方已经实现了mysql主主同步。当运行一段时间后，要是发现同步有问题，比如只能单向同步，双向同步失效。可以重新执行下上面的change master同步操作，只不过这样同步后，只能同步在此之后的更新数据。







