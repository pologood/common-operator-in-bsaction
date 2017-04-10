package com.gomeplus.comx.utils.redis;

import com.gomeplus.comx.utils.config.Config;
import com.gomeplus.comx.utils.config.ConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by xue on 1/3/17.
 */
@Component
public class CacheFactory {
    private static HashMap<Config, AbstractCache> instances = new HashMap<>();

    // Todo 加锁 不过因为幂等暂时简单实现
    public static AbstractCache fromConf(Config config) throws ConfigException{
        if (!instances.containsKey(config)) {
            if (config.str("type", "").equals("springbootredis")) {
                instances.put(config, new SpringbootRedis());
            }else if (config.str("engine", "").equals("redis") || config.str("type", "").equals("redis")) {
                System.out.println("init doing1");
                if (config.sub("redis").sub("options").str("cluster", "").equals("redis")) {
                    instances.put(config, new JedisClusterCache(config));
                }
            }
        }
        return instances.get(config);
    }
}
