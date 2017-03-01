package com.gomeplus.bs.common.dubbor.rabbitmq.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/22
 */
@Configuration
public class RabbitMqConfiguration {

    @Autowired
    private RabbitTemplate amqpTemplate;

    @Value("${spring.rabbitmq.topic-exchange}")
    private String rabbitmq_topic_exchange;

    @Bean
    TopicExchange  exchange() {
        TopicExchange topicExchange = new TopicExchange(rabbitmq_topic_exchange);
        amqpTemplate.setExchange(topicExchange.getName());
        return topicExchange;
    }
}
