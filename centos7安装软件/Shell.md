# Shell

## 1. 命令

### 1.1 wc 查看行数 

```shell
#查看行数
wc -l  a.txt  #统计a.txt 行数
ifconfig | wc -l # 查看行数
```

### 1.2 sed 查看行数据

```shell
sed -n 'a,bp' a.txt  #读取自第a行到第b行的数据
sed -n '190,196p' a.txt #查看a.txt 190行到196行
sed -n '190,1p' a.txt  #查看190行,一行
```

### 1.3 awk 

```shell
pwd | awk -F '/' '{print $NF}' #获取目录的最后一层
#-F指定分隔符
#$NF 获取最后一列
```

### 1.4 cut

```shell
#查看CPU
top -b -n 1 | grep Cpu | awk '{print $7}' | cut -f 2 -d "," | cut -f 1 -d "."
#-f 指定获取哪个区域
#-d 指定分隔符
```

### 1.5 grep

```shell
#获取本机IP
ifconfig eth0 | grep inet | sed -n '1,1p' | awk '{print $2}'
#grep 获取包含inet字符串的一行
```

### 1.6 删除rm

```shell
#语法
rm -rf [目录或文件]
# -r:递归删除 -f:不询问直接删除
```

### 1.7 复制(cp),移动(mv)

```shell
#复制a.txt为b.txt
cp [a.txt] [b.txt]
#复制a.txt到local目录下
cp [a.txt] [/usr/local]

#移动a.txt到local目录下
mv [a.txt] [/usr/local]
#重命名
mv [a.txt] [b.txt]
```

### 1.8 打压缩包和解压缩包

```shell
#打tar包
tar -zcvf test.tar.gz test/
#解tar包
tar -zxvf [tar包]
```

### 1.9 网络命令

```shell
#显示网络设备
ifconfig
#启用eth0网卡
ifconfig eth0 up
#停用eth0网卡
ifconfig eth0 down
#探测网络是否通畅
ping [127.0.0.1]
#查看网络端口
netstat -an | grep [3306]
```

### 1.10 系统管理命令

```shell
#显示当前系统时间
date
#设置系统时间
date -s "2019-01-01 10:12:55"

#友好显示磁盘信息
df -h
#显示内存状态
free -m
#显示,管理执行中的程序
top
```

### 1.11 多用户

``` shell
#[drwxr-xr-x.]  3 root root     33     Apr 28 10:13      tomcat
#  文件权限        用户  组    文件大小   创建更新日期    文件或目录名
#[drwxr-xr-x.]
#第一位：d代表目录，-代表文件
#第二三四位：文件所有者权限，r-读-4，w-写-2，x-执行-1，-表示没有
#第五六七位：文件所属组权限
#第八九十位：其他人权限
#创建组和用户
groupadd [组名]
useradd -g [组名] [用户名]
passwd [用户名](设置密码)
#删除组和用户
userdel [用户名]
groupdel [组名]
#改变文件所属用户组   -R:递归
chgrp -R [组名] [文件/文件夹]
#改变文件所有者   chgrp [-R] 账号名称 filename/dirname
chown -R [用户名] [文件/文件夹]
#改变文件权限
chmod -R 777 [文件/目录]
#u-用户 g-组 o-其他人 a-所有三种角色 =代表给角色那些权限 +代表给角色添加哪些权限 -代表给角色去除哪些权限
chmod u=rwx g=rx o=r [文件/目录]
```

### 1.12 查看日志

```shell
# tail [必要参数] [选择参数] [文件]
-f #循环读取
-q #不显示处理信息
-v #显示详细的处理信息
-c<数目> #显示的字节数
-n<行数> #显示行数
#查看日志尾部最后10行
tail -n 10 test.log
#查看10行以后的日志
tail -n +10 test.log
#循环实时查看最后10条日志
tail -fn 10 test.log
```

### 1.13 SCP

