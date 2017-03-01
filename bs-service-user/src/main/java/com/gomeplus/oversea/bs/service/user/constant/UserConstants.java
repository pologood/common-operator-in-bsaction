package com.gomeplus.oversea.bs.service.user.constant;

/**
 * 用户相关常量
 * Created by baixiangzhu on 2017/2/21.
 */
public class UserConstants {

    /**
     * 用户账号前缀
     */
    public static final String USER_NAME_PREFIX = "gm_";
    /**
     * 用户冻结时长1小时
     */
    public static final Long USER_LOGIN_FROZEN_TIME = 60*60L;
    /**
     * 密码错误记录时长24小时
     */
    public static final Long USER_LOGIN_PASSWORD_ERROR_EXPIRE_TIME= 24*60*60L;

    /**
     * 用户登录token记录时长
     */
    public static final Long USER_LOGIN_TOKEN_EXPIRE_TIME = 7*24*60*60L;

}
