# RabbitMQ
## 基础篇
前言：之前学习的feign是基于同步调用的，它的劣势在于虽然使用了微服务，解除了项目业务之间的耦合性，但是微服务与微服务之间也存在一定的耦合。比如支付业务，在进行支付业务的过程中，我们需要获取用户的支付状态，之后再调用用户接口来扣减用户的余额，这是属于支付业务模块的，
但是，后面又要去修改订单的支付状态，达到更新用户购物车的作用，这时的业务就和支付业务模块关系不大了，而且这种同步调用，会造成等待的时间变长。所以对于这种业务，可以采用异步的模式进行处理。

Rabbimq可以实现异步调用，有一定的**优势**
* 1.耦合度更低
* 2.性能更好
* 3.业务的拓展性更强
* 4.可以控制故障隔离

同时也有一定的**劣势**：
* 1.项目业务之间的消息，完全依赖于RabbitMQ，RabbitMQ出现异常的话，业务就会出现异常
* 2.RabbitMQ整体架构复杂，维护和调试成本高

RabbitMQ中简单的发消息

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSimpleQueue() {
        //1.队列名
        String queuename = "simple.queue";

        //2.消息内容
        String message = "我是zgj";

        //3.发送消息
        rabbitTemplate.convertAndSend(queuename, message);

    }


RabbitMQ中的simplequeue

    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String msg){
    log.info("监听到simple.queue的消息：【{}】",msg);
    }

