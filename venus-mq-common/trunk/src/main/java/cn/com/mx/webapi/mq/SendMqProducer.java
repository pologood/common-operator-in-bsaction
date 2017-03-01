package cn.com.mx.webapi.mq;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.com.mx.webapi.mq.common.SubmitAction;
import lombok.extern.slf4j.Slf4j;

/**
 * 推送mq
 * @author wangchangye
 *
 */
@Slf4j
@Component
public class SendMqProducer {

	@Autowired
	private SpringRabbitMqSender springRabbitMqSender;
	
	/**
	 * 
	 * @param id 消息ID
	 * @param type 消息类型
	 * @param action 消息动作
	 * @param data 消息主体
	 */
	public void sendVo2Mq(String id, String type, SubmitAction action, Object data) {
		// 发送到RabbitMQ
		if (type == null || type.equals(""))
			return;
		if (id == null || id.equals(""))
			return;
		JSONObject dataJson = JSONObject.parseObject(JSON.toJSONString(data));
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("type", type);
		json.put("action", action.name());
		json.put("send_time", System.currentTimeMillis());
		json.put("data", dataJson);
		// 添加sid
		ExtraInfo info = new ExtraInfo("sid", MDC.get("sid"));
		addExtraInfo(json, info);
		String routingKey = type + "." + action.name();
		springRabbitMqSender.send(routingKey, json.toJSONString());
		log.info("id:{}, routingKey:{} sent success", id, routingKey);
	}
	
	
	private void addExtraInfo(JSONObject orgJson, ExtraInfo...extraInfos){
		if(null == extraInfos || 0 == extraInfos.length){
			return;
		}
		Map<String, String> extraObj = new HashMap<String, String>();
		for (ExtraInfo info : extraInfos) {
			extraObj.put(info.getKey(), info.getValue());
		}
		orgJson.put("_extra", JSONObject.parseObject(JSON.toJSONString(extraObj)));
	}
	
	class ExtraInfo{
		String key;
		String value;
		public ExtraInfo(String key, String value) {
			this.key = key;
			this.value = value;
		}
		public String getKey() {
			return key;
		}
		public String getValue() {
			return value;
		}
	}
	
}
