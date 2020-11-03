# Mysql自动备份

## 1. Mysql备份脚本

```shell
#!/bin/bash
backup_path="/usr/local/mysqlBackup"
user="root"
passwd="tortVF2i-#XnN"
dbname="etrain"
host="localhost"
today=`date +"%Y%m%d-%H%M%S"`
sqlname=$dbname$today.sql
if [ -e /usr/local/log/log_function.sh ]
then
source /usr/local/log/log_function.sh
else
echo -e "\033[41;37m /usr/local/log/log_function.sh is not exist. \033[0m"
exit 1
fi
backup(){
log_correct "开始执行备份脚本,删除15天过期备份"
#back
mysqldump -h$host -u$user -p$passwd $dbname >$backup_path/$sqlname
}
delete(){
#delete expired 15 days
find /usr/local/mysqlBackup -mtime +5 -type f -name '*.sql' -exec rm -f {} \;
#-mtime +15   15天以前
}
size(){
cd /usr/local/mysqlBackup
dd=`du -sh $sqlname`
if [ -s ./$sqlname ] ; then 
 log_correct 'fsl_prod备份正常'
 log_correct $dd
else
 log_error 'fsl_prod备份失败'
fi
}
backup
delete
size
```

## 2. 备份记录日志脚本

```shell
#!/bin/bash
#打印备份OK的记录日志输出到日志文件
function log_correct () {
DATE=`date +"%Y%m%d-%H%M%S"` #显示打印日志的时间
USER=$(whoami) #哪个用户在操作
echo "${DATE} ${USER} execute $0 [INFO] $@" >>/usr/local/log/log_info.log #（$0脚本本身，$@将参数作为整体传输调用）
}
#log_error打印shell脚本中错误的输出到日志文件
function log_error ()
{
DATE=`date +"%Y%m%d-%H%M%S"`
USER=$(whoami)
echo "${DATE} ${USER} execute $0 [INFO] $@" >>/usr/local/log/log_error.log #（$0脚本本身，$@将参数作为整体传输调用）
}
#fn_log函数 通过if判断执行命令的操作是否正确，并打印出相应的操作输出
function fn_log ()
{
if [ $? -eq 0 ]
then
log_correct "$@ sucessed!"
echo -e "\033[32m $@ sucessed. \033[0m"
else
log_error "$@ failed!"
echo -e "\033[41;37m $@ failed. \033[0m"
exit
fi
}
```

## 3. crontab计划

```shell
[root@localhost ~]# crontab -e
* 3 * * * bash /root/mysql.back.sh   #每天凌晨三点执行一次
```

