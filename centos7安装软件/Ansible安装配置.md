# Ansible安装配置

## yum安装

```shell
yum -y install ansible
#查看是否安装成功
ansible --version
```

## 配置ssh免交互登录

```shell
#生成密钥对
[root@ngf8cdlchihmcygt-1112932 ~]# ssh-keygen -t rsa 
Generating public/private rsa key pair.
#密钥对存放路径
Enter file in which to save the key (/root/.ssh/id_rsa): 
#输入私钥保护密码，直接按Enter键表示无密码
Enter passphrase (empty for no passphrase):
#再次输入
Enter same passphrase again: 
Your identification has been saved in /root/.ssh/id_rsa.
Your public key has been saved in /root/.ssh/id_rsa.pub.
The key fingerprint is:
SHA256:r07Af8j9xpdALQ589cZy74L9cL91DpQub50Xd57hhPI root@ngf8cdlchihmcygt-1112932.novalocal
The key's randomart image is:
+---[RSA 2048]----+
|                 |
|              .  |
|         .   o o |
|     .    o + o.=|
|      o S  = .++.|
|       + + .o+ ++|
|        = +.++*=@|
|       . o .+E*OO|
|       .o  ..o.+*|
+----[SHA256]-----+
#查看ssh文件是否生成
[root@ngf8cdlchihmcygt-1112932 ~]# cd /root/.ssh/
[root@ngf8cdlchihmcygt-1112932 .ssh]# ll
total 8
-rw------- 1 root root    0 Jan 16  2020 authorized_keys
-rw------- 1 root root 1675 Nov 11 16:21 id_rsa
-rw-r--r-- 1 root root  421 Nov 11 16:21 id_rsa.pub
#复制公钥到远端服务器47.116.133.153
[root@ngf8cdlchihmcygt-1112932 ~]# ssh-copy-id -i .ssh/id_rsa.pub root@47.116.133.153
```

## 配置主机组

> 默认情况下，通过访问22端口（SSH）来管理设备。若目标主机使用了非默认的SSH端口，还可以在主机名称之后使用冒号加端口标明，以行为单位分隔配置。另外，hosts文件还支持通配符。

```shell
#修改配置文件
[root@ngf8cdlchihmcygt-1112932 ~]# vim /etc/ansible/hosts
#hosts文件内容
[test]
47.116.133.153
```

## Ansible命令

```shell
#test分组下的服务器执行指定命令
[root@ngf8cdlchihmcygt-1112932 ansible]# ansible test -m command -a "sh /usr/local/yinminxin/test.sh"
#参数
#-v（—verbose）：输出详细的执行过程信息，可以得到执行过程所有信息；
#-i PATH（—inventory=PATH）：指定inventory信息，默认为/etc/ansible/hosts；
#-f NUM（—forks=NUM）：并发线程数，默认为5个线程；
#—private-key=PRIVATE_KEY_FILE：指定密钥文件；
#-m NAME，—module-name=NAME：指定执行使用的模块；
#-M DIRECTORY（—module-path=DIRECTORY） ：指定模块存放路径，默认为/usr/share/ansible；
#-a ARGUMENTS（—args=ARGUMENTS）：指定模块参数；
#-u USERNAME（—user=USERNAME）：指定远程主机以USERNAME运行命令；
#-l subset（—limit=SUBSET）：限制运行主机；
```

## 栗子

> **test **关键字需要提前在 **/etc/ansible/hosts** 文件中定义组
>
> Ansible的返回结果非常友好，一般会用三种颜色来表示执行结果：
>
> **红色**：表示执行过程出现异常；
>
> **橘黄颜色**：表示命令执行后目标有状态变化；
>
> **绿色**：表示执行成功且没有目标机器做修改；

