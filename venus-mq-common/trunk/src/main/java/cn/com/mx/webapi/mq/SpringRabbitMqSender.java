package cn.com.mx.webapi.mq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SpringRabbitMqSender {
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	public void convertAndSend(String routingKey, Object data) {
//		Message message = MessageBuilder.withBody("foo".getBytes())
//				.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
////				.setMessageId("123")
////				.setHeader("bar", "baz")
//				.build();
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
	
	
	public static void main(final String... args) throws Exception {

		AbstractApplicationContext ctx =
			new ClassPathXmlApplicationContext("spring/venus-mq-servlet-persistence-context.xml");
		RabbitTemplate template = ctx.getBean(RabbitTemplate.class);
		int i = 0;
		while (i < 10000000) {
			template.convertAndSend("rabbitExchange", "foo.bar", "Hello, world!" + i);
		
			Thread.sleep(1000);
			i++;
		}
		ctx.destroy();
	}

}
