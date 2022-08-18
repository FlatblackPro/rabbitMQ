package com.atguigu.rabbitmq.controller;

import com.atguigu.rabbitmq.config.DelayExchangeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {
    /**
     * 用controller模拟生产者发送消息：
     * 1. 通过配置文件声明、绑定普通交换机和队列；
     * 2. 通过配置文件绑定普通队列和死信交换机；
     * 3. 通过配置文件绑定死信队列和死信交换机；
     * 4. 通过rabbitTemplate，选择不同的routingkey来发送消息。
     */

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable String message){
        //打印日志,输出的是string，使用占位符：
        log.info("当前时间是{}，发送的消息是{}", new Date().toString(), message);

        //void convertAndSend(String exchange, String routingKey, Object message) throws AmqpException;
        rabbitTemplate.convertAndSend("X", "XA","该消息发送给10秒延迟的队列QA" + message);
        rabbitTemplate.convertAndSend("X", "XB","该消息发送给40秒延迟的队列QB" + message);
    }


    /**
     * 在请求中定义TTL：
     * 看起来似乎没什么问题，但是在最开始的时候，就介绍过如果使用在消息属性上设置 TTL 的方式，消
     * 息可能并不会按时“死亡“，因为 RabbitMQ 只会检查第一个消息是否过期，如果过期则丢到死信队列，
     * 如果第一个消息的延时时长很长，而第二个消息的延时时长很短，第二个消息并不会优先得到执行。
     * 比如：
     * 消息1 延迟20S
     * 消息2 延迟2S
     * 最后两者都延迟了20S
     */
    @GetMapping("/sendTTL/{message}/{ttl}")
    public void sendMsgTTL(@PathVariable String message,@PathVariable String ttl){
        log.info("当前时间是{}，发送的消息是:{},延迟时常为：{}", new Date().toString(), message, ttl);
        rabbitTemplate.convertAndSend("X", "XC", message, (msg) ->{
            msg.getMessageProperties().setExpiration(ttl);
            return msg;
        });
    }

    /**
     * 延迟消息发送
     */
    @GetMapping("/sendDelayed/{msg}/{delayedTime}")
    public void sendDelayedMsg(@PathVariable String msg,
                               @PathVariable Integer delayedTime){
        log.info("延时消息发送时间：{}，延时{}秒，发送的消息内容是：{}",new Date().toString(), delayedTime/1000, msg);
        rabbitTemplate.convertAndSend(DelayExchangeConfig.DELAYED_EXCHANGE, DelayExchangeConfig.DELAYED_ROUTINGKEY, msg, (messagePostProcessor) ->{
            messagePostProcessor.getMessageProperties().setDelay(delayedTime);//这里通过setDelay来设置延时
            return messagePostProcessor;
        });

    }

}
