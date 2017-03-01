/**
 * 
 */
package cn.com.mx.webapi.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

/**
 * @author daisyli
 *
 */
@Component
public class ReceiveMqConsumer {

	@Autowired
	private SpringRabbitMqReceiver springRabbitMqReceiver;
	
	public String receive(String queueName) {
		String jsonString = springRabbitMqReceiver.receive(queueName);
		
		if (jsonString == null || jsonString.equals(""))
			return "";
		
		JSONObject json = JSONObject.parseObject(jsonString);
//		String id = json.getString("id");
//		json.remove("id");
//		
//		String type = json.getString("type");
//		json.remove("type");
//		
//		json.remove("action");
		JSONObject data = json.getJSONObject("data");
		if (data == null)
			return null;
		return data.toString();
	}
}
