package com.gomeplus.oversea.bi.service.item.mq;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gomeplus.oversea.bi.service.item.enums.SubmitAction;

/**
 * RabbiMQ消息发送者
 * 2017/2/14
 */
@Slf4j
@Component
public class MessageProducer {
	@Autowired
	private SpringRabbitMqSender springRabbitMqSender;
	
	public void sendMessageToMq(String id, String exchanger, String type, SubmitAction action, Object data) {
		// 发送到RabbitMQ
		if (StringUtils.isBlank(id)) {
			return;
		}
		JSONObject dataJson = JSONObject.parseObject(JSON.toJSONString(data));
		JSONObject sendMsg = new JSONObject();
		sendMsg.put("id", id);
		sendMsg.put("type", type);
		String routingKey = type + "." + action.name();
		sendMsg.put("action", action.name());
		sendMsg.put("send_time", System.currentTimeMillis());
		sendMsg.put("data", dataJson);

		springRabbitMqSender.send(exchanger, routingKey, sendMsg.toJSONString());
		log.info("Exchanger:{} routingKey:{} rabbitMQ message:{} send succeed",
				exchanger, routingKey, sendMsg);
	}
	
	public void sendMessageToRecommend(String id, String exchanger, String type, SubmitAction action, Object data) {
		// 发送到RabbitMQ
		if (StringUtils.isBlank(id)) {
			return;
		}
		JSONObject dataJson = JSONObject.parseObject(JSON.toJSONString(data));
		JSONObject sendMsg = new JSONObject();
		sendMsg.put("id", id);
		sendMsg.put("type", type);
		String routingKey = type;
		sendMsg.put("action", action.name());
		sendMsg.put("send_time", System.currentTimeMillis());
		sendMsg.put("data", dataJson);

		springRabbitMqSender.send(exchanger, routingKey, sendMsg.toJSONString());
		log.info("Exchanger:{} routingKey:{} rabbitMQ message:{} send succeed",
				exchanger, routingKey, sendMsg);
	}
	
}
