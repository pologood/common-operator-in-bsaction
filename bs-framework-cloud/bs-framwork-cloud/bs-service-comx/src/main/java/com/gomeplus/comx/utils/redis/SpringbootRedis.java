package com.gomeplus.comx.utils.redis;

import com.alibaba.fastjson.JSONObject;
import com.gomeplus.comx.utils.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * Created by xue on 3/22/17.
 */
public class SpringbootRedis implements AbstractCache{

    public SpringbootRedis() {}
    private static RedisTemplate<String, String> redisTemplate;

    public static void setTemplate(RedisTemplate<String, String> template) {
        if (redisTemplate == null) redisTemplate = template;
    }


    public void set(String key, String value, Integer ttl) {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
