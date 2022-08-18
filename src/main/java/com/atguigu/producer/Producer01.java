package com.atguigu.producer;

import com.atguigu.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 生产者，发送大量消息
 */

public class Producer01 {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        //队列声明：
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //从控制台接收信息：
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish("", QUEUE_NAME,null,message.getBytes());
            System.out.println("消息发送完毕");
        }
    }
}
