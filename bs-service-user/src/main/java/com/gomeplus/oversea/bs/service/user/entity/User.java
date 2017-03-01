package com.gomeplus.oversea.bs.service.user.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;


@Data
@Entity
public class User {

    @Id
    private Long id;
    /**用户账号*/
    private String userName;
    /**昵称*/
    private String nickname;
    /**密码*/
    private String password;
    /**手机区域码*/
    private String countryCode;
    /**手机号*/
    private String mobile;
    /**头像*/
    private String headImageUrl;
    /**状态*/
    private String status;
    /**注册来源*/
    private String registerOrigin;
    /**邀请人ID*/
    private Long inviteUserId;
    /**IM用户ID*/
    private String imUserId;
    private Date createTime;
    private Date updateTime;
    private String remark1;
    private String remark2;
    private String remark3;

}
