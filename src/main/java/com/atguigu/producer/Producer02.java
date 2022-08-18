package com.atguigu.producer;

import com.atguigu.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 发送消息，并且不设置自动应答ACK，模拟消费者宕机后，消息是否重新加入队列并且被消费。
 * 对应消费者：02、03
 */
public class Producer02 {
    public static final String ACK_QUEUE = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        //开启消息发布确认：
        channel.confirmSelect();
        //队列持久化标记：
        Boolean durable = true;
        //声明队列
        channel.queueDeclare(ACK_QUEUE,durable,false,false,null);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            //第三个属性，用于告诉MQ，将消息保存到磁盘上，进行持久化，但是不完全保证消息不丢失，因为有可能在保存时突然宕机。
            //所以后续要通过发布确认，来确保消息真正的被持久化了！
            channel.basicPublish("", ACK_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println("消息"+ message +"发送完毕");
        }
    }
}
