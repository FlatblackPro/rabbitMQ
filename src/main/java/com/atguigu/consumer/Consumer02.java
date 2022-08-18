package com.atguigu.consumer;

import com.atguigu.utils.RabbitMqUtil;
import com.atguigu.utils.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者，手动应答
 */
public class Consumer02 {

    public static final String ACK_QUEUE = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        System.out.println("02等待处理消息，sleep3秒");

        //收到消息后：回调函数：
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            //沉睡一秒
            SleepUtils.sleep(3);
            System.out.println("接收到的消息是" + new String(message.getBody()));
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
        /**设置不公平分发：
         * 1代表不公平分发；
         * 其他数字代表预取值，比如取5，另一个取2：
         * 那么信道会按照预取值，把5条消息发送到这个消费者，另外两条发送给另一个消费者。
         * 如果某一个消费者已经处理了消息，那么信道内的消息数量就减少了，因此MQ会再次推送，直到达到设定的prefetch值。
         * 预取值其实设定的是：信道最多允许排队的消息的数量上限！
         */

        int prefechCount = 1;
        channel.basicQos(prefechCount);

        //采用手动ACK：
        Boolean autoAck = false;
        channel.basicConsume(ACK_QUEUE, autoAck, deliverCallback, cancelCallback);
    }
}
