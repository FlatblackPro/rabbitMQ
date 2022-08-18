package com.atguigu.consumer;

import com.atguigu.utils.RabbitMqUtil;
import com.atguigu.utils.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer03 {
    public static final String ACK_QUEUE = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        System.out.println("03等待处理消息，sleep10秒");

        //收到消息后：回调函数：
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            //沉睡一秒
            SleepUtils.sleep(10);
            System.out.println("接收到的消息是---->" + new String(message.getBody()));
            /**手动应答：
             * 1.标记的封包
             * 2.是否批量应答
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };

        //消息接收被取消时：
        CancelCallback cancelCallback = (consumerTag) ->{
            System.out.println(consumerTag + "---消费者取消了消息接收");
        };

        //设置不公平分发：1代表不公平分发；
        int prefechCount = 1;
        channel.basicQos(prefechCount);

        //采用手动ACK：
        Boolean autoAck = false;
        channel.basicConsume(ACK_QUEUE, autoAck, deliverCallback, cancelCallback);
    }
}
