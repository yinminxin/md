## RabbitMq延时队列

### 配置队列

> 机制  
>
> “死信”是RabbitMQ中的一种消息机制，当你在消费消息时，如果队列里的消息出现以下情况：
>
> 1. 消息被否定确认，使用 `channel.basicNack` 或 `channel.basicReject` ，并且此时`requeue` 属性被设置为`false`。
>
> 2. 消息在队列的存活时间超过设置的TTL时间。
>
> 3. 消息队列的消息数量已经超过最大队列长度。
>
>    
>
> 思路: 
>
> 1. 声明一个默认队列 或 交换机绑定的队列, 不设置消费者, 称为死信队列
> 2. 死信队列设置转发交换机以及路由
> 3. 生产者设置消息过期时间 或 死信队列统一设置过期时间
> 4. 过期后转发至新的交换机/队列进行消费

```java
@Component
public class DelayRabbitSmsOrderConfig {

    /**
     * 重发
     */
    public static final String RE_SMS_ORSER_EXCHANGE = "re.sms.order";
    public static final String RE_SMS_ORDER_NOT_PAID_K = "re.sms.order.not.k.paid";
    public static final String RE_SMS_ORDER_NOT_PAID_QUEUE = "re.sms.order.not.q.paid";
    /**
     * 死信
     */
    public static final String DL_SMS_ORSER_EXCHANGE = "dead.sms.order";
    public static final String DL_SMS_ORDER_NOT_PAID_K = "dead.sms.order.not.k.paid";
    public static final String DL_SMS_ORDER_NOT_PAID_QUEUE = "dead.sms.order.not.q.paid";

    /**
     * 声明重发交换机
     */
    @Bean
    public Exchange reExchange() {
        return ExchangeBuilder
                .directExchange(RE_SMS_ORSER_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 声明重发队列
     */
    @Bean
    public Queue reQueue() {
        return QueueBuilder
                .durable(RE_SMS_ORDER_NOT_PAID_QUEUE)
                .build();
    }

    /**
     * 绑定重发交换机 重发路由
     */
    @Bean
    public Binding reExchangeBinding(Exchange reExchange, Queue reQueue) {
        return BindingBuilder
                .bind(reQueue)
                .to(reExchange)
                .with(RE_SMS_ORDER_NOT_PAID_K)
                .noargs();
    }

    
    /**
     * 声明死信队列
     */
    @Bean
    public Queue dlQueue() {
        Map<String, Object> args = new HashMap<>(4);
        // 指定过期之后的交换机为重发交换机
        args.put("x-dead-letter-exchange", RE_SMS_ORSER_EXCHANGE);
        // 指定过期之后的路由为重发路由
        args.put("x-dead-letter-routing-key", RE_SMS_ORDER_NOT_PAID_K);
        // 统一的毫秒单位过期  如果过期时间是动态的,需要rabbitMq插件,并在每条消息上设置过期时间
        args.put("x-expires", "600000");
        
        return QueueBuilder
                .durable(DL_SMS_ORDER_NOT_PAID_QUEUE)
                .withArguments(args)
                .build();
    }

    // 下面两个可不设置, 则消费者发消息的时候不设置交换机和路由(即使用默认的交换机)
    
    /**
     * 声明死信交换机
     */
    @Bean
    public Exchange dlExchange() {
        return ExchangeBuilder
                .directExchange(DL_SMS_ORSER_EXCHANGE)
                .durable(true)
                .build();
    }
    
    /**
     * 绑定死信交换机 死信路由
     */
    @Bean
    public Binding dlExchangeBinding(Exchange dlExchange, Queue dlQueue) {
        return BindingBuilder
                .bind(dlQueue)
                .to(dlExchange)
                .with(DL_SMS_ORDER_NOT_PAID_K)
                .noargs();
    }
    
}
```

### 生产者

```java
			// 延时 创单10min未支付
            Message msg = MessageBuilder.withBody(params.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("utf-8")
                // 若上面配置设置了死信交换机/死信路由 则配置上
                    .setReceivedExchange("")
                    .setReceivedRoutingKey("")
                    .setMessageId(UUID.randomUUID() + "")
                // 若上面配置设置了统一的毫秒单位过期, 则不用配置
                    .setExpiration("600000")
                    .build();
            amqpTemplate.convertAndSend(QueueConstant.DEAD_SMS_ORDER_QUEUE, msg);
```

