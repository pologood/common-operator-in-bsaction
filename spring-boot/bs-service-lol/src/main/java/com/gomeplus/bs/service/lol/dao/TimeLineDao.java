package com.gomeplus.bs.service.lol.dao;

import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.entity.TimeLine;
import com.gomeplus.bs.interfaces.lol.vo.PublishContentVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishReplyVo;
import com.gomeplus.bs.interfaces.lol.vo.UserVo;
import com.gomeplus.bs.service.lol.friendship.cache.FriendshipCache;
import com.gomeplus.bs.service.lol.mongo.impl.BaseMongoDaoImpl;

@Slf4j
@Repository("timeLineDao")
public class TimeLineDao extends BaseMongoDaoImpl<TimeLine> {

	@Autowired
	@Qualifier("mongoTemplate")
	
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Autowired
	private PublishContentDao publishContentDao;
	
	@Autowired
	private FriendshipCache friendshipCache;
	
	@Autowired
	private PublishReplyDao publishReplyDao;
	
	@Autowired
	private PraiseDao praiseDao;
	
	@Autowired
	private RedisTemplate<String,Long> redisTemplate;
	
	
	/**
	 * 时间线的动态详情信息包装：包含点赞 评论
	 * @param type 0:评论点赞数目填充 1：评论点赞列表填充
	 * @return
	 */
	public PublishContentVo lineDetailToContentVo(TimeLine line, Integer type, Long userId) {
		PublishContentVo vo = new PublishContentVo();
		PublishContent content = publishContentDao.findById(line.getEntryId());
		BeanUtils.copyProperties(content, vo);
		vo.setUserId(content.getOutUserId());

		List<PublishReplyVo> replyVoList = null;
		List<UserVo> userVoList = null;
		if(vo.getUserId().equals(userId)) { //查看自己发布的动态
			//评论列表
			replyVoList = publishReplyDao.findEntryReplyList(vo.getId());
			//点赞用户列表
			userVoList = praiseDao.findEntryPraiseList(vo.getId());
		} else {
			//好友列表
			Set<Long> friends = friendshipCache.getFriendIds(userId);
			//将自己加入到好友列表，方便去重处理
			friends.add(userId);
			//评论列表
			replyVoList = publishReplyDao.findVisibleReplyList(vo.getId(), userId, friends);
			//点赞用户列表
			userVoList = praiseDao.findVisiblePraiseList(vo.getId(), userId, friends);
		}

		if(type == 1) {
			vo.setReplys(replyVoList);
			vo.setPraiseUsers(userVoList);
		} else {
			vo.setPraiseQuantity(userVoList.size());
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