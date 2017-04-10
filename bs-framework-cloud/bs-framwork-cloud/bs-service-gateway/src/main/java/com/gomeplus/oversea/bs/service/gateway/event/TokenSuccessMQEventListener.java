package com.gomeplus.oversea.bs.service.gateway.event;

import com.alibaba.fastjson.JSONObject;
import com.gomeplus.oversea.bs.service.gateway.common.RabbitContant;
import com.gomeplus.oversea.bs.service.gateway.model.MQModel;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by neowyp on 2016/7/9.
 * Author   : wangyunpeng
 * Date     : 2016/7/9
 * Time     : 9:16
 * Version  : V1.0
 * Desc     : 示例代码，模拟事件处理
 */
@Slf4j
@Component
public class TokenSuccessMQEventListener implements EventListener {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Subscribe
    public void sendTokenSuccessMQ(TokenSuccessMQEvent tokenSuccessMQEvent) {
        MQModel data=tokenSuccessMQEvent.getMqModel();
        String dataStr = JSONObject.toJSONString(data);
        if(data!=null){
            rabbitTemplate.convertAndSend(RabbitContant.RABBIT_EXCHANGE_TOKEN_SUCCESS,RabbitContant.RABBIT_ROUTINGKEY_TOKEN_SUCCESS,dataStr);
            log.info("TokenSuccessMQEventListener SEND SUCCESS messageContent:{}",dataStr);
        }else{
            log.error("TokenSuccessMQEvent some properties is null or empty .data:{},",data);
        }
    }

}
