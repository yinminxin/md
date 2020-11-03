# Mysql

## 基础

### 数据库备份

```shell
#备份数据库到D盘跟目录
mysqldump -h127.0.0.1 -uroot -ppass myweb > d:/backupfile.sql

#备份到当前目录 备份MySQL数据库为带删除表的格式，能够让该备份覆盖已有数据库而不需要手动删除原有数据库
mysqldump --add-drop-table -h127.0.0.1 -uroot -ppass myweb > backupfile.sql

#直接将MySQL数据库压缩备份  备份到D盘跟目录
mysqldump -h127.0.0.1 -uroot -ppass myweb | gzip > d:/backupfile.sql.gz

#备份MySQL数据库某个(些)表。此例备份table1表和table2表。备份到linux主机的/home下
mysqldump -h127.0.0.1 -uroot -ppass myweb table1 table2 > /home/backupfile.sql

#同时备份多个MySQL数据库
mysqldump -h127.0.0.1 -uroot -ppass --databases myweb myweb2 > multibackupfile.sql

#仅仅备份数据库结构。同时备份名为myweb数据库和名为myweb2数据库
mysqldump --no-data -h127.0.0.1 -uroot -ppass --databases myweb myweb2 > structurebackupfile.sql

#备份服务器上所有数据库
mysqldump --all-databases -h127.0.0.1 -uroot -ppass > allbackupfile.sql

#还原MySQL数据库的命令。还原当前备份名为backupfile.sql的数据库
mysql -h127.0.0.1 -uroot -ppass myweb < backupfile.sql

#还原压缩的MySQL数据库
gunzip < backupfile.sql.gz | mysql -h127.0.0.1 -uroot -ppass myweb

#将数据库转移到新服务器。此例为将本地数据库myweb复制到远程数据库名为serweb中，其中远程数据库必须有名为serweb的数据库
mysqldump -h127.0.0.1 -uroot -ppass myweb | mysql --host=***.***.***.*** -u数据库用户名 -p数据库密码 -C serweb
```

## 错误

### 导入sql备份文件报错

```mysql
#报的错误是时间的默认值有错误，查阅后发现 MySQL 5.7 版本开始有了一个 STRICT MODE（严格模式），此模式中对默认值做了一些限制。

#解决方法就是修改 sql_mode

-- 查看当前
select @@sql_mode;

-- 去掉 NO_ZERO_IN_DATE 和 NO_ZERO_DATE
set @@sql_mode=(select replace(@@sql_mode,'NO_ZERO_IN_DATE,NO_ZERO_DATE','')); 

-- 查看全局
select @@global.sql_mode;

set @@global.sql_mode=(select replace(@@global.sql_mode,'NO_ZERO_IN_DATE,NO_ZERO_DATE',''));
```

