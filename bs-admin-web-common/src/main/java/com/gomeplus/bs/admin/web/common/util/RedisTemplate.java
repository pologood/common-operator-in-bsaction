package com.gomeplus.bs.admin.web.common.util;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Description redis操作 
 * @author yuanchangjun
 * @date 2016年10月20日 下午5:10:26
 */
@Slf4j
public class RedisTemplate {
    
    private static Integer LOGIN_TOKEN_SECOND = 60*60*24*7;
    
    public String setToken(){
    	return null;
    }
    
    /**
     * @Description 存储token 
     * @author yuanchangjun
     * @date 2016年10月19日 下午5:20:49
     * @param userId
     * @return
     */
	public static String set(StringRedisTemplate stringRedisTemplate,final Long userId) {
		final String UUToken = String.valueOf(UUID.randomUUID()).replaceAll("-", "");
    	return stringRedisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
            	byte[] key = ("user:admin:"+userId+"").getBytes();
            	byte[] token = UUToken.getBytes();
                connection.set(key, token);
                connection.expire(key, LOGIN_TOKEN_SECOND);
                return UUToken;
            }
        });
    }
	
    /**
     * @Description 获取存储token 
     * @author yuanchangjun
     * @date 2016年10月19日 下午5:20:49
     * @param userId
     * @return
     */
	public static String get(StringRedisTemplate stringRedisTemplate,final Long userId) {
    	return stringRedisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
            	byte[] key = ("user:admin:"+userId+"").getBytes();
            	String token = null;
                try {
                	byte[] tokenByte = connection.get(key);
                	if (tokenByte != null && tokenByte.length > 0) {
                		token = new String(connection.get(key),"UTF-8");
					}
					return token;
				} catch (UnsupportedEncodingException e) {
					log.error("获取存储token {}",userId, e);
				}
				return null;
            }
        });
    }
	
    /**
     * @Description 获取存储token 
     * @author yuanchangjun
     * @date 2016年10月19日 下午5:20:49
     * @param userId
     * @return
     */
	public static Boolean delte(StringRedisTemplate stringRedisTemplate,final Long userId) {
    	return stringRedisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
            	byte[] key = ("user:admin:"+userId+"").getBytes();
            	if(connection.del(key) == 1L){
            		return true;
            	}
            	return false;
            }
        });
    }
}