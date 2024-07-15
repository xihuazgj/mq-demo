package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    //声明队列
    @Bean
    public Queue fanoutQueue2(){
        return new Queue("fanout.queue2"); //durable默认为true，即持久化

//        return QueueBuilder.durable("fanout.queue1").build(); //durable是把这个队列持久化，存储到磁盘里。不易丢失
    }

    //绑定交换机与队列
    @Bean
    public Binding fanoutQueue2Binding(Queue fanoutQueue2,FanoutExchange fanoutExchange){

        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }
}

