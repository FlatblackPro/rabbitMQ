package com.atguigu.rabbitmq.controller;

import com.atguigu.rabbitmq.config.ConfirmExchangeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/confirm")
public class ConfirmController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("sendMsg/{message}")
    public void sendMsg(@PathVariable String message){
        /**
         * 如果要使用发布确认功能，需要在发送端加入CorrelationData，然后设置消息编号（ID）
         */
        CorrelationData correlationData1 = new CorrelationData("1");
        CorrelationData correlationData2 = new CorrelationData("2");
        CorrelationData correlationData3 = new CorrelationData("3");

        //下面这行代码是正确发送消息的，未发生错误：
        //rabbitTemplate.convertAndSend(ConfirmExchangeConfig.CONFIRM_EXCHANGE, ConfirmExchangeConfig.CONFIRM_ROUTINGKEY,message, correlationData1);
        rabbitTemplate.convertAndSend(ConfirmExchangeConfig.CONFIRM_EXCHANGE, ConfirmExchangeConfig.CONFIRM_ROUTINGKEY,message, correlationData1);
        rabbitTemplate.convertAndSend(ConfirmExchangeConfig.CONFIRM_EXCHANGE + "1", ConfirmExchangeConfig.CONFIRM_ROUTINGKEY,message, correlationData2);

        //开始模拟rabbitmq宕机：
        rabbitTemplate.convertAndSend(ConfirmExchangeConfig.CONFIRM_EXCHANGE,ConfirmExchangeConfig.CONFIRM_ROUTINGKEY + "111",message,correlationData3);
        log.info("发送的消息是：{}", message);
    }
}
