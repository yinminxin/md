# Springboot配置AOP

## 自定义注解类

```java
package com.course.ymx.jwt.aop;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogInfo {

    //方法说明
    String info();
}

```

## AOP配置类

```java
package com.course.ymx.jwt.aop;

import com.alibaba.fastjson.JSON;
import com.course.ymx.jwt.common.result.ResponseVO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author yinminxin
 * @description AOP配置类
 * @date 2020/5/27 16:02
 */
@Aspect
@Component
public class AopAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopAspect.class);

    /**
     * 切点-针对类
     * @param logInfo 注解参数
     */
//    @Pointcut("execution(public * com.course.ymx.jwt.controller.*.*(..))")
    @Pointcut("@within(logInfo)")
    public void classPointCut(LogInfo logInfo){

    }

    /**
     * 切点-针对方法
     * @param logInfo 注解参数
     */
    @Pointcut("@annotation(logInfo)")
    public void methodPointCut(LogInfo logInfo) {
    }

    /**
     * @description  使用环绕通知--针对类
     */
    @Around(value = "classPointCut(logInfo)", argNames = "pjp,logInfo")
    public Object doAroundClass(ProceedingJoinPoint pjp, LogInfo logInfo) throws Throwable {
        try{
            LOGGER.info("前置******************进入Aop类业务之前！");
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            //目标方法实体
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            Object defaultValue = method.getDefaultValue();
            boolean annotationPresent = method.isAnnotationPresent(LogInfo.class);
            if (annotationPresent) {
                // 记录下请求内容
                LOGGER.info("请求地址-URL : " + request.getRequestURL().toString());
                LOGGER.info("请求类型-HTTP_METHOD_TYPE : " + request.getMethod());
                LOGGER.info("客户端-IP : " + request.getRemoteAddr());
                LOGGER.info("方法全路径-CLASS_METHOD : " + pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName());
                LOGGER.info("参数-ARGS : " + Arrays.toString(pjp.getArgs()));
                LOGGER.info("注解信息-LOGINFO : " + logInfo.info());
            }
            Object result = pjp.proceed();
            LOGGER.info("后置******************Aop类业务之后！");
            LOGGER.info("后置******************Aop类业务之后====result:" + JSON.toJSONString(result));
            return result;
        }
        catch(Throwable e){
            System.out.println("异常通知：Aop类业务异常！");
        }
        return null;
    }


    /**
     * @description  使用环绕通知--针对方法
     */
    @Around(value = "methodPointCut(logInfo)", argNames = "pjp,logInfo")
    public Object doAroundMethod(ProceedingJoinPoint pjp, LogInfo logInfo) throws Throwable {
        try{
            LOGGER.info("前置******************进入Aop方法业务之前！");
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            //目标方法实体
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            Object defaultValue = method.getDefaultValue();
            boolean annotationPresent = method.isAnnotationPresent(LogInfo.class);
            if (annotationPresent) {
                // 记录下请求内容
                LOGGER.info("请求地址-URL : " + request.getRequestURL().toString());
                LOGGER.info("请求类型-HTTP_METHOD_TYPE : " + request.getMethod());
                LOGGER.info("客户端-IP : " + request.getRemoteAddr());
                LOGGER.info("方法全路径-CLASS_METHOD : " + pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName());
                LOGGER.info("参数-ARGS : " + Arrays.toString(pjp.getArgs()));
                LOGGER.info("注解信息-LOGINFO : " + logInfo.info());
            }
            Object result = pjp.proceed();
            if (annotationPresent) {
                LOGGER.info("后置******************Aop方法业务之后！");
                LOGGER.info("后置******************Aop方法业务之后====result:" + JSON.toJSONString(result));
            }
            return result;
        }
        catch(Throwable e){
            System.out.println("异常通知：Aop方法业务异常！");
            return new ResponseVO("500","Aop业务异常！");
        }
    }

    //前置方法
    @Before(value = "methodPointCut(logInfo))", argNames = "joinPoint,logInfo")
    public void doMethodBefore(JoinPoint joinPoint, LogInfo logInfo) {
        LOGGER.info("单独前置方法Before******************进入Aop方法业务之前！");
    }

    //前置类
    @Before(value = "classPointCut(logInfo))", argNames = "joinPoint,logInfo")
    public void doClassBefore(JoinPoint joinPoint, LogInfo logInfo) {
        LOGGER.info("单独前置类Before******************进入Aop方法业务之前！");
    }

    //后置方法
    @After(value = "methodPointCut(logInfo))", argNames = "joinPoint,logInfo")
    public void doMethodAfter(JoinPoint joinPoint, LogInfo logInfo) {
        LOGGER.info("单独后置方法After******************进入Aop方法业务之后！");
    }

    //后置类
    @After(value = "classPointCut(logInfo))", argNames = "joinPoint,logInfo")
    public void doClassAfter(JoinPoint joinPoint, LogInfo logInfo) {
        LOGGER.info("单独后置类After******************进入Aop方法业务之后！");
    }

    //异常方法
    @AfterThrowing(value = "methodPointCut(logInfo)", throwing = "ex", argNames = "joinPoint,logInfo,ex")
    public void doMethodAfterThrowing(JoinPoint joinPoint,  LogInfo logInfo, Throwable ex) {
        LOGGER.info("单独异常方法AfterThrowing******************Aop方法业务异常！");
        LOGGER.info("单独异常方法AfterThrowing******************logInfo:" + logInfo.info());
        LOGGER.info("单独异常方法AfterThrowing******************Throwable！" +ex.toString());
    }

    //异常类
    @AfterThrowing(value = "classPointCut(logInfo)", throwing = "ex", argNames = "joinPoint,logInfo,ex")
    public void doClassAfterThrowing(JoinPoint joinPoint,  LogInfo logInfo, Throwable ex) {
        LOGGER.info("单独异常类AfterThrowing******************Aop类业务异常！");
        LOGGER.info("单独异常类AfterThrowing******************logInfo:" + logInfo.info());
        LOGGER.info("单独异常类AfterThrowing******************Throwable！" +ex.toString());
    }
}

```

