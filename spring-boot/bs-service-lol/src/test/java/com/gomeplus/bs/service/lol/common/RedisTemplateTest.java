package com.gomeplus.bs.service.lol.common;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.service.lol.LOLApplication;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)  
@SpringApplicationConfiguration(classes=LOLApplication.class)
public class RedisTemplateTest {

	@Autowired
	private RedisTemplate<String,Long> longRedisTemplate;
	
	
	@Test
	public void testAdd() {
		longRedisTemplate.opsForSet().add(Constants.RedisKey.friends(9527L), 1L,2L,9528L,9526L);
	}
	
	@Test
	public void testGet() {
		Set<Long> friends = longRedisTemplate.opsForSet().members(Constants.RedisKey.friends(9528L));
		System.out.println(friends);
	}
	

}
