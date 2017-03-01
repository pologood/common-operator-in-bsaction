package com.gomeplus.bs.common.dubbor.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringRabbitMqReceiver {

	@Autowired
	private RabbitTemplate amqpTemplate;

	public String receive(String queueName) {
		Message message;
		try {
			message = amqpTemplate.receive(queueName);
		} catch (Exception e) {
			log.info("rabbitMQ received failed, queueName:" + queueName);
            throw e;
		}
		if (message == null)
			return null;

		return new String(message.getBody());
	}

	public Object receiveAndConvert(String queueName) {
        return amqpTemplate.receiveAndConvert(queueName);
	}

}
