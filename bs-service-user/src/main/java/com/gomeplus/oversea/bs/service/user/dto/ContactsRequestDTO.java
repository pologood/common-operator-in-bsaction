package com.gomeplus.oversea.bs.service.user.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuliyun-ds on 2017/2/15.
 * 通讯录请求体
 */
@Data
public class ContactsRequestDTO implements Serializable{
    private static final long serialVersionUID = 7765085546125045264L;
    /**
     * 通讯录列表
     */
    private List<ContactsDTO> contacts;


}
