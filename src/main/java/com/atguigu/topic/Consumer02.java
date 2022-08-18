package com.atguigu.topic;

import com.atguigu.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer02 {

    public static final String EXCHANGE_NAME = "exchange_topic";

    public static void main(String[] args) throws IOException, TimeoutException {

        String routingName1 = "*.*.rabbit";
        String routingName2 = "lazy.#";

        Channel channel = RabbitMqUtil.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //临时队列：
        String queueName = channel.queueDeclare().getQueue();
        /**
         * TOPIC模式下绑定队列：
         */
        channel.queueBind(queueName,EXCHANGE_NAME,routingName1);
        channel.queueBind(queueName,EXCHANGE_NAME,routingName2);
        System.out.println("Consumer02等待消息接收...");

        //成功接收：
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            long deliveryTag = message.getEnvelope().getDeliveryTag();

            System.out.println("消息接收成功，消息是：" + new String(message.getBody()));
            System.out.println("consumerTag--->" + consumerTag);
            System.out.println("deliveryTag--->" + deliveryTag);
            System.out.println("RoutingKey--->" + message.getEnvelope().getRoutingKey());
        };

        //失败：
        CancelCallback cancelCallback = (consumerTag) ->{
            System.out.println("消息接收失败");
        };

        //接收消息：
        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }
}
