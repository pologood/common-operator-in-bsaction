package com.gomeplus.oversea.bi.service.item.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * 外部类目 redis
 * 2017/2/14
 */
@Repository
public class OuterCategoryRedisDao {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * 存放set集合通用方法
	 */
	public void setCache(String key, String value){
		stringRedisTemplate.opsForValue().set(key, value);
	}
	
	public String getCategoryIdByCategoryPath(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	public void saveFinalCategeryPathId(String key, Long pid) {
		stringRedisTemplate.opsForValue().set(key, String.valueOf(pid));
	}

}
