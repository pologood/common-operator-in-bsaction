package com.gomeplus.oversea.bs.service.user.dto.password;

import lombok.Data;

/**
 * 重置密码页面传过来的对象
 * Created by yangzongbin on 2017/2/15.
 */
@Data
public class PasswordVO {


    /**
     * 验证码token
     */
    private String smsToken;
    /**
     * 密码
     */
    private String password;
    /**
     * 确认密码
     */
    private String confirmedPassword;
}
