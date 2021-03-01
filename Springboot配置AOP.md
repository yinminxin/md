# Springboot配置AOP

## 依赖

```xml
<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- 或 -->
<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```



## 自定义注解类

> 说明
>
> @Target({ElementType.TYPE, ElementType.METHOD}) ：允许使用的地方；如-ElementType.TYPE==》类；ElementType.METHOD==》方法
>
> @Retention(RetentionPolicy.RUNTIME)：注解保留在程序运行期间，此时可以通过反射获得定义在某个类上的所有注解
>
> @Inherited：当@Inherited修饰过的注解加在某个类A上时，假如类B继承了A，则B也会带上该注解。加在接口上无效

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

## 切点表达式

### 通配符

1. `*`通配符

   该通配符主要用于匹配单个单词，或者是以某个词为前缀或后缀的单词

   如下示例表示返回值为任意类型，在com.spring.service.BusinessObject类中，并且参数个数为零的方法：

   ```java
   execution(* com.spring.service.BusinessObject.*())
   ```

   下述示例表示返回值为任意类型，在com.spring.service包中，以Business为前缀的类，并且是类中参数个数为零方法：

   ```java
   execution(* com.spring.service.Business*.*())
   ```

2. `..`通配符

   该通配符表示0个或多个项，主要用于declaring-type-pattern和param-pattern中，如果用于declaring-type-pattern中，则表示匹配当前包及其子包，如果用于param-pattern中，则表示匹配0个或多个参数。

   如下示例表示匹配返回值为任意类型，并且是com.spring.service包及其子包下的任意类的名称为businessService的方法，而且该方法不能有任何参数：

   ```java
   execution(* com.spring.service..*.businessService())
   ```

   这里需要说明的是，包路径`service..*.businessService()`中的`..`应该理解为延续前面的service路径，表示到service路径为止，或者继续延续service路径，从而包括其子包路径；后面的`*.businessService()`，这里的`*`表示匹配一个单词，因为是在方法名前，因而表示匹配任意的类。

   如下示例是使用`..`表示任意个数的参数的示例，需要注意，表示参数的时候可以在括号中事先指定某些类型的参数，而其余的参数则由`..`进行匹配：

   ```java
   execution(* com.spring.service.BusinessObject.businessService(java.lang.String,..))
   ```

### execution

- execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)

- 这里问号表示当前项可以有也可以没有，其中各项的语义如下：

  - modifiers-pattern：方法的可见性，如public，protected；
  - ret-type-pattern：方法的返回值类型，如int，void等；
  - declaring-type-pattern：方法所在类的全路径名，如com.spring.Aspect；
  - name-pattern：方法名类型，如buisinessService()；
  - param-pattern：方法的参数类型，如java.lang.String；
  - throws-pattern：方法抛出的异常类型，如java.lang.Exception；

- 例子

  ```java
  // controller包下的所有类的所有方法
  @Pointcut("execution(public * com.course.ymx.jwt.controller.*.*(..))")
  ```

### annotation

- @annotation的使用方式与@within的相似，表示匹配使用@annotation指定注解标注的方法将会被环绕，其使用语法如下：

  ```java
  @annotation(annotation-type)
  ```

- 如下示例表示匹配使用com.spring.annotation.BusinessAspect注解标注的方法：

  ```java
  @annotation(com.spring.annotation.BusinessAspect)
  ```

- 这里我们继续复用`5`节使用的例子进行讲解`@annotation`的用法，只是这里需要对Apple和MyAspect使用和指定注解的方式进行修改，FruitAspect不用修改的原因是声明该注解时已经指定了其可以使用在类，方法和参数上：

  ```java
  // 目标类，将FruitAspect移到了方法上
  public class Apple {
    @FruitAspect
    public void eat() {
      System.out.println("Apple.eat method invoked.");
    }
  }
  ```

  ```java
  @Aspect
  public class MyAspect {
    @Around("@annotation(com.business.annotation.FruitAspect)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
      System.out.println("this is before around advice");
      Object result = pjp.proceed();
      System.out.println("this is after around advice");
      return result;
    }
  }
  ```

- 这里Apple.eat()方法使用FruitAspect注解进行了标注，因而该方法的执行会被切面环绕，其执行结果如下：

  ```
  this is before around advice
  Apple.eat method invoked.
  this is after around advice
  ```

