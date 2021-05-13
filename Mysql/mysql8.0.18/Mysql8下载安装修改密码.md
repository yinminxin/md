# Mysql8下载安装

### 1. 下载Mysql压缩包

- #### 进入mysql网站：[点击进入](https://dev.mysql.com/downloads/mysql/)，下载压缩包，下载完成后直接解压

### 2. 配置环境变量

### 3. 配置文件

- #### 在 `D:\yinminxin\mysoft\mysql8\mysql-8.0.18-winx64` 目录下新建一个data文件夹和my.ini的文件


- #### my.ini文件内容 :

  ```
  [mysqld]

  # 设置3306端口

  port=3306

  # 设置mysql的安装目录

  basedir=D:\\yinminxin\\mysoft\\mysql8\\mysql-8.0.18-winx64

  # 切记此处一定要用双斜杠\\，单斜杠这里会出错。

  # 设置mysql数据库的数据的存放目录

  datadir=D:\\yinminxin\\mysoft\\mysql8\\mysql-8.0.18-winx64\\data
  # 此处同上

  # 允许最大连接数

  max_connections=200

  # 允许连接失败的次数。这是为了防止有人从该主机试图攻击数据库系统

  max_connect_errors=10

  # 服务端使用的字符集默认为UTF8

  character-set-server=utf8

  # 创建新表时将使用的默认存储引擎

  default-storage-engine=INNODB

  # 默认使用“mysql_native_password”插件认证

  default_authentication_plugin=mysql_native_password

  [mysql]

  # 设置mysql客户端默认字符集

  default-character-set=utf8

  [client]

  # 设置mysql客户端连接服务端时默认使用的端口

  port=3306

  default-character-set=utf8
  ```

### 4. 初始化

- #### 首先以管理员身份运行cmd，进入mysql的bin目录下

- #### 然后输入`mysqld --initialize --console`，等待一会出现几行代码

- #### root@localhost：后面的是随机生成的数据库初始密码，将初始密码记下来后面会用到。


- > #### ***注：没记住初始密码的话，删掉初始化的 data目录，再执行一遍初始化命令又会重新生成。***

### 5. 安装

- #### 初始化完毕后，使用管理员权限打开命令行，执行`mysqld --install`

### 6. 启动服务

- #### 执行命令`net start mysql`

### 7. 登录、修改密码

- #### 服务启动成功后，输入`mysql -u root -p` 回车，在password后面输入刚才记下来的初始密码

- #### 执行修改密码：`alter user root@localhost identified by '123456';`