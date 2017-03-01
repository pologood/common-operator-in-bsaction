package com.gomeplus.oversea.bs.service.user.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by liusongnan on 2017/2/15.
 */
@SuppressWarnings("unchecked")
@Component
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }
    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }
    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取redis维护的自增序列号
     * @param key
     * @return
     */
    public Long incr(final String key){
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        Long increment = operations.increment(key, 1L);

        return increment;
    }

    /**
     * 获取key的失效时间
     * @param key
     * @return
     */
    public Long getExipre(final String key){

        Long expireTime = 0L;

        try{
            expireTime = redisTemplate.getExpire(key);
        }catch (Exception e){
            e.printStackTrace();
        }

      return  expireTime;
    }

    /**
     * 删除key
     * @return
     */
    public void del(final String key){

        redisTemplate.delete(key);
    }
}
