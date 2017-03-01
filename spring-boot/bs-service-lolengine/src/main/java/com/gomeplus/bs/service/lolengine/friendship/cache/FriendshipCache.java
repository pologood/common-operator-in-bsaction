/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年12月15日 下午6:07:28
 */
package com.gomeplus.bs.service.lolengine.friendship.cache;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.service.lolengine.friendship.dao.FriendshipRelationDao;

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

    public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * @Description 缓存根据userId查询所有好友ids
     * @author mojianli
     * @date 2016年12月16日 下午2:29:05
     * @param userId
     * @return
     */
    public Set<Long> getFriendIds(Long userId) {

        final String cacheKey = Constants.RedisKey.friends(userId);

        Boolean exist = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

                return connection.exists(cacheKey.getBytes());
            }
        });

        if (!exist) {
            // 数据库查询相应的好友ids,存入redis
            Set<Long> friendIdSet = friendshipRelationDao.getFriendshipsByUserId(userId);
            for (Long friendUserId : friendIdSet) {
                redisTemplate.opsForSet().add(cacheKey, friendUserId);
            }
            return friendIdSet;
        } else {
            Set<Long> friendIdsCache = redisTemplate.opsForSet().members(cacheKey);
            return friendIdsCache;
        }
    }

    public void updateFriendCache(String action, Long userId, Long friendId) {

        String userIdKey = Constants.RedisKey.friends(userId);

        String friendIdKey = Constants.RedisKey.friends(friendId);

        // 俩人第一成为好友
        if (Constants.Action.ACTION_CREATE.equals(action)) {
            redisTemplate.opsForSet().add(userIdKey, friendId);
            redisTemplate.opsForSet().add(friendIdKey, userId);
        } else if (Constants.Action.ACTION_UPDATE.equals(action)) {
            // 单向删除，再次成为好友
            redisTemplate.opsForSet().add(userIdKey, friendId);
            redisTemplate.opsForSet().add(friendIdKey, userId);
        } else if (Constants.Action.ACTION_DELETE.equals(action)) {
            // 删除好友关系
            redisTemplate.opsForSet().remove(userIdKey, friendId);
            redisTemplate.opsForSet().remove(friendIdKey, userId);
        }

    }
}
