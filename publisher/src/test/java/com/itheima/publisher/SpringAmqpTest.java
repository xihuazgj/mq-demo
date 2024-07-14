package com.itheima.publisher;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        //1.队列名
        String exchangename = "hmall.fanout";
        //2.消息内容
        String message = "hello,everyone";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangename, null,message);
    }
    @Test
    public void testDirectQueue() {
        //1.队列名
        String exchangename = "hmall.direct";
        //2.消息内容
        String message = "hello,红色";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangename, "red",message);
    }

    @Test
    public void testDirectQueue1() {
        //1.队列名
        String exchangename = "hmall.direct";
        //2.消息内容
        String message = "hello,蓝色";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangename, "blue",message);
    }

    @Test
    public void testDirectQueue2() {
        //1.队列名
        String exchangename = "hmall.direct";
        //2.消息内容
        String message = "hello,红色";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangename, "yellow",message);
    }


}
