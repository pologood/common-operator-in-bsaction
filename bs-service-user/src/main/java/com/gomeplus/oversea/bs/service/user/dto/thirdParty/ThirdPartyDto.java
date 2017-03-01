package com.gomeplus.oversea.bs.service.user.dto.thirdParty;

import lombok.Data;

/**
 * Created by zhangxinxing on 2017/2/14.
 */
@Data
public class ThirdPartyDto {

    /**第三方登录id*/
    private String id;
    /**第三方用户昵称*/
    private String name;
    /**第三方用户头像*/
    private String facePicUrl;
    /**FaceBook:"FACEBOOK"*/
    private String type;


}