### 消费者

```java
@Component
public class SmsPushNotPaidReceiver {

    public static final Logger logger = LoggerFactory.getLogger(SmsPushNotPaidReceiver.class);

    @RabbitHandler
    // 监听重发队列
    @RabbitListener(queues = QueueConstant.RE_SMS_ORDER_NOT_PAID_QUEUE)
    public void process(Message message) {
        logger.info("----------- sms.notPayOrder start -----------");

        String params = new String(message.getBody(), StandardCharsets.UTF_8);
        JSONObject param = JSONObject.parseObject(params);
        String code = param.getString("code");

        logger.info("----------- sms.notPayOrder end -----------");
    }
}
```

## 四种exchange

### Fanout Exchange

#### 简介

Fanout Exchange 会忽略 RoutingKey 的设置，直接将 Message 广播到所有绑定的 Queue 中。

#### 应用场景

以日志系统为例：假设我们定义了一个 Exchange 来接收日志消息，同时定义了两个 Queue 来存储消息：一个记录将被打印到控制台的日志消息；另一个记录将被写入磁盘文件的日志消息。我们希望 Exchange 接收到的每一条消息都会同时被转发到两个 Queue，这种场景下就可以使用 Fanout Exchange 来广播消息到所有绑定的 Queue。

### Direct Exchange

#### 简介

Direct Exchange 是 RabbitMQ 默认的 Exchange，完全根据 RoutingKey 来路由消息。设置 Exchange 和 Queue 的 Binding 时需指定 RoutingKey（一般为 Queue Name），发消息时也指定一样的 RoutingKey，消息就会被路由到对应的Queue。

#### 应用场景

现在我们考虑只把重要的日志消息写入磁盘文件，例如只把 Error 级别的日志发送给负责记录写入磁盘文件的 Queue。这种场景下我们可以使用指定的 RoutingKey（例如 error）将写入磁盘文件的 Queue 绑定到 Direct Exchange 上。

### Topic Exchange

#### 简介

Topic Exchange 和 Direct Exchange 类似，也需要通过 RoutingKey 来路由消息，区别在于Direct Exchange 对 RoutingKey 是精确匹配，而 Topic Exchange 支持模糊匹配。分别支持`*`和`#`通配符，`*`表示匹配一个单词，`#`则表示匹配没有或者多个单词。

#### 应用场景

假设我们的消息路由规则除了需要根据日志级别来分发之外还需要根据消息来源分发，可以将 RoutingKey 定义为 `消息来源.级别` 如 `order.info`、`user.error`等。处理所有来源为 `user` 的 Queue 就可以通过 `user.*` 绑定到 Topic Exchange 上，而处理所有日志级别为 `info` 的 Queue 可以通过 `*.info` 绑定到 Exchange上。

### 两种特殊的 Exchange

#### Headers Exchange

Headers Exchange 会忽略 RoutingKey 而根据消息中的 Headers 和创建绑定关系时指定的 Arguments 来匹配决定路由到哪些 Queue。

Headers Exchange 的性能比较差，而且 Direct Exchange 完全可以代替它，所以不建议使用。

#### Default Exchange

Default Exchange 是一种特殊的 Direct Exchange。当你手动创建一个队列时，后台会自动将这个队列绑定到一个名称为空的 Direct Exchange 上，**绑定 RoutingKey 与队列名称相同。**有了这个默认的交换机和绑定，使我们只关心队列这一层即可，这个比较适合做一些简单的应用。

#### 思考: 

死信队列的转发设置不设置交换机,仅仅设置路由, 可以转发到默认交换机上的队列上吗?

### 总结

在 Exchange 的基础上我们可以通过比较简单的配置绑定关系来灵活的使用消息路由，在简单的应用中也可以直接使用 RabbitMQ 提供的 Default Exchange 而无需关心 Exchange 和绑定关系。Direct Exchange、Topic Exchange、Fanout Exchange 三种类型的交换机的使用方式也很简单，容易掌握。

## 设置消费者的Qos

```java
    @RabbitHandler
    @RabbitListener(queues = QueueConstant.RE_SMS_ORDER_NOT_PAID_QUEUE)
    public void process(Channel channel, Message message) throws IOException {
        channel.basicQos(12);
    }
```