```shell
#发送文件或目录到远程服务器 
# [-r] ==> 递归目录下的所有目录和文件
# [local_folder] ==> 源文件目录（可使用绝对路径）
# [local_file] ==> 源文件（可使用绝对路径）
# [remote_username] ==> 远程登录用户名
# [remote_ip] ==> 目标服务器IP
# [remote_folder] ==> 目标服务器目录
scp -r local_folder remote_username@remote_ip:remote_folder #发送本地目录到目标服务器的指定目录下
scp local_file remote_username@remote_ip:remote_folder #发送本地文件到目标服务器的指定目录下

#从远程复制到本地
scp -r remote_username@remote_ip:remote_folder local_folder  #复制目标服务器的目录到本地目录
scp remote_username@remote_ip:remote_folder local_folder #复制目标服务器的文件到本地
```

## 2. 脚本

### 2.1 `

```shell
命令替换`command` 结构使字符(`)[译者注：这个字符不是单引号，而是在标准美国键盘上的ESC键下面，在字符1左边，在TAB键上面的那个键，要特别留心]引住的命令（command）执行结果能赋值给一个变量。它也被称为后引号（backquotes）或是斜引号（backticks）.
例子：
A=`ls -l`
把ls -l的结果赋给A ls -l 作为命令来执行
```

### 2.2 while read line

2.2.1 解释

```shell
while read line
do
       …
done < file
read通过输入重定向，把file的第一行所有的内容赋值给变量line，循环体内的命令一般包含对变量line的处理；然后循环处理file的第二行、第三行。。。一直到file的最后一行。还记得while根据其后的命令退出状态来判断是否执行循环体吗？是的，read命令也有退出状态，当它从文件file中读到内容时，退出状态为0，循环继续进行；当read从文件中读完最后一行后，下次便没有内容可读了，此时read的退出状态为非0，所以循环才会退出。
```

2.2.2 例子

```shell
#例如:
#计算文档a.txt中每一行中出现的数字个数并且要计算一下整个文档中一共出现了几个数字。例如a.txt内容如下：
#12aa*lkjskdj
#alskdflkskdjflkjj
#我们脚本名字为 ncount.sh, 运行它时：
#bash ncount.sh a.txt
#输出结果应该为：
#2
#0
#sum:2

#!/bin/sh
sum=0
while read line
do
line_n=`echo $line|sed 's/[^0-9]//g'|wc -L` #[`command`]:将command作为命令运行,结果赋值给line_n
echo $line_n
sum=$[$sum+$line_n]
done < $1
echo "sum:$sum"
```

### 2.3 expect

> expect是一个自动交互功能的工具

2.3.1 安装expect

```shell
sudo yum install expect
```

2.3.2 expect使用

```shell
#!/usr/bin/expect
set timeout 100
set password "123456"
spawn sudo rm -rf zzlogic
expect "root123456"
send "$password\n"
interact

