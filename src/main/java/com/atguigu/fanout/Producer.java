package com.atguigu.fanout;

import com.atguigu.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static final String EXCHANGE_FANOUT = "exchange_fanout";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();

        channel.exchangeDeclare(EXCHANGE_FANOUT, "fanout");

        //消息发布：
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            System.out.println("请输入发布消息内容：");
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_FANOUT,"",null,message.getBytes());
            System.out.println("消息发送完毕");
        }
    }
}
