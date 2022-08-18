package com.atguigu.rabbitmq.config;

import org.omg.CORBA.TRANSACTION_MODE;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  * 3. 备份交换机的使用（基于死信）：
 *  *  3.1 声明交换机、队列、routingKey （Fanout）
 *  *  3.2 绑定备份交换机-队列
 *  *  3.3 构建交换机时，加入args（备份交换机）
 *  只有交换机成功收到消息时，备份交换机才管用（provider--->exchange--这里开始往后，出问题管用->queue--->consumer）。不然的话通过callback回调函数找丢失的消息。
 */
@Configuration
public class ConfirmExchangeConfig {

    public static final String CONFIRM_EXCHANGE = "confirm.exchange";

    public static final String CONFIRM_QUEUE = "confirm.queue";

    public static final String CONFIRM_ROUTINGKEY = "key1";

    //fanout,不需要routingkey
    public static final String BACKUP_EXCHANGE = "backup.exchange";

    public static final String BACKUP_QUEUE = "backup.queue";

    public static final String WARNING_QUEUE = "warning.queue";


    @Bean
    public DirectExchange confirmExchange(){
        //return new DirectExchange(CONFIRM_EXCHANGE);
        /**
         * 启用备用交换机：(key：alternate-exchange)
         */
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE).durable(true).
                withArgument("alternate-exchange", BACKUP_EXCHANGE).build();
    }

    @Bean
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE).build();
    }

    @Bean
    //如果是自定义的交换机绑定，需要在后面.noargs();
    public Binding bindingExchange2Queue(@Qualifier("confirmExchange") DirectExchange confirmExchange,
                                         @Qualifier("confirmQueue") Queue confirmQueue){
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTINGKEY);
    }

    @Bean
    public FanoutExchange backupExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE);
    }

    @Bean
    public Queue backupQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE).build();
    }

    @Bean
    public Queue warningQueue(){
        return QueueBuilder.durable(WARNING_QUEUE).build();
    }

    @Bean
    public Binding bindWarningQueue2Exchange(@Qualifier("backupExchange") FanoutExchange fanoutExchange,
                                             @Qualifier("warningQueue") Queue queue){
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public Binding bindBackupQueue2Exchange(@Qualifier("backupExchange") FanoutExchange fanoutExchange,
                                             @Qualifier("backupQueue") Queue queue){
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

}
