# redis安装和配置

## 1.安装
- 下载安装包
[官网下载](https://redis.io/download)
上传到我们的/usr/local下：/usr/local/redis/

- 解压
```shell
 tar -xvf redis-5.0.5.tar.gz
```

- 编译安装
```shell
 cd redis-5.0.5
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
或者
kill -9 进程ID
```

## 4.相关文件位置

```
可执行文件存放在/usr/local/bin目录
库文件会存放在/usr/local/lib目录
配置文件会存放在/usr/local/etc目录
其他的资源文件会存放在usr/local/share目录
配置文件存放在/usr/local/redis/redis-5.0.5/redis.conf
```

