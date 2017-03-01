package com.gomeplus.bs.service.lol.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C404Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C410Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.vo.AuditLolVo;
import com.gomeplus.bs.interfaces.lol.vo.AuditVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishContentVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishReplyVo;
import com.gomeplus.bs.interfaces.lol.vo.UserVo;
import com.gomeplus.bs.service.lol.friendship.cache.FriendshipCache;
import com.gomeplus.bs.service.lol.mongo.entity.PageParam;
import com.gomeplus.bs.service.lol.mongo.impl.BaseMongoDaoImpl;
import com.gomeplus.bs.service.lol.util.LolMessageUtil;
import com.gomeplus.bs.service.lol.util.ValidateUtil;

@Slf4j
@Repository("publishContentDao")
public class PublishContentDao extends BaseMongoDaoImpl<PublishContent> {

	@Autowired
	private FriendshipCache friendshipCache;
	
	@Autowired
	private PublishReplyDao publishReplyDao;
	
	@Autowired
	private PraiseDao praiseDao;
	
	@Autowired
	private RedisTemplate<String,Long> redisTemplate;
	
	@Autowired
	@Qualifier("mongoTemplate")
	
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	
	/**
	 * @Description 校验动态是否有效 
	 * <p>异常提示顺序：不存在 > 审核未通过 > 删除</p>
	 * @author yanyuyu
	 * @date 2016年12月17日 上午11:25:13
	 */
	public void validateContent(PublishContent content, Long userId) {
		if(content == null) {
			throw new C404Exception(LolMessageUtil.CONTENT_IS_NOT_EXISTED);
		}
		if(	!content.getIsPublic() && content.getAudited()) {
			Map<String,Object> errorCode = new HashMap<String,Object>();
			errorCode.put("code", "1");
			throw new C410Exception(LolMessageUtil.CONTENT_AUDIT_NOT_PASS).error(errorCode);
		}
		if(content.getIsDelete()) {
			Map<String,Object> errorCode = new HashMap<String,Object>();
			errorCode.put("code", "2");
			throw new C410Exception(LolMessageUtil.CONTENT_IS_DELETED).error(errorCode);
		}
		//如果非公开且非自己发布的动态
		if( (!content.getIsPublic() && !content.getOutUserId().equals(userId)) ) {
			throw new C404Exception(LolMessageUtil.CONTENT_IS_NOT_EXISTED);
		}
	}
	
	/**
	 * 根据条件查询审核动态列表
	 * @param vo
	 * @return
	 */
	public Map<String,Object> findAuditList(AuditVo vo) {
		if(ValidateUtil.isNull(vo.getAuditState())) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		Integer auditState = vo.getAuditState();
		if(auditState == null) {
			throw  new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		Criteria criteria = Criteria.where("auditState").is(auditState);
		if(auditState == 1 || auditState == 2) { //查询待审核的或者审核通过的
			criteria.and("isDelete").is(false);
		}
		if(!ValidateUtil.isNull(vo.getBusinessId())) {
			criteria.and("id").is(vo.getBusinessId());
		}
		if(!ValidateUtil.isNull(vo.getBusinessUserId())) {
			criteria.and("outUserId").is(vo.getBusinessUserId());
		}
		if(!ValidateUtil.isNull(vo.getBusinessContent())) { //内容模糊查询
			String patternStr = "^.*"+vo.getBusinessContent()+".*$";
			Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
			criteria.and("components.text").regex(pattern);
		}
		Query query = new Query(criteria);
		if(auditState == 1) {
			query.with(new Sort(Sort.Direction.ASC,"createTime"));
		} else {
			query.with(new Sort(Sort.Direction.ASC,"auditTime"));
		}
		int total = this.count(query);
		Integer pageSize = vo.getPageSize() == null ? 10 : vo.getPageSize();
		Integer pageNum = vo.getPageNum() == null ? 1 : vo.getPageNum();
		PageParam page = new PageParam(pageNum, pageSize);
		List<PublishContent> list = this.findPage(page, query);
		List<AuditLolVo> auditList = new ArrayList<AuditLolVo>();
		if(list != null && list.size() > 0) {
			AuditLolVo auditVo = null;
			for(PublishContent content : list) {
				auditVo = new AuditLolVo();
				BeanUtils.copyProperties(content, auditVo);
				auditVo.setEntryId(content.getId());
				// 融合业务：outUserId赋值到userId输出
				auditVo.setUserId(content.getOutUserId());
				auditList.add(auditVo);
			}
		}
		map.put("rows", auditList);
		map.put("total", total);
		return map;
	}
	
	/**
	 * 动态详情信息包装：包含点赞 评论
	 * @param type 0:评论点赞数目填充 1：评论点赞列表填充
	 * @return
	 */
	public PublishContentVo contentDetailToVo(PublishContent content, Integer type, Long userId) {
		PublishContentVo vo = new PublishContentVo();
		BeanUtils.copyProperties(content, vo);
		vo.setUserId(content.getOutUserId());
		//好友列表
		Set<Long> friends = friendshipCache.getFriendIds(userId);
		//将自己加入到好友列表，方便去重处理
		friends.add(userId);
		//评论列表
		List<PublishReplyVo> replyVoList = null;
		if(vo.getUserId().equals(userId)) {//如果动态创建者是当前查看着，则查动态下所有有效的评论
			replyVoList = publishReplyDao.findEntryReplyList(vo.getId());
		} else {
			replyVoList = publishReplyDao.findVisibleReplyList(vo.getId(), userId, friends);
		}
		if(type == 1) {
			//点赞用户列表
			List<UserVo> praiseUserList = null;
			if(vo.getUserId().equals(userId)) {  //如果动态创建者是当前查看着，则查动态下所有有效的点赞
				praiseUserList = praiseDao.findEntryPraiseList(vo.getId());
			} else {
				praiseUserList = praiseDao.findVisiblePraiseList(vo.getId(), userId, friends);
			}
			vo.setReplys(replyVoList);
			vo.setPraiseUsers(praiseUserList);
		} else {
			int praiseCount = 0;
			if(vo.getUserId().equals(userId)) {
				praiseCount = praiseDao.findEntryPraiseCounts(vo.getId());
			} else {
				praiseCount = praiseDao.findVisiblePraiseCounts(vo.getId(), userId, friends);
			}
			vo.setPraiseQuantity(praiseCount);
			vo.setReplyQuantity(replyVoList.size());
			vo.setReplys(null);
			vo.setPraiseUsers(null);
		}
		
		//查看用户是否已经点赞
		Boolean isPraise = false;
		try {
			isPraise = redisTemplate.opsForSet().isMember(Constants.RedisKey.praiseSet(vo.getId()), userId);
		} catch (Exception e) {
			log.error("find praiseSet from redis is error", e);
		}
		vo.setIsPraise(isPraise);
		return vo;
	}
	
}