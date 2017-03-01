package com.gomeplus.oversea.bi.service.item.mq;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.gomeplus.oversea.bi.service.item.enums.SubmitAction;
import com.gomeplus.oversea.bi.service.item.service.ItemWriteService;
import com.gomeplus.oversea.bi.service.item.vo.ItemVo;

@Slf4j
@RabbitListener(queues="bi.spider")
@Component
public class RabbitMqSpiderConsumer {
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	@Autowired
	private ItemWriteService itemService;
	
	@RabbitHandler
	public void receive(String message) {
		JSONObject result = null;
		try {
			result = JSONObject.parseObject(message);
		} catch (JSONException e) {
			log.error("JSONObject parse message:{}", message, e);
			return;
		}
		if (result == null) {
			log.error("Message is null");
			return;
		}
		String id = result.getString("id");
		String type = result.getString("type");
		String action = result.getString("action");
		
		JSONObject data = result.getJSONObject("data");
		if (data == null) {
			log.error("Message data is null");
			return;
		}
		if (type == null) {
			return;
		}
		if (action == null)
			return;
		// 保存
		log.info("Rabbitmq routing key:{}", type + "." + action);
		if (SubmitAction.SPIDER_CREATE.name().equals(action)) {
			ItemVo itemVo = JSONObject.toJavaObject(data, ItemVo.class);
			itemService.createForSpider(itemVo);
		} 
	}
	
}
