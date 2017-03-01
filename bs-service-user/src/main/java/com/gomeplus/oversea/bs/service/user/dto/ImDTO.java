package com.gomeplus.oversea.bs.service.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by liuliyun-ds on 2017/2/15.
 * im接口dto
 */
@Data
public class ImDTO implements Serializable {
    private static final long serialVersionUID = 1728146047516968956L;

    private String message;
    private Integer code;
    private Data data;

    @lombok.Data
    public class Data implements Serializable{
        private static final long serialVersionUID = 7889833676816781627L;
        /**
         * 项目用户id
         */
        private String uid;
        /**
         * im用户id
         */
        private String imUserId;
        /**
         * imToken
         */
        private String token;
        /**
         * imToken过期时间
         */
        private Long tokenExpires;
    }
}
