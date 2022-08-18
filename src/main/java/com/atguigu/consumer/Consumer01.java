package com.atguigu.consumer;

import com.atguigu.utils.RabbitMqUtil;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 工作线程01，模拟轮询
 */
public class Consumer01 {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {

        //接受消息：
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("接受到的消息是：" + new String(message.getBody()));
        };

        //消息接收被取消时：
        CancelCallback cancelCallback = (consumerTag) ->{
            System.out.println(consumerTag + "---消费者取消了消息接收");
        };

        Channel channel = RabbitMqUtil.getChannel();
        System.out.println("Consumer01等待接收消息");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback, cancelCallback);

    }

}
