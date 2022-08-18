package com.atguigu.rabbitmq.consumer;


import com.atguigu.rabbitmq.config.DelayExchangeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
@Slf4j
@Component
public class DelayedConsumer {

    @RabbitListener(queues = DelayExchangeConfig.DELAYED_QUEUE)
    public void delayedListener(Message message){
        String retMsg = new String(message.getBody());
        log.info("收到的消息是{}，当前时间是{}", retMsg, new Date().toString());
    }
}
