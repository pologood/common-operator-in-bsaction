package com.gomeplus.bs.service.lol.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.Praise;
import com.gomeplus.bs.interfaces.lol.vo.UserVo;
import com.gomeplus.bs.service.lol.mongo.impl.BaseMongoDaoImpl;

@Repository("praiseDao")
public class PraiseDao extends BaseMongoDaoImpl<Praise> {

	@Autowired
	@Qualifier("mongoTemplate")
	
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	
	@Autowired
	private RedisTemplate<String,Long> redisTemplate;
	
	/**
	 * 获取当前用户某动态下可见的点赞列表
	 * @param entryId
	 * @param userId
	 * @param friends 好友set集合
	 * @return
	 */
	public List<UserVo> findVisiblePraiseList(String entryId, Long userId, Set<Long> friends) {
		List<UserVo> userVoList = new ArrayList<UserVo>();
		if(friends.size() > 0) {
			List<Praise> praiseList = this.findAll(new Query(Criteria.where("entryId").is(entryId).and("isDelete").is(false)).with(new Sort(Direction.ASC, "createTime")));
			if(praiseList != null && praiseList.size() > 0) {
				UserVo userVo = null;
				for(Praise praise : praiseList) {
					if(friends.contains(praise.getOutUserId())) {
						userVo = new UserVo();
						userVo.setId(praise.getOutUserId());
						userVoList.add(userVo);
					}
				}
			}
		}
		return userVoList;
	}

	/**
	 * 获取当前动态下有效的点赞列表
	 * @param entryId
	 * @return
	 */
	public List<UserVo> findEntryPraiseList(String entryId) {
		List<UserVo> userVoList = new ArrayList<UserVo>();
		List<Praise> praiseList = this.findAll(new Query(Criteria.where("entryId").is(entryId).and("isDelete").is(false)).with(new Sort(Direction.ASC, "createTime")));
		if(praiseList != null && praiseList.size() > 0) {
			UserVo userVo = null;
			for(Praise praise : praiseList) {
				userVo = new UserVo();
				userVo.setId(praise.getOutUserId());
				userVoList.add(userVo);
			}
		}
		return userVoList;
	}
	
	/**
	 * 获取当前用户某动态下可见的点赞数
	 * @param entryId
	 * @param userId
	 * @param friends 好友set集合
	 * @return
	 */
	public int findVisiblePraiseCounts(String entryId, Long userId, Set<Long> friends) {
		String friendshipKey = Constants.RedisKey.friends(userId);
		String praiseSetKey = Constants.RedisKey.praiseSet(entryId);
		Set<Long> visiblePraiseSet = redisTemplate.opsForSet().intersect(friendshipKey, praiseSetKey);
		//判断用户自己是否参与了点赞
		Boolean isPraise = redisTemplate.opsForSet().isMember(praiseSetKey, userId);
		if(isPraise) {
			return visiblePraiseSet.size() + 1;
		} else {
			return visiblePraiseSet.size();
		}
	}

	/**
	 * 获取当前动态下所有的点赞数
	 * @param entryId
	 * @return
	 */
	public int findEntryPraiseCounts(String entryId) {
		String praiseSetKey = Constants.RedisKey.praiseSet(entryId);
		Set<Long> members = redisTemplate.opsForSet().members(praiseSetKey);
		if(members != null) {
			return members.size();
		} else {
			return 0;
		}
	}

	
	/**
	 * @Description 通过好友列表过滤得到可见的点赞用户VO列表
	 * @author yanyuyu
	 * @date 2016年12月19日 上午11:08:44
	 * @param list
	 * @param friends
	 */
	public List<UserVo> filterListByFriends(List<Praise> list, Set<Long> friends) {
		List<UserVo> userVoList = new ArrayList<UserVo>();
		UserVo userVo = null;
		if( list != null && list.size()>0 && friends != null && friends.size() > 0) {
			for(Praise praise : list) {
				if(friends.contains(praise.getOutUserId())) {
					userVo = new UserVo();
					userVo.setId(praise.getOutUserId());
					userVoList.add(userVo);
				}
			}
		}
		return userVoList;
	}
}