package com.gomeplus.bs.service.lol.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C404Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C410Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.interfaces.lol.entity.PublishReply;
import com.gomeplus.bs.interfaces.lol.vo.AuditLolVo;
import com.gomeplus.bs.interfaces.lol.vo.AuditVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishReplyVo;
import com.gomeplus.bs.service.lol.mongo.entity.PageParam;
import com.gomeplus.bs.service.lol.mongo.impl.BaseMongoDaoImpl;
import com.gomeplus.bs.service.lol.util.LolMessageUtil;
import com.gomeplus.bs.service.lol.util.ValidateUtil;

@Repository("publishReplyDao")
public class PublishReplyDao extends BaseMongoDaoImpl<PublishReply> {

	@Autowired
	@Qualifier("mongoTemplate")
	
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	/**
	 * @Description 校验评论是否有效 
	 * <p>异常提示顺序：不存在 > 审核未通过 > 删除</p>
	 * @author yanyuyu
	 * @date 2016年12月17日 上午11:25:13
	 */
	public void validateReply(PublishReply reply, Long userId) {
		if(reply == null) {
			throw new C404Exception(LolMessageUtil.REPLY_IS_NOT_EXISTED);
		}
		if(	!reply.getIsPublic() && reply.getAudited()) {
			Map<String,Object> errorCode = new HashMap<String,Object>();
			errorCode.put("code", "3");
			throw new C410Exception(LolMessageUtil.REPLY_AUDIT_NOT_PASS).error(errorCode);
		}
		if(reply.getIsDelete()) {
			Map<String,Object> errorCode = new HashMap<String,Object>();
			errorCode.put("code", "4");
			throw new C410Exception(LolMessageUtil.REPLY_IS_DELETED).error(errorCode);
		}
		//如果非公开且不是自己的评论
		if( (!reply.getIsPublic() && !reply.getOutUserId().equals(userId)) ) {
			throw new C404Exception(LolMessageUtil.REPLY_IS_NOT_EXISTED);
		}
	}
	
	/**
	 * @Description 查询所有的回复 
	 * @author yanyuyu
	 * @date 2016年12月19日 上午11:29:37
	 * @param entryId
	 * @param userId
	 * @return
	 */
	public List<PublishReply> findAllReplyList(String entryId, Long userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("entryId").is(entryId).and("isDelete").is(false));
		query.addCriteria(new Criteria().orOperator(Criteria.where("isPublic").is(true),Criteria.where("outUserId").is(userId)));
		query.with(new Sort(Direction.ASC, "createTime"));
		return this.findAll(query);
	}

	/**
	 * @Description 查询所有的回复
	 * @author yanyuyu
	 * @date 2016年12月19日 上午11:29:37
	 * @param entryId
	 * @return
	 */
	public List<PublishReply> findAllReplyList(String entryId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("entryId").is(entryId).and("isDelete").is(false));
		query.with(new Sort(Direction.ASC, "createTime"));
		return this.findAll(query);
	}
	
	/**
	 * 获取当前用户某动态下可见的评论列表
	 * @param entryId
	 * @param userId
	 * @param friends 好友set集合
	 * @return
	 */
	public List<PublishReplyVo> findVisibleReplyList(String entryId, Long userId, Set<Long> friends) {
		List<PublishReplyVo> replyVoList = new ArrayList<PublishReplyVo>();
		if(friends.size() > 0) {
			List<PublishReply> replyList = this.findAllReplyList(entryId, userId);
			PublishReplyVo replyVo = null;
			if( replyList != null && replyList.size()>0 ) {
				for(PublishReply reply : replyList) {
					if(friends.contains(reply.getOutUserId())) {
						//修改代码 如果A发动态 AB好友 AC好友 B回复动态 A回复B的回复  那么C不应该看到A回复B的这条回复
						//即判断被回复者是否和当前查看动态的用户互为好友
						if(reply.getOutBeReplyedUserId() != null && !reply.getOutBeReplyedUserId().equals(0L)) {
							if(!friends.contains(reply.getOutBeReplyedUserId())) {
								continue;
							}
						}
						replyVo = new PublishReplyVo();
						BeanUtils.copyProperties(reply, replyVo);
						// 融合业务：outUserId赋值到userId输出
						replyVo.setUserId(reply.getOutUserId());
						replyVo.setBeReplyedUserId(reply.getOutBeReplyedUserId());
						replyVoList.add(replyVo);
					}
				}
			}
		}
		return replyVoList;
	}

	/**
	 * 获取当前某动态下所有的有效评论列表
	 * @param entryId
	 * @return
	 */
	public List<PublishReplyVo> findEntryReplyList(String entryId) {
		List<PublishReplyVo> replyVoList = new ArrayList<PublishReplyVo>();
		List<PublishReply> replyList = this.findAllReplyList(entryId);
		PublishReplyVo replyVo = null;
		if( replyList != null && replyList.size()>0 ) {
			for(PublishReply reply : replyList) {
				replyVo = new PublishReplyVo();
				BeanUtils.copyProperties(reply, replyVo);
				// 融合业务：outUserId赋值到userId输出
				replyVo.setUserId(reply.getOutUserId());
				replyVo.setBeReplyedUserId(reply.getOutBeReplyedUserId());
				replyVoList.add(replyVo);
			}
		}
		return replyVoList;
	}
	
	/**
	 * 根据条件查询审核评论列表
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
			criteria.and("contents").regex(pattern);
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
		List<PublishReply> list = this.findPage(page, query);
		List<AuditLolVo> auditList = new ArrayList<AuditLolVo>();
		if(list != null && list.size() > 0) {
			AuditLolVo auditVo = null;
			for(PublishReply reply : list) {
				auditVo = new AuditLolVo();
				BeanUtils.copyProperties(reply, auditVo);
				auditVo.setReplyId(reply.getId());
				// 融合业务：outUserId赋值到userId输出
				auditVo.setUserId(reply.getOutUserId());
				auditList.add(auditVo);
			}
		}
		map.put("rows", auditList);
		map.put("total", total);
		return map;
	}
}