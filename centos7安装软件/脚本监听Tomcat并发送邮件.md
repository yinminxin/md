# 定时运行脚本监听Tomcat并发送邮件

> 需要的软件：mariadb，mailx

1. 安装mariadb和mailx

   ```shell
   yum install -y mariadb #mariadb和mysql一样的数据库
   yum install -y mailx   #发送邮件
   ```

2. 配置mailx

   ```shell
   #打开配置文件
   vi /etc/mail.rc
   #在文件最后面加上下面4行
   set from=****@wondersgroup.com   #发送人
   set smtp=****.mail.com        #邮箱服务器
   set smtp-auth-user=YOUR_MAIL_USER          #用户名
   set smtp-auth-password=YOUR_MAIL_PWD.   #密码
   ```

3. 在任意目录下(如：`/home`)创建脚本文件,添加执行权限

   ```shell
   touch tomcatstatus.sh
   chmod -R 777 tomcatstatus.sh
   ```

4. 将下面脚本代码赋值到脚本文件，修改参数

   ```shell
   #!/bin/bash
   #获取当前IP地址
   #local_ip=`ifconfig | grep inet | grep -v inet6 | grep -v 127 | cut -d ' ' -f2`
   #local_ip=`ifconfig -a|grep -o -e 'inet [0-9]\{1,3\}.[0-9]\{1,3\}.[0-9]\{1,3\}.[0-9]\{1,3\}'|grep -v "127.0.0"|grep -v "192.168.*"|awk '{print $2}'`
   local_ip='127.0.0.1'
   #netip='https://tywj2.eduincloud.net'
   #echo $local_ip
   #当前时间
   alivetime=$(date +%Y%m%d%H%M%S);
   #数据库IP
   mysqlhost='YOUR_MYSQL_IP'
   #数据库用户名
   mysqluser='YOUR_MYSQL_USER'
   #数据库密码
   mysqlpwd='YOUR_MYSQL_PWD'
   #请求Tomcat地址
   echo $alivetime
   #-I 仅测试HTTP头
   #-m 10 最多查询10s
   #-o /dev/null 屏蔽原有输出信息
   #-s silent 模式，不输出任何东西
   #-w %{http_code} 控制额外输出
   alive8080=`curl -I -m 10 -o /dev/null -s -w %{http_code}  $local_ip:8080/p/TESB`
   alive8081=`curl -I -m 10 -o /dev/null -s -w %{http_code}  $local_ip:8081/p/TESB`
   MYSQL="mysql -h$mysqlhost -u$mysqluser -p$mysqlpwd --default-character-set=utf8 -A -N"
   if [[ ${alive8080} == 200 ]] ; then
       echo "$URL is up."
       sql="insert into hoststatus.wj(hostname, port,status,alivetime) values('${local_ip}', '8080','${alive8080}', '${alivetime}');"
       $MYSQL -e "$sql"
       #发送邮件
   	echo Tomcat Up=${local_ip}:8080 | mailx -v -s "IPAddress" *****@wondersgroup.com
   else
       echo "$URL is down."
       sql="insert into hoststatus.wj(hostname, port,status,alivetime) values('${local_ip}', '8080','${alive8080}', '${alivetime}');"
       $MYSQL -e "$sql"
   	echo Tomcat Down=${local_ip}:8080 | mailx -v -s "IPAddress" *****@wondersgroup.com
   	#重启服务tomcat
   	/usr/local/tomcat/bin/shutdown.sh #服务停止命令
   	/usr/local/tomcat/bin/startup.sh #服务启动命令
   fi
   
   if [[ ${alive8081} == 200 ]] ; then
       echo "$URL is up."
       sql="insert into hoststatus.wj(hostname, port,status,alivetime) values('${local_ip}', '8081','${alive8081}', '${alivetime}');"
       $MYSQL -e "$sql"
   else
       echo "$URL is down."
       sql="insert into hoststatus.wj(hostname, port,status,alivetime) values('${local_ip}', '8081','${alive8081}', '${alivetime}');"
       $MYSQL -e "$sql"
   	echo Tomcat Down=${local_ip}:8081 | mailx -v -s "IPAddress" *****@wondersgroup.com
   	#重启服务tomcat
   	/usr/local/tomcat/bin/shutdown.sh #服务停止命令
   	/usr/local/tomcat/bin/startup.sh #服务启动命令
   fi
   ```

5. 添加脚本到定时任务

   ```shell
   #一般执行crontab -e命令都是直接往/var/spool/cron下创建一个文件，这个文件的名称就是你的当前用户名，内容就是你添加的任务具体内容。
   #打开定时任务文件
   crontab -e
   #查看定时任务文件
   crontab -l
   #添加下面这行代码
   */30 * * * * /home/tomcatstatus.sh   #30分钟执行一次
   ```
   
6. 脚本添加任务

   ```shell
   #一般执行crontab -e命令都是直接往/var/spool/cron下创建一个文件，这个文件的名称就是你的当前用户名，内容就是你添加的任务具体内容。
   #创建脚本
   #!/bin/bash
   echo "*/30 * * * * /home/tomcatstatus.sh" >> /var/spool/cron/username
   ```

> 获取最新数据sql
>
> SELECT b.* from
> (SELECT hostname,port,MAX(alivetime) alivetime FROM wj GROUP BY hostname,port) a
> LEFT JOIN wj b ON a.hostname=b.hostname AND a.port=b.port AND a.alivetime = b.alivetime
> ORDER BY colonyname ,servicename

注：

```shell
#mailx 发送带附件的邮件
echo "这里输入你邮件内容" | mailx -s "邮件标题" -a file.txt   xxx@163.com
```

