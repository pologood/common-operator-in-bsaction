package com.gomeplus.oversea.bs.service.gateway.event;

import com.gomeplus.oversea.bs.service.gateway.common.RabbitContant;
import com.gomeplus.oversea.bs.service.gateway.model.MQModel;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;
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
public class ItemLogMQEventListener implements EventListener {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Subscribe
    public void sendItemLogMQ(ItemLogMQEvent itemLogMQEvent) {
        MQModel data=itemLogMQEvent.getMqModel();
        String dataStr = JSONObject.toJSONString(data);
        if(data!=null){
            try {
                rabbitTemplate.convertAndSend(RabbitContant.RABBIT_EXCHANGE_ITEM_LOG, RabbitContant.RABBIT_ROUTINGKEY_ITEM_LOG, dataStr);
            }catch (Exception e){
                log.error("ItemLogMQEventListener sendItemLogMq exception.data:{}.",data,e);
            }
            log.info("ItemLogMQEventListener sendItemLogMq success,data:{}",data);
        }else{
            log.error("ItemLogMQEvent some properties is null or empty .itemLogMQEvent:{},",itemLogMQEvent);
        }
    }

}
