package com.atguigu.rabbitmq.consumer;

import com.atguigu.rabbitmq.config.ConfirmExchangeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfirmConsumer {

    @RabbitListener(queues = ConfirmExchangeConfig.CONFIRM_QUEUE)
    public void receiveMsg(Message message){
        String msg = new String(message.getBody());
        log.info("接收到的消息是：{}", msg);
    }
}
