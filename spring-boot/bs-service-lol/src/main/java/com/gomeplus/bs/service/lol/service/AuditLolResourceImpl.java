package com.gomeplus.bs.service.lol.service;

import io.terminus.ecp.common.event.CoreEventDispatcher;
import io.terminus.ecp.config.center.ConfigCenter;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C404Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C409Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C410Exception;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.AuditContentLog;
import com.gomeplus.bs.interfaces.lol.entity.AuditReplyLog;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.entity.PublishReply;
import com.gomeplus.bs.interfaces.lol.service.AuditLolResource;
import com.gomeplus.bs.interfaces.lol.vo.AuditVo;
import com.gomeplus.bs.interfaces.lol.vo.mq.EntryMqVo;
import com.gomeplus.bs.interfaces.lol.vo.mq.ReplyMqVo;
import com.gomeplus.bs.service.lol.dao.AuditContentLogDao;
import com.gomeplus.bs.service.lol.dao.AuditReplyLogDao;
import com.gomeplus.bs.service.lol.dao.PublishContentDao;
import com.gomeplus.bs.service.lol.dao.PublishReplyDao;
import com.gomeplus.bs.service.lol.enums.AuditEnum;
import com.gomeplus.bs.service.lol.event.audit.AuditContentEvent;
import com.gomeplus.bs.service.lol.event.audit.AuditReplyEvent;
import com.gomeplus.bs.service.lol.util.LolMessageUtil;
import com.gomeplus.bs.service.lol.util.ValidateUtil;


@Slf4j
@Service("auditLolResource")
public class AuditLolResourceImpl implements AuditLolResource{

	@Autowired
	private AuditContentLogDao auditContentLogDao;
	
	@Autowired
	private AuditReplyLogDao auditReplyLogDao;
	
	@Autowired
	private PublishContentDao publishContentDao;
	
	@Autowired
	private PublishReplyDao publishReplyDao;
	
	@Autowired
    private CoreEventDispatcher coreEventDispatcher;
	
	@Autowired
	private ConfigCenter configCenter;
	
