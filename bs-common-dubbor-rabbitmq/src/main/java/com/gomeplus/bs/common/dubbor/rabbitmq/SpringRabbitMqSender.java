package com.gomeplus.bs.common.dubbor.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringRabbitMqSender {
	
	@Autowired
	private RabbitTemplate amqpTemplate;
	
	public void convertAndSend(String routingKey, Object data) {
		amqpTemplate.convertAndSend(routingKey, data);
	}
	
	public void send(String routingKey, String data) {
		Message message = MessageBuilder.withBody(data.getBytes())
				.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
				.build();
		try {
			amqpTemplate.send(routingKey, message);
		} catch (Exception e) {
			log.info("rabbitMQ msg sent failed, routingKey:" + routingKey + ", message:" + data);
			throw e;
		}
	}

}
