# redis安装和配置

## 1.安装
- 下载安装包
  [官网下载](https://redis.io/download)

  建议上传到我们的home下：/home/redis/

  或者命令下载

  ```shell
  [root@localhost redis]# wget http://download.redis.io/releases/redis-5.0.7.tar.gz
  ```

- 解压
```shell
 [root@localhost redis]# tar -zxvf redis-5.0.7.tar.gz
```

- 编译安装
```shell
 mv redis-5.0.7 redis
 cd redis
 make && make install
```

## 2.配置
修改安装目录下的redis.conf文件
```shell
vim redis.conf
```

修改以下配置：
```shell
#bind 127.0.0.1 # 将这行代码注释，监听所有的ip地址，外网可以访问
protected-mode no # 把yes改成no，允许外网访问
daemonize yes # 把no改成yes，后台运行
```

## 3.启动或停止
```shell
#启动
redis-server /usr/local/redis/redis-5.0.5/redis.conf
#停止
[root@ecs-b049-0729334 ~]# redis-cli
127.0.0.1:6379> shutdown
#或者
kill -9 进程ID
```

> redis提供了服务端命令和客户端命令：
>
> - redis-server 服务端命令，可以包含以下参数：
>   start 启动
>   stop 停止
> - redis-cli 客户端控制台，包含参数：
>   -h xxx 指定服务端地址，缺省值是127.0.0.1
>   -p xxx 指定服务端端口，缺省值是6379
>

## 4.相关文件位置

```
可执行文件存放在/usr/local/bin目录
库文件会存放在/usr/local/lib目录
配置文件会存放在/usr/local/etc目录
其他的资源文件会存放在usr/local/share目录
配置文件存放在/usr/local/redis/redis-5.0.5/redis.conf
```

## 5.设置开机启动

### 5.1 第一种方式

1) 输入命令，新建文件

```sh
vim /etc/init.d/redis
```

输入下面内容：

```sh
#!/bin/sh
# chkconfig:   2345 90 10
# description:  Redis is a persistent key-value database
PATH=/usr/local/bin:/sbin:/usr/bin:/bin

REDISPORT=6379
EXEC=/usr/local/bin/redis-server
REDIS_CLI=/usr/local/bin/redis-cli

PIDFILE=/var/run/redis.pid

CONF="/home/leyou/redis/redis.conf"

case "$1" in  
    start)  
        if [ -f $PIDFILE ]  
        then  
                echo "$PIDFILE exists, process is already running or crashed"  
        else  
                echo "Starting Redis server..."  
                $EXEC $CONF  
        fi  
        if [ "$?"="0" ]   
        then  
              echo "Redis is running..."  
        fi  
        ;;  
    stop)  
        if [ ! -f $PIDFILE ]  
        then  
                echo "$PIDFILE does not exist, process is not running"  
        else  
                PID=$(cat $PIDFILE)  
                echo "Stopping ..."  
                $REDIS_CLI -p $REDISPORT SHUTDOWN  
                while [ -x ${PIDFILE} ]  
               do  
                    echo "Waiting for Redis to shutdown ..."  
                    sleep 1  
                done  
                echo "Redis stopped"  
        fi  
        ;;  
   restart|force-reload)  
        ${0} stop  
        ${0} start  
        ;;  
  *)  
    echo "Usage: /etc/init.d/redis {start|stop|restart|force-reload}" >&2  
        exit 1  
esac

```

然后保存退出

注意：以下信息需要根据安装目录进行调整：

> EXEC=/usr/local/bin/redis-server # 执行脚本的地址
>
> REDIS_CLI=/usr/local/bin/redis-cli # 客户端执行脚本的地址
>
> PIDFILE=/var/run/redis.pid # 进程id文件地址
>
> CONF="/usr/local/src/redis-3.0.2/redis.conf" #配置文件地址

2）设置权限

```sh
chmod 755 /etc/init.d/redis
```



3）启动测试

```sh
/etc/init.d/redis start
```

启动成功会提示如下信息：

```
Starting Redis server...
Redis is running...
```



4）设置开机自启动

```sh
chkconfig --add /etc/init.d/redis
chkconfig redis on
```

### 5.2 第二种方式

1. 在/etc目录下新建redis目录

   ```shell
   [root@localhost ~]# mkdir /etc/redis
   ```

2. 将/usr/local/redis-4.0.6/redis.conf 文件复制一份到/etc/redis目录下，并命名为6379.conf

   ```shell
   [root@localhost ~]# cp /usr/local/src/redis/redis-5.0.7/redis.conf /etc/redis/6379.conf
   ```

3. 将redis的启动脚本复制一份放到/etc/init.d目录下

   ```shell
   [root@localhost ~]# cp /usr/local/src/redis/redis-5.0.7/utils/redis_init_script /etc/init.d/redisd
   ```

   

4. 设置redis开机自启动
   先切换到/etc/init.d目录下然后执行自启命令

   ```shell
   [root@localhost ~]# cd /etc/init.d/
   [root@localhost init.d]# chkconfig redisd on
   ```

   看结果是redisd不支持chkconfig
   解决方法：使用vim编辑redisd文件，在第一行加入如下两行注释，保存退出

   ```shell
   #!/bin/sh
   # chkconfig: 2345 90 10
   # descriprion: Redis is a persistent key-value database
   ```

   注释的意思：redis服务必须在运行级2，3，4，5下被启动或关闭，启动优先级是90，关闭优先级是10。
   再次执行开机自启命令，成功

   ```shell
   [root@localhost init.d]# chkconfig redisd on
   ```

   现在可以直接已服务的形式启动和关闭redis了启动：

   ```shell
   [root@localhost init.d]# service redisd start
   ```

   > 备注
   > 出现错误：/var/run/redis_6379.pid exists, process is already running or crashed
   > 解决方案：
   > 执行命令：rm -rf /var/run/redis_6379.pid

5. 关闭redis
   方法1：service redisd stop

   ```shell
   [root@localhost /]# service redisd stop
   ```

   方法2：redis-cli SHUTDOWN

   ```shell
   [root@localhost /]# redis-cli SHUTDOWN
   ```

   