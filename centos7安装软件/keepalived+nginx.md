# 服务器搭建高可用Nginx

> 服务器两台
>
> 172.27.1.66
>
> 172.27.1.72

## 1. 安装Nginx

### 1.1 一键安装四个依赖

```shell
yum -y install gcc zlib zlib-devel pcre-devel openssl openssl-devel
```

### 1.2 下载并解压安装包(或者直接上传压缩包)

```shell
# 进入文件夹
cd /usr/local
# 下载nginx包
wget http://nginx.org/download/nginx-1.13.7.tar.gz
# 解压
tar -xvf nginx-1.13.7.tar.gz
```

### 1.3 安装Nginx

```shell
# 进入nginx目录,执行命令
./configure
# 执行make命令
make
# 执行make install命令
make install
```

### 1.4 注：Linux下nginx编译安装ssl模块

```shell
# 进入nginx目录,重新编译
./configure --prefix=/usr/local/nginx --with-http_stub_status_module --with-http_ssl_module
# 执行make && make install
make && make install
```

### 1.5 执行访问

```shell
#进入nginx目录
cd /usr/local/nginx
#启动
./sbin/nginx
#在72服务器访问测试
curl 172.27.1.66
#在66服务器访问测试
curl 172.27.1.72
```

## 2. 安装KeepAlived

### 1.1 安装相关依赖

```shell
yum -y install gcc openssl-devel libnl libnl-devel libnfnetlink-devel net-tools
```

### 1.2 下载并解压安装包(或者直接上传压缩包)

```shell
# 进入安装目录,执行命令
./configure --prefix=/usr/local/keepalived --sysconf=/etc
# 执行make && make install
make && make install
```

### 1.3 复制相关文件

```shell
cp /usr/local/keepalived/keepalived-2.0.10/keepalived/etc/init.d/keepalived /etc/init.d
```

### 1.4 设置开机启动

```shell
chkconfig keepalived on
或
systemctl enable keepalived #centos7下命令
```

### 1.5 修改配置文件

#### 1.5.1 主keepalived

```shell
! Configuration File for keepalived

vrrp_instance VI_1 {
  state MASTER
  interface eth0
  virtual_router_id 51 
  priority 100
  advert_int 1
  authentication {
      auth_type PASS
      auth_pass 1111
  }
  virtual_ipaddress {
      172.27.1.200
  }
}

```

#### 1.5.2 从keepalived

```shell
! Configuration File for keepalived

vrrp_instance VI_1 {
  state BACKUP
  interface eth0
  virtual_router_id 51 
  priority 99
  advert_int 1
  authentication {
      auth_type PASS
      auth_pass 1111
  }
  virtual_ipaddress {
      172.27.1.200
  }
}
```

## 3. 查看VIP是否绑定成功

### 3.1 基本命令

```shell
#启动keepalived
service keepalived start
#停止keepalived
service keepalived stop
#重启keepalived
service keepalived restart
#查看keepalived状态
service keepalived status
#查看VIP绑定
ip a
```