#解释
# #!/usr/bin/expect表示使用expect的shell交互模式
# set是对变量password赋值
# set timeout 100：设置超时时间为100秒，如果要执行的shell命令很长可以设置超时时间长一些。expect超过超 时时间没有监测到要找的字符串，则不执行，默认timeout为10秒
# spawn在expect下执行shell脚本
# expect对通过spawn执行的shell脚本的返回进行判断，是否包含“”中的字段
# send：如果expect监测到了包含的字符串，将输入send中的内容，\n相当于回车
# interact：留在开的子进程内，可以继续输入，否则将退出子进程回到shell中（比如ssh登录到某台服务器上，只有加了interact才可以留在登录后的机器上进行操作）
```

2.3.3 expect的命令行参数

> [lindex $argv n]获得index为n的参数（index从0开始计算）
>
> $argv为命令行参数的个数
>
> [lrange $argv 0 0]表示第一个参数
>
> [lrange $argv 0 3]表示第1到第3个参数
>
>  
>
> 例如scp_service.sh文件，可以./scp_service.sh -rm来执行，这时是赋值了一个参数
>
> set option  [lindex $argv 0]（获得第一个参数存到变量option中，参数是的index是从0开始计算的）

2.3.4 expect支持if语句

> if { 条件1 } {
>      条件1执行语句
> } elif { 条件2 } {
>      条件2执行语句
> } else {
>      其他情况执行语句
> }
>
> 1.if的条件用{}来包含条件
>
> 2.if和后面的{}必须有空格隔开
>
> 3.两个花括号之间必须有空格隔开，比如if {} {}，否则会报错 expect:extra characters after close-brace
>
> 3.使用{来衔接下一行，所以if的条件后需要加左花括号{
>
> 4.else不能单独放一行，所以else要跟在}后面

```shell
#!/usr/bin/expect
set option [lindex $argv 0]
set timeout 100
set root123456passwd "123456"
set workpasswd "MhxzKhl"
if {$option == "-rm"} {
    #spawn cd /opt/
    spawn sudo rm -rf /opt/zzlogic
    expect "root123456"
    send "123456\n"
    interact
} else {
    if {$option == "-scp"} {
        spawn sudo scp -r YOUR_USER$IP:远程文件路径 本地存放路径
        expect "root123456"
        send "$root123456passwd\n"
        #expect "(yes/no)"
        #send "yes\n"
        expect "work"
        send "$workpasswd\n"
        interact
    } else {
        send_user "bad arg:$option\n"
        exit
    }
}
```

2.3.5 expect {},多行期望

> 背景：有时执行shell后预期结果是不固定的，有可能是询问是yes/no，有可能是去输入密码，所以可以用expect{}（比如sudo命令，第一次使用sudo时需要输入密码，但是它有5分钟的有效时间，5分钟内是不需要再去输入的）
>
> 花括号内放多行语句，从上至下匹配，匹配到哪个expect执行哪句。（这里如果匹配到第一行会执行第一行；然后第一行的执行结果如果匹配到第二行也会执行第三行；如果某一行没有匹配到会向下寻找匹配到的那一行进行执行）

> 注意：多行的expect的{后不要跟语句，否则读不到这条。需要换行后去写具体的期望值和操作。

```shell
expect {
    "work" {send "$workpasswd\n"}
    "root123456" {send "$root123456passwd\n"}
    "(yes/no)" {send "yes\n"}
    exp_continue
}
```

> 说明：exp_continue表示继续执行下面的expect。

### 2.4 if

2.4.1 基本语法

```shell
if [ command ]; then
     符合该条件执行的语句
fi
```

2.4.2 扩展语法

```shell
if [ command ];then
     符合该条件执行的语句
elif [ command ];then
     符合该条件执行的语句
else
     符合该条件执行的语句
fi
```

2.4.3 语法说明

> bash shell会按顺序执行if语句，如果command执行后且它的返回状态是0，则会执行符合该条件执行的语句，否则后面的命令不执行，跳到下一条命令。
> 当有多个嵌套时，只有第一个返回0退出状态的命令会导致符合该条件执行的语句部分被执行,如果所有的语句的执行状态都不为0，则执行else中语句。
> 返回状态：最后一个命令的退出状态，或者当没有条件是真的话为0。

2.4.4 注意

> 1、[ ]表示条件测试。注意这里的空格很重要。要注意在'['后面和']'前面都必须要有空格
> 2、在shell中，then和fi是分开的语句。如果要在同一行里面输入，则需要用分号将他们隔开。
> 3、注意if判断中对于变量的处理，需要加引号，以免一些不必要的错误。没有加双引号会在一些含空格等的字符串变量判断的时候产生错误。比如[ -n "$var" ]如果var为空会出错
> 4、判断是不支持浮点值的
> 5、如果只单独使用>或者<号，系统会认为是输出或者输入重定向，虽然结果显示正确，但是其实是错误的，因此要对这些符号进行转意
> 6、在默认中，运行if语句中的命令所产生的错误信息仍然出现在脚本的输出结果中
> 7、使用-z或者-n来检查长度的时候，没有定义的变量也为0
> 8、空变量和没有初始化的变量可能会对shell脚本测试产生灾难性的影响，因此在不确定变量的内容的时候，在测试号前使用-n或者-z测试一下
> 9、? 变量包含了之前执行命令的退出状态（最近完成的前台进程）（可以用于检测退出状态）

2.4.5 常用参数

1. 文件目录判断

```shell
常用的：
[ -a FILE ] 如果 FILE 存在则为真。
[ -d FILE ] 如果 FILE 存在且是一个目录则返回为真。
[ -e FILE ] 如果 指定的文件或目录存在时返回为真。
[ -f FILE ] 如果 FILE 存在且是一个普通文件则返回为真。
[ -r FILE ] 如果 FILE 存在且是可读的则返回为真。
[ -w FILE ] 如果 FILE 存在且是可写的则返回为真。（一个目录为了它的内容被访问必然是可执行的）
[ -x FILE ] 如果 FILE 存在且是可执行的则返回为真。

