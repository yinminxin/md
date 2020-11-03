# centos7安装redis

1.下载安装包

```shell
[root@localhost redis]# wget http://download.redis.io/releases/redis-5.0.7.tar.gz
```

2.解压压缩包

```shell
[root@localhost redis]# tar -zxvf redis-5.0.7.tar.gz
```

3.yum安装gcc依赖

```shell
[root@localhost redis]# yum -y install gcc*
```

4.cd切换到redis解压目录下，执行编译

```shell
[root@localhost redis]# cd redis-5.0.7/
[root@localhost redis-5.0.7]# make MALLOC=libc
```

5.进入/usr/local/src/redis/redis-5.0.7/src目录，然后安装并指定安装目录

```shell
[root@localhost redis-5.0.7]# cd src/
[root@localhost redis-5.0.7]# make install
```

6.修改配置文件

```
 使用目录/usr/local/redis-4.0.10下redis.conf作为配置文件，修改内容如下
* 注释掉 bind 127.0.0.1 或者修改为 bind [服务器IP] 127.0.0.1
* 把 daemonize no 改为 daemonize yes
```

------

# 三种启动方式

1.直接启动

```shell
[root@localhost src]# ./redis-server
```

2.以后台方式启动

- 修改redis.conf文件

  ```
  将daemonize no修改为daemonize yes
  ```

- 指定配置文件启动

  ```shell
  [root@localhost src]# ./redis-server /usr/local/src/redis/redis-5.0.7/redis.conf
  ```

- 关闭redis进程

  ```shell
  [root@localhost src]# ps -aux | grep redis
  [root@localhost src]# kill -9 16784
  ```

  ​

# 设置redis开机自启动

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

   ​

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

   ​

