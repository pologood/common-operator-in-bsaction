package com.gomeplus.oversea.bs.service.user.enums;

/**
 *
 * Created by baixiangzhu on 2017/2/14.
 */
public enum ErrorCodeEnum {


    INVITE_ID_ERROR("推荐码错误。errorCode:2002"),
    SMS_CODE_ERROR("验证码输入有误，请重新输入"),
    ACCOUNT_ALREADY_EXIST("该手机号已被注册，请直接登录"),
    THIRD_PARTY_ACCOUNT_LOGIN_MSG("已绑定三方账号，请使用三方账号登录"),
    USER_NOT_EXIST("手机号不存在，请重新输入"),
    MOBILE_HAS_BOUND("该手机号已被占用"),
    SMS_DAY_TIMES_LIMITED("发送次数超过限制"),
    SMS_CODE_NOT_EXIST("请获取验证码后输入"),
    SMS_CODE_INVALID("验证码已过期，请重新获取");


    private String message;

    private ErrorCodeEnum(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
