package com.gomeplus.bs.service.lol.service;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import io.terminus.ecp.common.event.CoreEventDispatcher;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gomeplus.bs.framework.dubbor.annotations.PublicParam;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.Praise;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.service.PraiseResource;
import com.gomeplus.bs.interfaces.lol.vo.mq.PraiseMqVo;
import com.gomeplus.bs.service.lol.dao.PraiseDao;
import com.gomeplus.bs.service.lol.dao.PublishContentDao;
import com.gomeplus.bs.service.lol.event.praise.DeletePraiseEvent;
import com.gomeplus.bs.service.lol.event.praise.PostPraiseEvent;
import com.gomeplus.bs.service.lol.util.LolMessageUtil;
import com.gomeplus.bs.service.lol.util.ValidateUtil;


/**
 * @Description 好友动态- 点赞服务
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@RestController("praiseResource")
@RequestMapping("/lol/praise")
public class PraiseResourceImpl implements PraiseResource{
	
	@Autowired
	private PraiseDao praiseDao;
	
	@Autowired
	private PublishContentDao publishContentDao;
	
	@Autowired
    private CoreEventDispatcher coreEventDispatcher;

	@RequestMapping(method = PUT)
	@Override
	public void doPut(String entryId,@PublicParam(name = "userId")  Long userId) {
        
		if(ValidateUtil.isNull(entryId)) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		
		//增加动态校验
		PublishContent content = publishContentDao.findById(entryId);
		publishContentDao.validateContent(content, userId);
		
		Praise praise = praiseDao.findOne(new Query(Criteria.where("entryId").is(entryId).and("outUserId").is(userId)));
		Date currentTime = new Date();
		boolean isFirstPraise = false;
		if(praise == null) { //第一次点赞
			isFirstPraise = true;
			String newId = praiseDao.getNewId(Constants.CollectionName.PRAISE);
			praise = new Praise();
			praise.setCreateTime(currentTime);
			praise.setEntryId(entryId);
			praise.setId(newId);
			praise.setIsDelete(false);
			praise.setUpdateTime(currentTime);
			praise.setOutUserId(userId);
			praiseDao.save(praise);
			
			//更新动态的最后点赞时间
			publishContentDao.updateById(entryId, Update.update("praiseUpdateTime", currentTime));
			
		} else { //判断点赞状态 是否可以点赞
			if(!praise.getIsDelete()) {
				//此处屏蔽异常
				//throw new C409Exception(LolMessageUtil.PRAISE_IS_EXISTED);
				return;
			} else {
				praiseDao.update(new Query(Criteria.where("entryId").is(entryId).and("outUserId").is(userId)), Update.update("isDelete", false).set("updateTime", currentTime).set("createTime", currentTime));
				//更新动态的最后点赞时间
				publishContentDao.updateById(entryId, Update.update("praiseUpdateTime", currentTime));
			}
		}
		
		//发布异步事件
		PostPraiseEvent event = new PostPraiseEvent(praise.getId());
		PraiseMqVo mqVo = new PraiseMqVo();
		BeanUtils.copyProperties(praise, mqVo);
		mqVo.setIsFirst(isFirstPraise);
		mqVo.setCreateTime(currentTime);
		mqVo.setUpdateTime(currentTime);
		mqVo.setIsDelete(false);
		mqVo.setEntryUserId(content.getOutUserId());
		mqVo.setUserId(praise.getOutUserId());
		event.setData(mqVo);
		coreEventDispatcher.publish(event);
	}
	
	@RequestMapping(method = DELETE)
	@Override
	public void doDelete(String entryId,@PublicParam(name = "userId")  Long userId) {
		if(entryId == null) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		
		//增加动态校验
		PublishContent content = publishContentDao.findById(entryId);
		publishContentDao.validateContent(content, userId);
		
		Praise praise = praiseDao.findOne(new Query(Criteria.where("entryId").is(entryId).and("outUserId").is(userId)));
		if(praise == null) {
			//此处屏蔽异常
			//throw new C409Exception(LolMessageUtil.PRAISE_IS_NOT_EXISTED);
			return;
		} else {
			if(praise.getIsDelete()) {
				//此处屏蔽异常
				//throw new C409Exception(LolMessageUtil.PRAISE_IS_NOT_EXISTED);
				return;
			} else {
				Date currentTime = new Date();
				praiseDao.update(new Query(Criteria.where("entryId").is(entryId).and("outUserId").is(userId)), Update.update("isDelete", true).set("updateTime", currentTime));
				
				//更新动态的最后点赞时间
				publishContentDao.updateById(entryId, Update.update("praiseUpdateTime", currentTime));
				
				//发布异步事件
				DeletePraiseEvent event = new DeletePraiseEvent(praise.getId());
				PraiseMqVo mqVo = new PraiseMqVo();
				BeanUtils.copyProperties(praise, mqVo);
				mqVo.setIsDelete(true);
				mqVo.setUpdateTime(currentTime);
				mqVo.setEntryUserId(content.getOutUserId());
				mqVo.setUserId(praise.getOutUserId());
				event.setData(mqVo);
				coreEventDispatcher.publish(event);
			}
		}
	}
}
