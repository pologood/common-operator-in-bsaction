package com.gomeplus.oversea.bs.service.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by liuliyun-ds on 2017/2/14.
 * 验证短信验证码请求体
 */
@Data
public class SmsVerifyRequestDTO implements Serializable{
    private static final long serialVersionUID = -2302660388642461739L;
    /**
     * 发送短信token
     */
    private String smsToken;
    /**
     * 发送短信序号
     */
    private String sequence;
    /**
     * 短信验证码
     */
    private String smsCode;
    /**
     *国际电话区号
     */
    private String countryCode;
    /**
     *手机号码
     */
    private String mobile;
}
