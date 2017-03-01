package com.gomeplus.oversea.bs.service.user.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by liuliyun-ds on 2017/2/13.
 * 短信记录表实体
 */
@Data
@Entity
@Table(name = "sms_records")
public class SmsRecord {
    /**
     *主键
     */
    @Id
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     *项目(用户USER/交易TRADE)
     */
    private String businessType;
    /**
     *国际电话区号
     */
    private String countryCode;
    /**
     *手机号码
     */
    private String mobile;
    /**
     *发送token
     */
    private String token;
    /**
     *发送序号(10,15,20...)
     */
    private Integer smsSeq;
    /**
     *短信类型(REGIST:注册；FINDPWD:找回密码；BINDPHONE:绑定手机号)
     */
    private String serviceType;
    /**
     *短信验证码
     */
    private String smsCode;
    /**
     *短信内容
     */
    private String msg;
    /**
     *有效时长(单位秒,-1表示消息无时限)
     */
    private Integer validPeriod;
    /**
     *发送时间
     */
    private Date beginTime;
    /**
     *结束时间（不包括）
     */
    private Date endTime;
    /**
     *使用状态(0未使用1已使用2不检查)
     */
    private Byte status;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *修改时间
     */
    private Date updateTime;
    /**
     *备注1
     */
    private String remark1;
    /**
     *备注2
     */
    private String remark2;
    /**
     *备注3
     */
    private String remark3;
}
