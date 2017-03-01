package com.gomeplus.oversea.bs.service.user.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class UserSnsBind {
    @Id
    private Long id;
    private Long userId;
    private String snsType;
    private String snsUserId;
    private String nickname;
    private String userName;
    private String headImageUrl;
    private Date createTime;
    private Date updateTime;
    private String remark1;
    private String remark2;
    private String remark3;
}
