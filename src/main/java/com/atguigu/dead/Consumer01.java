package com.atguigu.dead;

import com.atguigu.utils.RabbitMqUtil;
import com.atguigu.utils.SleepUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 死信的产生来源：
 * TTL
 * 消息被拒
 * 超过队列最大长度
 *
 */
public class Consumer01 {
    //声明普通交换机名：
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    //声明死信交换机名：
    public static final String DEAD_EXCHANGE = "dead_exchange";

    //声明普通队列名：
    public static final String NORMAL_QUEUE = "normal_queue";
    //声明死信队列名：
    public static final String DEAD_QUEUE = "dead_queue";


    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();

        /**
         * 要让普通交换机和死信交换机产生绑定，需要定义参数：
         */
        Map<String, Object> arguments = new HashMap<>();
        //绑定操作，当出现死信时，将消息路由给死信交换机：
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //设置死信交换机的routingkey
        arguments.put("x-dead-letter-routing-key", "lisi");
        //设置队列的最大长度，超出长度之后的消息将进入死信队列：
        //arguments.put("x-max-length", 6);

        //声明普通交换机(direct形式)
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        //声明死信交换机(direct形式)
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        /**普通队列声明：
         * 通过arguments参数，将普通队列和死信队列进行绑定，所有的参数在arg中设置好，如上！
         */
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        /**死信队列声明：
         * 死信队列就是一个普通队列，不需要进行什么特别的操作。主要的操作都在普通队列中：
         * 1. 绑定死信队列，当出现死信时，转发；
         * 2. 设置死信触发条件，比如TTL；
         */
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);



        //绑定普通交换机和队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE, "zhangsan");
        //绑定死信交换机和队列
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE, "lisi");




        ////////////////////////////////回调函数///////////////////////////////////////////
        /**
         * 死信消息被拒后的情况测试：
         * 测试拒绝消息必须开启手动应答！！！
         * 假设拒收message---5：
         */
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            //SleepUtils.sleep(10);
            String ret = new String(message.getBody());
            if (ret.equals("message---5")){
                //拒收：
                System.out.println("拒收消息是===================================>" + ret);
                //false代表不再重新放回队列:
                channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
            }else {
                System.out.println("收到的消息是：" + new String(message.getBody()));
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
        };
        System.out.println("Consumer01等待消息中....");
        channel.basicConsume(NORMAL_QUEUE, false, deliverCallback, CancelCallback ->{});

    }
}
