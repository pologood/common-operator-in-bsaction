package com.gomeplus.oversea.bs.service.user.dto.user;

import lombok.Data;

/**
 * Created by baixiangzhu on 2017/2/14.
 */
@Data
public class UserRequestDto {

    /**手机号区域码*/
    private String countryCode;

    /**手机号*/
    private String mobile;

    /**密码*/
    private String password;

    /**验证码*/
    private String smsCode;

    /**邀请人ID*/
    private String refereeUserId;

    /**用户注册TOKEN*/
    private String smsToken;



}
