

# 创建SpringBoot项目+JWT鉴权

## 1. idea创建SpringBoot项目

### 搭建项目

![1579140178](springboot+jwt\img\1579140178.png)

![1579140414](springboot+jwt\img\1579140414.png)

![1579140479](springboot+jwt\img\1579140479.png)

![1579140578](springboot+jwt\img\1579140578.png)

### 基本设置

1. 设置maven：File-->settings-->Maven

   ![设置maven](springboot+jwt\img\1579140722.png)

2. 设置jdk： File-->Project Structure-->SDKs

   ![设置JDK](springboot+jwt\img\1579141353.png)

### 多环境设置

1. resources目录下创建多环境目录，[点我获取env文件夹](springboot+jwt\code)，结构如下图：

   ![目录结构](springboot+jwt\img\1579143046.jpg)

2. 设置`application.yml`

   ```yaml
   #使用@ @引用maven中定义的属性，springboot屏蔽默认maven资源占位符$ $,并修改为@ @
   spring:
     profiles:
       active: @profiles.active@
   ```

3. 设置`application-local.yml`

   ```yaml
   server:
     port: 9002
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/itemstaffmanage?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2b8
       username: root
       password: root123
     jpa:
       database: mysql
       #配置数据库引擎和字符集
       properties:
         hibernate:
           dialect: org.hibernate.dialect.MySQL5Dialect
       #配置自动建表：updata:没有表新建，有表更新操作
       hibernate:
         ddl-auto: update
       #控制台显示建表语句
       show-sql: true
     thymeleaf:
       #关闭模板缓存
       cache: false
   ```

