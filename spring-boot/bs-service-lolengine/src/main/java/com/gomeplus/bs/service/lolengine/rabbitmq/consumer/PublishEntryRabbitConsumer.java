/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年9月20日 下午5:39:12
 */
package com.gomeplus.bs.service.lolengine.rabbitmq.consumer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.gomeplus.bs.interfaces.lol.vo.mq.EntryMqVo;
import com.gomeplus.bs.service.lolengine.rabbitmq.analysis.PublishEntryMqAnalysis;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description 好友动态发布事件mq消费者
 * @author mojianli
 * @date 2016年11月21日 下午4:36:27
 */
@Slf4j
@Component
public class PublishEntryRabbitConsumer {

    @Autowired
    private PublishEntryMqAnalysis publishEntryMqAnalysis;


    /**
     * @Description 好友动态发布事件监听
     * @author mojianli
     * @date 2016年9月21日 下午6:30:35
     * @param obj
     */
    public void listen(Object obj) {
        String text = String.valueOf(obj);
        if (StringUtils.isBlank(text)) {
            log.error("msg is null or empty");
            return;
        }

        JSONObject json = null;
        try {
            json = JSONObject.parseObject(text);
            log.info("好友动态发布mq消息，msg={}", json.toString());
        } catch (JSONException e) {
            log.error("jsonObject parse error ", e);
            return;
        }

        String id = json.getString("id");
        String type = json.getString("type");
        String action = json.getString("action");

        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            log.error("data is null");
            return;
        }
        if (type == null)
            return;
        if (action == null)
            return;

        try {
            // 好友动态对象
            EntryMqVo entryMqVo = JSONObject.toJavaObject(data, EntryMqVo.class);

            // 解析好友动态
            publishEntryMqAnalysis.dealPublishEntryEvent(action, entryMqVo);
        } catch (Exception e) {
            log.error("好友动态发布mq消息解析异常！", e);
        }

    }

}
