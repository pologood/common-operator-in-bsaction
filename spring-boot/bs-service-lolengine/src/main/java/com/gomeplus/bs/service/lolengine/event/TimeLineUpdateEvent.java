/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年7月21日 下午7:50:23
 */
package com.gomeplus.bs.service.lolengine.event;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * @Description 时间线更新事件定义
 * @author mojianli
 * @date 2016年7月21日 下午7:50:23
 */
@Data
@ToString
public class TimeLineUpdateEvent implements Serializable {

    private static final long serialVersionUID = -1129220966535053127L;

    // 是否有效
    private boolean isValid;
    // 用户id
    private Long userId;
    // 好友id
    private Long friendId;

}
