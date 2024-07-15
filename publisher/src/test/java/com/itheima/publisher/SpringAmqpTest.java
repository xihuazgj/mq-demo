package com.itheima.publisher;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

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

}
