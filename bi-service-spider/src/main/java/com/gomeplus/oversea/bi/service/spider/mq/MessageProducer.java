package com.gomeplus.oversea.bi.service.spider.mq;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * RabbiMQ消息发送
 * 2017/2/14
 */
@Component
public class MessageProducer {
	private Logger logger = LoggerFactory.getLogger(MessageProducer.class);

	@Autowired
	private SpringRabbitMqSender springRabbitMqSender;
	
	/*public void sendMessageToMq(String exchanger, String routingKey, SubmitAction action, Object data) {
		// 发送到RabbitMQ
	
		JSONObject dataJson = JSONObject.parseObject(JSON.toJSONString(data));
		JSONObject sendMsg = new JSONObject();
		sendMsg.put("action", action.name());
		sendMsg.put("send_time", System.currentTimeMillis());
		sendMsg.put("data", dataJson);

		springRabbitMqSender.send(exchanger, routingKey, sendMsg.toJSONString());
		logger.info("Exchanger:{} routing key:{} rabbitMQ message:{} send succeed",
				exchanger, routingKey, sendMsg);
	}*/
	public void sendMessageToMq(String exchanger, String type, SubmitAction action, Object data) {
		
		JSONObject dataJson = JSONObject.parseObject(JSON.toJSONString(data));
		JSONObject sendMsg = new JSONObject();
		sendMsg.put("type", type);
		String routingKey = type + "." + action.name();
		sendMsg.put("action", action.name());
		sendMsg.put("send_time", System.currentTimeMillis());
		sendMsg.put("data", dataJson);

		springRabbitMqSender.send(exchanger, routingKey, sendMsg.toJSONString());
		logger.info("Exchanger:{} routingKey:{} rabbitMQ message:{} send succeed",
				exchanger, routingKey, sendMsg);
	}

	
}
