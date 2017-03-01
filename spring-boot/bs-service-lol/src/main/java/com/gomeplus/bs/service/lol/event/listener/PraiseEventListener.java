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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.gomeplus.bs.common.dubbor.rabbitmq.SendMqProducer;
import com.gomeplus.bs.common.dubbor.rabbitmq.common.SubmitAction;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.vo.mq.PraiseMqVo;
import com.gomeplus.bs.service.lol.event.praise.DeletePraiseEvent;
import com.gomeplus.bs.service.lol.event.praise.PostPraiseEvent;
import com.google.common.eventbus.Subscribe;

/**
 * @Description 点赞事件相关监听
 * @author yanyuyu
 * @date 2016年12月16日 下午3:58:36
 */
@Component
@Slf4j
public class PraiseEventListener implements EventListener {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
    private RedisTemplate<String, Long> redisTemplate;
	
	@Autowired
	private SendMqProducer sendMqProducer;	
	
    @Subscribe
    public void onPostPraise(PostPraiseEvent event) {
        log.info("listener start deal PostPraiseEvent: [{}]",event.id());
        PraiseMqVo mqVo = event.getData();
        
    	//redis 计数
        try {
        	stringRedisTemplate.opsForValue().increment(Constants.RedisKey.contentPraiseNum(mqVo.getEntryId()), 1L);
        } catch(Exception e) {
        	log.error("contentPraiseNum incre error, praiseId-[{}]", mqVo.getId(), e);
        }
        
        //维护点赞用户redis
        try {
        	redisTemplate.opsForSet().add(Constants.RedisKey.praiseSet(mqVo.getEntryId()), mqVo.getUserId());
        } catch(Exception e) {
        	log.error("praiseList add error, praiseId-[{}],userId-[{}]", mqVo.getId(),mqVo.getUserId(), e);
        }
        
        // 推送MQ给计算引擎 : 第一次点赞推送
        if(mqVo.getIsFirst()) {
        	sendMqProducer.sendVo2Mq(mqVo.getId(), Constants.RoutingKey.PRAISE, SubmitAction.CREATE, mqVo);
        }
    }
    
    @Subscribe
    public void onDeletePraise(DeletePraiseEvent event) {
        log.info("listener start deal DeletePraiseEvent: [{}]",event.id());
        PraiseMqVo mqVo = event.getData();
        
    	//redis 计数
        try {
        	stringRedisTemplate.opsForValue().increment(Constants.RedisKey.contentPraiseNum(mqVo.getEntryId()), -1L);
        } catch(Exception e) {
        	log.error("contentReplyNum decre error, praiseId-[{}]", mqVo.getId(), e);
        }
        
        //维护点赞用户redis
        try {
        	redisTemplate.opsForSet().remove(Constants.RedisKey.praiseSet(mqVo.getEntryId()), mqVo.getUserId());
        } catch(Exception e) {
        	log.error("praiseList remove error, praiseId-[{}],userId-[{}]", mqVo.getId(),mqVo.getUserId(), e);
        }
    }
}
