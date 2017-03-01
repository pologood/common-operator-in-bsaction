package com.gomeplus.oversea.bs.service.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by liuliyun-ds on 2017/2/14.
 * 发送短信验证码请求体
 */
@Data
public class SmsSendRequestDTO implements Serializable {
    private static final long serialVersionUID = -8711491054936843745L;
    /**
     *国际电话区号
     */
    private String countryCode;
    /**
     *手机号码
     */
    private String mobile;
    /**
     * 业务类型(REGIST:注册；FINDPWD:找回密码；BINDPHONE:绑定手机号)
     */
    private String type;
}
