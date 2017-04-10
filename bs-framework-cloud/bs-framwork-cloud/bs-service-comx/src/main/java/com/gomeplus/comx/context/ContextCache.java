package com.gomeplus.comx.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gomeplus.comx.utils.redis.AbstractCache;

import java.util.Map;

/**
 * Created by xue on 1/3/17.
 * context redis 业务相关及日志等 封装在这里，业务直接调用，不直接使用utils/redis
 * 每个请求将有一个contextcache 实例 , utils/redis 同一个
 * TODO 针对 key 需要做处理 （特殊字符, 空格等)
 * TODO springframework.data.redis.core 当中有一个interface ValueOperations 若有必要，其实 AbstractCache 是那个interface
 */
public class ContextCache {
    private AbstractCache cache;
    private Boolean refreshingEnabled;

    public ContextCache(AbstractCache cache, Boolean refreshingEnabled) {
        this.cache = cache;
        this.refreshingEnabled = refreshingEnabled;
    }

    public void set(String key, Object value, Integer ttl) {
        JSONObject valueObj = new JSONObject();
        valueObj.put("v", value);
        cache.set(key, valueObj.toJSONString(), ttl);
    }

    public Object get(String key) {
        if (refreshingEnabled) return null;
        try {
            String valueStr = cache.get(key);
            if (null == valueStr) return null;
            JSONObject valueObj = JSON.parseObject(valueStr);
            return valueObj.get("v");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}