RabbitMQ中的（部署多个，形成集群）workqueue可处理秒杀类业务，处理消息堆叠类的需求
* **workqueue模型的特征：**
* * 1.多个消费者绑定到一个队列，加快消息处理进度
* * 2.同一条消息只会被一个消费者处理
* * 3.可以通过设置prefetch来控制消费者预取的消息数量，处理完一条再处理下一条，实现能者多劳
* * 加快效率
    


    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue1(String msg) throws InterruptedException {
        System.out.println("消费者1接受到消息：" + msg + "，" + LocalTime.now());
        Thread.sleep(25);//        log.info("监听到work.queue的消息：【{}】",msg);}

    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue2(String msg) throws InterruptedException {
    System.err.println("消费者2...........接受到消息：" + msg + "，" + LocalTime.now());
    Thread.sleep(200);
    //        log.info("监听到work.queue的消息：【{}】",msg);
    }

    //在对应消费者的yml配置文件中配置
    spring:
    rabbitmq:
    host: 192.168.32.131 # 你的虚拟机IP
    port: 5672 # 端口
    virtual-host: /hmall # 虚拟主机
    username: hmall # 用户名
    password: hmall # 密码
    listener:
    simple:
    prefetch: 1 #每次只能获取一条消息，处理完之后

**Fanout交换机**

发送消息到Fanout交换机，再由Fanout交换机路由给绑定的queue->消费者
实现一条消息多个消费者处理，广播模式

        @Test
        public void testFanoutQueue() {
        //1.交换机名
        String exchangename = "hmall.fanout";
        //2.消息内容
        String message = "hello,everyone";
        //3.发送消息 与直接发送到queue，Api改变（3个参数）
        rabbitTemplate.convertAndSend(exchangename, null,message);
        }

**Direct交换机**

Direct Exchange会将接收到的消息根据规则路由到指定的Queue,因此称为定向路由。
* 每一个Queue都与Exchange设置一个BindingKey
* 发布者发送消息时，指定消息的RoutingKey
* Exchange:将消息路由到BindingKey.与消息Routing Key一致的队列

规范是在进行参数的传递时，发送消息者要传入一个routingkey参数，用于识别对应的queue，匹配的queue才能收到消息。
我认为这与社交聊天中的私发逻辑对应上了

      @Test
      public void testDirectQueue1() {
      //1.交换机名
      String exchangename = "hmall.direct";
      //2.消息内容
      String message = "hello,蓝色";
      //3.发送消息
      rabbitTemplate.convertAndSend(exchangename, "blue",message);
      }
    
      @Test
      public void testDirectQueue2() {
      //1.交换机名
      String exchangename = "hmall.direct";
      //2.消息内容
      String message = "hello,红色";
      //3.发送消息
      rabbitTemplate.convertAndSend(exchangename, "yellow",message);
      }


**Direct交换机与Fanout交换机的差异？**
* Fanout交换机将消息路由给每一个与之绑定的队列
* Direct交换机根据RoutingKey判断路由给哪个队列
* 如果多个队列具有相同RoutingKey,则与Fanout功能类似


**Topic交换机**

TopicExchange也是基于RoutingKey做消息路由，但是routingKey通常是多个单词的组合，并且以.分割。
Queue与Exchange指定BindingKeyl时可以使用通配符：
* ◆#：代指0个或多个单词
* ◆*：代指一个单词


      @Test
      public void testTopicQueue1() {
          //1.交换机名
          String exchangename = "hmall.topic";
          //2.消息内容
          String message = "天气：今天天气不错";
          //3.发送消息
          rabbitTemplate.convertAndSend(exchangename, "china.weather",message);
      }

**Topic交换机相比Direct:交换机的差异？**

Topic的RoutingKey和bindingKey可以是多个单词，以.分割
Topic交换机与队列绑定时的bindingKey可以指定通配符
#:代表0个或多个词
*:代表1个词

利用SpringAMQP api进行RabbitMQ的操作
**基于代码声明队列和交换机**

* SpringAMQP:提供了几个类，用来声明队列、交换机及其绑定关系：
* Queue:用于声明队列，可以用工厂类QueueBuilder构建
* Exchange:用于声明交换机，可以用工厂类ExchangeBuilder构建
* Binding:用于声明队列和交换机的绑定关系，可以用工厂类BindingBuilder构建
* 通常在消费者端（消息接收者）写出**声明队列和交换机的配置类**，生产者端（消息发送者）只关心把消息发到交换机里就行了


    @Configuration
    public class FanoutConfiguration {
    
        //声明交换机
        @Bean
        public FanoutExchange fanoutExchange(){
            return new FanoutExchange("hamll.fanout");
    //        return ExchangeBuilder.fanoutExchange("hmall.fanout").build();
    }
    
        //声明队列
        @Bean
        public Queue fanoutQueue1(){
            return new Queue("fanout.queue1"); //durable默认为true，即持久化
    
    //        return QueueBuilder.durable("fanout.queue1").build(); //durable是把这个队列持久化，存储到磁盘里。不易丢失
    }
    
        //绑定交换机与队列
        @Bean
        public Binding fanoutQueue1Binding(Queue fanoutQueue1,FanoutExchange fanoutExchange){
    
            return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
        }
    }

基于@RabbitListener注解来声明交换机与队列

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1",durable = "true"),
            exchange = @Exchange(name = "hmall.direct",type = ExchangeTypes.DIRECT),
            key = {"red","blue"}
    ))
    public void listenDirectQueue1(String msg) throws InterruptedException {
        System.out.println("消费者1监听到 direct.queue1 的消息：" + msg);

    }

## 高级篇

前言：由于在使用mq传递消息的过程中，可能会出现我们意料不到的情况，比如网络中断，服务宕机；因此我们就必须采用一些更可靠的策略来保证数据的可靠性

**策略1：发送者重连**
注意：当网络不稳定的时候，利用重试机制可以有效提高消息发送的成功率。不过SpringAMOP提供的重试机制是
**阻塞式**的重试，也就是说多次重试等待的过程中，当前线程是被阻塞的，会影响业务性能
如果对于业务性能有要求，建议**禁用**重试机制。如果一定要使用，请合理配置等待时长和重试次数，当然也
可以考虑使用**异步**线程来执行发送消息的代码。