## 结果

### 正常结果 : 

```
前置******************进入Aop类业务之前！          ------类环绕前置
请求地址-URL : http://localhost:9002/aop/testAop
请求类型-HTTP_METHOD_TYPE : GET
客户端-IP : 0:0:0:0:0:0:0:1
方法全路径-CLASS_METHOD : com.course.ymx.jwt.controller.AopController.testAop
参数-ARGS : []
注解信息-LOGINFO : testAop类信息!
前置******************进入Aop方法业务之前！         ------方法环绕前置
请求地址-URL : http://localhost:9002/aop/testAop
请求类型-HTTP_METHOD_TYPE : GET
客户端-IP : 0:0:0:0:0:0:0:1
方法全路径-CLASS_METHOD : com.course.ymx.jwt.controller.AopController.testAop
参数-ARGS : []
注解信息-LOGINFO : testAop方法信息!
单独前置类Before******************进入Aop方法业务之前！ ------类单独前置
单独前置方法Before******************进入Aop方法业务之前！ ------方法单独前置
testAop,开始ing...                                      ------切点方法运行
后置******************Aop方法业务之后！                   ------方法环绕后置
后置******************Aop方法业务之后====result:{"code":"200","message":"successed","success":true}
后置******************Aop类业务之后！                          ------类环绕后置
后置******************Aop类业务之后====result:{"code":"200","message":"successed","success":true}
单独后置类After******************进入Aop方法业务之后！             ------类单独后置
单独后置方法After******************进入Aop方法业务之后！            ------方法单独后置
```

### 异常结果 :

> **注 : 当有环绕异常时,单独异常不执行**

#### 环绕异常

```
前置******************进入Aop类业务之前！                    ------类环绕前置
请求地址-URL : http://localhost:9002/aop/testAop
请求类型-HTTP_METHOD_TYPE : GET
客户端-IP : 0:0:0:0:0:0:0:1
方法全路径-CLASS_METHOD : com.course.ymx.jwt.controller.AopController.testAop
参数-ARGS : []
注解信息-LOGINFO : testAop类信息!
前置******************进入Aop方法业务之前！                 ------方法环绕前置
请求地址-URL : http://localhost:9002/aop/testAop
请求类型-HTTP_METHOD_TYPE : GET
客户端-IP : 0:0:0:0:0:0:0:1
方法全路径-CLASS_METHOD : com.course.ymx.jwt.controller.AopController.testAop
参数-ARGS : []
注解信息-LOGINFO : testAop方法信息!
单独前置类Before******************进入Aop方法业务之前！     ------类单独前置
单独前置方法Before******************进入Aop方法业务之前！   ------方法单独前置
异常通知：Aop方法业务异常！                                 ------环绕方法异常
后置******************Aop类业务之后！                       ------类环绕后置
后置******************Aop类业务之后====result:{"code":"500","message":"Aop业务异常！","success":false}
单独后置类After******************进入Aop方法业务之后！        ------类单独后置
单独后置方法After******************进入Aop方法业务之后！     ------方法单独后置
```

#### 单独异常

```
前置******************进入Aop类业务之前！            ------类环绕前置
请求地址-URL : http://localhost:9002/aop/testAop
请求类型-HTTP_METHOD_TYPE : GET
客户端-IP : 0:0:0:0:0:0:0:1
方法全路径-CLASS_METHOD : com.course.ymx.jwt.controller.AopController.testAop
参数-ARGS : []
注解信息-LOGINFO : testAop类信息!
前置******************进入Aop方法业务之前！            ------方法环绕前置
请求地址-URL : http://localhost:9002/aop/testAop
请求类型-HTTP_METHOD_TYPE : GET
客户端-IP : 0:0:0:0:0:0:0:1
方法全路径-CLASS_METHOD : com.course.ymx.jwt.controller.AopController.testAop
参数-ARGS : []
注解信息-LOGINFO : testAop方法信息!
单独前置类Before******************进入Aop方法业务之前！        ------类单独前置
单独前置方法Before******************进入Aop方法业务之前！         ------方法单独前置
HikariPool-1 - Pool stats (total=10, active=0, idle=10, waiting=0)
单独后置类After******************进入Aop方法业务之后！           ------类单独后置
单独后置方法After******************进入Aop方法业务之后！          ------方法单独后置
单独异常类AfterThrowing******************Aop类业务异常！          ------单独类异常
单独异常类AfterThrowing******************logInfo:testAop类信息!
单独异常类AfterThrowing******************Throwable！java.lang.ArithmeticException: / by zero
单独异常方法AfterThrowing******************Aop方法业务异常！      ------单独类异常
单独异常方法AfterThrowing******************logInfo:testAop方法信息!
单独异常方法AfterThrowing******************Throwable！java.lang.ArithmeticException: / by zero
```

