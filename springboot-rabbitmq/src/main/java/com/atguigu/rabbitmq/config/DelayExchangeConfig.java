package com.atguigu.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayExchangeConfig {

    //声明交换机
    public static final String DELAYED_EXCHANGE = "delayed.exchange";
    //声明队列
    public static final String DELAYED_QUEUE = "delayed.queue";
    //声明routingKey
    public static final String DELAYED_ROUTINGKEY = "delayed.routingkey";


    //创建交换机
    @Bean
    public CustomExchange delayedExchange(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type","direct");
        return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false, args);
    }

    //创建队列
    @Bean
    public Queue delayedQueue(){
        return QueueBuilder.durable(DELAYED_QUEUE).build();
    }

    //绑定延迟交换机和队列
    @Bean
    public Binding bindDelayedExchange2DelayedQueue(@Qualifier("delayedQueue") Queue DELAYED_QUEUE,
                                                    @Qualifier("delayedExchange") CustomExchange DELAYED_EXCHANGE){
        //由于这里的交换机是自定义的，因此构建时，需要noargs()方法。
        return BindingBuilder.bind(DELAYED_QUEUE).to(DELAYED_EXCHANGE).with(DELAYED_ROUTINGKEY).noargs();
    }

}
