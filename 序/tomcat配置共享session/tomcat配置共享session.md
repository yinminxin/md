# Tomcat配置Redis共享session

1. 解压 `tomcat-cluster-redis-session-manager.rar` ，获取lib依赖包，和conf配置文件。同目录下如果没有压缩包可通过链接下载，[点击获取](https://huanxijiuhao.oss-cn-shanghai.aliyuncs.com/tomcat-cluster-redis-session-manager.rar)

2. 将lib文件夹下的jar包添加到tomcat的lib目录。

3. 将conf下的配置文件中redis的IP，端口，密码（没有密码就不设置）修改成自己的redis配置。将配置文件放到tomcat的conf文件夹下。

4. 修改tomcat的conf文件夹下的context.xml，在<Context>标签下添加下面两个标签内容

   ```xml
   <Context>
   
       <Valve className="tomcat.request.session.redis.SessionHandlerValve" />
       <Manager className="tomcat.request.session.redis.SessionManager" />
   
   </Context>
   
   ```

5. 可以在web.xml中配置session过期时间(根据需要设置)

   ```xml
   <!--  默认是30分钟  -->
   <session-config>
           <session-timeout>30</session-timeout>
   </session-config>
   ```

   