### within

1. **指定类**

   - within表达式的粒度为类，其参数为全路径的类名（可使用通配符），表示匹配当前表达式的所有类都将被当前方法环绕。如下是within表达式的语法：

     ```
     within(declaring-type-pattern)
     ```

   - within表达式只能指定到类级别，如下示例表示匹配`com.spring.service.BusinessObject`中的所有方法：

     ```
     within(com.spring.service.BusinessObject)
     ```

   - within表达式路径和类名都可以使用通配符进行匹配，比如如下表达式将匹配com.spring.service包下的所有类，不包括子包中的类：

     ```
     within(com.spring.service.*)
     ```

   - 如下表达式表示匹配com.spring.service包及子包下的所有类：

     ```
     within(com.spring.service..*)
     ```

2. **指定注解的类**

   - 前面我们讲解了within的语义表示匹配指定类型的类实例，这里的@within表示匹配带有指定注解的类，其使用语法如下所示：

     ```
     @within(annotation-type)
     ```

   - 如下所示示例表示匹配使用`com.spring.annotation.BusinessAspect`注解标注的类：

     ```
     @within(com.spring.annotation.BusinessAspect)
     ```

   - 这里我们使用一个例子演示`@within`的用法（这里驱动类和xml文件配置与`5`节使用的一致，这里省略）：

     ```java
     // 注解类
     @Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
     @Retention(RetentionPolicy.RUNTIME)
     public @interface FruitAspect {
     }
     ```

     ```java
     // 目标类
     @FruitAspect
     public class Apple {
       public void eat() {
         System.out.println("Apple.eat method invoked.");
       }
     }
     ```

     ```java
     // 切面类
     @Aspect
     public class MyAspect {
       @Around("@within(com.business.annotation.FruitAspect)")
       public Object around(ProceedingJoinPoint pjp) throws Throwable {
         System.out.println("this is before around advice");
         Object result = pjp.proceed();
         System.out.println("this is after around advice");
         return result;
       }
     }
     ```

   - 上述切面表示匹配使用FruitAspect注解的类，而Apple则使用了该注解，因而Apple类方法的调用会被切面环绕，执行运行驱动类可得到如下结果，说明Apple.eat()方法确实被环绕了：

     ```
     this is before around advice
     Apple.eat method invoked.
     this is after around advice
     ```

### args

1. 针对指定参数类型

   - args表达式的作用是匹配指定参数类型和指定参数数量的方法，无论其类路径或者是方法名是什么。这里需要注意的是，args指定的参数必须是全路径的。如下是args表达式的语法：

     ```
     args(param-pattern)
     ```

   - 如下示例表示匹配所有只有一个参数，并且参数类型是java.lang.String类型的方法：

     ```
     args(java.lang.String)
     ```

   - 也可以使用通配符，但这里通配符只能使用..，而不能使用*。如下是使用通配符的实例，该切点表达式将匹配第一个参数为java.lang.String，最后一个参数为java.lang.Integer，并且中间可以有任意个数和类型参数的方法：

     ```
     args(java.lang.String,..,java.lang.Integer)
     ```

2. 针对注解

   - @within和@annotation分别表示匹配使用指定注解标注的类和标注的方法将会被匹配，@args则表示使用指定注解标注的类作为某个方法的参数时该方法将会被匹配。如下是@args注解的语法：

     ```
     @args(annotation-type)
     ```

   - 如下示例表示匹配使用了com.spring.annotation.FruitAspect注解标注的类作为参数的方法：

     ```
     @args(com.spring.annotation.FruitAspect)
     ```

   - 这里我们使用如下示例对@args的用法进行讲解：

     ```xml
     <!-- xml配置文件 -->
     <bean id="bucket" class="chapter7.eg1.FruitBucket"/>
     <bean id="aspect" class="chapter7.eg6.MyAspect"/>
     <aop:aspectj-autoproxy/>
     ```

     ```java
     // 使用注解标注的参数类
     @FruitAspect
     public class Apple {}
     // 使用Apple参数的目标类
     public class FruitBucket {
       public void putIntoBucket(Apple apple) {
         System.out.println("put apple into bucket.");
       }
     }
     ```

     ```java
     @Aspect
     public class MyAspect {
       @Around("@args(chapter7.eg6.FruitAspect)")
       public Object around(ProceedingJoinPoint pjp) throws Throwable {
         System.out.println("this is before around advice");
         Object result = pjp.proceed();
         System.out.println("this is after around advice");
         return result;
       }
     }
     ```

     ```java
     // 驱动类
     public class AspectApp {
       public static void main(String[] args) {
         ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
         FruitBucket bucket = (FruitBucket) context.getBean("bucket");
         bucket.putIntoBucket(new Apple());
       }
     }
     ```

   - 这里FruitBucket.putIntoBucket(Apple)方法的参数Apple使用了@args注解指定的FruitAspect进行了标注，因而该方法的调用将会被环绕。执行驱动类，结果如下：

     ```
     this is before around advice
     put apple into bucket.
     this is after around advice
     ```

