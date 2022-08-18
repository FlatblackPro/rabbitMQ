package com.atguigu.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 发布确认高级：
 * 1. 生产者->交换机：通过ConfirmCallback来确认交换机是否收到消息，确保消息不丢失；
 * ------>如果交换机OK，但是队列出了问题，那么交换机把消息发出后，消息就丢失了，但是交换机也回复接收成功了消息，所以有风险。
 * 2. 交换机->消费者：通过ReturnCallback来确认消息是否送达了目的地，确保消息不丢失；

 */
@Slf4j
@Component
public class ConfirmCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 实现完接口后，由于ConfirmCallback是一个内部接口，所以需要把这个类注入到RabbitTemplate中：
     * @PostConstruct:@PostContruct是spring框架的注解，在方法上加该注解会在项目启动的时候执行该方法，
     * 也可以理解为在spring容器初始化的时候执行该方法。
     * 一般用于一些项目初始化的设定。比如Spring IOC Container 初始化之后，
     * 用@PostConstruct注解Quartz的 CronTrigger 用于初始化定时器（向定时器中添加定时启动的JOB）
     */
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this::confirm);
        rabbitTemplate.setReturnCallback(this::returnedMessage);
    }

    /**
     * 通过此方法，如果交换机/队列宕机，那么就知道什么消息没发送出去了。
     * @param correlationData 发送的数据
     * @param ack 交换机返回的响应，是否收到消息
     * @param cause 未发送成功的原因，如果成功，是NULL
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null? correlationData.getId(): "ERROR";
        if (ack){
            log.info("消息已经成功发出，消息编号是：{}", id);
        }else {
            log.info("消息发送失败，未发送的消息编号是：{}", id);
        }

    }

    /**
     * 该方法可以在消息传递过程中，若消息无法到达目的地，则将消息返回给生产者。
     * 仅当消息不可送达时，进行回退！
     * @param message 消息
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("退回的消息是：{}，送出消息的交换机是：{}，RoutingKEY是：{}", new String(message.getBody()), exchange, routingKey);
    }
}
