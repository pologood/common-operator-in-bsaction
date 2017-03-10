package com.gomeplus.oversea.bi.service.spider.test;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


public class ConsumerTest {
    private static final String QUEUE_NAME = "bi.spider";

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String queueName = QUEUE_NAME;


        
                channel.exchangeDeclare("bi.topic", "topic", true, true, null);
                queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, "bi.topic", "warning.#"); //监听两种模式 #匹配一个或多个单词 *匹配一个单词
                channel.queueBind(queueName, "bi.topic", "*.blue");
              

        // 在同一时间不要给一个worker一个以上的消息。
        // 不要将一个新的消息分发给worker知道它处理完了并且返回了前一个消息的通知标志（acknowledged）
        // 替代的，消息将会分发给下一个不忙的worker。
        channel.basicQos(1); //server push消息时的队列长度

        //用来缓存服务器推送过来的消息
        QueueingConsumer consumer = new QueueingConsumer(channel);

        //为channel声明一个consumer，服务器会推送消息
        //参数1:队列名称
        //参数2：是否发送ack包，不发送ack消息会持续在服务端保存，直到收到ack。  可以通过channel.basicAck手动回复ack
        //参数3：消费者
        channel.basicConsume(queueName, false, consumer);
        //channel.basicGet() //使用该函数主动去服务器检索是否有新消息，而不是等待服务器推送

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            System.out.println("Received " + new String(delivery.getBody()));

            //回复ack包，如果不回复，消息不会在服务器删除
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            //channel.basicReject(); channel.basicNack(); //可以通过这两个函数拒绝消息，可以指定消息在服务器删除还是继续投递给其他消费者
        }
    }
}