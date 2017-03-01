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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.gomeplus.bs.common.dubbor.rabbitmq.SendMqProducer;
import com.gomeplus.bs.common.dubbor.rabbitmq.common.SubmitAction;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishEntry;
import com.gomeplus.bs.interfaces.lol.vo.mq.EntryMqVo;
import com.gomeplus.bs.service.lol.dao.PraiseDao;
import com.gomeplus.bs.service.lol.dao.PublishContentDao;
import com.gomeplus.bs.service.lol.dao.PublishEntryDao;
import com.gomeplus.bs.service.lol.dao.PublishReplyDao;
import com.gomeplus.bs.service.lol.event.content.DeleteContentEvent;
import com.gomeplus.bs.service.lol.event.content.PostContentEvent;
import com.google.common.eventbus.Subscribe;

/**
 * @Description 动态事件相关监听
 * @author yanyuyu
 * @date 2016年12月16日 下午3:58:36
 */
@Component
@Slf4j
public class ContentEventListener implements EventListener {
	
	@Autowired
	private PublishEntryDao publishEntryDao;
	
	@Autowired
	private PublishContentDao publishContentDao;
	
	@Autowired
	private PublishReplyDao publishReplyDao;
	
	@Autowired
	private PraiseDao praiseDao;
	
	@Autowired
	private SendMqProducer sendMqProducer;
	
    @Subscribe
    public void onPostContent(PostContentEvent event) {
        log.info("listener start deal PostContentEvent: [{}]",event.id());
        
        // 推送MQ给计算引擎
        EntryMqVo vo = event.getData();
        sendMqProducer.sendVo2Mq(event.id(), Constants.RoutingKey.PUBLISH_ENTRY , SubmitAction.CREATE, vo);
    }
    
    @Subscribe
    public void onDeleteContent(DeleteContentEvent event) {
        log.info("listener start deal DeleteContentEvent: [{}]",event.id());
        String id =  event.id();
        EntryMqVo dataVo = event.getData();
        
        // 推送MQ给计算引擎
        PublishEntry entry = publishEntryDao.findById(id);
        EntryMqVo mqVo = new EntryMqVo();

        if(entry != null) {
        	BeanUtils.copyProperties(entry, mqVo);
        	mqVo.setIsPublic(dataVo.getIsPublic());
            mqVo.setUserId(entry.getOutUserId());
        	sendMqProducer.sendVo2Mq(event.id(), Constants.RoutingKey.PUBLISH_ENTRY , SubmitAction.DELETE, mqVo);
        } else {
        	log.error("DeleteContentEvent error: entry not find by id-[{}]", id);
        }
        
        // 删除动态下的所有评论
        publishReplyDao.updateMulti(new Query(Criteria.where("entryId").is(id)), Update.update("isDelete", true)
																					   .set("updateTime", dataVo.getUpdateTime()));
        // 删除动态下的所有点赞
        praiseDao.updateMulti(new Query(Criteria.where("entryId").is(id)), Update.update("isDelete", true)
																					   .set("updateTime", dataVo.getUpdateTime()));
    }
}
