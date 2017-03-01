package cn.com.mx.webapi.mq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SpringRabbitMqReceiver {

	@Autowired
	private AmqpTemplate amqpTemplate;

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

	public static void main(String[] args) throws InterruptedException {

		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(
				"spring/venus-mq-servlet-persistence-context.xml");
		RabbitTemplate template = ctx.getBean(RabbitTemplate.class);
		Message message = template.receive("myQueue");
		System.out.println(new String(message.getBody()));
		Thread.sleep(1000);
		ctx.destroy();
	}

}
