/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年12月15日 下午6:07:28
 */
package com.gomeplus.bs.service.lol.friendship.cache;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.service.lol.friendship.dao.FriendshipRelationDao;

/**
 * @Description 好友关系redis缓存
 * @author mojianli
 * @date 2016年12月15日 下午6:07:28
 */
@Repository("friendshipCache")
public class FriendshipCache {

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @Autowired
    private FriendshipRelationDao friendshipRelationDao;

    /**
     * @Description 缓存根据userId查询所有好友ids
     * @author mojianli
     * @date 2016年12月16日 下午2:29:05
     * @param userId
     * @return
     */
    public Set<Long> getFriendIds(Long userId) {

    	String cacheKey = Constants.RedisKey.friends(userId);
    	if(redisTemplate.hasKey(cacheKey)) {
    		return redisTemplate.opsForSet().members(cacheKey);
    	} else {
            // 数据库查询相应的好友ids,存入redis
            Set<Long> friendIdSet = friendshipRelationDao.getFriendshipsByUserId(userId);
            for (Long friendUserId : friendIdSet) {
                redisTemplate.opsForSet().add(cacheKey, friendUserId);
            }
            return friendIdSet;
        }
    }

}
