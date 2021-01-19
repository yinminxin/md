# 脚本监控系统负载和CPU

## 一 邮件安装

### 1. 安装mailx

```shell
yum install -y mailx   #发送邮件
```

### 2. 配置mailx

```shell
#打开配置文件
vi /etc/mail.rc
#在文件最后面加上下面4行
set from=****@mail.com   #发送人
set smtp=***.mail.com        #邮箱服务器
set smtp-auth-user=YOUR_MAIL_USER          #用户名
set smtp-auth-password=YOUR_MAIL_PWD.   #密码
```

### 3. 测试发送邮件,(-s 标题,-a 附件)

```shell
echo "测试邮件内容123456" | mailx -v -s "测试标题" -a "附件全路径" ******@xxx.com
```

## 二 监控服务器系统负载情况

### 1. 系统负载的脚本文件

```shell
# vim /scripts/load-warning.sh
#!/bin/bash 
#使用uptime命令监控linux系统负载变化 

#提取本服务器的IP地址信息 
#ip a | grep eth0 | grep inet | awk -F ' ' '{print $2}' | awk -F '/' '{print $1}'
IP=`ifconfig eth0 | grep inet | sed -n '1,1p' | awk '{print $2}'` 
#IP=`ifconfig eth0 | grep "inet" | cut -f 2 -d ":" | awk '{print $2}'`
#IP=`ifconfig eth0 | grep "netmask" | awk '{print $2}'`

#抓取cpu的总核数 
cpu_num=`grep -c 'model name' /proc/cpuinfo` 

#抓取当前系统15分钟的平均负载值 
load_15=`uptime | awk '{print $NF}'` 

#计算当前系统单个核心15分钟的平均负载值，结果小于1.0时前面个位数补0。 
average_load=`echo "scale=2;a=$load_15/$cpu_num;if(length(a)==scale(a)) print 0;print a" | bc` 

#取上面平均负载值的个位整数 
average_int=`echo $average_load | cut -f 1 -d "."` 

#设置系统单个核心15分钟的平均负载的告警值为0.70(即使用超过70%的时候告警)。 
load_warn=0.70 

#当单个核心15分钟的平均负载值大于等于1.0（即个位整数大于0） ，直接发邮件告警；如果小于1.0则进行二次比较 
if (($average_int > 0)); then 
　　echo "$IP服务器15分钟的系统平均负载为$average_load，超过警戒值1.0，请立即处理！！！" | mailx -v -s "$IP 服务器系统负载严重告警！！！" ******@mail.com 
　　else 
#当前系统15分钟平均负载值与告警值进行比较（当大于告警值0.70时会返回1，小于时会返回0 ） 
load_now=`expr $average_load \> $load_warn` 

#如果系统单个核心15分钟的平均负载值大于告警值0.70（返回值为1），则发邮件给管理员 
　　if (($load_now == 1)); then 
　　　　echo "$IP服务器15分钟的系统平均负载达到 $average_load，超过警戒值0.70，请及时处理。" | mailx -v -s "$IP 服务器系统负载告警" ******@mail.com
　　fi 
fi
# chmod a+x /scripts/load-warning.sh
```

### 2. 监控服务器系统cpu占用情况

```shell
# vim /scripts/cpu-warning.sh
#!/bin/bash 
#监控系统cpu的情况脚本程序 

#提取本服务器的IP地址信息 
IP=`ifconfig eth0 | grep inet | sed -n '1,1p' | awk '{print $2}'` 
#IP=`ifconfig eth0 | grep "inet" | cut -f 2 -d ":" | awk '{print $2}'`
#IP=`ifconfig eth0 | grep "netmask" | awk '{print $2}'`

#取当前空闲cpu百份比值（只取整数部分） 
#cpu_idle=`top -b -n 1 | grep Cpu | awk '{print $5}' | cut -f 1 -d "."` 
#cpu_idle=`top -b -n 1 | grep Cpu | awk '{print $7}' | cut -f 2 -d "," | cut -f 1 -d "."`
cpu_idle=`top -b -n 1 | grep "Cpu" | cut -f 4 -d "," | awk '{print $1}'`

#设置空闲cpu的告警值为20%，如果当前cpu使用超过80%（即剩余小于20%），立即发邮件告警 
if (($cpu_idle < 20)); then 
　　echo "$IP服务器cpu剩余$cpu_idle%，使用率已经超过80%，请及时处理。" | mailx -v -s "$IP 服务器CPU告警" ******@mail.com 
fi
# chmod a+x /scripts/cpu-warning.sh
```

> 参考链接 : <https://www.cnblogs.com/weijiangbao/p/7823807.html>