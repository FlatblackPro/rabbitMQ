package com.atguigu.fanout;

import com.atguigu.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 扇出模式测试
 */
public class Consumer02 {

    public static final String EXCHANGE_FANOUT = "exchange_fanout";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();
        //声明交换机类型:
        channel.exchangeDeclare(EXCHANGE_FANOUT, BuiltinExchangeType.FANOUT);

        /**声明一个临时队列：
         * 当消费者与MQ断开连接后，该队列就被删除了
         */
        String queueName = channel.queueDeclare().getQueue();
        //绑定交换机和队列：
        channel.queueBind(queueName,EXCHANGE_FANOUT,"222");
        System.out.println("Consumer02等待消息接收...");

        //成功接收：
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("消息接收成功，消息是：" + new String(message.getBody()));
            System.out.println("consumerTag--->" + consumerTag);
        };

        //失败：
        CancelCallback cancelCallback = (consumerTag) ->{
            System.out.println("消息接收失败");
        };

        //接收并处理消息：
        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);

    }
}
