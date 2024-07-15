package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class DirectConfiguration {

    //声明交换机
    @Bean
    public DirectExchange DirectExchange(){
        return new DirectExchange("hamll.direct");

    }
    //声明队列
    @Bean
    public Queue directQueue1(){
        return new Queue("direct.queue1");
    }
    @Bean
    public Queue directQueue2(){
        return new Queue("direct.queue2");
    }

    //绑定交换机与队列
    @Bean
    public Binding fanoutQueue1BindingRed(Queue directQueue1,DirectExchange directExchange){

        return BindingBuilder.bind(directQueue1).to(directExchange).with("red");
    }
    //绑定交换机与队列
    @Bean
    public Binding fanoutQueue1BindingBlue(Queue directQueue1,DirectExchange directExchange){

        return BindingBuilder.bind(directQueue1).to(directExchange).with("blue");
    }
    //绑定交换机与队列
    @Bean
    public Binding fanoutQueue2BindingRed(Queue directQueue2,DirectExchange directExchange){

        return BindingBuilder.bind(directQueue2).to(directExchange).with("red");
    }
    //绑定交换机与队列
    @Bean
    public Binding fanoutQueue2BindingYellow(Queue directQueue2,DirectExchange directExchange){

        return BindingBuilder.bind(directQueue2).to(directExchange).with("yellow");
    }


}