不常用的：
[ -b FILE ] 如果 FILE 存在且是一个块文件则返回为真。
[ -c FILE ] 如果 FILE 存在且是一个字符文件则返回为真。
[ -g FILE ] 如果 FILE 存在且设置了SGID则返回为真。
[ -h FILE ] 如果 FILE 存在且是一个符号符号链接文件则返回为真。（该选项在一些老系统上无效）
[ -k FILE ] 如果 FILE 存在且已经设置了冒险位则返回为真。
[ -p FILE ] 如果 FILE 存并且是命令管道时返回为真。
[ -s FILE ] 如果 FILE 存在且大小非0时为真则返回为真。
[ -u FILE ] 如果 FILE 存在且设置了SUID位时返回为真。
[ -O FILE ] 如果 FILE 存在且属有效用户ID则返回为真。
[ -G FILE ] 如果 FILE 存在且默认组为当前组则返回为真。（只检查系统默认组）
[ -L FILE ] 如果 FILE 存在且是一个符号连接则返回为真。
[ -N FILE ] 如果 FILE 存在 and has been mod如果ied since it was last read则返回为真。
[ -S FILE ] 如果 FILE 存在且是一个套接字则返回为真。
[ FILE1 -nt FILE2 ] 如果 FILE1 比 FILE2 新, 或者 FILE1 存在但是 FILE2 不存在则返回为真。
[ FILE1 -ot FILE2 ] 如果 FILE1 比 FILE2 老, 或者 FILE2 存在但是 FILE1 不存在则返回为真。
[ FILE1 -ef FILE2 ] 如果 FILE1 和 FILE2 指向相同的设备和节点号则返回为真。
```

2. 字符串判断

```shell
[ -z STRING ] 如果STRING的长度为零则返回为真，即空是真
[ -n STRING ] 如果STRING的长度非零则返回为真，即非空是真
[ STRING1 ]　 如果字符串不为空则返回为真,与-n类似
[ STRING1 == STRING2 ] 如果两个字符串相同则返回为真
[ STRING1 != STRING2 ] 如果字符串不相同则返回为真
[ STRING1 < STRING2 ] 如果 “STRING1”字典排序在“STRING2”前面则返回为真。
[ STRING1 > STRING2 ] 如果 “STRING1”字典排序在“STRING2”后面则返回为真。
```

3. 数值判断

```shell
[ INT1 -eq INT2 ] INT1和INT2两数相等返回为真 ,=
[ INT1 -ne INT2 ] INT1和INT2两数不等返回为真 ,<>
[ INT1 -gt INT2 ] INT1大于INT2返回为真 ,>
[ INT1 -ge INT2 ] INT1大于等于INT2返回为真,>=
[ INT1 -lt INT2 ] INT1小于INT2返回为真 ,<
[ INT1 -le INT2 ] INT1小于等于INT2返回为真,<=
```

4. 逻辑判断

```shell
[ ! EXPR ] 逻辑非，如果 EXPR 是false则返回为真。
[ EXPR1 -a EXPR2 ] 逻辑与，如果 EXPR1 and EXPR2 全真则返回为真。
[ EXPR1 -o EXPR2 ] 逻辑或，如果 EXPR1 或者 EXPR2 为真则返回为真。
[ ] || [ ] 用OR来合并两个条件
[ ] && [ ] 用AND来合并两个条件
```

5. 其他判断

```shell
[ -t FD ] 如果文件描述符 FD （默认值为1）打开且指向一个终端则返回为真
[ -o optionname ] 如果shell选项optionname开启则返回为真
```

2.4.6 if的高级特性

1. 双圆括号(( ))

> 表示数学表达式在判断命令中只允许在比较中进行简单的算术操作，而双圆括号提供更多的数学符号，而且在双圆括号里面的'>','<'号不需要转意。

2. 双方括号[[ ]]

> 表示高级字符串处理函数双方括号中判断命令使用标准的字符串比较，还可以使用匹配模式，从而定义与字符串相匹配的正则表达式。

3. 双括号的作用

> 在shell中，`[ $a != 1 || $b = 2 ]`是不允许出，要用`[ $a != 1 ] || [ $b = 2 ]`，而双括号就可以解决这个问题的，`[[ $a != 1 || $b = 2 ]]`。又比如这个`[ "$a" -lt "$b" ]`，也可以改成双括号的形式`(("$a" < "$b"))`

2.4.7 实例

1. 判断目录$doiido是否存在，若不存在，则新建一个

```shell
if [ ! -d "$doiido"]; then
　　mkdir "$doiido"
fi
```

2. 判断普通文件$doiido是否存，若不存在，则新建一个

```shell
if [ ! -f "$doiido" ]; then
　　touch "$doiido"
fi
```

3. 判断$doiido是否存在并且是否具有可执行权限

```shell
if [ ! -x "$doiido"]; then
　　mkdir "$doiido"
chmod +x "$doiido"
fi
```

4. 是判断变量$doiido是否有值

```shell
if [ ! -n "$doiido" ]; then
　　echo "$doiido is empty"
　　exit 0
fi
```

5. 两个变量判断是否相等

```shell
if [ "$var1" = "$var2" ]; then
　　echo '$var1 eq $var2'
else
　　echo '$var1 not eq $var2'
fi
```

6. 测试退出状态：

```shell
if [ $? -eq 0 ];then
    echo 'That is ok'
