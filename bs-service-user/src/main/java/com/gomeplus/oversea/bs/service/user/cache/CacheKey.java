package com.gomeplus.oversea.bs.service.user.cache;

/**
 * Redis key
 * Created by baixiangzhu on 2017/2/16.
 */
public class CacheKey {


    public static final String SEPERATOR=":";

    /**
     * 用户账号获取redis自增序列号
     */
    public static final String USER_REGISTER_NAME_INCR = "user:register:username:incr";

    /**
     * 记录用户密码错误次数
     */
    public static final String USER_LOGIN_PASSWORD_ERROR_TIMES = "user:login:password:error:times";


    /**
     * 用户登录token
     */
    public static final String USER_LOGIN_TOKEN = "user:login:token";

}
