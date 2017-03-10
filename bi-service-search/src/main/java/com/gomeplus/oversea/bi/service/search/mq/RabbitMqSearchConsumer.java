package com.gomeplus.oversea.bi.service.search.mq;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.gomeplus.oversea.bi.service.search.dao.es.ItemIndexDao;
import com.gomeplus.oversea.bi.service.search.entity.ItemSearchVo;

@Slf4j
@RabbitListener(queues="bi.search")
@Component
public class RabbitMqSearchConsumer {
	
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Autowired
	private ItemIndexDao itemIndexDao;
	
	@RabbitHandler
	public void receive(String message) {
//		log.info("Receive message : {}", message);
		
		JSONObject result = null;
		try {
			result = JSONObject.parseObject(message);
		} catch (JSONException e) {
			log.error("JSONObject parse error " + e.getMessage());
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
		
		if (action == null)
			return;
		// 保存
		log.info("Rabbitmq routing key:{},id:{}", type + "." + action, id);
		if (SubmitAction.CREATE.name().equals(action)||SubmitAction.UPDATE.name().equals(action)) {
			// 处理
			ItemSearchVo o = new ItemSearchVo();
			
			String itemId = data.getString("id");
			String name = data.getString("name");
			Long sellingPrice = data.getJSONObject("sellingPrice").getLong("amount");
			Long originPrice = data.getJSONObject("originPrice").getLong("amount");
			Integer status = data.getInteger("status");
			String categoryId = data.getString("categoryId");
			String source = data.getString("source");
			o.setId(itemId);
			o.setName(name);
			o.setSellingPrice(sellingPrice);
			o.setOriginPrice(originPrice);
			o.setCategoryId(categoryId);
			o.setStatus(status);
			o.setSource(source);
			itemIndexDao.add(o);
		} 
	}
	
	public enum SubmitAction {
		UPDATE, CREATE, DELETE, SPIDER_CREATE;
	}
	
}