fi
```

7. 数值的比较：

```shell
if [ "$num" -gt "150" ];then
   echo "$num is biger than 150"
fi
```

8. a>b且a<c

```shell
(( a > b )) && (( a < c ))
[[ $a > $b ]] && [[ $a < $c ]]
[ $a -gt $b -a $a -lt $c ]
```

9. a>b或a<c

```shell
(( a > b )) || (( a < c ))
[[ $a > $b ]] || [[ $a < $c ]]
[ $a -gt $b -o $a -lt $c ]
```

10. 检测执行脚本的用户

```shell
if [ "$(whoami)" != 'root' ]; then
   echo  "You  have no permission to run $0 as non-root user."
   exit  1;
fi

#上面的语句也可以使用以下的精简语句
[ "(whoami)" != 'root' ] && ( echo "You have no permission to run 0 as non-root user."; exit 1 )

```

11. 正则表达式

```shell
doiido="hero"
if  [[ "$doiido" == h* ]];then
    echo "hello，hero"
fi
```

2.4.8 其他例子

1. 查看当前操作系统类型

   ```shell
   #!/bin/sh

   SYSTEM=`uname  -s`
   if [ $SYSTEM = "Linux" ] ; then
      echo "Linux"
   elif
       [ $SYSTEM = "FreeBSD" ] ; then
      echo "FreeBSD"
   elif
       [ $SYSTEM = "Solaris" ] ; then
       echo "Solaris"
   else
       echo  "What?"
   fi
   ```

2. if利用read传参判断

   ```shell
   #!/bin/bash
   read -p "please  input a score:"  score
   echo  -e "your  score [$score] is judging by sys now"
   if [ "$score" -ge "0" ]&&[ "$score" -lt "60" ];then
       echo  "sorry,you  are lost!"
   elif [ "$score" -ge "60" ]&&[ "$score" -lt "85" ];then
       echo "just  soso!"
   elif [ "$score" -le "100" ]&&[ "$score" -ge "85" ];then
        echo "good  job!"
   else
        echo "input  score is wrong , the range is [0-100]!"
   fi
   ```

3. 判断文件是否存在

   ```shell
   #!/bin/sh
   today=`date  -d yesterday +%y%m%d`
   file="apache_$today.tar.gz"
   cd  /home/chenshuo/shell

   if [ -f "$file" ];then
       echo “”OK"
   else
       echo "error  $file" >error.log
       mail  -s "fail  backup from test" loveyasxn924@126.com <error.log
   fi
   ```

4. 这个脚本在每个星期天由cron来执行。如果星期的数是偶数，他就提醒你把垃圾箱清理：

   ```shell
   #!/bin/bash
   WEEKOFFSET=$[ $(date +"%V") % 2 ]
   if [ $WEEKOFFSET -eq "0" ]; then
      echo "Sunday evening, put out the garbage cans." | mail -s "Garbage cans out"  your@your_domain.org
   fi
   ```

5. 挂载硬盘脚本(windows下的ntfs格式硬盘)

   ```shell
   #! /bin/sh
   dir_d=/media/disk_d
   dir_e=/media/disk_e
   dir_f=/media/disk_f

   a=`ls $dir_d | wc -l`
   b=`ls $dir_e | wc -l`
   c=`ls $dir_f | wc -l`
   echo "checking disk_d..."
   if [ $a -eq 0 ]; then
       echo "disk_d  is not exsit,now creating..."
       sudo  mount -t ntfs /dev/disk/by-label/software /media/disk_d
   else
       echo "disk_d exits"
   fi

   echo  "checking  disk_e..."
   if [ $b -eq 0 ]; then 
       echo "disk_e is not exsit,now creating..."
       sudo mount -t ntfs /dev/disk/by-label/elitor /media/disk_e
   else
       echo  "disk_e exits"
   fi

   echo  "checking  disk_f..."
   if [ $c -eq 0 ]; then
       echo  "disk_f  is not exsit,now creating..."
       sudo mount -t ntfs /dev/disk/by-label/work /media/disk_f
   else
       echo "disk_f  exits"
   fi
   ```

### 2.5 read

> read命令 -n(不换行) -p(提示语句) -n(字符个数) -t(等待时间) -s(不回显)

2.5.1 基本读取

> read命令接收标准输入（键盘）的输入，或其他文件描述符的输入（后面在说）。得到输入后，read命令将数据放入一个标准变量中。下面是read命令的最简单形式::

```shell
#!/bin/bash
echo -n "Enter your name:"   //参数-n的作用是不换行，echo默认是换行
read  name                   //从键盘输入
echo "hello $name,welcome to my program"     //显示信息
exit 0                       //退出shell程序。
```

> 由于read命令提供了-p参数，允许在read命令行中直接指定一个提示。所以上面的脚本可以简写成下面的脚本::

```shell
#!/bin/bash
read -p "Enter your name:" name
echo "hello $name, welcome to my program"
exit 0
```

***在上面read后面的变量只有name一个，也可以有多个，这时如果输入多个数据，则第一个数据给第一个变量，第二个数据给第二个变量，如果输入数据个数过多，则最后所有的值都给第一个变量。如果太少输入不会结束。***

> 在read命令行中也可以不指定变量.如果不指定变量，那么read命令会将接收到的数据放置在环境变量REPLY中。
>
> 例如::
>
> read -p "Enter a number"
>
> 环境变量REPLY中包含输入的所有数据，可以像使用其他变量一样在shell脚本中使用环境变量REPLY.

2.5.2 计时输入

> 使用read命令存在着潜在危险。脚本很可能会停下来一直等待用户的输入。如果无论是否输入数据脚本都必须继续执行，那么可以使用-t选项指定一个计时器。-t选项指定read命令等待输入的秒数。当计时满时，read命令返回一个非零退出状态;

```shell
#!/bin/bash
if read -t 5 -p "please enter your name:" name
then
    echo "hello $name ,welcome to my script"
