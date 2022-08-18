package com.atguigu.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class DeadLetterListener {

    //接收消息通过注解方式进行：
    @RabbitListener(queues = "QD")
    public void receiveDeadMsg(Message message){
        String msg = new String(message.getBody());
        log.info("当前时间是{}，死信队列收到的消息是{}", new Date().toString(), msg);
    }
}
