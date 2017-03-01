package com.gomeplus.bs.image_uploader.util;


import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import cn.com.mx.webapi.redis.factory.JedisClusterFactory;
import cn.com.mx.webapi.redis.template.SimpleJedisTemplate;
import cn.com.mx.webapi.redis.template.SimpleJedisTemplate.RedisCallback;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

/**
 * redis存取工具类
 * @author liucheng
 * @since 2016年9月19日
 */
@Slf4j
public class RedisUtil {

	private static SimpleJedisTemplate jedisTemplate;
	static{
		Properties prop = new Properties(); 
	    InputStream fis = null;
	    try {
	    	fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties");
			prop.load(fis);
			// GenericObjectPoolConfig
			Integer redis_cache_MaxTotal = Integer.parseInt(prop.getProperty("redis_cache_MaxTotal").trim());
			Integer redis_cache_MaxIdle = Integer.parseInt(prop.getProperty("redis_cache_MaxIdle").trim());
			Integer redis_cache_MaxWaitMillis = Integer.parseInt(prop.getProperty("redis_cache_MaxWaitMillis").trim());
			Boolean redis_cache_TestOnBorrow = Boolean.parseBoolean(prop.getProperty("redis_cache_TestOnBorrow").trim());
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(redis_cache_MaxTotal);
	        config.setMaxIdle(redis_cache_MaxIdle);
	        config.setMaxWaitMillis(redis_cache_MaxWaitMillis);
	        config.setTestOnBorrow(redis_cache_TestOnBorrow);
	        // JedisCluster jc
	        JedisCluster jc = null;
	        String redis_cache_Servers = prop.getProperty("redis_cache_Servers").trim();
			Integer redis_cache_MaxRedirections = Integer.parseInt(prop.getProperty("redis_cache_MaxRedirections").trim());
			Integer redis_cache_Timeout = Integer.parseInt(prop.getProperty("redis_cache_Timeout").trim());
	        JedisClusterFactory fac = new JedisClusterFactory();
			fac.setRedisServers(redis_cache_Servers);
			fac.setMaxRedirections(redis_cache_MaxRedirections);
			fac.setRedisTimeout(redis_cache_Timeout);
			fac.setPoolConfig(config);
			fac.afterPropertiesSet();
			jc = fac.getObject();
	        
	        jedisTemplate = new SimpleJedisTemplate(jc);
			fis.close();
		} catch (Exception e) {
			log.error("load app.properties failed:{}", e);
			throw new RuntimeException("load app.properties failed");
		}
	}
	
	
	/**
	 * 存redis
	 */
	public static String set(final String key, final String value) {
		return jedisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(JedisCommands commands) {
				return commands.set(key, value);
			}
		});
	}
	
	/**
	 * 取redis
	 */
	public static String get(final String key) {
		return jedisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(JedisCommands commands) {
				return commands.get(key);
			}
		});
	}
	
}
