package com.itheima.publisher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@SpringBootTest
class SpringAmqpTest {

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

    @Test
    public void testWorkQueue() {
        //1.队列名
        String queuename = "work.queue";

        for (int i = 1; i < 50; i++) {
            //2.消息内容
            String message = "Hello，我是第" + i + "条消息";
            //3.发送消息
            rabbitTemplate.convertAndSend(queuename, message);
        }


    }

    @Test
    public void testFanoutQueue() {
        //1.交换机名
        String exchangename = "hmall.fanout";
        //2.消息内容
        String message = "hello,everyone";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangename, null,message);
    }
    @Test
    public void testDirectQueue() {
        //1.交换机名
        String exchangename = "hmall.direct";
        //2.消息内容
        String message = "hello,红色";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangename, "red",message);
    }

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

    @Test
    public void testTopicQueue1() {
        //1.交换机名
        String exchangename = "hmall.topic";
        //2.消息内容
        String message = "天气：今天天气不错";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangename, "china.weather",message);
    }

    @Test
    public void testSendObject() {

        Map<String,Object> msg = new HashMap<>(2);
        msg.put("name","Jack");
        msg.put("age",21);
        //3.发送消息
        rabbitTemplate.convertAndSend("object.queue",msg);
    }

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
        rabbitTemplate.convertAndSend(exchangename, "red1", message, cd);

        // 等待回调完成
        Thread.sleep(2000);
    }


}