### this和target

- this和target需要放在一起进行讲解，主要目的是对其进行区别。this和target表达式中都只能指定类或者接口，在面向切面编程规范中，this表示匹配调用当前切点表达式所指代对象方法的对象，target表示匹配切点表达式指定类型的对象。比如有两个类A和B，并且A调用了B的某个方法，如果切点表达式为this(B)，那么A的实例将会被匹配，也即其会被使用当前切点表达式的Advice环绕；如果这里切点表达式为target(B)，那么B的实例也即被匹配，其将会被使用当前切点表达式的Advice环绕。

- 在讲解Spring中的this和target的使用之前，首先需要讲解一个概念：业务对象（目标对象）和代理对象。对于切面编程，有一个目标对象，也有一个代理对象，目标对象是我们声明的业务逻辑对象，而代理对象是使用切面逻辑对业务逻辑进行包裹之后生成的对象。如果使用的是Jdk动态代理，那么业务对象和代理对象将是两个对象，在调用代理对象逻辑时，其切面逻辑中会调用目标对象的逻辑；如果使用的是Cglib代理，由于是使用的子类进行切面逻辑织入的，那么只有一个对象，即织入了代理逻辑的业务类的子类对象，此时是不会生成业务类的对象的。

- 在Spring中，其对this的语义进行了改写，即如果当前对象生成的代理对象符合this指定的类型，那么就为其织入切面逻辑。简单的说就是，this将匹配代理对象为指定类型的类。target的语义则没有发生变化，即其将匹配业务对象为指定类型的类。如下是使用this和target表达式的简单示例：

  ```
  this(com.spring.service.BusinessObject)
  target(com.spring.service.BusinessObject)
  ```