```shell
#列出test组的主机列表
[root@ngf8cdlchihmcygt-1112932 ansible]# ansible test --list
  hosts (1):
    47.116.133.153
#####################################################################################
#批量显示web组中的磁盘使用空间
[root@ngf8cdlchihmcygt-1112932 ansible]# ansible test -m command -a "df -hT"
47.116.133.153 | CHANGED | rc=0 >>
#文件系统 类型 容量 已用 可用 已用% 挂载点
Filesystem     Type      Size  Used Avail Use% Mounted on
devtmpfs       devtmpfs  1.8G     0  1.8G   0% /dev
tmpfs          tmpfs     1.8G     0  1.8G   0% /dev/shm
tmpfs          tmpfs     1.8G  460K  1.8G   1% /run
tmpfs          tmpfs     1.8G     0  1.8G   0% /sys/fs/cgroup
/dev/vda1      xfs        40G  2.7G   38G   7% /
tmpfs          tmpfs     364M     0  364M   0% /run/user/0
#####################################################################################
#检查所有主机是否存活
#调用ping模块，all表示/etc/ansible/hosts文件中的所有主机，不用创建all分组（默认存在）
[root@ngf8cdlchihmcygt-1112932 ansible]# ansible all -f 5 -m ping
47.116.133.153 | SUCCESS => { #表示执行成功
    "ansible_facts": {
        "discovered_interpreter_python": "/usr/libexec/platform-python"
    }, 
    "changed": false, #没有对主机做出更改
    "ping": "pong"#表示执行ping命令的返回结果
}
```

## 模块

### command模块

> command模块在远程主机执行命令，不支持管道、重定向等shell的特性。常用的参数如下：

- chdir：在远程主机上运行命令前要提前进入的目录；
- creates：在命令运行时创建一个文件，如果文件已存在，则不会执行创建任务；
- removes：在命令运行时移除一个文件，如果文件不存在，则不会执行移除任务；
- executeable：指明运行命令的shell程序；

```shell
#在所有主机上运行“ls ./”命令，运行前切换到/home目录下。操作如下：
[root@centos01 ~]# ansible web -m command -a "chdir=/ ls ./"
```

### shell模块

> shell模块在远程主机执行命令，相当于调用远程主机的Shell进程，然后在该Shell下打开一个子Shell运行命令。和command模块的区别是它支持Shell特性：如管道、重定向等

```shell
#输出到屏幕
[root@ngf8cdlchihmcygt-1112932 ansible]# ansible test -m shell -a "echo hello world"  
47.116.133.153 | CHANGED | rc=0 >>
hello world
#输出到1.txt文件中
[root@ngf8cdlchihmcygt-1112932 ansible]# ansible test -m shell -a "echo hello world > /1.txt"  
47.116.133.153 | SUCCESS | rc=0 >>
```

### copy模块

> copy模块用于复制指定主机文件到远程主机的指定位置。常见的参数如下：

- dest：指出复制文件的目标目录位置，使用绝对路径。如果源是目录，则目标也要是目录，如果目标文件已存在，会覆盖原有内容；
- src：指出源文件的路径，可以使用相对路径和绝对路径，支持直接指定目录。如果源是目录，则目标也要是目录；
- mode：指出复制时，目标文件的权限，可选；
- owner：指出复制时，目标文件的属主，可选；
- group：指出复制时目标文件的属组，可选；
- content：指出复制到目标主机上的内容，不能和src一起使用，相当于复制content指明的数据到目标文件中；

```shell
#将本机的hosts文件复制到web组中的所有主机上存放在家目录下的a1.hosts目录，权限是777，属主是root，属组是root
[root@centos01 ~]# ansible test -m copy -a "src=/etc/hosts dest=/root/a1.hosts mode=777 owner=root group=root"
```

### hostname模块

> hostname模块用于管理远程主机上的主机名。常用的参数如下：

```shell
#将192.168.100.20的主机名改为test，但是47.116.133.153需要敲一下bash才生效
[root@centos01 ~]# ansible 47.116.133.153 -m hostname -a "name=wangdongxi"
47.116.133.153 | CHANGED => {
    "ansible_facts": {
        "ansible_domain": "", 
        "ansible_fqdn": "wangdongxi", 
        "ansible_hostname": "wangdongxi", 
        "ansible_nodename": "wangdongxi", 
        "discovered_interpreter_python": "/usr/libexec/platform-python"
    }, 
    "changed": true, 
    "name": "wangdongxi"
}
```

