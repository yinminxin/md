#!/bin/bash
#mysql 连接信息
dbName=etrain
useName=root
pwd=tortVF2i-#XnN
#系统当前时间
fullDate=`date +"%F_%H.%M.%S"`
#mysqldump备份文件执行路径
dump=/usr/local/mysql/mysql-5.7.16/bin/mysqldump
#备份文件存储路径
backdir=/usr/local/mysqlBackup
#备份输出sql文件名
outSql=${dbName}_${fullDate}.sql
#备份输出压缩tar.gz文件名
outTar=${dbName}_${fullDate}.tar.gz
#备份输出sql文件全路径
#backupFilePath= ${backdir}/${outSql}
#执行sql备份 --> /usr/bin/mysqldump
${dump} -u ${useName} -p${pwd} ${dbName} --hex-blob --add-drop-table > ${backdir}/${outSql};
#进入备份目录
cd ${backdir}
#进行文件压缩
tar zcvf ./${outTar} ./${outSql} --remove-files;
#删除sql文件

# 等到时间到了再过来试试看  删除
find /usr/local/mysqlBackup -name "etrain_*" -type f -mtime +7 -exec rm -rf {} \;
