# Linux下Mysql的下载安装

## 1.安装前准备

- 检查是否装过Mysql，执行命令：

  ```shell
  [root@localhost /]# rpm -qa | grep mysql
  ```


- 从执行结果，可以看出我们已经安装了**mysql-libs-5.1.73-5.el6_6.x86_64**，执行删除命令：

  ```shell
  [root@localhost /]# rpm -e --nodeps mysql-libs-5.1.73-5.el6_6.x86_64
  ```


- 再次执行查询命令，查看是否删除

- 查询所有的Mysql文件夹:`whereis mysql`,`find / -name mysql`

  ```shell
  [root@localhost /]# whereis mysql
  mysql: /usr/bin/mysql /usr/include/mysql
  [root@localhost lib]# find / -name mysql
  /data/mysql
  /data/mysql/mysql
  ```

- 删除相关目录或文件

  ```shell
  [root@localhost /]#  rm -rf /usr/bin/mysql /usr/include/mysql /data/mysql /data/mysql/mysql
  ```

- 验证是否删除完毕

  ```shell
  [root@localhost /]# whereis mysql
  mysql:
  [root@localhost /]# find / -name mysql
  [root@localhost /]# 
  ```

- 检查mysql用户组和用户是否存在，如果没有，则创建

  ```shell
  [root@localhost /]# cat /etc/group | grep mysql
  [root@localhost /]# cat /etc/passwd |grep mysql
  [root@localhost /]# groupadd mysql
  [root@localhost /]# useradd -r -g mysql mysql
  [root@localhost /]# 
  ```

## 2.下载安装