4. 设置日志配置文件`logback.xml`

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <configuration scan="true" scanPeriod="60 seconds" debug="false">
       <contextName>logback</contextName>

       <property name="catalina.base" value="${catalina.base:-./}"/>
       <property name="logstash.path" value="${catalina.base}/logs"/>

       <!-- 彩色日志依赖的渲染类 -->
       <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter"/>
       <conversionRule conversionWord="wex"
                       converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter"/>
       <conversionRule conversionWord="wEx"
                       converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter"/>

       <!-- 彩色日志格式 -->
       <property name="CONSOLE_LOG_PATTERN"
                 value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>

       <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
           <Prudent>true</Prudent>
           <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
               <FileNamePattern>${logstash.path}/jwt.%d{yyyy-MM-dd}.%i.log</FileNamePattern>
               <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                   <maxFileSize>500MB</maxFileSize>
               </timeBasedFileNamingAndTriggeringPolicy>
               <maxHistory>15</maxHistory>
           </rollingPolicy>
           <layout class="ch.qos.logback.classic.PatternLayout">
               <Pattern>%-20(%d{HH:mm:ss.SSS} [%thread]) %-5level %logger{32}:%L - %msg%n</Pattern>
           </layout>
       </appender>

       <!-- 输出到控制台配置 -->
       <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
           <encoder>
               <!--<Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%contextName] %-5level %logger{36} -%msg%n</Pattern>-->
               <pattern>${CONSOLE_LOG_PATTERN}</pattern>
               <charset>utf8</charset>
           </encoder>
       </appender>

       <!-- 最基础的日志输出级别 -->
       <root level="DEBUG">
           <appender-ref ref="CONSOLE"/>
           <appender-ref ref="FILE"/>
       </root>
   </configuration>
   ```

   

### POM文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.course.ymx</groupId>
    <artifactId>jwt</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>jwt</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <!-- json库统一使用fastjson -->
                <exclusion>
                    <groupId>com.fasterxml.jackson.core</groupId>
                    <artifactId>jackson-databind</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-tomcat -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.49</version>
        </dependency>

        <!-- mysql -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <!-- spring data jpa -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>${httpclient.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.9</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- jwt -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>
        <dependency>
            <groupId>joda-time</groupId>
            <artifactId>joda-time</artifactId>
        </dependency>

        <!--  redis-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <!-- rsa-->
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcpkix-jdk15on</artifactId>
            <version>1.56</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

    <profiles>
        <!-- 本地开发 -->
        <profile>
            <id>local</id>
            <properties>
                <profiles.active>local</profiles.active>
            </properties>
            <!-- 是否默认 true表示默认-->
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
        <!-- 开发环境 打包命令mvn clean package -Pdev或mvn clean package -D profiles.active=dev -->
        <profile>
            <id>dev</id>
            <properties>
                <profiles.active>dev</profiles.active>
            </properties>
        </profile>
        <!-- 测试环境 打包命令mvn clean package -Ptest -->
        <profile>
            <id>test</id>
            <properties>
                <profiles.active>test</profiles.active>
            </properties>
        </profile>
        <!-- 生产环境 打包命令mvn clean package -Pprod -->
        <profile>
            <id>prod</id>
            <properties>
                <profiles.active>prod</profiles.active>
            </properties>
        </profile>
    </profiles>

    <build>
        <finalName>${project.artifactId}</finalName>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>templates/**</include>
                    <include>static/**</include>
                    <include>application.yml</include>
                </includes>
                <excludes>
                    <!--                    <exclude>env/local/*</exclude>-->
                    <!--                    <exclude>env/dev/*</exclude>-->
                    <!--                    <exclude>env/test/*</exclude>-->
                    <exclude>env/**</exclude>
                </excludes>
                <filtering>true</filtering>
            </resource>
            <!--需要动态添加的资源-->
            <resource>
                <directory>src/main/resources/env/${profiles.active}</directory>
                <includes>
                    <include>*.*</include>
                </includes>
                <filtering>true</filtering>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <!--apidoc文档-->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>Creating a Production Build with Sencha Command</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>exec</goal>
                        </goals>
                        <configuration>
                            <executable>cmd</executable>
                            <arguments>
                                <argument>/C</argument>
                                <argument>sh ${basedir}\apidoc.sh</argument>
                            </arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### apidoc文档设置

> 注：开发环境需要安装`node.js`,`idea`安装`shellscript`,环境变量配置参考下图：

![环境变量配置参考](springboot+jwt\img\1579246694.png)



1. 在项目根路径下创建`apidoc.json`和`apidoc.sh`文件，结构如下：

   ![pidoc文件位置](springboot+jwt\img\1579145316.png)

2. `apidoc.sh`文件

   ```sh
   #!/usr/bin/env bash

   workspace=$(pwd)
   appName=$(basename ${workspace})
   logDir=${workspace}/logs
   targetDir=${workspace}/target

   echo "<<<<<<<<<<<<<<<<<<<<<<<<"
   echo "BUILD APIDOC ..."
   echo ">>>>>>>>>>>>>>>>>>>>>>>>"

   apiName=${appName}

   #apidoc -i ${workspace}/src/main/java/com/lk/hm/api -o ${targetDir}/${apiName} -t ../apidoc-contentType-plugin/template/ --parse-parsers apicontenttype=../apidoc-contentType-plugin/api_content_type.js
   #指定Controller层路径
   apidoc -i ${workspace}/src/main/java/com/course/ymx/jwt/controller -o ${targetDir}/${apiName}
   echo $(date +%Y%m%d%H%M%S)
   echo "BUILD APIDOC SUCCESS"
   ```

3. apidoc.json

   ```json
   {
     "name": "当前项目服务名称",
     "version": "1.0.0",
     "description": "Sport Test Data API",
     "title": "当前项目服务标题",
     "url": "http://localhost:9001",
     "sampleUrl": "http://localhost:9001/[项目名]",
     "template": {
       "forceLanguage": "zh-cn"
     },
     "template": {
       "withCompare": true,
       "withGenerator": true
     },
     "template": {
       "jQueryAjaxSetup": {
         "contentType": "application/json"
       }
     },
     "order": [
       "demo",
       "demo_list",
       "demo_find",
       "demo_create",
       "demo_update",
       "demo_delete"
     ]
   }
   ```

4. 示例：`TestController.java`如下：

   ```java
   package com.course.ymx.jwt.controller;

   import com.alibaba.fastjson.JSON;
   import com.alibaba.fastjson.JSONObject;
   import com.course.ymx.jwt.common.base.BaseController;
   import com.course.ymx.jwt.common.result.ResponseVO;
   import com.course.ymx.jwt.entity.Test;
   import com.course.ymx.jwt.service.TestService;
   import com.course.ymx.jwt.vo.request.PageVo;
   import org.apache.commons.lang3.StringUtils;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.data.domain.Page;
   import org.springframework.web.bind.annotation.*;

   import java.util.List;

   /**
    * @author yinminxin
    * @description
    * @date 2020/1/16 12:20
    */
   @RestController
   @RequestMapping("test")
   public class TestController extends BaseController {

       @Autowired
       private TestService testService;

       /**
        * @apiDefine sendSuccessList
        * @apiSuccess {Number} code 响应码;200(成功),402(未知错误)
        * @apiSuccess {String[]} data 数据
        * @apiSuccess {String} message 响应信息;接口请求success或failed返回相关信息
        * @apiSuccess {Boolean} success 是否成功;通过该字段可以判断请求是否到达.
        */

       /**
        * @apiDefine sendSuccess
        * @apiSuccess {Number} code 响应码;200(成功),402(未知错误)
        * @apiSuccess {Object} data 数据
        * @apiSuccess {String} message 响应信息;接口请求success或failed返回相关信息
        * @apiSuccess {Boolean} success 是否成功;通过该字段可以判断请求是否到达.
        */

       /**
        * @api {get} /test/getList get请求获取数据
        * @apiName getList
        * @apiDescription 测试api详情
        * @apiGroup test
        * @apiParam {Number} id 标题ID
        * @apiContentType application/json
        * @apiParamExample {json} Request-Example:
       {
       "id":"1"
       }
        * @apiUse sendSuccess
        * @apiSuccess {Object} data 数据
        * @apiSuccess {Integer} data.id 标签ID
        * @apiSuccess {String} data.name 标签名称
        * @apiSuccess {String} data.updatedTime 更新时间
        * @apiSuccessExample {json} Success-Response:
       {
       "code": "200",
       "data": "1",
       "message": "successed",
       "success": true
       }
        */
       @GetMapping("/getList")
       @ResponseBody
       private ResponseVO getList(@RequestBody String str){
           if (StringUtils.isNotBlank(str)) {
               String id = null; //ID
               JSONObject jsonObject = JSON.parseObject(str);
               if (StringUtils.isNotBlank(jsonObject.getString("id"))) {
                   id = jsonObject.getString("id");
               } else {
                   return getFailure();
               }
               if (StringUtils.isNotBlank(id)) {
                   return getFromData(id);
               }
           }
           List<Test> list = testService.findList();
           return getFromData(list);
       }
       
       
     /**
       * @api {post} /test/postList post请求获取数据
       * @apiName postList
       * @apiDescription 测试api详情
       * @apiGroup test
       * @apiParam {Number} id 标题ID
       * @apiContentType application/json
       * @apiParamExample {json} Request-Example:
      {
      "id":"1"
      }
       * @apiUse sendSuccess
       * @apiSuccess {Object} data 数据
       * @apiSuccess {String} data.id ID
       * @apiSuccess {String} data.name 名称
       * @apiSuccess {String} data.title 标题
       * @apiSuccess {date} data.time 时间
       * @apiSuccessExample {json} Success-Response:
      {
      "code": "200",
      "data": [
      {
      "createTime": 1579156726000,
      "id": "1",
      "name": "测试1",
      "state": 0,
      "time": 1579104000000,
      "title": "测试标题1",
      "updateTime": 1579156726000
      }
      ],
      "message": "successed",
      "success": true
      }
       */
      @PostMapping("/postList")
      @ResponseBody
      private ResponseVO postList(@RequestBody String str){
          if (StringUtils.isNotBlank(str)) {
              String id = null; //ID
              JSONObject jsonObject = JSON.parseObject(str);
              if (StringUtils.isNotBlank(jsonObject.getString("id"))) {
                  id = jsonObject.getString("id");
              } else {
                  return getFailure();
              }
              if (StringUtils.isNotBlank(id)) {
                  List<Test> list = testService.findList();
                  return getFromData(list);
              }
              return getFailure("id为空");
          }else{
              return getFailure("id为空");
          }
      }

      /**
       * @api {post} /test/findByPage 分页请求数据
       * @apiName findByPage
       * @apiDescription 测试api详情
       * @apiGroup test
       * @apiParam {Number} id 标题ID
       * @apiContentType application/json
       * @apiParamExample {json} Request-Example:
      {
      "pageNum":1,
      "pageSize":4,
      "searchKey":"5"
      }
       * @apiUse sendSuccess
       * @apiSuccess {Object} data 数据
       * @apiSuccess {String} data.id ID
       * @apiSuccess {String} data.name 名称
       * @apiSuccess {String} data.title 标题
       * @apiSuccess {date} data.time 时间
       * @apiSuccessExample {json} Success-Response:
      {
      "code": "200",
      "data": {
      "content": [
      {
      "createTime": 1579156726000,
      "id": "1",
      "name": "测试1",
      "state": 0,
      "time": 1579104000000,
      "title": "测试标题1",
      "updateTime": 1579156726000
      }
      ],
      "empty": false,
      "first": true,
      "last": true,
      "number": 0,
      "numberOfElements": 4,
      "pageable": {
      "offset": "0",
      "pageNumber": 0,
      "pageSize": 20,
      "paged": true,
      "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
      },
      "unpaged": false
      },
      "size": 20,
      "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
      },
      "totalElements": "4",
      "totalPages": 1
      },
      "message": "successed",
      "success": true
      }
       */
      @PostMapping("findByPage")
      public ResponseVO findByPage(@RequestBody PageVo pageVo){
          Page<Test> test = testService.findByPage(pageVo);
          return getFromData(test);
      }
   }
   ```




5. 示例：`TestService.java`如下：

   ```java
   package com.course.ymx.jwt.service;

   import com.course.ymx.jwt.entity.Test;
   import com.course.ymx.jwt.vo.request.PageVo;
   import org.springframework.data.domain.Page;

   import java.util.List;

   /**
    * @author yinminxin
    * @description
    * @date 2020/1/14 20:02
    */
   public interface TestService {

       List<Test> findList();

       /**
        * 分页查询
        * @param pageVo
        * @return
        */
       Page<Test> findByPage(PageVo pageVo);
   }
   ```

6. 示例：`TestServiceImpl.java`如下：

   ```java
   package com.course.ymx.jwt.service.impl;

   import com.course.ymx.jwt.entity.Test;
   import com.course.ymx.jwt.repository.TestRepository;
   import com.course.ymx.jwt.service.TestService;
   import com.course.ymx.jwt.vo.request.PageVo;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.data.domain.Page;
   import org.springframework.data.domain.PageRequest;
   import org.springframework.data.domain.Pageable;
   import org.springframework.data.domain.Sort;
   import org.springframework.data.jpa.domain.Specification;
   import org.springframework.stereotype.Service;
   import org.springframework.util.CollectionUtils;

   import javax.persistence.criteria.Predicate;
   import java.util.ArrayList;
   import java.util.List;

   /**
    * @author yinminxin
    * @description
    * @date 2020/1/14 20:02
    */
   @Service
   public class TestServiceImlp implements TestService {

       @Autowired
       private TestRepository testRepository;

       @Override
       public List<Test> findList() {
           return testRepository.findList();
       }

       @Override
       public Page<Test> findByPage(PageVo pageVo) {
           //默认分页参数
           int pageNum = 1;
           int pageSize = 20;
           //有分页参数给分页参数赋值
           if(pageVo.getPageNum() != null){
               pageNum=pageVo.getPageNum();
           }
           if(pageVo.getPageSize() != null){
               pageSize=pageVo.getPageSize();
           }
           //根据更新时间默认倒叙
           Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC,"updateTime"));
   //        return estateRepository.findAll(pageable);

           // TODO Specification
           Specification<Test> specification = (Specification<Test>) (root, query, cb) -> {
               //查询列表
               List<Predicate> predicates = new ArrayList<>();
               //添加状态正常的条件
               predicates.add(cb.equal(root.get("state").as(short.class), (short)0));
               if (!org.springframework.util.StringUtils.isEmpty(pageVo.getSearchKey())) {
                   Predicate p1 = cb.like(root.get("name").as(String.class), "%" + pageVo.getSearchKey().trim() + "%");
                   Predicate p2 = cb.like(root.get("title").as(String.class), "%" + pageVo.getSearchKey().trim() + "%");
                   predicates.add(cb.or(p1,p2));
               }
               //封装where
               if(!CollectionUtils.isEmpty(predicates)){
                   Predicate[] preArr = new Predicate[predicates.size()];
                   query.where(predicates.toArray(preArr));
               }
               return query.getRestriction();
           };
           //查询
           Page<Test> all = testRepository.findAll(specification, pageable);
           return all;
       }
   }
   ```

7. 示例：`TestRepository.java` 如下：

   ```java
   package com.course.ymx.jwt.repository;

   import com.course.ymx.jwt.entity.Test;
   import org.springframework.data.jpa.repository.JpaRepository;
   import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
   import org.springframework.data.jpa.repository.Query;
   import org.springframework.stereotype.Repository;

   import java.util.List;

   /**
    * @author yinminxin
    * @description
    * @date 2020/1/14 20:04
    */
   @Repository
   public interface TestRepository extends JpaRepository<Test,String>, JpaSpecificationExecutor<Test> {

       @Query(value = "SELECT * from test;",nativeQuery = true)
       List<Test> findList();
   }
   ```

8. 示例：`Test.java` 如下：

   ```java
   package com.course.ymx.jwt.entity;

   import com.course.ymx.jwt.common.base.BaseEntity;

   import javax.persistence.*;
   import java.io.Serializable;
   import java.sql.Timestamp;
   import java.util.Date;

   /**
    * @author yinminxin
    * @description 测试表
    * @date 2020/1/14 20:05
    */
   @Entity
   @Table(name = "test")
   @org.hibernate.annotations.Table(appliesTo = "test", comment = "测试表")
   public class Test extends BaseEntity {

       @Column(name = "name", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '名称'")
       private String name;
       @Column(name = "title", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '标题'")
       private String title;
       @Column(name = "time", columnDefinition = "date NOT NULL COMMENT '时间'")
       private Date time;

       //get,set方法略
   }
   ```

9. [点击获取代码地址](springboot+jwt\code\代码simple\jwt)

## 2. 加入jwt鉴权和redis

### jwt和redis的pom文件

```xml
<!-- jwt -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>
        <dependency>
            <groupId>joda-time</groupId>
            <artifactId>joda-time</artifactId>
        </dependency>

        <!--  redis-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <!-- rsa-->
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcpkix-jdk15on</artifactId>
            <version>1.56</version>
        </dependency>
```

### 项目中加入工具类，[点击获取](springboot+jwt\code\utils)，结构如下：

![目录结构](springboot+jwt\img\1579160250.png)

### 完整的yml文件

```yaml
server:
  port: 9002
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/itemstaffmanage?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2b8
    username: root
    password: root123
  jpa:
    database: mysql
    #配置数据库引擎和字符集
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL5Dialect
    #配置自动建表：updata:没有表新建，有表更新操作
    hibernate:
      ddl-auto: update
    #控制台显示建表语句
    show-sql: true
  thymeleaf:
    #关闭模板缓存
    cache: false
  #redis
  redis:
    # 数据库索引，默认0
    database: 0
    # 服务器IP地址
    host: localhost
    # 连接端口
    port: 6379
    #    # Redis服务器连接密码（默认为空）
    #password:
    #使用lettuce连接池
    lettuce:
      pool:
        # 连接池最大连接数（使用负值表示没有限制）
        max-active: 800
        # 连接池最大阻塞等待时间（使用负值表示没有限制）
        max-wait: -1ms
        # 连接池中的最大空闲连接
        max-idle: 20
        # 连接池中的最小空闲连接
        min-idle: 2
    # 连接超时时间（毫秒）默认是2000ms
    timeout: 30000ms
ymx:
  jwt:
    secret: ymx@Login(Auth}*^31)&wanda% # 登录校验的密钥
    pubKeyPath: D:/rsa/estate/rsa.pub # 公钥地址
    priKeyPath: D:/rsa/estate/rsa.pri # 私钥地址
    expire: 10080 # 7天 过期时间,单位分钟

cors:
  #allowedOrigins: http://localhost:8080,https://localhost:8443
  allowedOrigins: "*"

ports:
  isPorts: true
```

### 拦截器拦截请求并验证Token

- User.java

  ```java
  package com.second.boss.estate.entity;

  import com.alibaba.fastjson.annotation.JSONField;
  import com.second.boss.estate.common.base.BaseEntity;
  import jdk.nashorn.internal.ir.annotations.Ignore;

  import javax.persistence.*;

  /**
   * @author yinminxin
   * @description 用户表
   * @date 2019/12/11 17:50
   */
  @Entity
  @Table(name = "user")
  @org.hibernate.annotations.Table(appliesTo = "user", comment = "用户表")
  public class User extends BaseEntity {

      @Column(name = "name", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '用户姓名'")
      private String name;
      @Column(name = "user_name", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '用户名'")
      private String userName;
      @Column(name = "pass_word", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '密码'")
      @JSONField(serialize = false)
      private String passWord;
      @Column(name = "phone", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '手机号'")
      private String phone;
      @Column(name = "back_phone", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '备用手机号'")
      private String backPhone;
      @Column(name = "card_no", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '身份证号码'")
      private String cardNo;

      @Transient
      private String token;

      //get,set方法略
  }
  ```

- LoginController.java

  ```java
  package com.course.ymx.jwt.controller;

  import com.alibaba.fastjson.JSON;
  import com.alibaba.fastjson.JSONObject;
  import com.course.ymx.jwt.common.base.BaseController;
  import com.course.ymx.jwt.common.result.ResponseVO;
  import com.course.ymx.jwt.config.properties.JwtProperties;
  import com.course.ymx.jwt.entity.User;
  import com.course.ymx.jwt.utils.JwtUtils;
  import com.course.ymx.jwt.utils.RedisUtils;
  import com.course.ymx.jwt.vo.entity.UserInfo;
  import org.apache.commons.lang3.StringUtils;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Controller;
  import org.springframework.web.bind.annotation.*;

  /**
   * @author yinminxin
   * @description 登陆相关控制层
   * @date 2020/1/3 15:57
   */
  @Controller
  @RequestMapping("/")
  public class LoginController extends BaseController {

      @Autowired
      private UserService userService;
      @Autowired
      private JwtProperties jwt;
      @Autowired
      private RedisUtils redisUtils;

      @PostMapping("/login")
      @ResponseBody
      private ResponseVO testLogin(@RequestBody String str){
          //用户Id
          String userName = null;
          String passWord = null;
          if (StringUtils.isNotBlank(str)) {
              //解析json字符串
              JSONObject jsonObject = JSON.parseObject(str);
              try {
                  if(StringUtils.isNotBlank(jsonObject.getString("userName")) && StringUtils.isNotBlank(jsonObject.getString("passWord"))){
                      userName = jsonObject.getString("userName");
                      passWord = jsonObject.getString("passWord");
                  }else{
                      return getFailure("参数错误");
                  }
              } catch (NumberFormatException e) {
                  return getFailure();
              }
          }else {
              return getFailure("参数错误");
          }
          //根据用户名和密码查找用户
          User user = userService.findByUserNameAndPassWord(userName,passWord);
          if (user == null) {
              return getFailure("用户名或密码错误!");
          }
          String token = null;
          try {
              //用户存在生成token
              token = JwtUtils.generateToken(new UserInfo(user.getId(),user.getUserName()), jwt.getPrivateKey(), jwt.getExpire());
          } catch (Exception e) {
              e.printStackTrace();
          }
          if (StringUtils.isBlank(token)) {
              return getFailure();
          }
          //将用户信息存入redis
          redisUtils.set(token,user,60L*30);
          //将token存入cookie
  //        CookieUtils.setCookie(request,response,jwt.getCookieName(),token,jwt.getCookieMaxAge(),null,true);
          user.setToken(token);
          return getFromData(user);
      }
  }
  ```

- JwtProperties.properties

  ```java
  package com.course.ymx.jwt.config.properties;

  import com.course.ymx.jwt.utils.RsaUtils;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.boot.context.properties.ConfigurationProperties;
  import org.springframework.stereotype.Component;

  import javax.annotation.PostConstruct;
  import java.io.File;
  import java.security.PrivateKey;
  import java.security.PublicKey;

  @Component
  @ConfigurationProperties(prefix = "ymx.jwt")
  public class JwtProperties {

      private String secret; // 密钥

      private String pubKeyPath;// 公钥

      private String priKeyPath;// 私钥

      private int expire;// token过期时间

      private PublicKey publicKey; // 公钥

      private PrivateKey privateKey; // 私钥

      private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

      @PostConstruct
      public void init() {
          try {
              File pubKey = new File(pubKeyPath);
              File priKey = new File(priKeyPath);
              if (!pubKey.exists() || !priKey.exists()) {
                  // 生成公钥和私钥
                  RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
              }
              // 获取公钥和私钥
              this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
              this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
          } catch (Exception e) {
              logger.error("初始化公钥和私钥失败！", e);
              throw new RuntimeException();
          }
      }
      //get,set方法略
  }
  ```

- 拦截器拦截验证token的接口

  - 添加WebConfig.java

    ```java
    package com.course.ymx.jwt.config;

    import com.alibaba.fastjson.serializer.SerializeConfig;
    import com.alibaba.fastjson.serializer.SerializerFeature;
    import com.alibaba.fastjson.serializer.ToStringSerializer;
    import com.alibaba.fastjson.support.config.FastJsonConfig;
    import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
    import com.course.ymx.jwt.config.Interceptor.JwtInterceptor;
    import com.course.ymx.jwt.config.properties.Cors;
    import com.course.ymx.jwt.utils.RedisUtils;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.MediaType;
    import org.springframework.http.converter.HttpMessageConverter;
    import org.springframework.http.converter.StringHttpMessageConverter;
    import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

    import java.math.BigInteger;
    import java.nio.charset.Charset;
    import java.util.ArrayList;
    import java.util.List;

    @Configuration
    public class WebConfig extends WebMvcConfigurationSupport {

        @Autowired
        private Cors cors;
        @Autowired
        private RedisUtils redisUtils;

        public HttpMessageConverter<String> stringConverter() {
            StringHttpMessageConverter converter = new StringHttpMessageConverter(
                    Charset.forName("UTF-8"));
            return converter;
        }

        public HttpMessageConverter fastConverter() {

            //创建fastJson消息转换器
            FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

            //升级最新版本需加=============================================================
            List<MediaType> supportedMediaTypes = new ArrayList<>();
            supportedMediaTypes.add(MediaType.APPLICATION_JSON);
            supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
            supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
            supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
            supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
            supportedMediaTypes.add(MediaType.APPLICATION_PDF);
            supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
            supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
            supportedMediaTypes.add(MediaType.APPLICATION_XML);
            supportedMediaTypes.add(MediaType.IMAGE_GIF);
            supportedMediaTypes.add(MediaType.IMAGE_JPEG);
            supportedMediaTypes.add(MediaType.IMAGE_PNG);
            supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
            supportedMediaTypes.add(MediaType.TEXT_HTML);
            supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
            supportedMediaTypes.add(MediaType.TEXT_PLAIN);
            supportedMediaTypes.add(MediaType.TEXT_XML);

            //处理中文乱码问题
            supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

            fastConverter.setSupportedMediaTypes(supportedMediaTypes);

            FastJsonConfig fastJsonConfig = new FastJsonConfig();
            fastJsonConfig.setSerializerFeatures(
                    SerializerFeature.DisableCircularReferenceDetect,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullNumberAsZero,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullListAsEmpty,
                    SerializerFeature.WriteNullBooleanAsFalse,
                    SerializerFeature.WriteNonStringKeyAsString,
                    SerializerFeature.BrowserCompatible);

            //解决Long转json精度丢失的问题
            SerializeConfig serializeConfig = SerializeConfig.globalInstance;
            serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
            serializeConfig.put(Long.class, ToStringSerializer.instance);
            serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
            fastJsonConfig.setSerializeConfig(serializeConfig);

            //设置返回时间格式
    //        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
            fastConverter.setFastJsonConfig(fastJsonConfig);
            //将FastJson添加到视图消息转换器列表内
            return fastConverter;
        }

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.clear();
            converters.add(stringConverter());
            converters.add(fastConverter());
        }
        
        @Override
        public void addCorsMappings(CorsRegistry registry) {

            //设置允许跨域的路径
            registry.addMapping("/api/**")
                    //设置允许跨域请求的域名
                    //.allowedOrigins("*")
                    .allowedOrigins(cors.getAllowedOrigins())
                    //是配置是否允许发送Cookie,用于凭证请求,默认不发送cookie
                    .allowCredentials(true)
                    //.allowedHeaders("*")
                    //设置允许的方法
                    //.allowedMethods("*")
                    .allowedMethods("OPTIONS", "GET", "POST")
                    //.allowedMethods(cors.getAllowedMethods())
                    //跨域允许时间
                    .maxAge(3600);
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            //添加拦截器
            String[] excludePath = {"/login"};
            registry.addInterceptor(new JwtInterceptor(redisUtils)).excludePathPatterns(excludePath);
        }

    }
    ```

  - 添加jwt拦截器JwtInterceptor.java

    ```java
    package com.course.ymx.jwt.config.Interceptor;

    import com.course.ymx.jwt.config.properties.Cors;
    import com.course.ymx.jwt.utils.RedisUtils;
    import org.apache.commons.lang3.StringUtils;
    import org.springframework.context.ApplicationContext;
    import org.springframework.web.bind.annotation.RequestMethod;
    import org.springframework.web.context.support.WebApplicationContextUtils;
    import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

    import javax.servlet.ServletContext;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    import java.util.Arrays;
    import java.util.HashSet;
    import java.util.Set;

    //拦截器
    public class JwtInterceptor extends HandlerInterceptorAdapter {

        private RedisUtils redisUtils;

        public JwtInterceptor(RedisUtils redisUtils) {
            this.redisUtils = redisUtils;
        }

        public JwtInterceptor() {
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            StringBuffer requestURL = request.getRequestURL();
            System.out.println(requestURL);
            //自动排除生成token的路径,并且如果是options请求是cors跨域预请求，设置allow对应头信息
            if(RequestMethod.OPTIONS.toString().equals(request.getMethod())){
                return true;
            }

            //设置allow对应头信息
            this.allowedOrigins(request,response);

            //从请求的头字段中拿到token信息
            String token = request.getHeader("Authorization");

            if (StringUtils.isNotBlank(token) && redisUtils.hasKey(token) && redisUtils.getExpire(token) >= 0) {
                //token不为空,判断redis中token对应的value是否存在,判断redis中的用户数据是否过期
                //刷新token
                redisUtils.expire(token, 60L * 30);
                return true;
            }
            throw new Exception(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
        }
        
        /**
         * 处理跨域问题
         */
        private void allowedOrigins(HttpServletRequest request, HttpServletResponse response) {

            ServletContext context = request.getServletContext();
            ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
            Cors cors = ctx.getBean(Cors.class);

            String[] allowDomains = cors.getAllowedOrigins();
            Set allowOrigins = new HashSet(Arrays.asList(allowDomains));
            String originHeads = request.getHeader("Origin");
            if(allowOrigins.contains(originHeads)){
                //设置允许跨域的配置
                // 这里填写你允许进行跨域的主机ip（生产环境可以动态配置具体允许的域名和IP）
                response.setHeader("Access-Control-Allow-Origin", originHeads);
                /*response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Cookie");
                response.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
                response.setHeader("Access-Control-Allow-Credentials", "true");*/
            }else if(allowOrigins.contains("*")){
                //设置允许跨域的配置
                // dev环境设置*
                response.setHeader("Access-Control-Allow-Origin", "*");
            }
        }
    }
    ```




### 暂时结束

1. 完整的结构目录，[点击获取](springboot+jwt\code\代码jwt+redis\jwt)，目录如下(这里没有实现Login业务层，所以报错)：

   ![项目结构](springboot+jwt\img\1579241629.jpg)

## 3. 前端加密后端解密

### 思路：

1. 前端获取RSA公钥，两种方式，一种直接页面写死，二种从后台接口获取
2. 前端生成随机AES密钥，加密参数
3. 使用RSA公钥加密AES密钥，将加密后的AES密钥放在头信息中，传输到后台
4. 后台使用RSA私钥解密头信息中的AES密钥
5. 通过解密后的AES密钥解密参数

### RSA公钥私钥

```java
//RSA公钥私钥
public static void main(String[] args) {
        //RSA公钥，前端页面保存
        String rsaPublicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJuKaCkL12seqGwO/Vri8ZkBGqPqGn/uyZ6AW3e+8fSxDs6KDf69X4po4xhOMpFto/qZM2OmiCTcULsIlM/4+wcCAwEAAQ==";
        //RSA私钥，后端保存
        String rsaPrivateKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAm4poKQvXax6obA79WuLxmQEao+oaf+7JnoBbd77x9LEOzooN/r1fimjjGE4ykW2j+pkzY6aIJNxQuwiUz/j7BwIDAQABAkAe95d1VJ42/YuauAFJLxXa2JVtPRa5kdkibXvIg4O4q5YD0aQkMrYjw0Wl+JxsfJWxBbNTMqx6TCc2oK2uXUfBAiEA/VXGRQBj4dIjNUrXdvUQl/mD838AukNfNTcHL1C4sbUCIQCdLUY4AYLh47jb7JNoyx4QSgCaSKt2CxIIJ7c6MfgfSwIgWYeLt20I35C9IqNdfEvlXmAu8snKfwk6R3s4Dc+wRhUCIBU/GH3bnnCgks2PxkFlK5QyHLC+YAcd6fsWO0tvk8XPAiEAly64JQEHxTXErTeOwMFEqa31pgksOnbAnLG64EAUoa0=";
    }
```

### 前端加密工具类

1. [aes.min.js](https://pan.baidu.com/s/1l-cOv8rDFRBEOzKJRL7mOA)  提取码：oqdq

2. [jsencrypt.min.js](https://pan.baidu.com/s/1EHPOn-t7TetI_RZ9pea4Aw) 提取码：3kh3

3. [encryptUtil.js](https://pan.baidu.com/s/13cf3hYTVHeZUySV2_plZFQ)  提取码：rxtd

   ```js
   // RSA公钥
   var rsaPublicKey = RSA公钥;
   // AES密钥(加密前)
   var aesKey;
   // AES密钥(加密后)
   var aesKeyEncrypt;
   //项目地址
   var address = 'http://localhost:9001/';
   /**
     * aes加密
     *@param word：需要加密的内容
     *@returns {*} ：返回加密的内容
     */
   function encryptFun(word) {
       //初始化AES密钥
       aesKey = get32RandomNum();

       //加密AES密钥
       var encrypt = new JSEncrypt();
       encrypt.setPublicKey(rsaPublicKey);
       aesKeyEncrypt = encrypt.encrypt(aesKey);
       // console.log("加密后AES密钥 ===> " + aesKeyEncrypt);

       //AES加密密钥
       var key = CryptoJS.enc.Utf8.parse(aesKey);
       //AES加密内容
       var srcs =CryptoJS.enc.Utf8.parse(word);
       ////AES加密
       var encrypted =CryptoJS.AES.encrypt(srcs, key, { mode: CryptoJS.mode.ECB, padding:CryptoJS.pad.Pkcs7 });
       //返回
       return encrypted.toString();
   }

   /**
    * 加密参数
    * @param data
    * @returns {string}
    */
   function getEncryptData(data) {
       return JSON.stringify({"data":encryptFun(JSON.stringify(data))});
   }

   /**
    * 获取32位随机码
    * @returns {string}
    */
   function get32RandomNum(){
       var chars = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'];
       var nums="";
       for(var i=0;i<32;i++){
           var id = parseInt(Math.random()*61);
           nums+=chars[id];
       }
       return nums;
   }

   /**
    * ajax请求封装
    * @param url POST请求地址
    * @param data 未加密的请求参数JSON
    * @param success 成功回调
    * @returns {string}
    */
   function encryptAjaxPost(url,data,success) {
       $.ajax({
           url: url,
           contentType: "application/json; charset=utf-8",
           data:getEncryptData(data),
           type: "post",
           async:false,
           beforeSend: function (XMLHttpRequest) {
               XMLHttpRequest.setRequestHeader("token", aesKeyEncrypt);
               // console.log("aesKeyEncrypt====>" + aesKeyEncrypt);

               var authorization = localStorage.getItem("Authorization");
               if (!(authorization || verifyWhiteList(url))){
                   location.href = address;
               }
               XMLHttpRequest.setRequestHeader("Authorization", authorization);
               // XMLHttpRequest.setRequestHeader("Authorization", localStorage.getItem("Authorization"));
               // console.log("Authorization====>" + localStorage.getItem("Authorization"));
           },
           success: function(data) {
               if(data.code == 401){
                   location.href = address;
               }else{
                   success(data);
               }
               // console.info(data);
               // if(data.code == '200'){
               //     var token = data.data.token;
               //     localStorage.setItem("Authorization",data.data.token);
               // }else {
               //     alert(data.message);
               // }
           },
           error : function() {
               console.info("错误");
           }
       });
   }

   /**
    * 封装没有参数的GET请求
    * @param url 请求路径
    * @param success 成功回调
    */
   function ajaxGetNoParam(url,success){
       $.ajax({
           url: url,
           type: "get",
           async:false,
           beforeSend: function (XMLHttpRequest) {
               // XMLHttpRequest.setRequestHeader("token", aesKeyEncrypt);
               var authorization = localStorage.getItem("Authorization");
               if (!(authorization || verifyWhiteList(url))){
                   location.href = address;
               }
               XMLHttpRequest.setRequestHeader("Authorization", authorization);
           },
           success: function(data) {
               if(data.code == 401){
                   location.href = address;
               }else{
                   success(data);
               }
           },
           error : function() {
               console.info("错误");
           }
       });
   }

   /**
    * 判断url是否在验证白名单中
    * @param url
    */
   function verifyWhiteList(url) {
       var whiteList = ["/login"];
       for (var i = 0;i < whiteList.length;i++){
           if(url.indexOf(whiteList[i]) != -1){
               return true;
           }
       }
       return false;
   }

   /**
    * rsa加密
    */
   function rasEncrypt() {
       var publicKey = "替换为Java后台生成的公钥";
       var encrypt = new JSEncrypt();
       encrypt.setPublicKey(publicKey);
       // 这里输出加密后的字符串
       console.log(encrypt.encrypt("你好asd1"));
   }

   /**
     * aes加密
     *@param word：需要加密的内容
     *@returns {*} ：返回加密的内容
     */
   function encrypt(word) {
       varkey = CryptoJS.enc.Utf8.parse("abcdefgabcdefg12");
       var srcs =CryptoJS.enc.Utf8.parse(word);
       var encrypted =CryptoJS.AES.encrypt(srcs, key, { mode: CryptoJS.mode.ECB, padding:CryptoJS.pad.Pkcs7 });
       return encrypted.toString();
   }

   /**
     * aes解密
     * @param word
     * @returns {*}
     */
   function decrypt(word) {
       var key =CryptoJS.enc.Utf8.parse("abcdefgabcdefg12");
       var decrypt =CryptoJS.AES.decrypt(word, key, { mode: CryptoJS.mode.ECB, padding:CryptoJS.pad.Pkcs7 });
       returnCryptoJS.enc.Utf8.stringify(decrypt).toString();
   }
   ```

4. 前端请求

   ```javascript
   $(".tpl-login-btn").click(function () {
                   var username = $("#username").prop("value");
                   var password = $("#password").prop("value");
                   var data = {"userName" : username,"passWord" : password};
                   encryptAjaxPost("/login",data,function(data) {
                       console.info(data);
                       if(data.code == '200'){
                           var token = data.data.token;
                           localStorage.setItem("Authorization",data.data.token);
                           location.href = "/pageView/index";
                           // ajaxGetNoParam("/pageView/index",function (data) {
                           //     console.info("成功");
                           //     // $("#htmlAll").html(data);
                           //
                           // })
                       }else {
                           alert(data.message);
                       }
                   });
               });
   ```

### 后端解密

#### 创建过滤器解密参数

> **注：使用过滤器需要在启动类上加扫描注解指定过滤器路径**

> ```java
> @SpringBootApplication
> @ServletComponentScan(basePackages="com.course.ymx.jwt.security")
> public class JwtApplication {
>     public static void main(String[] args) {
>         SpringApplication.run(JwtApplication.class, args);
>     }
> }
> ```

1. 过滤器：`CustomizeParamsFilter.java`

   ```java
   @Order(2)
   @WebFilter(filterName = "paramsFilter", urlPatterns = "/*", asyncSupported = true)
   public class CustomizeParamsFilter implements Filter {

       private static final Logger LOGGER = LoggerFactory.getLogger(CustomizeParamsFilter.class);

       @Override
       public void init(FilterConfig filterConfig) {
           LOGGER.debug("Filter initialized");
       }

       @Override
       public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
           //参数处理
           HandleParamsDecryptHttpServletRequest handleParamsHttpServletRequest = new HandleParamsDecryptHttpServletRequest((HttpServletRequest) servletRequest);
           filterChain.doFilter(handleParamsHttpServletRequest, servletResponse);
       }

       @Override
       public void destroy() {
           LOGGER.debug("Filter destroy");
       }
   }
   ```

2. 参数处理：`HandleParamsDecryptHttpServletRequest.java`

   ```java
   package com.second.boss.estate.security;

   import com.alibaba.fastjson.JSONObject;
   import com.second.boss.estate.utils.RSA_EntryptUtil;
   import org.apache.commons.lang3.StringUtils;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   import javax.servlet.ReadListener;
   import javax.servlet.ServletInputStream;
   import javax.servlet.http.HttpServletRequest;
   import javax.servlet.http.HttpServletRequestWrapper;
   import java.io.*;
   import java.util.ArrayList;
   import java.util.Collections;
   import java.util.Enumeration;

   public class HandleParamsDecryptHttpServletRequest extends HttpServletRequestWrapper {

       private Logger logger = LoggerFactory.getLogger(HandleParamsDecryptHttpServletRequest.class);
       private byte[] bytes;

       /**
        * 功能描述: <br>
        * 〈POST请求的RequestBody替换〉
        * @author ymx
        * @date 2019/12/11
        */
       public HandleParamsDecryptHttpServletRequest(HttpServletRequest request) {

           super(request);

           try (BufferedInputStream bis = new BufferedInputStream(request.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
               byte[] buffer = new byte[1024];
               int len;
               while ((len = bis.read(buffer)) > 0) {
                   baos.write(buffer, 0, len);
               }
               bytes = baos.toByteArray();
               String body = new String(bytes);
               if (!StringUtils.isEmpty(body)) {
                   JSONObject jsonBody = JSONObject.parseObject(body);
                   if (null != jsonBody) {
                       String aesKey = super.getHeader("token");
                       String dataEncrypt = jsonBody.getString("data");
                       String data = null;
                       JSONObject json = null;
                       try {
                           if (StringUtils.isNotBlank(dataEncrypt) && StringUtils.isNotBlank(aesKey)) {
                               data = RSA_EntryptUtil.decryptionRsaAndAes(dataEncrypt, aesKey);
                           }
                           json = null;
                           if (StringUtils.isNotBlank(data)) {
                               json = JSONObject.parseObject(data);
                           }
                       } catch (Exception e) {
                           json = null;
                       }
                       if (json != null) {
                           body = json.toJSONString();
                       }
                       bytes = body.getBytes();
                   }
               }

               logger.info("body: {}", body);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       /**
        * 功能描述: <br>
        * 〈POST请求的RequestBody替换〉
        * @author ymx
        * @date 2019/12/11
        */
       @Override
       public BufferedReader getReader() {
           return new BufferedReader(new InputStreamReader(this.getInputStream()));
       }

       /**
        * 功能描述: <br>
        * 〈GET 请求替换〉
        * @author ymx
        * @date 2019/12/11
        */
       @Override
       public Enumeration<String> getParameterNames() {
           Enumeration<String> enumeration = super.getParameterNames();
           ArrayList<String> list = Collections.list(enumeration);
           return Collections.enumeration(list);
       }

       /**
        * 功能描述: <br>
        * 〈GET 请求替换〉
        * @author ymx
        * @date 2019/12/11
        */
       @Override
       public String getParameter(String name) {
   //        if ("schoolName".equals(name)) {
   ////            return schoolName;
   ////        }
           return super.getParameter(name);
       }

       /**
        * 功能描述: <br>
        * 〈GET 请求替换〉
        * @author ymx
        * @date 2019/12/11
        */
       @Override
       public String[] getParameterValues(String name) {
   //        if ("schoolName".equals(name)) {
   //            return new String[]{schoolName};
   //        }
           return super.getParameterValues(name);
       }

       /**
        * 功能描述: <br>
        * 〈POST请求的RequestBody替换〉
        * @author ymx
        * @date 2019/12/11
        */
       @Override
       public ServletInputStream getInputStream() {
           final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
           return new ServletInputStream() {
               @Override
               public boolean isFinished() {
                   return false;
               }

               @Override
               public boolean isReady() {
                   return false;
               }

               @Override
               public void setReadListener(ReadListener readListener) {

               }

               @Override
               public int read() {
                   return byteArrayInputStream.read();
               }
           };
       }
   }
   ```

#### 加密工具类

1. RSA加密工具类：`RSA_EntryptUtil.java`

   ```java
   package com.second.boss.estate.utils;

   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   import javax.crypto.BadPaddingException;
   import javax.crypto.Cipher;
   import javax.crypto.IllegalBlockSizeException;
   import javax.crypto.NoSuchPaddingException;
   import java.io.UnsupportedEncodingException;
   import java.security.*;
   import java.security.interfaces.RSAPrivateKey;
   import java.security.interfaces.RSAPublicKey;
   import java.security.spec.InvalidKeySpecException;
   import java.security.spec.PKCS8EncodedKeySpec;
   import java.security.spec.X509EncodedKeySpec;
   import java.util.Base64;
   import java.util.HashMap;
   import java.util.Map;
   import java.util.Objects;

   public class RSA_EntryptUtil {
       //日志对象
       private static final Logger logger = LoggerFactory.getLogger(RSA_EntryptUtil.class);
       //加密方式
       public static final String KEY_ALGORITHM = "RSA";
       /**
        * 貌似默认是RSA/NONE/PKCS1Padding，未验证
        */
       public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
       public static final String PUBLIC_KEY = "publicKey";
       public static final String PRIVATE_KEY = "privateKey";
       //字符集
       private static final String CHARSET = "UTF-8";
       private static Map<String, byte[]> keyMap;

       /**
        * RSA密钥长度必须是64的倍数，在512~65536之间。默认是1024
        */
       public static final int KEY_SIZE = 2048;

       static {
           generateKeyBytes();
       }

       public static void main(String[] args) throws UnsupportedEncodingException {
           // 加密
           PublicKey publicKey = restorePublicKey(keyMap.get(PUBLIC_KEY));
           byte[] encodedText = encodeBytes(publicKey, "123456".getBytes(CHARSET));
           logger.info("RSA 加密结果：【{}】", Base64.getEncoder().encodeToString(encodedText));

           // 解密
           PrivateKey privateKey = restorePrivateKey(keyMap.get(PRIVATE_KEY));
           byte[] decodetext = decodeBytes(privateKey,encodedText);
           logger.info("RSA 解密结果：【{}】", new String(decodetext));

           String encryptString = encryptString("我是谁2312313", PUBLIC_KEY);
           logger.info("RSA 加密结果：【{}】", encryptString);

           String decryptString = decryptString(encryptString, PRIVATE_KEY);
           logger.info("RSA 解密结果：【{}】", decryptString);
           
           //可以生成公钥私钥，并打印
           //jdkRSA();
       }

       /**
        * 给AES密钥解密，再调用AES解密
        * @param encodedText 密文
        * @param aesKeyEncrypt 加密后的AES密钥
        * @return
        */
       public static String decryptionRsaAndAes(String encodedText,String aesKeyEncrypt){
           // 解密
           byte[] decode = Base64.getDecoder().decode("后端私钥");
           PrivateKey privateKey = restorePrivateKey(decode);
           byte[] decodetext = decodeBytes(privateKey, Base64.getDecoder().decode(aesKeyEncrypt));
           //解密后aes密钥
           String aesKey = new String(decodetext);
           String data = AES_DES_EntryptUtil.decryptStringBySecret(encodedText,aesKey);
           return data;
       }

       /**
        * 功能描述: <br>
        * 〈生成密钥对。注意这里是生成密钥对KeyPair，再由密钥对获取公私钥〉
        *
        * @author Blare
        * @date 2019/12/11
        */
       public static Map<String, byte[]> generateKeyBytes() {

           if (null == keyMap) {
               try {
                   KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
                   keyPairGenerator.initialize(KEY_SIZE);
                   KeyPair keyPair = keyPairGenerator.generateKeyPair();
                   RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
                   RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

                   keyMap = new HashMap<>();
                   keyMap.put(PUBLIC_KEY, publicKey.getEncoded());
                   keyMap.put(PRIVATE_KEY, privateKey.getEncoded());
                   return keyMap;
               } catch (NoSuchAlgorithmException e) {
                   e.printStackTrace();
               }
           }
           return null;
       }

       /**
        * 功能描述: <br>
        * 〈还原公钥，X509EncodedKeySpec 用于构建公钥的规范〉
        *
        * @author Blare
        * @date 2019/12/11
        */
       public static PublicKey restorePublicKey(byte[] keyBytes) {
           X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
           try {
               KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
               return factory.generatePublic(x509EncodedKeySpec);
           } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
               e.printStackTrace();
           }
           return null;
       }

       /**
        * 功能描述: <br>
        * 〈还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范〉
        *
        * @author Blare
        * @date 2019/12/11
        */
       public static PrivateKey restorePrivateKey(byte[] keyBytes) {
           PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
           try {
               KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
               return factory.generatePrivate(pkcs8EncodedKeySpec);
           } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
               e.printStackTrace();
           }
           return null;
       }
       
          /**
       * 功能描述: <br>
       * 〈加密〉
       *
       * @author Blare
       * @date 2019/12/11
       */
      public static byte[] encodeBytes(PublicKey key, byte[] plainText) {

          try {
              Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
              cipher.init(Cipher.ENCRYPT_MODE, key);
              return cipher.doFinal(plainText);
          } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
              e.printStackTrace();
          }
          return null;

      }

      /**
       * 功能描述: <br>
       * 〈解密〉
       *
       * @author Blare
       * @date 2019/12/11
       */
      public static byte[] decodeBytes(PrivateKey key, byte[] encodedText) {

          try {
              Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
              cipher.init(Cipher.DECRYPT_MODE, key);
              return cipher.doFinal(encodedText);
          } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
              e.printStackTrace();
          }
          return null;

      }

      /**
       * 功能描述: <br>
       * 〈字符串加密〉
       *
       * @author Blare
       * @date 2019/12/11
       */
      public static String encryptString(String source, String publicKey) {
          try {
              if (null != keyMap) {
                  PublicKey pk = restorePublicKey(keyMap.get(publicKey));
                  byte[] sourceBytes = source.getBytes(CHARSET);
                  byte[] encryptBytes = encodeBytes(pk, sourceBytes);
                  return Base64.getEncoder().encodeToString(encryptBytes);
              }
          } catch (UnsupportedEncodingException e) {
              e.printStackTrace();
          }
          return null;
      }

      /**
       * 功能描述: <br>
       * 〈字符串解密〉
       *
       * @author Blare
       * @date 2019/12/11
       */
      public static String decryptString(String source, String privateKey) {
          if (null != keyMap) {
              PrivateKey pk = restorePrivateKey(keyMap.get(privateKey));
              byte[] sourceBytes = Base64.getDecoder().decode(source);
              byte[] encryptBytes = decodeBytes(pk, sourceBytes);
              return null == encryptBytes ? null : new String(encryptBytes);
          }
          return null;
      }

      public static void jdkRSA() {
          try {
              // 1.初始化发送方密钥
              KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
              keyPairGenerator.initialize(512);
              KeyPair keyPair = keyPairGenerator.generateKeyPair();
              RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
              RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
              System.out.println("Public Key:" + Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded()));
              System.out.println("Private Key:" + Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded()));

              // 2.私钥加密、公钥解密 ---- 加密
              PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
              KeyFactory keyFactory = KeyFactory.getInstance("RSA");
              PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
              Cipher cipher = Cipher.getInstance("RSA");
              cipher.init(Cipher.ENCRYPT_MODE, privateKey);
              byte[] result = cipher.doFinal("rsa test".getBytes());
              System.out.println("私钥加密、公钥解密 ---- 加密:" + Base64.getEncoder().encodeToString(result));

              // 3.私钥加密、公钥解密 ---- 解密
              X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
              keyFactory = KeyFactory.getInstance("RSA");
              PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
              cipher = Cipher.getInstance("RSA");
              cipher.init(Cipher.DECRYPT_MODE, publicKey);
              result = cipher.doFinal(result);
              System.out.println("私钥加密、公钥解密 ---- 解密:" + new String(result));

              // 4.公钥加密、私钥解密 ---- 加密
              X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
              KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");
              PublicKey publicKey2 = keyFactory2.generatePublic(x509EncodedKeySpec2);
              Cipher cipher2 = Cipher.getInstance("RSA");
              cipher2.init(Cipher.ENCRYPT_MODE, publicKey2);
              byte[] result2 = cipher2.doFinal("rsa test".getBytes());
              System.out.println("公钥加密、私钥解密 ---- 加密:" + Base64.getEncoder().encodeToString(result2));

              // 5.私钥解密、公钥加密 ---- 解密
              PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
              KeyFactory keyFactory5 = KeyFactory.getInstance("RSA");
              PrivateKey privateKey5 = keyFactory5.generatePrivate(pkcs8EncodedKeySpec5);
              Cipher cipher5 = Cipher.getInstance("RSA");
              cipher5.init(Cipher.DECRYPT_MODE, privateKey5);
              byte[] result5 = cipher5.doFinal(result2);
              System.out.println("公钥加密、私钥解密 ---- 解密:" + new String(result5));

          } catch (Exception e) {
              e.printStackTrace();
          }
      }

      /**
       * 将二进制转换成16进制
       *
       * @param buf
       * @return
       */
      public static String parseByte2HexStr(byte buf[]) {
          StringBuffer sb = new StringBuffer();
          for (int i = 0; i < buf.length; i++) {
              String hex = Integer.toHexString(buf[i] & 0xFF);
              if (hex.length() == 1) {
                  hex = '0' + hex;
              }
              sb.append(hex.toUpperCase());
          }
          return sb.toString();
      }

      /**
       * 将16进制转换为二进制
       *
       * @param hexStr
       * @return
       */
      public static byte[] parseHexStr2Byte(String hexStr) {
          if (hexStr.length() < 1) {
              return null;
          }
          byte[] result = new byte[hexStr.length() / 2];
          for (int i = 0; i < hexStr.length() / 2; i++) {
              int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
              int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
              result[i] = (byte) (high * 16 + low);
          }
          return result;
      }
   }
   ```


1. AES解密类：`AES_DES_EntryptUtil.java`

   ```java
       package com.second.boss.estate.utils;

       import org.apache.commons.codec.binary.Hex;
       import org.slf4j.Logger;
       import org.slf4j.LoggerFactory;

       import javax.crypto.*;
       import javax.crypto.spec.DESKeySpec;
       import javax.crypto.spec.SecretKeySpec;
       import java.io.UnsupportedEncodingException;
       import java.security.InvalidKeyException;
       import java.security.Key;
       import java.security.NoSuchAlgorithmException;
       import java.security.SecureRandom;
       import java.security.spec.InvalidKeySpecException;
       import java.util.Base64;
       import java.util.Objects;

       /**
        * 〈一句话功能简述〉<br>
        * 〈AES和DES对称加密解密算法实现〉
        *
        * @author ymx
        * @create 2019/12/11
        * @since 1.0.0
        */
       public class AES_DES_EntryptUtil {

           //日志对象
           private static final Logger logger = LoggerFactory.getLogger(AES_DES_EntryptUtil.class);
           //DES算法要有一个随机数源,因为Random是根据时间戳生成的有限随机数,比较容易破解,所以在这里使用SecureRandom
           private static SecureRandom secureRandom = new SecureRandom();
           //自定义密钥
           private static final String DEFAULT_SECRET = "wandersgroup2020";
           //字符集
           private static final String CHARSET = "UTF-8";

           public static void main(String[] args) {
               desTestencrypt("我是谁2312313".getBytes());
               String encrypt = encryptString("我是谁2312313", SYMMETRY_ENCRYPT.AES);
               logger.info(encrypt);
               String decrypt = decryptString(Objects.requireNonNull("3egzvTepb6UdNGPq31H3Vw=="), SYMMETRY_ENCRYPT.AES);
               logger.info(decrypt);
           }

           /**
            * 功能描述: <br>
            * 〈字符串解密〉<密钥参数>
            *
            * @author Blare
            * @date 2019/12/11
            */
           public static String decryptStringBySecret(String source, String secret) {
               try {
                   byte[] sourceBytes = Base64.getDecoder().decode(source);
                   byte[] secretBytes = secret.getBytes(CHARSET);
                   byte[] encryptBytes = decrypt(sourceBytes, secretBytes, SYMMETRY_ENCRYPT.AES);
                   return new String(encryptBytes, CHARSET);
               } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
                   e.printStackTrace();
               }
               return null;
           }

           /**
            * 功能描述: <br>
            * 〈字符串加密〉
            *
            * @author Blare
            * @date 2019/12/11
            */
           public static String encryptString(String source, SYMMETRY_ENCRYPT encryptType) {
               try {
                   byte[] sourceBytes = source.getBytes(CHARSET);
                   byte[] secretBytes = DEFAULT_SECRET.getBytes(CHARSET);
                   byte[] encryptBytes = encrypt(sourceBytes, secretBytes, encryptType);
                   return Base64.getEncoder().encodeToString(encryptBytes);
               } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
                   e.printStackTrace();
               }
               return null;
           }

           /**
            * 功能描述: <br>
            * 〈字符串解密〉
            *
            * @author Blare
            * @date 2019/12/11
            */
           public static String decryptString(String source, SYMMETRY_ENCRYPT encryptType) {
               try {
                   byte[] sourceBytes = Base64.getDecoder().decode(source);
                   byte[] secretBytes = DEFAULT_SECRET.getBytes(CHARSET);
                   byte[] encryptBytes = decrypt(sourceBytes, secretBytes, encryptType);
                   return new String(encryptBytes, CHARSET);
               } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
                   e.printStackTrace();
               }
               return null;
           }

           /**
            * 功能描述: <br>
            * 〈使用原始密钥数据转换为SecretKey对象〉
            *
            * @author Blare
            * @date 2019/12/11
            */
           private static SecretKey getSecretKey(byte[] keyBytes, String type) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
               if ("DES".equals(type)) {
                   // 创建一个密匙工厂
                   SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(type);
                   // 创建一个DESKeySpec对象
                   DESKeySpec dks = new DESKeySpec(keyBytes);
                   // 将DESKeySpec对象转换成SecretKey对象
                   return keyFactory.generateSecret(dks);
               }

               return new SecretKeySpec(keyBytes, type);
           }

           /**
            * 功能描述: <br>
            * 〈DES加密〉
            *
            * @author Blare
            * @date 2019/12/11
            */
           private static byte[] encrypt(byte[] contentArray, byte[] keyArray, SYMMETRY_ENCRYPT encryptType) throws InvalidKeyException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeySpecException {
               return symmetryDE(contentArray, keyArray, Cipher.ENCRYPT_MODE, encryptType);
           }

           /**
            * 功能描述: <br>
            * 〈DES解密〉
            *
            * @author Blare
            * @date 2019/12/11
            */
           private static byte[] decrypt(byte[] encryptArray, byte[] keyArray, SYMMETRY_ENCRYPT encryptType) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
               return symmetryDE(encryptArray, keyArray, Cipher.DECRYPT_MODE, encryptType);
           }

           /**
            * 功能描述: <br>
            * 〈Cipher 加密解密操作〉
            *
            * @author Blare
            * @date 2019/12/11
            */
           private static byte[] symmetryDE(byte[] contentArray, byte[] keyArray, int mode, SYMMETRY_ENCRYPT encryptType) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
               //获取密钥对象
               SecretKey secretKey = getSecretKey(keyArray, encryptType.getType());
               //获取真正执行加/解密操作的Cipher
               Cipher cipher = Cipher.getInstance(encryptType.getEncrypt());
               //用密匙初始化Cipher对象
               cipher.init(mode, secretKey, secureRandom);
               //执行加/解密操作
               return cipher.doFinal(contentArray);
           }

           /**
            * 功能描述: <br>
            * 〈加密解密〉
            *
            * @author Blare
            * @date 2019/12/11
            */
           public static void desTestencrypt(byte[] src) {

               try {
                   //生成key
                   KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
                   keyGenerator.init(56);
                   SecretKey secretKey = keyGenerator.generateKey();
                   byte[] encoded = secretKey.getEncoded();
                   logger.info("密钥【{}】", Hex.encodeHexString(encoded));
                   //key转换
                   Key key = new SecretKeySpec(encoded, "DES");
                   logger.info("生成的key：【{}】", key);
                   //加密
                   Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                   cipher.init(Cipher.ENCRYPT_MODE, key);
                   byte[] bytes = cipher.doFinal(src);
                   logger.info("JDK自带DES加密结果：【{}】", Hex.encodeHexString(bytes));
                   logger.info("JDK自带DES加密结果：【{}】", Base64.getEncoder().encodeToString(bytes));
                   cipher.init(Cipher.DECRYPT_MODE, key);
                   bytes = cipher.doFinal(bytes);
                   logger.info("JDK自带DES解密结果：【{}】", new String(bytes));
               } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                   e.printStackTrace();
               }
           }

           /**
            * 功能描述: <br>
            * 〈加密方式〉
            *
            * @author Blare
            * @date 2019/12/11
            */
           enum SYMMETRY_ENCRYPT {
               AES("AES", "AES/ECB/PKCS5Padding"),
               DES("DES", "DES/ECB/PKCS5Padding");

               SYMMETRY_ENCRYPT(String type, String encrypt) {
                   this.type = type;
                   this.encrypt = encrypt;
               }

               private String type;
               private String encrypt;

               public String getType() {
                   return type;
               }

               public String getEncrypt() {
                   return encrypt;
               }
           }
       }
   ```

#### 结构

1. 过滤器位置，如下：

   ![项目结构](springboot+jwt\img\1579242886.png)

## 4.添加Mybatis数据连接

### 添加POM依赖

```xml
<!--mybatis-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.2</version>
        </dependency>
        <!--mapper-->
        <dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper-spring-boot-starter</artifactId>
            <version>2.0.4</version>
            <exclusions>
                <exclusion>
                    <groupId>javax.persistence</groupId>
                    <artifactId>persistence-api</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!--pagehelper-->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.2.7</version>
            <exclusions>
                <exclusion>
                    <groupId>org.mybatis.spring.boot</groupId>
                    <artifactId>mybatis-spring-boot-starter</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

