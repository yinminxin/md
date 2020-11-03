# 1. Monit安装配置

1. yum安装

   ```shell
   yum install -y monit
   ```

2. 在`/etc/monit.d`文件夹下添加监控配置**文件**，如`nginx`:

   ```shell
   #设置nginx的pid文件
   check process nginx with pidfile /usr/local/nginx/logs/nginx.pid
   #启动脚本位置
   #start program = "/usr/local/nginx/sh/start.sh"
   #停止脚本位置
   #stop program = "/usr/local/nginx/sh/stop.sh"
   #启动命令位置
   start program = "/usr/local/nginx/sbin/nginx"
   #停止命令位置
   stop program = "/usr/local/nginx/sbin/nginx -s stop"
   #监控IP端口重启
   if failed host 127.0.0.1 port 9001 then restart # nginx
   #if failed port 8081 for 5 cycles then restart  # tomcat
   ```
   - start.sh脚本文件内容

   ```shell
   #!/bin/bash
   cd /usr/local/nginx
           /usr/local/nginx/sbin/nginx
   exit $?
   ```

   - stop.sh脚本文件内容

   ```shell
   #!/bin/bash
   cd /usr/local/nginx
   killall nginx
   exit $?
   ```

3. monit相关命令

   ```shell
   #启动
   monit
   #停止
   monit qiut
   #重启
   monit reload
   #查看服务启动状态
   monit status
   #停止所有服务
   monit stop all
   #启动所有服务
   monit start all
   #停止单个服务
   monit stop nginx #服务名
   #开启单个服务
   monit start nginx #服务名
   ```

------

> #### 注：
>
> 1. Tomcat的PID文件设置，在Tomcat的`/bin/catalina.sh` 文件中`PRGDIR`这一行下面添加一行：
>
>    ```shell
>    CATALINA_PID=/usr/local/tomcat8.5.50/logs/tomcat.pid
>    ```
>
> 2. redis.conf配置文件里面搜索pidfile，默认是设置的是/var/run/redis_6379.pid，可以修改为其它文件名
>
>    ​