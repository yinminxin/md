# 安装配置FTP服务器

基本命令

```shell
#启动
systemctl start vsftpd.service
#停止
systemctl stop vsftpd.service
#重启
systemctl restart vsftpd.service
```



1. 查看是否安装vsftpd

   ```shell
   rpm -qa| grep vsftpd
   ```

2. yum安装

   ```shell
   yum install -y vsftpd
   ```

3. 添加用户

   ```shell
   useradd -d /usr/local/nginx/html/pms -g ftp -s /sbin/nologin yinminxin
   #/usr/local/nginx/html  用户根目录
   #/sbin/nologin   不能登录，只能连接ftp
   #yinminxin 用户名
   ```

4. 设置密码

   ```shell
   passwd yinminxin
   #回车，输入密码
   ```

5. 禁用匿名用户登录

   ```shell
   #打开配置文件
   vim /etc/vsftpd/vsftpd.conf
   #将
   anonymous_enable=YES
   #改为
   anonymous_enable=NO
   ```

6. 将要操作的目录给属主给用户

   ```shell
   chown -R yinminxin /usr/local/nginx/html/pms
   ```

注：如要限制FTP登录人数，我们进入 vsftpd 目录下的 user_list 中修改，如下：

```shell
# vsftpd userlist
# If userlist_deny=NO, only allow users in this file
# If userlist_deny=YES (default), never allow users in this file, and
# do not even prompt for a password.
# Note that the default vsftpd pam config also checks /etc/vsftpd/ftpusers
# for users that are denied.
root　　
yinminxin
```

user_list 里面的内容是能够登录FTP的用户列表，若是不想让其他用户登录，则删除该文件内的其他用户名，仅保留我们刚才创建的一个用户（root为系统管理员，若是不需要也可以删除）。