在消息发送者配置文件yml文件中配置

        spring:
        rabbitmq:
        connection-timeout: 1s # 设置MQ的连接超时时间
        template:
        retry:
        enabled: true # 开启超时重试机制
        initial-interval: 1000ms # 失败后的初始等待时间
        multiplier: 1 # 失败后下次的等待时长倍数，下次等待时长 = initial-interval * multiplier
        max-attempts: 3 # 最大重试次数


**策略2：发送者确认**

SpringAMQP提供了Publisher Confirm和Publisher Return两种确认机制。开启确机制认后，当发送者发送消息给
MQ后，MQ会返回确认结果给发送者。返回的结果有以下几种情况：

* 消息投递到了MQ,但是路由失败。此时会通过PublisherReturn:返回路由异常原因，然后返回ACK,告知投递成功
* 临时消息投递到了MQ,并且入队成功，返回ACK,告知投递成功
* 持久消息投递到了MQ,并且入队完成持久化，返回ACK,告知投递成功
* 其它情况都会返回NACK,告知投递失败


     spring:
     rabbitmq:
     host: 192.168.32.131 # 你的虚拟机IP
     port: 5672 # 端口
     virtual-host: /hmall # 虚拟主机
     username: hmall # 用户名
     password: hmall # 密码
     connection-timeout: 1s
     template:
     retry:
     enabled: true
     publisher-confirm-type: correlated
     publisher-returns: false

配置说明：
* 这里publisher-confirm-type有三种模式可选：
* none:关闭confirm机制
* simple:同步阻塞等待MQ的回执消息
* correlated:MQ异步回调方式返回回执消息

消息发送过程中，可能会出现两种情况：

1.消息发送成功：

        @Test
        public void testConfirmCallback() throws InterruptedException {
        //创建correlationData
        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
        @Override
        public void onFailure(Throwable ex) {
        log.error("spring amqp 处理结果异常", ex);
        }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                //判断是否成功
                if (result.isAck()) {
                    log.debug("收到ConfirmCallback ack，消息发送成功！");
                } else {
                    log.error("收到ConfirmCallback nack，消息发送失败！reason: {}", result.getReason());
                }
            }
        });

        //1.交换机名
        String exchangename = "hmall.direct";
        //2.消息内容
        String message = "hello,红色";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangename, "red", message, cd);

        // 等待回调完成
        Thread.sleep(2000);
    }

日志直接记录：收到ConfirmCallback ack，消息发送成功！

2.消息发送失败，比如我们这里交换机名字填写错误
日志直接记录：收到ConfirmCallback nack，消息发送失败！

        reason: channel error; protocol method: 
        #method<channel.close>(reply-code=404, 
        reply-text=NOT_FOUND - 
        no exchange 'hmall.direct1' in vhost '/hmall', 
        class-id=60, method-id=40)

通过日志，我们就能快速定位到错误：no exchange 'hmall.direct1' in vhost '/hmall',

3.2.消息发送成功，但路由失败，比如routingkey填写错误

这时我们就要编写一个配置类，用来记录

    @Configuration
    @Slf4j
    @RequiredArgsConstructor
    public class MqConfig {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){

        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("监听到消息 return callback");
            log.debug("交换机：{}",returned.getExchange());
            log.debug("routingKey：{}",returned.getRoutingKey());
            log.debug("message：{}",returned.getMessage());
            log.debug("replyCode：{}",returned.getReplyCode());
            log.debug("replyText：{}",returned.getReplyText());
        });
    }
    }

这时，日志记录：

        监听到消息 return callback
        收到ConfirmCallback ack，消息发送成功！
        :交换机：hmall.direct
        DEBUG 31544 message：(Body:'"hello,红色"'
        MessageProperties [headers={spring_returned_message_correlation=744b7a0f-3cf4-4aae-81cc-72ac9fa4507d, __TypeId__=java.lang.String}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, deliveryTag=0])
        replyCode：312
        replyText：NO_ROUTE

NO_ROUTE，我们就能定位到错误了