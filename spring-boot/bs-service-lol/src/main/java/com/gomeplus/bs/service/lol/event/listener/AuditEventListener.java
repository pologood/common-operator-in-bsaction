/**
 * gomeo2o.com 
 * Copyright (c) 2015-2025 All Rights Reserved.
 * @Description TODO 
 * @author mojianli
 * @date 2016年2月23日 下午4:25:41
 */
package com.gomeplus.bs.service.lol.event.listener;

import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import io.terminus.ecp.common.event.EventListener;
import io.terminus.ecp.config.center.ConfigCenter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;




import com.gomeplus.bs.common.dubbor.rabbitmq.SendMqProducer;
import com.gomeplus.bs.common.dubbor.rabbitmq.common.SubmitAction;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishEntry;
import com.gomeplus.bs.interfaces.lol.vo.mq.EntryMqVo;
import com.gomeplus.bs.interfaces.lol.vo.mq.ReplyMqVo;
import com.gomeplus.bs.service.lol.dao.PraiseDao;
import com.gomeplus.bs.service.lol.dao.PublishContentDao;
import com.gomeplus.bs.service.lol.dao.PublishEntryDao;
import com.gomeplus.bs.service.lol.dao.PublishReplyDao;
import com.gomeplus.bs.service.lol.event.audit.AuditContentEvent;
import com.gomeplus.bs.service.lol.event.audit.AuditReplyEvent;
import com.google.common.eventbus.Subscribe;

/**
 * @Description 审核事件相关监听
 * @author yanyuyu
 * @date 2016年12月16日 下午3:58:36
 */
@Component
@Slf4j
public class AuditEventListener implements EventListener {
	
	@Autowired
	private SendMqProducer sendMqProducer;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private PublishEntryDao publishEntryDao;
	
	@Autowired
	private PublishContentDao publishContentDao;
	
	@Autowired
	private PublishReplyDao publishReplyDao;
	
	@Autowired
	private PraiseDao praiseDao;
	
	@Autowired
	private ConfigCenter configCenter;
	
    @Subscribe
    public void onAuditContent(AuditContentEvent event) {
        log.info("listener start deal AuditContentEvent: [{}]",event.id());
        String contentId = event.id();
        EntryMqVo dataVo = event.getData();
        
        Boolean isPubFirst = Boolean.valueOf(configCenter.get(Constants.SOCIAL_AUDIT_PUBFIRST));
        // 推送MQ给计算引擎 : 先发后审模式审核不通过 、 先审后发模式
        if((isPubFirst && !dataVo.getIsPublic()) || !isPubFirst) {
        	PublishEntry entry = publishEntryDao.findById(contentId);
        	EntryMqVo mqVo = new EntryMqVo();
            if(entry != null) {
            	BeanUtils.copyProperties(entry, mqVo);
            	mqVo.setIsPublic(dataVo.getIsPublic());
                mqVo.setUserId(entry.getOutUserId());
                mqVo.setUpdateTime(dataVo.getUpdateTime());
            	sendMqProducer.sendVo2Mq(contentId, Constants.RoutingKey.PUBLISH_ENTRY , SubmitAction.UPDATE, mqVo);
            } else {
            	log.error("AuditContentEvent error: entry not find by id-[{}]", contentId);
            }
        }
        
        if(!dataVo.getIsPublic()) {
        	// 删除动态下的所有评论
            publishReplyDao.updateMulti(new Query(Criteria.where("entryId").is(contentId)), Update.update("isDelete", true)
    																					   .set("updateTime", dataVo.getUpdateTime()));
            // 删除动态下的所有点赞
            praiseDao.updateMulti(new Query(Criteria.where("entryId").is(contentId)), Update.update("isDelete", true)
    																					   .set("updateTime", dataVo.getUpdateTime()));
        }
    }
    
    @Subscribe
    public void onAuditReply(AuditReplyEvent event) {
        log.info("listener start deal AuditReplyEvent: [{}]",event.id());
        
        String replyId = event.id();
        ReplyMqVo dataVo = event.getData();
        
        Boolean isPubFirst = Boolean.valueOf(configCenter.get(Constants.SOCIAL_AUDIT_PUBFIRST));
        
        // redis 计数 : 先发后审 审核不通过、 先审后发 审核通过
        if((isPubFirst && !dataVo.getIsPublic()) || (!isPubFirst && dataVo.getIsPublic())) {
            try {
            	stringRedisTemplate.opsForValue().increment(Constants.RedisKey.contentReplyNum(dataVo.getEntryId()), -1L);
            } catch(Exception e) {
            	log.error("contentReplyNum decre error, replyId-[{}]", replyId, e);
            }
            
            //更新动态的最后点赞时间
    		publishContentDao.updateById(dataVo.getEntryId(), Update.update("replyUpdateTime", dataVo.getUpdateTime()));
        }
        
        // 推送MQ给计算引擎 :  审核不通过 、 先审后发 审核通过
        if( !dataVo.getIsPublic() || (!isPubFirst && dataVo.getIsPublic()) ) {
            PublishContent content = publishContentDao.findById(dataVo.getEntryId());
            if(content != null) {
                dataVo.setEntryUserId(content.getOutUserId());
            }
            sendMqProducer.sendVo2Mq(event.id(), Constants.RoutingKey.PUBLISH_REPLY , SubmitAction.UPDATE, dataVo);
        }
    }
}
