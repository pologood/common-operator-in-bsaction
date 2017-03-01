package com.gomeplus.oversea.bs.service.user.enums;

/**
 * 注册来源
 * Created by baixiangzhu on 2017/2/13.
 */
public enum RegisterOriginEnum {

    /**
     * 手机号
     */
    PHONE("手机号"),
    /**
     * facebook
     */
    FACEBOOK("Facebook");

    private String desc;

    private RegisterOriginEnum(String desc){

        this.desc = desc;
    }
}