else
    echo "sorry,too slow"
fi
exit 0
```

> 除了输入时间计时，还可以设置read命令计数输入的字符。当输入的字符数目达到预定数目时，自动退出，并将输入的数据赋值给变量。

```shell
#!/bin/bash
read -n1 -p "Do you want to continue [Y/N]?" answer
case $answer in
Y | y)
      echo "fine ,continue";;
N | n)
      echo "ok,good bye";;
*)
     echo "error choice";;
esac
exit 0
```

> 该例子使用了-n选项，后接数值1，指示read命令只要接受到一个字符就退出。只要按下一个字符进行回答，read命令立即接受输入并将其传给变量。无需按回车键。



2.5.3 默读（输入不显示在监视器上）

> 有时会需要脚本用户输入，但不希望输入的数据显示在监视器上。典型的例子就是输入密码，当然还有很多其他需要隐藏的数据。-s选项能够使read命令中输入的数据不显示在监视器上（实际上，数据是显示的，只是read命令将文本颜色设置成与背景相同的颜色）。

```shell
#!/bin/bash
read  -s  -p "Enter your password:" pass
echo "your password is $pass"
exit 0
```

2.5.4 读文件

> 最后，还可以使用read命令读取Linux系统上的文件。每次调用read命令都会读取文件中的"一行"文本。当文件没有可读的行时，read命令将以非零状态退出。读取文件的关键是如何将文本中的数据传送给read命令。最常用的方法是对文件使用cat命令并通过管道将结果直接传送给包含read命令的while命令

```shell
#!/bin/bash
count=1    //赋值语句，不加空格
cat test | while read line        //cat 命令的输出作为read命令的输入,read读到的值放在line中
do
   echo "Line $count:$line"
   count=$[ $count + 1 ]          //注意中括号中的空格。
