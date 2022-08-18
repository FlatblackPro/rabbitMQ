package com.atguigu.dead;

import com.atguigu.utils.RabbitMqUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.AMQBasicProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    //声明普通交换机名：
    public static final String NORMAL_EXCHANGE = "normal_exchange";

    //声明普通队列名：
    public static final String NORMAL_QUEUE = "normal_queue";

    //声明普通交换机/队列的routingkey：
    public static final String NORMAL_ROUTINGKEY = "zhangsan";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);

        /**
         * 通过AMQBasicProperties，定义消息的属性：
         * 设置TTL时间，让消息在某一段时间后自动过期：这里设置10秒
         */
/*        AMQBasicProperties amqBasicProperties = new AMQP.BasicProperties();
        AMQP.BasicProperties basicProperties = ((AMQP.BasicProperties) amqBasicProperties).
                builder().expiration("10000").build();*/
        System.out.println("准备发送消息");
        //开始发送消息：
        for (int i = 1; i < 11; i++) {
            String message = "message---" + i;
            channel.basicPublish(NORMAL_EXCHANGE, NORMAL_ROUTINGKEY, null, message.getBytes());
        }
        System.out.println("消息发送完毕");
    }
}