### yum模块

> 管理端只是发送yum指令到被管理端，被管理端要存在可用的yum仓库才可以成功安装
>
> yum模块基于yum机制，对远程主机管理程序包。常用的参数如下：

- name：程序包名称，可以带上版本号。若不指明版本，则默认为最新版本；
- state=present|atest|absent：指明对程序包执行的操作:present表明安装程序包，latest表示安装最新版本的程序包，absent表示卸载程序包
- disablerepo：在用yum安装时，临时禁用某个仓库的ID；
- enablerepo：在用yum安装时，临时启用某个仓库的ID；
- conf_file：yum运行时的配置文件，而不是使用默认的配置文件；
- disable_gpg_check=yes|no：是否启用完整性校验功能；

```shell
#批量化删除web组主机的yum源
[root@centos01 ~]# ansible web -m shell -a "/usr/bin/rm -rf/etc/yum.repos.d/CentOS-*" 
#批量化挂载光盘
[root@centos01 ~]# ansible web -m shell -a "/usr/bin/mount/dev/cdrom /mnt" 
[WARNING]: Consider using mount module rather than running mount192.168.100.20 | SUCCESS | rc=0 >>mount: /dev/sr0 #写保护，将以只读方式挂载
#批量化安装httpd程序
[root@centos01 ~]# ansible web -m yum -a "name=httpd state=present" 
#批量化查看安装的httpd程序包
[root@centos01 ~]# ansible web -m shell -a "rpm -qa | grep httpd"
[WARNING]: Consider using yum, dnf or zypper module rather than running rpm192.168.100.20 | SUCCESS | rc=0 >>httpd-2.4.6-67.el7.centos.x86_64httpd-tools-2.4.6-67.el7.centos.x86_64
#批量启动服务
[root@centos01 ~]# ansible web -m shell -a "systemctl start httpd"
#批量化监听httpd服务是否启动成功
[root@centos01 ~]# ansible web -m shell -a "netstat -anptu | grep httpd"
192.168.100.20 | SUCCESS | rc=0 >>tcp6 0 0 :::80 :::* LISTEN 2072/httpd
```

### service模块

> service模块为用来管理远程主机上的服务的模块。常见的参数如下：

- name:被管理的服务名称；
- state=started|stopped|restarted：动作包含启动，关闭或重启；
- enable=yes|no:表示是否设置该服务开机自启动；
- runlevel:如果设定了enabled开机自启动，则要定义在哪些运行目标下自动启动；

```shell
#设置httpd服务重新启动和开机自动启动
[root@centos01 ~]# ansible web -m service -a "name=httpdenabled=yes state=restarted"
```

### user模块

> user模块主要用于管理远程主机上的用户账号。常见的参数如下：

- name:必选参数，账号名称；
- state=present|absent:创建账号或者删除账号，present表示创建，absent表示删除；
- system=yes|no:是否为系统账户；
- uid:用户UID；
- group:用户的基本组groups:用户的附加组；
- shell:默认使用的shell；
- home:用户的家目录；
- mve_home=yes|no：如果设置的家目录已经存在，是否将已存在的家目录进行移动；
- pssword:用户的密码，建议使用加密后的字符串；
- comment：用户的注释信息；
- remore=yes|no：当state=absent时，是否要删除用户的家目录；

```shell
#在web组的所有主机上新建一个系统用户，UID为502，属组是root，名字是user01，密码是pwd@123
[root@centos01 ~]# ansible web -m user -a "name=user01 system=yes uid=502 group=root groups=root shell=/etc/nologin home=/home/user01 password=pwd@123"
```



[参考资料](https://baijiahao.baidu.com/s?id=1673800420241002267&wfr=spider&for=pc)