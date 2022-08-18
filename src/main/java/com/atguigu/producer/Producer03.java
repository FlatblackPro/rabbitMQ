package com.atguigu.producer;

import com.atguigu.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * 异步发布确认：
 */
public class Producer03 {
    public static final String ASYNC_QUEUE = "async_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        String queueName = "async";
        channel.queueDeclare(queueName,true,false,false,null);
        /**发布确认前，准备一个map，用于装消息：
         * 然后发布一条消息，往map里面放一个消息
         * 然后在回调函数中，如果成功确认，那么这条消息就删除。
         * 最后剩下的消息就是未发送成功的，需要处理的消息了。
         *
         *
         * 通过map，把序号和消息关联：
         */
        ConcurrentSkipListMap<Long, String> confirmContainer = new ConcurrentSkipListMap<>();


        //开启发布确认：
        channel.confirmSelect();
        //计算时间：
        long begin = System.currentTimeMillis();
        /**消息发布前，需要准备一个消息监听器：
         * 使用多参的监听器，写两个回调函数，分别指定消息成功确认和失败之后干什么；
         * deliveryTag：消息的编号（消息队列中的每一个消息都有key和value，这个参数就是key）
         */
        //成功：
        ConfirmCallback ackCallBack = (deliveryTag, multiple) ->{
            /**
             * 消息确认成功
             * Headmap：返回一个sub-map，这个map的key小于方法的参数,对这个map的所有操作，会映射到主map。
             */
            ConcurrentNavigableMap<Long, String> confirmedMap = confirmContainer.headMap(deliveryTag);
            confirmedMap.remove(deliveryTag);
            System.out.println("确认成功的消息：" + deliveryTag);
        };
        //失败：
        ConfirmCallback nackCallBack = (deliveryTag, multiple) ->{
            String failedMessage = confirmContainer.get(deliveryTag);
            System.out.println("确认失败的消息：" + deliveryTag);
        };
        channel.addConfirmListener(ackCallBack,nackCallBack);


        //发布1000条消息：
        for (int i = 0; i <1000; i++){
            String message = i+"";
            //消息发布：
            channel.basicPublish("",queueName,null,message.getBytes());
            //每发送一次消息，把消息放到容器中一次：
            confirmContainer.put(channel.getNextPublishSeqNo(), message);

        }


        long end = System.currentTimeMillis();
        System.out.println("异步发布确认消息，耗时：" + (end-begin) + "ms");

    }
}