done
echo "finish"
exit 0
```

### 2.6 select

例子:testWhile.sh

```shell
#!/bin/sh
temp=1;
while [ $temp == 1 ]; do
    echo '请选择:'
        select var in '殷' '民' '鑫' '退出';do break
        done
            echo "你选择得是：$var"
        if [[ $var == '殷' ]]; then
            echo "属于姓"
            break;
        elif [[ $var == '民' ]]; then
            echo "属于辈"
            break;
        elif [[ $var == '鑫' ]]; then
            echo "属于名"
            break;
        elif [[ $var == '退出' ]]; then
            break;    
        fi
done

```

运行testWhile.sh,得到下面的提示

```shell
请选择:
1) 殷
2) 民
3) 鑫
4) 退出
#? 
```

输入1,回车

```shell
请选择:
1) 殷
2) 民
3) 鑫
4) 退出
#? 1
你选择得是：殷
属于姓
```

### 2.7 定时任务

1. 添加脚本到定时任务

   ```shell
   #一般执行crontab -e命令都是直接往/var/spool/cron下创建一个文件，这个文件的名称就是你的当前用户名，内容就是你添加的任务具体内容。
   #打开定时任务文件
   crontab -e
   #查看定时任务文件
   crontab -l
   #添加下面这行代码
   */30 * * * * /home/tomcatstatus.sh   #30分钟执行一次
   ```

2. 脚本添加任务

   ```shell
   #一般执行crontab -e命令都是直接往/var/spool/cron下创建一个文件，这个文件的名称就是你的当前用户名，内容就是你添加的任务具体内容。
   
   #获取当前用户名
   who | awk -F ' ' '{print $1}'
   
   #创建脚本
   #!/bin/bash
   echo "*/30 * * * * /home/tomcatstatus.sh" >> /var/spool/cron/$(who | awk -F ' ' '{print $1}')
   ```

## 3.  常用命令

```shell
#查看当前所在目录
pwd
#跳转到指定目录
cd [目录]
#查看目录下所有内容
ll 或 ls
#创建文件夹
mkdir [文件夹名称]
#删除文件夹
rmdir
#查看文件内容
cat
#创建文件
touch
#清屏
clear
#查看某个进程
ps -ef|grep [java]
#杀掉某个进程
kill -9 [34653]
#显示当前目录的大小
du -h
#显示目前登陆系统的用户信息
who
#查看当前主机名
hostname #修改 vi /etc/sysconfig/network
#显示本机详细信息
uname -a
#修改文件夹属组
chown -R [用户] [文件/文件夹]
#有的机器可能不能使用 rz/sz 命令，这个时候我们就需要安装lrzsz： yum install lrzsz -y
#上传，不能上传文件夹
rz
#下载
sz file
#取txt文件的 前 n行到另一个文件
head -n etrain.txt > err.txt
```

