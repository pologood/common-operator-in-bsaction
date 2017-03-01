package com.gomeplus.oversea.bs.service.user.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by liuliyun-ds on 2017/2/15.
 * 通讯录返回dto
 */
@Data
public class ContactsResponseDTO implements Serializable {
    private static final long serialVersionUID = 415860485569948533L;

    private List<Contacts> contacts;

    @Data
    public class Contacts implements Serializable{
        private static final long serialVersionUID = -5356053679000913142L;
        /**
         * 国际电话区号
         */
        private String countryCode;
        /**
         * 手机号
         */
        private String mobile;
        /**
         * 联系人名称
         */
        private String name;
        /**
         * 联系人是否注册用户
         */
        private boolean isBound;

        /**
         * boolean类型如果命名前带有is,返回字段会直接省略掉is
         * 此处做特殊处理,以后boolean字段命名建议不带is
         * @return
         */
        @JsonProperty(value="isBound")
        public boolean getIsBound(){
            return isBound;
        }
        /**
         * 用户信息
         */
//        @JsonInclude(Include.NON_NULL) 
        private User user;

//        @JsonInclude(Include.NON_NULL)
        @Data
        public class User implements Serializable{

            private static final long serialVersionUID = 452731106205005871L;

            /**
             * 用户id
             */
            private String id;
            /**
             * imUserId
             */
            private String imUserId;
            /**
             * 用户名称
             */
            private String userName;
            /**
             * 用户昵称
             */
            private String nickname;
            /**
             * 用户头像
             */
            private String facePicUrl;
            /**
             * 用户注册时间
             */
            private Long registerTime;
        }
    }

}
