package com.gomeplus.oversea.bs.service.user.dto.user;

import lombok.Data;

/**
 * Created by baixiangzhu on 2017/2/14.
 */
@Data
public class UserResponseDto {

    private UserVO user;
    private String loginToken;

}
