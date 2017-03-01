package com.gomeplus.bs.service.lol.service;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import io.terminus.ecp.common.event.CoreEventDispatcher;
import io.terminus.ecp.config.center.ConfigCenter;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gomeplus.bs.framework.dubbor.annotations.PublicParam;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C403Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C404Exception;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.entity.PublishReply;
import com.gomeplus.bs.interfaces.lol.service.ReplyResource;
import com.gomeplus.bs.interfaces.lol.vo.PublishReplyVo;
import com.gomeplus.bs.interfaces.lol.vo.mq.ReplyMqVo;
import com.gomeplus.bs.service.lol.dao.PublishContentDao;
import com.gomeplus.bs.service.lol.dao.PublishReplyDao;
import com.gomeplus.bs.service.lol.enums.AuditEnum;
import com.gomeplus.bs.service.lol.event.reply.DeleteReplyEvent;
import com.gomeplus.bs.service.lol.event.reply.PostReplyEvent;
import com.gomeplus.bs.service.lol.util.LolMessageUtil;
import com.gomeplus.bs.service.lol.util.ValidateUtil;


@RestController("replyResource")
@RequestMapping("/lol/reply")
public class ReplyResourceImpl implements ReplyResource{
	
	@Autowired
	private PublishReplyDao publishReplyDao;
	
	@Autowired
	private PublishContentDao publishContentDao;
	
	@Autowired
	private ConfigCenter configCenter;
	
	@Autowired
	private CoreEventDispatcher coreEventDispatcher;

	@RequestMapping(method = POST)
	@Override
	public PublishReplyVo doPost(@RequestBody PublishReplyVo vo,@PublicParam(name = "userId")  Long userId) {
		String entryId = vo.getEntryId();
		String contents = vo.getContents();
		if( ValidateUtil.isNull(entryId) || ValidateUtil.isNull(contents)) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		PublishContent content = publishContentDao.findById(entryId);
		publishContentDao.validateContent(content, userId);
		
		//数据保存
		PublishReply reply = new PublishReply();
		BeanUtils.copyProperties(vo, reply);
		Boolean isPubFirst = Boolean.valueOf(configCenter.get(Constants.SOCIAL_AUDIT_PUBFIRST));
		String id = publishReplyDao.getNewId(Constants.CollectionName.PUBLISH_REPLY);
		Date currentDate = new Date();
		reply.setAudited(false);
		reply.setAuditState(AuditEnum.UNAUDITED.value);
		reply.setCreateTime(currentDate);
		reply.setId(id);
		reply.setIsDelete(false);
		reply.setIsPublic(isPubFirst);
		reply.setUpdateTime(currentDate);
		reply.setOutUserId(userId);
		reply.setOutBeReplyedUserId(vo.getBeReplyedUserId());
		publishReplyDao.save(reply);
		
		//更新动态的最后点赞时间
		publishContentDao.updateById(entryId, Update.update("replyUpdateTime", currentDate));
		
		PublishReplyVo returnVo = new PublishReplyVo();
		BeanUtils.copyProperties(reply, returnVo);
		returnVo.setBeReplyedUserId(reply.getOutBeReplyedUserId());
		returnVo.setUserId(reply.getOutUserId());
		
		//发布异步事件
		PostReplyEvent event = new PostReplyEvent(id);
		ReplyMqVo mqVo = new ReplyMqVo();
		BeanUtils.copyProperties(reply, mqVo);
		mqVo.setEntryUserId(content.getOutUserId());
		mqVo.setUserId(reply.getOutUserId());
		mqVo.setBeReplyedUserId(reply.getOutBeReplyedUserId());
		event.setData(mqVo);
		coreEventDispatcher.publish(event);
		
		return returnVo;
	}

	@RequestMapping(method = DELETE)
	@Override
	public void doDelete(String id,@PublicParam(name = "userId")  Long userId) {
		if( ValidateUtil.isNull(id) ) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		
		PublishReply reply = publishReplyDao.findById(id);
		if( reply == null ) {
			throw new C404Exception(LolMessageUtil.REPLY_IS_NOT_EXISTED);
		}
		PublishContent content = publishContentDao.findById(reply.getEntryId());
		publishContentDao.validateContent(content, userId);
		publishReplyDao.validateReply(reply, userId);
		
		//由于动态创建者不能进行删除他人的评论操作，所以只判断当前用户是否等于评论创建者
		if( !reply.getOutUserId().equals(userId)) {
			throw new C403Exception(LolMessageUtil.FORBIDDEN);
		}
		Date currentDate = new Date();
		publishReplyDao.update(new Query(Criteria.where("id").is(id).and("entryId").is(reply.getEntryId())), Update.update("isDelete", true)
																													.set("updateTime", currentDate));
		
		//更新动态的最后点赞时间
		publishContentDao.updateById(reply.getEntryId(), Update.update("replyUpdateTime", currentDate));
		
		//发布异步事件
		DeleteReplyEvent event = new DeleteReplyEvent(id);
		ReplyMqVo mqVo = new ReplyMqVo();
		BeanUtils.copyProperties(reply, mqVo);
		mqVo.setEntryUserId(content.getOutUserId());
		mqVo.setUpdateTime(currentDate);
		mqVo.setUserId(reply.getOutUserId());
		mqVo.setBeReplyedUserId(reply.getOutBeReplyedUserId());
		event.setData(mqVo);
		coreEventDispatcher.publish(event);
	}
}
