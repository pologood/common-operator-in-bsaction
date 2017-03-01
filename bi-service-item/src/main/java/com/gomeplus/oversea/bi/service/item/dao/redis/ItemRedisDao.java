package com.gomeplus.oversea.bi.service.item.dao.redis;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.gomeplus.oversea.bi.service.item.vo.ItemVo;

/**
 * 商品 redis
 * 2017/2/16
 */
@Slf4j
@Repository
public class ItemRedisDao {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * 存放缓存
	 */
	public void setCache(String key, String value){
		stringRedisTemplate.opsForValue().set(key, value);
	}

	/**
	 * 创建或更新商品详情缓存
	 * @param outerId
	 * @param source
	 * @param itemVo
	 */
	public void upsertItemDetail(String source, String outerId, ItemVo itemVo) {
		if(itemVo != null) {
			final String key = keyOfItemDetail(source, outerId);
			String json = JSON.toJSONString(itemVo);
			stringRedisTemplate.opsForValue().set(key, json);
		}
    }
	
	public ItemVo getItemDetail(String source, String outerId) {
        final String key = keyOfItemDetail(source, outerId);
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
        	return null;
		}  
        return JSON.parseObject(json, ItemVo.class);
    }
    
    public void delItemDetail(String source, String outerId) {
    	String key = keyOfItemDetail(source, outerId);
    	log.info("Del item cache:{}", key);
    	stringRedisTemplate.delete(keyOfItemDetail(source, outerId));
    }
    
    private String keyOfItemDetail(String source, String outerId) {
        return "item:detail:source:" + source + ":outerId:"+ outerId ;
    }
}