- 下载命令如下，或者前往 [mysql官网](https://dev.mysql.com/downloads/mysql/) 下载：

  ```shell
  [root@localhost /]#  wget https://dev.mysql.com/get/Downloads/MySQL-5.7/mysql-5.7.24-linux-glibc2.12-x86_64.tar.gz
  ```

- 在执行**`wget`**命令的目录下或你的上传目录下找到Mysql安装包，执行解压命令：

  ```shell
  [root@localhost /]#  tar xzvf mysql-5.7.24-linux-glibc2.12-x86_64.tar.gz
  [root@localhost /]# ls
  mysql-5.7.24-linux-glibc2.12-x86_64
  mysql-5.7.24-linux-glibc2.12-x86_64.tar.gz
  ```

- 解压完成后，可以看到当前目录下多了一个解压文件，移动该文件到`/usr/local/mysql`

  ```shell
  [root@localhost /]# mv mysql-5.7.24-linux-glibc2.12-x86_64 /usr/local/mysql
  ```

- 在`/usr/local/mysql`目录下创建data目录

  ```shell
  [root@localhost /]# mkdir /usr/local/mysql/data
  ```

- 更改mysql目录下所有的目录及文件夹所属的用户组和用户，以及权限

  ```shell
  [root@localhost /]# chown -R mysql:mysql /usr/local/mysql
  [root@localhost /]# chmod -R 755 /usr/local/mysql
  ```

- 编译安装并初始化mysql,**务必记住初始化输出日志末尾的密码（数据库管理员临时密码）**

  ```shell
  [root@localhost /]# cd /usr/local/mysql/bin
  [root@localhost bin]# ./mysqld --initialize --user=mysql --datadir=/usr/local/mysql/data --basedir=/usr/local/mysql
  ```

  > 补充说明，此时可能会出现错误：
  >
  > ![img](https://upload-images.jianshu.io/upload_images/4773465-f61d450c92002962.png?imageMogr2/auto-orient/strip|imageView2/2/w/962/format/webp)

  - 出现该问题首先检查该链接库文件有没有安装使用 命令进行核查

    ```shell
    [root@localhost bin]# rpm -qa|grep libaio   
    [root@localhost bin]# 
    ```

  - 运行命令后发现系统中无该链接库文件

    ```shell
    [root@localhost bin]#  yum install  libaio-devel.x86_64
    ```

  - 安装成功后，继续运行数据库的初始化命令，此时可能会出现如下错误：

    ![img](https://upload-images.jianshu.io/upload_images/4773465-1285a491ff73632f.png?imageMogr2/auto-orient/strip|imageView2/2/w/799/format/webp)

  - 执行如下命令后，再次运行数据库的初始化命令：

    ```shell
    [root@localhost bin]#  yum -y install numactl
    ```


- 运行初始化命令成功后，输出日志如下：

  ![img](https://upload-images.jianshu.io/upload_images/4773465-b193851d538e83c7.png?imageMogr2/auto-orient/strip|imageView2/2/w/576/format/webp)

  > 记录日志最末尾位置**root@localhost:**后的字符串，此字符串为mysql管理员临时登录密码

- 编辑配置文件my.cnf，添加配置如下

  ```shell
  [root@localhost bin]#  vi /etc/my.cnf

  [mysqld]
  datadir=/usr/local/mysql/data
  port = 3306
  sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES
  symbolic-links=0
  max_connections=400
  innodb_file_per_table=1
  #表名大小写不明感，敏感为
  lower_case_table_names=1
  ```

- 启动mysql服务器

  ```shell
  [root@localhost /]# /usr/local/mysql/support-files/mysql.server start
  ```

- 显示如下结果，说明数据库安装成功

  ![img](https://upload-images.jianshu.io/upload_images/4773465-f25f02b9deefad0d.png?imageMogr2/auto-orient/strip|imageView2/2/w/575/format/webp)

- 如果出现如下提示信息

  ```shell
  Starting MySQL... ERROR! The server quit without updating PID file
  ```

- 查看是否存在mysql和mysqld的服务，如果存在，则结束进程，再重新执行启动命令

  ```shell
  #查询服务
  ps -ef|grep mysql
  ps -ef|grep mysqld

  #结束进程
  kill -9 PID

  #启动服务
   /usr/local/mysql/support-files/mysql.server start
  ```

![img](https://upload-images.jianshu.io/upload_images/4773465-a241c31f84a0c85b.png?imageMogr2/auto-orient/strip|imageView2/2/w/525/format/webp)

- 添加软连接，并重启mysql服务

  ```shell
  [root@localhost /]#  ln -s /usr/local/mysql/support-files/mysql.server /etc/init.d/mysql 
  [root@localhost /]#  ln -s /usr/local/mysql/bin/mysql /usr/bin/mysql
  [root@localhost /]#  service mysql restart
  ```

- 登录mysql，修改密码(密码为步骤5生成的临时密码)

  ```shell
  [root@localhost /]#  mysql -u root -p
  Enter password:
  #老版本数据库，修改密码
  mysql>set password for root@localhost = password('yourpass');
  #新版本数据库，修改密码
  mysql>alter user 'root'@'localhost' identified by '000000';
  ```

- 开放远程连接

  ```shell
  mysql>use mysql;
  msyql>update user set user.Host='%' where user.User='root';
  mysql>flush privileges;
  ```

  ![img](https://upload-images.jianshu.io/upload_images/4773465-dca9c59c28b04fc7.png?imageMogr2/auto-orient/strip|imageView2/2/w/503/format/webp)

- 设置开机自动启动

  - 将服务文件拷贝到init.d下，并重命名为mysql

    ```shell
    cp /usr/local/mysql/support-files/mysql.server /etc/init.d/mysql
    ```

  - 赋予可执行权限

    ```shell
    chmod +x /etc/init.d/mysql
    ```

  - 添加服务

    ```shell
    chkconfig --add mysql
    ```

  - 显示服务列表

    ```shell
    chkconfig --list
    ```

    ![img](https://img2018.cnblogs.com/blog/872610/201809/872610-20180921171327848-1752990166.png)

    > 如果看到mysql的服务如上图所示2,3,4,5都是开的话则成功，默认级别是2345

    - 如果是关，则键入命令开启

      ```
      chkconfig --level 2345 mysql on
      ```

    - 重启服务器

      ```shell
      reboot
      ```

    - `chkconfig`命令说明

      ```
      --add：增加所指定的系统服务，让chkconfig指令得以管理它，并同时在系统启动的叙述文件内增加相关数据；
      --del：删除所指定的系统服务，不再由chkconfig指令管理，并同时在系统启动的叙述文件内删除相关数据；
      --level<等级代号>：指定读系统服务要在哪一个执行等级中开启或关毕。
      ```

    - 等级代号列表

      ```
      等级0表示：表示关机
      等级1表示：单用户模式
      等级2表示：无网络连接的多用户命令行模式
      等级3表示：有网络连接的多用户命令行模式
      等级4表示：不可用
      等级5表示：带图形界面的多用户模式
      等级6表示：重新启动
      ```

    - 说明

      > 需要说明的是，level选项可以指定要查看的运行级而不一定是当前运行级。对于每个运行级，只能有一个启动脚本或者停止脚本。当切换运行级时，init不会重新启动已经启动的服务，也不会再次去停止已经停止的服务。