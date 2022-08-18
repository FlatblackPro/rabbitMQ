package com.atguigu.dead;

import com.atguigu.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 由于交换机的声明、信道的声明、交换机和信道的绑定在consumer01中已经都完成了，因此这里不需要重复操作。
 */

public class Consumer02 {

    //声明死信队列名：
    public static final String DEAD_QUEUE = "dead_queue";


    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();

        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("consumerTag--->" + consumerTag);
            System.out.println("死信队列收到的消息是：" + new String(message.getBody()));
        };
        System.out.println("Consumer02等待消息中....");
        channel.basicConsume(DEAD_QUEUE, true, deliverCallback, CancelCallback ->{});
    }
}
