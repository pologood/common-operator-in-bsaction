package com.gomeplus.oversea.bs.service.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by liuliyun-ds on 2017/2/17.
 */
@Data
public class ContactsDTO implements Serializable {
    private static final long serialVersionUID = -6446391560040824893L;
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
}
