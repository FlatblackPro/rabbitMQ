package com.atguigu.rabbitmq.consumer;

import com.atguigu.rabbitmq.config.ConfirmExchangeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WarningConsumer {

    @RabbitListener(queues = ConfirmExchangeConfig.WARNING_QUEUE)
    public void getWaringMsg(Message message){
        log.error("报警消费者收到的消息是：{}", new String(message.getBody()));
    }
}
