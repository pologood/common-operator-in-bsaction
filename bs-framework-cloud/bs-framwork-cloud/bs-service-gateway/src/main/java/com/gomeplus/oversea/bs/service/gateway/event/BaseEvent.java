package com.gomeplus.oversea.bs.service.gateway.event;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by neowyp on 2016/7/9.
 * Author   : wangyunpeng
 * Date     : 2016/7/9
 * Time     : 9:18
 * Version  : V1.0
 * Desc     : 封装事件基类
 */
@Data
public class BaseEvent implements Serializable {

    private static final long serialVersionUID = 6950844257169553509L;
    //事件表示，用于追踪
    private String id;
    //存储上下文信息
    private Map<String, Object> context;
}