	/**
	 * 审核
	 */
	@Override
	public void doPut(AuditVo vo) {
		log.info("AuditLol execute, {} ", vo);
		if(vo == null || ValidateUtil.isNull(vo.getAuditState()) || ValidateUtil.isNull(vo.getAuditUserId()) || ValidateUtil.isNull(vo.getBusinessId())) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		//1: 好友动态  2：好友动态评论
		Integer type = vo.getBusinessType();
		if(type == null)  {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		switch (type) {
			case 1:
				this.auditContent(vo.getBusinessId(), vo.getAuditState(), vo.getAuditUserId(),vo.getNoThroughReason(), vo.getIsSecondAudit());
				break;
			case 2:
				this.auditReply(vo.getBusinessId(), vo.getAuditState(), vo.getAuditUserId(),vo.getNoThroughReason(), vo.getIsSecondAudit());
				break;
			default:
				throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
	}
	
	/**
	 * 进行评论审核操作
	 * @param id        评论ID
	 * @param auditState     审核状态
	 * @param auditUserId    审核人
	 * @param noThroughReason  原因
	 * @param isSecondAudit  是否二审
	 */
	public void auditReply(String id, Integer auditState, String auditUserId,String noThroughReason, Boolean isSecondAudit) {
		PublishReply reply = publishReplyDao.findById(id);
		if(reply == null) {
			throw new C404Exception(LolMessageUtil.REPLY_IS_NOT_EXISTED);
		}
		if(reply.getIsDelete()) {
			throw new C410Exception(LolMessageUtil.REPLY_IS_DELETED);
		}
		if(reply.getAudited() && !isSecondAudit) { // 如果已经审核且不是二审
			throw new C409Exception(LolMessageUtil.AUDIT_CONFLICT);
		}
		Boolean isPubFirst = Boolean.valueOf(configCenter.get(Constants.SOCIAL_AUDIT_PUBFIRST));
		Date currentTime = new Date();
		Update update = Update.update("auditState", auditState)
							  .set("auditUserId", auditUserId)
							  .set("auditTime", currentTime)
							  .set("audited", true);
		if(auditState == AuditEnum.NOTPASS.value) { //审核不通过
			update.set("noThroughReason", noThroughReason)
				  .set("isPublic", false)
				  .set("isDelete", true)
				  .set("updateTime", currentTime);//修改更新时间
		} else if(auditState == AuditEnum.PASS.value ){ //审核通过 
			update.set("isPublic", true);
			//先审后发 修改更新时间
			if(!isPubFirst) {
				update.set("updateTime", currentTime);
			}
		} else {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		publishReplyDao.update(new Query(Criteria.where("id").is(id).and("entryId").is(reply.getEntryId())), update);
		
		//修改实体，记录日志
		reply.setAudited(true);
		reply.setAuditState(auditState);
		reply.setAuditUserId(auditUserId);
		reply.setAuditTime(currentTime);
		reply.setUpdateTime(currentTime);
		if(auditState == AuditEnum.NOTPASS.value) {
			reply.setIsPublic(false);
			reply.setIsDelete(true);
			reply.setNoThroughReason(noThroughReason);
		} else {
			reply.setIsPublic(true);
		}
		this.saveReplyLog(reply);
		
		//发送异步事件
		AuditReplyEvent event = new AuditReplyEvent(id);
		ReplyMqVo mqVo = new ReplyMqVo();
		BeanUtils.copyProperties(reply, mqVo);
		mqVo.setIsSecondAudit(isSecondAudit);
		mqVo.setUserId(reply.getOutUserId());
		mqVo.setBeReplyedUserId(reply.getOutBeReplyedUserId());
		event.setData(mqVo);
		coreEventDispatcher.publish(event);
	}
	
	/**
	 * 进行动态审核操作
	 * @param id             动态ID
	 * @param auditState     审核状态
	 * @param auditUserId    审核人
	 * @param noThroughReason  原因
	 * @param isSecondAudit  是否二审
	 */
	public void auditContent(String id, Integer auditState, String auditUserId,String noThroughReason, Boolean isSecondAudit) {
		PublishContent content = publishContentDao.findById(id);
		if(content == null) {
			throw new C404Exception(LolMessageUtil.CONTENT_IS_NOT_EXISTED);
		}
		if(content.getIsDelete()) {
			throw new C410Exception(LolMessageUtil.CONTENT_IS_DELETED);
		}
		if(content.getAudited() && !isSecondAudit) { // 如果已经审核且不是二审
			throw new C409Exception(LolMessageUtil.AUDIT_CONFLICT);
		}
		Boolean isPubFirst = Boolean.valueOf(configCenter.get(Constants.SOCIAL_AUDIT_PUBFIRST));
		Date currentTime = new Date();
		Update update = Update.update("auditState", auditState)
							  .set("auditUserId", auditUserId)
							  .set("auditTime", currentTime)
							  .set("audited", true);
		if(auditState == AuditEnum.NOTPASS.value) { //审核不通过 
			update.set("noThroughReason", noThroughReason)
				  .set("isPublic", false)
				  .set("isDelete", true)
				  .set("updateTime", currentTime);//修改更新时间
		} else if(auditState == AuditEnum.PASS.value ){ //审核通过
			update.set("isPublic", true);
			//先审后发 修改更新时间
			if(!isPubFirst) {
				update.set("updateTime", currentTime);
			}
		} else {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		publishContentDao.updateById(id, update);
		
		//修改实体，记录日志
		content.setAudited(true);
		content.setAuditState(auditState);
		content.setAuditUserId(auditUserId);
		content.setAuditTime(currentTime);
		content.setUpdateTime(currentTime);
		if(auditState == AuditEnum.NOTPASS.value) {
			content.setIsPublic(false);
			content.setIsDelete(true);
			content.setNoThroughReason(noThroughReason);
		} else {
			content.setIsPublic(true);
		}
		this.saveContentLog(content);
		
		//发送异步事件
		AuditContentEvent event = new AuditContentEvent(id);
		EntryMqVo mqVo = new EntryMqVo();
		mqVo.setId(id);
		mqVo.setIsPublic(content.getIsPublic());
		mqVo.setUpdateTime(currentTime);
		event.setData(mqVo);
		coreEventDispatcher.publish(event);
	}
	
	
	/**
	 * 记录评论日志
	 * @param reply
	 */
	private void saveReplyLog(PublishReply reply) {
		AuditReplyLog log = new AuditReplyLog();
		BeanUtils.copyProperties(reply, log);
		String newId = auditReplyLogDao.getNewId(Constants.CollectionName.AUDIT_REPLY_LOG);
		log.setId(newId);
		log.setReplyId(reply.getId());
		log.setOutUserId(reply.getOutUserId());
		log.setOutBeReplyedUserId(reply.getOutBeReplyedUserId());
		auditReplyLogDao.save(log);
	}
	
	/**
	 * 记录动态日志
	 * @param content
	 */
	private void saveContentLog(PublishContent content) {
		AuditContentLog log = new AuditContentLog();
		BeanUtils.copyProperties(content, log);
		String newId = auditContentLogDao.getNewId(Constants.CollectionName.AUDIT_CONTENT_LOG);
		log.setId(newId);
		log.setEntryId(content.getId());
		log.setOutUserId(content.getOutUserId());
		auditContentLogDao.save(log);
	}

}
