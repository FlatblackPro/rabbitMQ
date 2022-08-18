package com.atguigu.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过配置类，完成信道、交换机、routingkey的命名、绑定等操作
 */
@Configuration
public class TtlConfig {
    //命名普通交换机X
    public static final String NORMAL_EXCHANGE = "X";
    //命名死信交换机Y
    public static final String DEAD_LETTER_EXCHANGE = "Y";
    //命名普通队列(这两个队列延时不同，从而满足不同业务需求)：
    public static final String NORMAL_QUEUE1 = "QA";
    public static final String NORMAL_QUEUE2 = "QB";
    //命名死信队列QD
    public static final String DEAD_LETTER_QUEUE = "QD";
    /**
     * 为了不把延迟时间写死，可以另外设置一个队列QC，在这个队列中不设定任何的TTL，而是由请求方提供：
     */
    public static final String NORMAL_QUEUE3 = "QC";




    /**
     * routing key暂时不命名，直接在绑定的时候写死。
     */

    //声明普通交换机X：xExchange
    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(NORMAL_EXCHANGE);
    }

    //声明死信交换机Y：yExchange
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    //声明普通队列QA 10秒延迟：
    //普通队列绑定死信交换机
    @Bean("queueA")
    public Queue queueA(){
        Map<String, Object> args = new HashMap<>(3);
        //设置死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        //设置死信routing key
        args.put("x-dead-letter-routing-key", "YD");
        //设置延迟时间
        args.put("x-message-ttl", 10000);
        return QueueBuilder.durable(NORMAL_QUEUE1).withArguments(args).build();
    }

    //声明普通队列QB 40秒延迟：
    //普通队列绑定死信交换机
    @Bean("queueB")
    public Queue queueB(){
        Map<String, Object> args = new HashMap<>(3);
        //设置死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        //设置死信routing key
        args.put("x-dead-letter-routing-key", "YD");
        //设置延迟时间
        args.put("x-message-ttl", 40000);
        return QueueBuilder.durable(NORMAL_QUEUE2).withArguments(args).build();
    }

    //声明普通队列QC 不设定延迟时间：
    //普通队列绑定死信交换机
    @Bean("queueC")
    public Queue queueC(){
        Map<String, Object> args = new HashMap<>(2);
        //设置死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        //设置死信routing key
        args.put("x-dead-letter-routing-key", "YD");
        //设置延迟时间
        //args.put("x-message-ttl", 40000);
        return QueueBuilder.durable(NORMAL_QUEUE3).withArguments(args).build();
    }

    //声明死信队列：
    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }
    /**
     * 声明完交换机、队列之后，需要把他们通过routingkey进行绑定
     */

    /**绑定QA和X
     *这里由于声明了多个队列，因此框架不知道这里的Queue是指哪一个，所以通过注解进行注入（byName）
     * 这里的bean不加（）的原因是，绑定关系的返回，后续不需要被调用。
     */
    @Bean
    public Binding bindQA2X(@Qualifier("xExchange") DirectExchange xExchange,
                            @Qualifier("queueA") Queue queueA){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    //绑定QB和X
    @Bean
    public Binding bindQB2X(@Qualifier("xExchange") DirectExchange xExchange,
                            @Qualifier("queueB") Queue queueB){
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    //绑定QD和Y
    @Bean
    public Binding bindQD2Y(@Qualifier("yExchange") DirectExchange yExchange,
                            @Qualifier("queueD") Queue queueD){
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }

    //绑定QC和X
    @Bean
    public Binding bindQC2X(@Qualifier("xExchange") DirectExchange xExchange,
                            @Qualifier("queueC") Queue queueC){
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
    }
}
