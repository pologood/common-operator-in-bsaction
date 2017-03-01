package com.gomeplus.oversea.bi.service.item.mq;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ消息发送
 * 2017/2/14
 */

@Slf4j
@Component
public class SpringRabbitMqSender {
	
	@Resource
	private AmqpTemplate amqpTemplate;
	
	public void send(String exchanger, String routingKey, String data) {
		Message message = MessageBuilder.withBody(data.getBytes())
				.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
				.build();
		amqpTemplate.send(exchanger, routingKey, message);
	}
	
}