- 通过上面的讲解可以看出，this和target的使用区别其实不大，大部分情况下其使用效果是一样的，但其区别也还是有的。Spring使用的代理方式主要有两种：Jdk代理和Cglib代理（关于这两种代理方式的讲解可以查看的文章[代理模式实现方式及优缺点对比](https://my.oschina.net/zhangxufeng/blog/1633187)）。针对这两种代理类型，关于目标对象与代理对象，理解如下两点是非常重要的：

- 如果目标对象被代理的方法是其实现的某个接口的方法，那么将会使用Jdk代理生成代理对象，此时代理对象和目标对象是两个对象，并且都实现了该接口；
- 如果目标对象是一个类，并且其没有实现任意接口，那么将会使用Cglib代理生成代理对象，并且只会生成一个对象，即Cglib生成的代理类的对象。

​    结合上述两点说明，这里理解this和target的异同就相对比较简单了。我们这里分三种情况进行说明：

- this(SomeInterface)或target(SomeInterface)：这种情况下，无论是对于Jdk代理还是Cglib代理，其目标对象和代理对象都是实现SomeInterface接口的（Cglib生成的目标对象的子类也是实现了SomeInterface接口的），因而this和target语义都是符合的，此时这两个表达式的效果一样；
- this(SomeObject)或target(SomeObject)，这里SomeObject没实现任何接口：这种情况下，Spring会使用Cglib代理生成SomeObject的代理类对象，由于代理类是SomeObject的子类，子类的对象也是符合SomeObject类型的，因而this将会被匹配，而对于target，由于目标对象本身就是SomeObject类型，因而这两个表达式的效果一样；
- this(SomeObject)或target(SomeObject)，这里SomeObject实现了某个接口：对于这种情况，虽然表达式中指定的是一种具体的对象类型，但由于其实现了某个接口，因而Spring默认会使用Jdk代理为其生成代理对象，Jdk代理生成的代理对象与目标对象实现的是同一个接口，但代理对象与目标对象还是不同的对象，由于代理对象不是SomeObject类型的，因而此时是不符合this语义的，而由于目标对象就是SomeObject类型，因而target语义是符合的，此时this和target的效果就产生了区别；这里如果强制Spring使用Cglib代理，因而生成的代理对象都是SomeObject子类的对象，其是SomeObject类型的，因而this和target的语义都符合，其效果就是一致的。

- 关于this和target的异同，我们使用如下示例进行简单演示：

  ```java
  // 目标类
  public class Apple {
    public void eat() {
      System.out.println("Apple.eat method invoked.");
    }
  }
  ```

  ```java
  // 切面类
  @Aspect
  public class MyAspect {
    @Around("this(com.business.Apple)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
      System.out.println("this is before around advice");
      Object result = pjp.proceed();
      System.out.println("this is after around advice");
      return result;
    }
  }
  ```

  ```xml
  <!-- bean声明文件 -->
  <bean id="apple" class="chapter7.eg1.Apple"/>
  <bean id="aspect" class="chapter7.eg6.MyAspect"/>
  <aop:aspectj-autoproxy/>
  ```

  ```java
  // 驱动类
  public class AspectApp {
    public static void main(String[] args) {
      ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      Apple fruit = (Apple) context.getBean("apple");
      fruit.eat();
    }
  }
  ```

- 执行驱动类中的main方法，结果如下：

  ```
  this is before around advice
  Apple.eat method invoked.
  this is after around advice
  ```

- 上述示例中，Apple没有实现任何接口，因而使用的是Cglib代理，this表达式会匹配Apple对象。这里将切点表达式更改为target，还是执行上述代码，会发现结果还是一样的：

  ```
  target(com.business.Apple)
  ```

- 如果我们对Apple的声明进行修改，使其实现一个接口，那么这里就会显示出this和target的执行区别了：

  ```java
  public class Apple implements IApple {
    public void eat() {
      System.out.println("Apple.eat method invoked.");
    }
  }
  public class AspectApp {
    public static void main(String[] args) {
      ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      Fruit fruit = (Fruit) context.getBean("apple");
      fruit.eat();
    }
  }
  ```

- 我们还是执行上述代码，对于this表达式，其执行结果如下：

  ```
  Apple.eat method invoked.
  ```

- 对于target表达式，其执行结果如下：可以看到，这种情况下this和target表达式的执行结果是不一样的，这正好符合我们前面讲解的第三种情况。

  ```
  this is before around advice
  Apple.eat method invoked.
  this is after around advice
  ```

### DeclareParents

- @DeclareParents也称为Introduction（引入），表示为指定的目标类引入新的属性和方法。关于@DeclareParents的原理其实比较好理解，因为无论是Jdk代理还是Cglib代理，想要引入新的方法，只需要通过一定的方式将新声明的方法织入到代理类中即可，因为代理类都是新生成的类，因而织入过程也比较方便。如下是@DeclareParents的使用语法：

  ```java
  @DeclareParents(value = "TargetType", defaultImpl = WeaverType.class)
  private WeaverInterface attribute;
  ```

- 这里TargetType表示要织入的目标类型（带全路径），WeaverInterface中声明了要添加的方法，WeaverType中声明了要织入的方法的具体实现。如下示例表示在Apple类中织入IDescriber接口声明的方法：

  ```java
  @DeclareParents(value = "com.spring.service.Apple", defaultImpl = DescriberImpl.class)
  private IDescriber describer;
  ```

- 这里我们使用一个如下实例对`@DeclareParents`的使用方式进行讲解，配置文件与3.4节的一致，这里略：

  ```java
  // 织入方法的目标类
  public class Apple {
    public void eat() {
      System.out.println("Apple.eat method invoked.");
    }
  }
  // 要织入的接口
  public interface IDescriber {
    void desc();
  }
  // 要织入接口的默认实现
  public class DescriberImpl implements IDescriber {
    @Override
    public void desc() {
      System.out.println("this is an introduction describer.");
    }
  }
  // 切面实例
  @Aspect
  public class MyAspect {
    @DeclareParents(value = "com.spring.service.Apple", defaultImpl = DescriberImpl.class)
    private IDescriber describer;
  }
  // 驱动类
  public class AspectApp {
    public static void main(String[] args) {
      ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      IDescriber describer = (IDescriber) context.getBean("apple");
      describer.desc();
    }
  }
  ```

- 在MyAspect中声明了我们需要将IDescriber的方法织入到Apple实例中，在驱动类中我们可以看到，我们获取的是apple实例，但是得到的bean却可以强转为IDescriber类型，因而说明我们的织入操作成功了。

### perthis和pertarget

- 在Spring AOP中，切面类的实例只有一个，比如前面我们一直使用的MyAspect类，假设我们使用的切面类需要具有某种状态，以适用某些特殊情况的使用，比如多线程环境，此时单例的切面类就不符合我们的要求了。在Spring AOP中，切面类默认都是单例的，但其还支持另外两种多例的切面实例的切面，即perthis和pertarget，需要注意的是perthis和pertarget都是使用在切面类的@Aspect注解中的。这里perthis和pertarget表达式中都是指定一个切面表达式，其语义与前面讲解的this和target非常的相似，perthis表示如果某个类的代理类符合其指定的切面表达式，那么就会为每个符合条件的目标类都声明一个切面实例；pertarget表示如果某个目标类符合其指定的切面表达式，那么就会为每个符合条件的类声明一个切面实例。从上面的语义可以看出，perthis和pertarget的含义是非常相似的。如下是perthis和pertarget的使用语法：

  ```java
  perthis(pointcut-expression)
  pertarget(pointcut-expression)
  ```

- 由于perthis和pertarget的使用效果大部分情况下都是一致的，我们这里主要讲解perthis和pertarget的区别。关于perthis和pertarget的使用，需要注意的一个点是，由于perthis和pertarget都是为每个符合条件的类声明一个切面实例，因而切面类在配置文件中的声明上一定要加上prototype，否则Spring启动是会报错的。如下是我们使用的示例：

  ```xml
  <!-- xml配置文件 -->
  <bean id="apple" class="chapter7.eg1.Apple"/>
  <bean id="aspect" class="chapter7.eg6.MyAspect" scope="prototype"/>
  <aop:aspectj-autoproxy/>
  ```

  ```java
  // 目标类实现的接口
  public interface Fruit {
    void eat();
  }
  ```

  ```java
  // 业务类
  public class Apple implements Fruit {
    public void eat() {
      System.out.println("Apple.eat method invoked.");
    }
  }
  ```

  ```java
  // 切面类
  @Aspect("perthis(this(com.spring.service.Apple))")
  public class MyAspect {
  
    public MyAspect() {
      System.out.println("create MyAspect instance, address: " + toString());
    }
  
    @Around("this(com.spring.service.Apple)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
      System.out.println("this is before around advice");
      Object result = pjp.proceed();
      System.out.println("this is after around advice");
      return result;
    }
  }
  ```

  ```java
  // 驱动类
  public class AspectApp {
    public static void main(String[] args) {
      ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      Fruit fruit = context.getBean(Fruit.class);
      fruit.eat();
    }
  }
  ```

- 这里我们使用的切面表达式语法为perthis(this(com.spring.service.Apple))，这里this表示匹配代理类是Apple类型的类，perthis则表示会为这些类的每个实例都创建一个切面类。由于Apple实现了Fruit接口，因而Spring使用Jdk动态代理为其生成代理类，也就是说代理类与Apple都实现了Fruit接口，但是代理类不是Apple类型，因而这里声明的切面不会匹配到Apple类。执行上述驱动类，结果如下：

  ```
  Apple.eat method invoked.
  ```

- 结果表明Apple类确实没有被环绕。如果我们讲切面类中的perthis和this修改为pertarget和target，效果如何呢：

  ```java
  @Aspect("pertarget(target(com.spring.service.Apple))")
  public class MyAspect {
  
    public MyAspect() {
      System.out.println("create MyAspect instance, address: " + toString());
    }
  
    @Around("target(com.spring.service.Apple)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
      System.out.println("this is before around advice");
      Object result = pjp.proceed();
      System.out.println("this is after around advice");
      return result;
    }
  }
  ```

- 执行结果如下：

  ```java
  create MyAspect instance, address: chapter7.eg6.MyAspect@48fa0f47
  this is before around advice
  Apple.eat method invoked.
  this is after around advice
  ```

- 可以看到，Apple类被切面环绕了。这里target表示目标类是Apple类型，虽然Spring使用了Jdk动态代理实现切面的环绕，代理类虽不是Apple类型，但是目标类却是Apple类型，符合target的语义，而pertarget会为每个符合条件的表达式的类实例创建一个代理类实例，因而这里Apple会被环绕。

- 由于代理类与目标类的差别非常小，因而与this和target一样，perthis和pertarget的区别也非常小，大部分情况下其使用效果是一致的。关于切面多实例的创建，其演示比较简单，我们可以将xml文件中的Apple实例修改为prototype类型，并且在驱动类中多次获取Apple类的实例：

  ```xml
  <!-- xml配置文件 -->
  <bean id="apple" class="chapter7.eg1.Apple" scope="prototype"/>
  <bean id="aspect" class="chapter7.eg6.MyAspect" scope="prototype"/>
  <aop:aspectj-autoproxy/>
  ```

  ```java
  public class AspectApp {
    public static void main(String[] args) {
      ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
      Fruit fruit = context.getBean(Fruit.class);
      fruit.eat();
      fruit = context.getBean(Fruit.class);
      fruit.eat();
    }
  }
  ```

- 执行结果如下：执行结果中两次打印的create MyAspect instance表示当前切面实例创建了两次，这也符合我们进行的两次获取Apple实例。

  ```
  create MyAspect instance, address: chapter7.eg6.MyAspect@48fa0f47
  this is before around advice
  Apple.eat method invoked.
  this is after around advice
  create MyAspect instance, address: chapter7.eg6.MyAspect@56528192
  this is before around advice
  Apple.eat method invoked.
  this is after around advice
  ```

## 例子

### 注解同时匹配类和方法

> 注：注解在接口上，实现类不会有注解，不进AOP逻辑
>
> @Before(value = "classPointCut() || methodPointCut()", argNames = "joinPoint")

```java
/**
* @Description: 自定义重放攻击注解
* @Author: yinminxin
* @Date: 2021/2/1
*/
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ReplayAttack {

    /**
     * 用户连续访问最高阀值，超过该值则认定为恶意操作的IP，进行限制
     */
    int threshold() default 5;

    /**
     * 用户访问最小安全时间(秒)，在该时间内如果访问次数大于阀值，则记录为恶意IP，否则视为正常访问
     */
    int time() default 5;
}
```

```java
/**
 * @author yinminxin
 * @description AOP配置类
 * @date 2020/5/27 16:02
 */
@Aspect
@Component
public class AopReplayAttack {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopReplayAttack.class);

    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 切点-针对类
     */
    @Pointcut("@within(com.course.ymx.jwt.aop.security.ReplayAttack)")
    public void classPointCut(){
    }

    /**
     * 切点-针对方法
     */
    @Pointcut("@annotation(com.course.ymx.jwt.aop.security.ReplayAttack)")
    public void methodPointCut() {
    }

    /**
     * 前置方法(当类或者方法上有《ReplayAttack》注解时，表示存在重放攻击)
     */
    @Before(value = "classPointCut() || methodPointCut()", argNames = "joinPoint")
    public void doMoreMethodBefore(JoinPoint joinPoint) {
        LOGGER.info("单独前置方法Before******************进入Aop方法业务之前！");

        ReplayAttack replayAttack = hasReplayAttackAnnotation(joinPoint);

        // 获取request请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 获取IP和请求地址
        String ip = request.getRemoteHost();
        StringBuffer url = request.getRequestURL();

        LOGGER.info(url + "====>接口为重放攻击接口");
        // 有注解,获取注解参数
        // 用户连续访问最高阀值，超过该值则认定为恶意操作的IP，进行限制
        int threshold = replayAttack.threshold();
        // 用户访问最小安全时间(秒)，在该时间内如果访问次数大于阀值，则记录为恶意IP，否则视为正常访问
        int time = replayAttack.time();

        // 判断用户是否在黑名单
        if (redisTemplate.hasKey(RedisTag.IP_FORBIDDEN_LIST + ip)) {
            // 在黑名单
            LOGGER.info("当前IP访问过于频繁已被限制;IP ===> " + ip + " | 剩余时间(单位秒) ===> " + redisTemplate.getExpire(RedisTag.IP_FORBIDDEN_LIST + ip));
            throw new DefaultException(ResultCode.FORBIDDEN);
        }
        // 不在黑名单，判断IP访问是否达到限制
        // 获取当前IP日志对象
        List<IpLoggerVo> voList = (List<IpLoggerVo>) redisTemplate.opsForHash().get(RedisTag.IP_LOGGER, ip);
        if (voList !=null) {
            Optional<IpLoggerVo> first = voList.stream().filter(v -> url.toString().equals(v.getUrl())).findFirst();
            if (first.isPresent()) {
                IpLoggerVo vo = first.get();
                List<LocalDateTime> voTime = vo.getTime();
                if (voTime.size() >= threshold && voTime.size() >= threshold && Duration.between(voTime.get(voTime.size() - threshold), LocalDateTime.now()).toMillis() <= time * 1000) {
                    // 当前接口访问次数达到阈值且时间低于最小安全时间, IP加入黑名单
                    redisTemplate.opsForValue().set(RedisTag.IP_FORBIDDEN_LIST + ip, 1, 60, TimeUnit.MINUTES);

                    LOGGER.info("当前IP访问过于频繁已被限制;IP ===> " + ip + " | 剩余时间(单位秒) ===> " + redisTemplate.getExpire(RedisTag.IP_FORBIDDEN_LIST + ip));
                    throw new DefaultException(ResultCode.FORBIDDEN);
                }else {
                    LOGGER.info("当前IP第" + voTime.size() + "次访问此接口：IP ===> " + ip + " | 接口 ===> " + url);
                    voTime.add(LocalDateTime.now());
//                    vo.setTime(voTime);
                }
            }else {
                LOGGER.info("当前IP第一次访问此接口：IP ===> " + ip + " | 接口 ===> " + url);
                List<LocalDateTime> localDateTimes = new ArrayList<>();
                localDateTimes.add(LocalDateTime.now());
                IpLoggerVo ipLoggerVo = new IpLoggerVo(url.toString(), localDateTimes);
                voList.add(ipLoggerVo);
            }

        }else {
            LOGGER.info("当前IP第一次访问服务：IP ===> " + ip);
            voList = new ArrayList<>();
            List<LocalDateTime> localDateTimes = new ArrayList<>();
            localDateTimes.add(LocalDateTime.now());
            IpLoggerVo ipLoggerVo = new IpLoggerVo(url.toString(), localDateTimes);
            voList.add(ipLoggerVo);
        }
        redisTemplate.opsForHash().put(RedisTag.IP_LOGGER, ip, voList);
    }

    /**
     * 获取本次请求重放攻击<ReplayAttack>注解信息
     * @param joinPoint
     * @return
     */
    private ReplayAttack hasReplayAttackAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取方法上是否有<ReplayAttack>注解
        ReplayAttack annotatiaon = method.getAnnotation(ReplayAttack.class);
        if (annotatiaon == null) {
            //方法上没有<ReplayAttack>注解，获取类上是否有<ReplayAttack>注解
            annotatiaon = joinPoint.getTarget().getClass().getAnnotation(ReplayAttack.class);
            if (annotatiaon == null) {
                // 类上没有<ReplayAttack>注解
                // 获取接口上是否有<ReplayAttack>注解
                for (Class<?> cla : joinPoint.getClass().getInterfaces()) {
                    annotatiaon = cla.getAnnotation(ReplayAttack.class);
                }
            }

        }
        return annotatiaon;
    }
```

### 直接获取注解信息

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
     * 切点-针对controller文件夹下的所有类的方法
     * @param logInfo 注解参数
     */
    @Pointcut("execution(public * com.course.ymx.jwt.controller.*.*(..))")
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

#### 正常结果 : 

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

#### 异常结果 :

> **注 : 当有环绕异常时,单独异常不执行**

##### 环绕异常

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

##### 单独异常

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

