package com.gomeplus.bs.service.lol.service;


import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import io.terminus.ecp.common.event.CoreEventDispatcher;
import io.terminus.ecp.config.center.ConfigCenter;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.gomeplus.bs.framework.dubbor.annotations.PublicParam;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C403Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.entity.PublishEntry;
import com.gomeplus.bs.interfaces.lol.service.EntryResource;
import com.gomeplus.bs.interfaces.lol.vo.PublishContentVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishEntryVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishReplyVo;
import com.gomeplus.bs.interfaces.lol.vo.UserVo;
import com.gomeplus.bs.interfaces.lol.vo.mq.EntryMqVo;
import com.gomeplus.bs.service.lol.dao.PraiseDao;
import com.gomeplus.bs.service.lol.dao.PublishContentDao;
import com.gomeplus.bs.service.lol.dao.PublishEntryDao;
import com.gomeplus.bs.service.lol.dao.PublishReplyDao;
import com.gomeplus.bs.service.lol.enums.AuditEnum;
import com.gomeplus.bs.service.lol.enums.EntryShowTypeEnum;
import com.gomeplus.bs.service.lol.event.content.DeleteContentEvent;
import com.gomeplus.bs.service.lol.event.content.PostContentEvent;
import com.gomeplus.bs.service.lol.util.LolMessageUtil;
import com.gomeplus.bs.service.lol.util.ValidateUtil;


@RestController("entryResource")
@RequestMapping("/lol/entry")
public class EntryResourceImpl implements EntryResource{

	@Autowired
	private ConfigCenter configCenter;
	
	@Autowired
	private PublishContentDao publishContentDao;

	@Autowired
	private PublishEntryDao publishEntryDao;
	
	@Autowired
    private CoreEventDispatcher coreEventDispatcher;
	
	@Autowired
    private PublishReplyDao publishReplyDao;
	
	
	
	@Autowired
	private PraiseDao praiseDao;
	
	@RequestMapping(method = GET)
	@Override
	public PublishContentVo doGet(String id, @PublicParam(name = "userId") Long userId) {
		if( ValidateUtil.isNull(id) ) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		PublishContent content = publishContentDao.findById(id);
		//校验动态有效性
		publishContentDao.validateContent(content, userId);
		
		PublishContentVo vo = publishContentDao.contentDetailToVo(content, 1, userId);
		return vo;
	}

	@RequestMapping(method = POST)
	@Override
	public PublishContentVo doPost(@RequestBody PublishEntryVo vo,@PublicParam(name = "userId")  Long userId) {
		if( vo == null ) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		Object[] components = vo.getComponents();
		if( components == null || components.length == 0) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		JSONObject component = null;
		for(int i=0; i<components.length; i++) {
			component = (JSONObject) JSONObject.toJSON(components[i]);
			String type = component.getString("type");
			if("text".equals(type) && !component.containsKey("text") ) {
				throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
			} else if("image".equals("type") && !component.containsKey("url")) {
				throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
			}
		}
		
		Boolean isPubFirst = Boolean.valueOf(configCenter.get(Constants.SOCIAL_AUDIT_PUBFIRST));
		
		// 由于暂无发布信息需求，VO数据需填充 后期可由客户端补充
		vo.setIsRemind(false);
		vo.setShowType(EntryShowTypeEnum.PUBLIC.value);
		
		String id = publishEntryDao.getNewId(Constants.CollectionName.PUBLISH_ENTRY);
		Date currentDate = new Date();
		//发布信息保存
		PublishEntry entry = new PublishEntry();
		BeanUtils.copyProperties(vo, entry);
		entry.setCreateTime(currentDate);
		entry.setId(id);
		entry.setOutUserId(userId);
		publishEntryDao.save(entry);
		
		//动态内容保存
		PublishContent content = new PublishContent();
		content.setComponents(components);
		content.setCreateTime(currentDate);
		content.setId(id);
		content.setIsDelete(false);
		content.setIsPublic(isPubFirst);
		content.setAudited(false);
		content.setAuditState(AuditEnum.UNAUDITED.value);
		content.setUpdateTime(currentDate);
		content.setOutUserId(userId);
		content.setReplyUpdateTime(currentDate);
		content.setPraiseUpdateTime(currentDate);
		publishContentDao.save(content);
		
		PublishContentVo returnVo = new PublishContentVo();
		BeanUtils.copyProperties(content, returnVo);
		returnVo.setIsPraise(false);
		returnVo.setPraiseUsers(new ArrayList<UserVo>());
		returnVo.setReplys(new ArrayList<PublishReplyVo>());
		returnVo.setUserId(content.getOutUserId());

		//发布事件
		PostContentEvent event = new PostContentEvent(id);
		EntryMqVo mqVo = new EntryMqVo();
		BeanUtils.copyProperties(entry, mqVo);
		mqVo.setIsPublic(isPubFirst);
		mqVo.setUserId(entry.getOutUserId());
		mqVo.setUpdateTime(currentDate);
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
		PublishContent content = publishContentDao.findById(id);
		
		//校验动态有效性
		publishContentDao.validateContent(content, userId);
		
		if( !content.getOutUserId().equals(userId) ) {
			throw new C403Exception(LolMessageUtil.FORBIDDEN);
		}
		Date currentDate = new Date();
		publishContentDao.updateById(id, Update.update("isDelete", true)
											   .set("updateTime", currentDate)
											   .set("updateUserId", userId));
		
		//发布事件
		DeleteContentEvent event = new DeleteContentEvent(id);
		EntryMqVo mqVo = new EntryMqVo();
		mqVo.setIsPublic(content.getIsPublic());
		mqVo.setUpdateTime(currentDate);
		event.setData(mqVo);
		coreEventDispatcher.publish(event);
	}
	
	
}