### 创建根级Mapper接口

```java
package com.course.ymx.jwt.common.base;


import tk.mybatis.mapper.common.*;
import tk.mybatis.mapper.common.special.InsertListMapper;
import java.io.Serializable;

/**
 * BaseMapper接口：使mapper包含完整的CRUD方法<br>
 * MySqlMapper接口：使mapper支持MySQL特有的批量插入和返回自增字段<br>
 * IdsMapper接口：使mapper支持批量ID操作<br>
 * ConditionMapper接口：使mapper支持Condition类型参数<br>
 * ExampleMapper
 * InsertListMapper 批量插入，支持批量插入的数据库可以使用，例如MySQL,H2等，另外该接口限制实体包含id属性并且必须为自增列
 * @param <T> 实体类.class
 */
public interface MyMapper<T, PK extends Serializable> extends BaseMapper<T>,
        MySqlMapper<T>,
        IdsMapper<T>,
        ConditionMapper<T>,
        ExampleMapper<T>,
        InsertListMapper<T> {
}

```

### 创建Mapper层接口

```java
package com.course.ymx.jwt.mapper;

import com.course.ymx.jwt.common.base.MyMapper;
import com.course.ymx.jwt.entity.Test;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMapper extends MyMapper<Test,String> {
}

```

### 启动类上添加包扫描和开启注解事务

```java
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.course.ymx.jwt.mapper")
public class JwtApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtApplication.class, args);
    }
}
```



# 结束

[点击获取参考项目源码地址](springboot+jwt\code\最终项目代码)

