package com.gomeplus.oversea.bs.service.user.dto.user;

import com.gomeplus.oversea.bs.service.user.entity.User;
import lombok.Data;

/**
 * Created by baixiangzhu on 2017/2/14.
 */
@Data
public class UserVO {

    private String id;
    /**用户账号*/
    private String userName;
    /**昵称*/
    private String nickname;
    /**手机区域码*/
    private String countryCode;
    /**手机号*/
    private String mobile;
    /**头像*/
    private String facePicUrl;
    /**推荐人ID*/
    private String refereeUserId;
    /**IM用户ID*/
    private String imUserId;
    /**注册时间*/
    private Long registerTime;

    public static UserVO fromModel(User user) {
        UserVO vo = new UserVO();
        if (user.getId() != null) {
            vo.setId(user.getId().toString());
        }
        vo.setUserName(user.getUserName());
        vo.setNickname(user.getNickname());
        vo.setCountryCode(user.getCountryCode());
        vo.setMobile(user.getMobile());
        vo.setFacePicUrl(user.getHeadImageUrl());
        if (user.getInviteUserId() != null){
            vo.setRefereeUserId(user.getInviteUserId().toString());
        }
        vo.setImUserId(user.getImUserId());
        vo.setRegisterTime(user.getCreateTime().getTime());
        return vo;
    }
}
