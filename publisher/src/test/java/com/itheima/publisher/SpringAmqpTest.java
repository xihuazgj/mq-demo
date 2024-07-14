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

        for (int i = 0; i < 50; i++) {
            //2.消息内容
            String message = "Hello，我是第" + i + "条消息";
            //3.发送消息
            rabbitTemplate.convertAndSend(queuename, message);
        }


    }
}