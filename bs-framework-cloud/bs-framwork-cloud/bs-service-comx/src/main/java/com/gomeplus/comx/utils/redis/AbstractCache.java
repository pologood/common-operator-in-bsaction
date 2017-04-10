package com.gomeplus.comx.utils.redis;

import com.gomeplus.comx.utils.config.Config;

/**
 * Created by xue on 1/3/17.
 * TODO 确认
 * 实例时可以带有config 也可以不带有
 * 或者AbstractCache 可能是一个 interface
 */
public interface AbstractCache {
    public static final Integer MAX_KEY_LENGTH = 255;


    public abstract void set(String key, String value);
    public abstract void set(String key, String value, Integer time);
    public abstract String get(String key);

    //TODO validates
    //public Boolean validateKey(String key){
    //    return true;
    //}
}
