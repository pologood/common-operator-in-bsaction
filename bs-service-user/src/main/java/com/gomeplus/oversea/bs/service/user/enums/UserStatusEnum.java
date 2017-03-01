package com.gomeplus.oversea.bs.service.user.enums;

/**
 * 用户状态
 * Created by baixiangzhu on 2017/2/13.
 */
public enum UserStatusEnum {

    /**
     * 使用中
     */
    USED("使用中"),
    /**
     * 冻结
     */
    FROZEN("冻结");

    private String desc;

    private UserStatusEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
