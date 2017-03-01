package com.gomeplus.oversea.bs.service.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by liuliyun-ds on 2017/2/14.
 * 发送短信验证码返回体
 */
@Data
public class SmsResponseDTO implements Serializable{

    private static final long serialVersionUID = 5956685239245407505L;
    /**
     * 短信发送token
     */
    private String smsToken;
    /**
     * 短信发送序号（10,15,20...）
     */
    private Integer sequence;

}
