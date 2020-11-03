# 配置tomcat通过https访问项目

## 1.进入jdk的bin目录运行命令下载证书

​		

```
keytool -v -genkey -alias tomcat -keyalg RSA -keystore d:/tomcat.keystore  -validity 36500
```

		注:d:/tomcat.keystore是将生成的tomcat.keystore放到d盘根目录下。
		   "-validity 36500”含义是证书有效期，36500表示100年，默认值是90天

## 2、输入keystore密码

密码任意，此处以123456为例，要记住这个密码，之后在进行server.xml配置时需要使用。

## 3、输入名字、组织单位、组织、市、省、国家等信息

注意事项：

A、Enter keystore password：
此处需要输入大于6个字符的字符串
B、“What is your first and last name?”
这是必填项，并且必须是TOMCAT部署主机的域名或者IP[如：gbcom.com 或者 10.1.25.251]，
就是你将来要在浏览器中输入的访问地址
C、
“What is the name of your organizational unit?”、单位
“What is the name of your organization?”、组织
“What is the name of your City or Locality?”、市
“What is the name of your State or Province?”、省
“What is the two-letter country code for this unit?”国家
可以按照需要填写也可以不填写直接回车，
在系统询问“correct?”时，对照输入信息，如果符合要求则使用键盘输入字母“y”，否则输入“n”重新填写上面的信息
D、Enter key password for <tomcat>，
这项较为重要，会在tomcat配置文件中使用，
建议输入与keystore的密码一致，设置其它密码也可以

## 4、输入之后会出现确认的提示

此时输入y，并回车。此时创建完成keystore。
进入到D盘根目录下可以看到已经生成的tomcat.keystore
复制tomcat.keystore 到tomcat/conf/ 下

## 5、进入tomcat文件夹

找到conf目录下的sever.xml并进行编辑,注掉原本的8080端口代码,加入下面的代码,将与8443端口有关的代码修改为8080

<Connector port="9001" protocol="HTTP/1.1" SSLEnabled="true"
     maxThreads="150" scheme="https" secure="true"
     clientAuth="false" keystoreFile="D:/AppServer/Tomcat/apache-tomcat-6.0.32/conf/tomcat.keystore"
     keystorePass="123456" sslProtocol="TLS" />
	 
注：keystoreFile是证书地址,keystorePass的密码，就是刚才我们设置的“123456”,编辑完成后关闭并保存sever.xml

## 6、应用程序HTTP自动跳转到HTTPS

强制https访问配置如下：
在 tomcat /conf/web.xml 中的 </welcome-file-list> 后面加上以下内容
	

```xml
<login-config>
		<!-- Authorization setting for SSL -->
		<auth-method>CLIENT-CERT</auth-method>
		<realm-name>Client Cert Users-only Area</realm-name>
	</login-config>
	<security-constraint>
		<!-- Authorization setting for SSL -->
		<web-resource-collection >
			<web-resource-name >SSL</web-resource-name>
			<url-pattern>/*</url-pattern>
		</web-resource-collection>
		<user-data-constraint>
			<transport-guarantee>CONFIDENTIAL</transport-guarantee>
		</user-data-constraint>
   </security-constraint>
```


7、多刷新等待一会,就可以了


注意事项：如要禁用options等访问请求将6中的代码更换为下面的代码

```xml
	<login-config>
		<!-- Authorization setting for SSL -->
		<auth-method>CLIENT-CERT</auth-method>
		<realm-name>Client Cert Users-only Area</realm-name>
	</login-config>
	<security-constraint>
            <web-resource-collection>
                <web-resource-name>fortune</web-resource-name>
                <url-pattern>/*</url-pattern>
                <http-method>PUT</http-method>
                <http-method>DELETE</http-method>
                <http-method>HEAD</http-method>
                <http-method>OPTIONS</http-method>
                <http-method>TRACE</http-method>
            </web-resource-collection>
            <auth-constraint></auth-constraint>
        </security-constraint>
```


