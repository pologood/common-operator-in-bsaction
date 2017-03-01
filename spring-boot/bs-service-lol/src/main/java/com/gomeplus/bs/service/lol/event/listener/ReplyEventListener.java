/**
 * gomeo2o.com 
 * Copyright (c) 2015-2025 All Rights Reserved.
 * @Description TODO 
 * @author mojianli
 * @date 2016年2月23日 下午4:25:41
 */
package com.gomeplus.bs.service.lol.event.listener;

import io.terminus.ecp.common.event.EventListener;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.gomeplus.bs.common.dubbor.rabbitmq.SendMqProducer;
import com.gomeplus.bs.common.dubbor.rabbitmq.common.SubmitAction;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.vo.mq.ReplyMqVo;
import com.gomeplus.bs.service.lol.event.reply.DeleteReplyEvent;
import com.gomeplus.bs.service.lol.event.reply.PostReplyEvent;
import com.google.common.eventbus.Subscribe;

/**
 * @Description 动态事件相关监听
 * @author yanyuyu
 * @date 2016年12月16日 下午3:58:36
 */
@Component
@Slf4j
public class ReplyEventListener implements EventListener {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private SendMqProducer sendMqProducer;
	
    @Subscribe
    public void onPostReply(PostReplyEvent event) {
        log.info("listener start deal PostReplyEvent: [{}]",event.id());
        ReplyMqVo mqVo = event.getData();
        //先发后审
        if(mqVo.getIsPublic()) {
        	//redis 计数
            try {
            	stringRedisTemplate.opsForValue().increment(Constants.RedisKey.contentReplyNum(mqVo.getEntryId()), 1L);
            } catch(Exception e) {
            	log.error("contentReplyNum incre error, replyId-[{}]", mqVo.getId(), e);
            }
            
            // 推送MQ给计算引擎
            sendMqProducer.sendVo2Mq(event.id(), Constants.RoutingKey.PUBLISH_REPLY , SubmitAction.CREATE, mqVo);
        }
    }
    
    @Subscribe
    public void onDeleteReply(DeleteReplyEvent event) {
        log.info("listener start deal DeleteReplyEvent: [{}]",event.id());
        ReplyMqVo mqVo = event.getData();
        
        //如果评论可见 则计数操作
        if(mqVo.getIsPublic()) {
        	//redis 计数
            try {
            	stringRedisTemplate.opsForValue().increment(Constants.RedisKey.contentReplyNum(mqVo.getEntryId()), -1L);
            } catch(Exception e) {
            	log.error("contentReplyNum decre error, replyId-[{}]", mqVo.getId(), e);
            }
            
            // 推送MQ给计算引擎
            sendMqProducer.sendVo2Mq(event.id(), Constants.RoutingKey.PUBLISH_REPLY , SubmitAction.DELETE, mqVo);
        }
    }
}
