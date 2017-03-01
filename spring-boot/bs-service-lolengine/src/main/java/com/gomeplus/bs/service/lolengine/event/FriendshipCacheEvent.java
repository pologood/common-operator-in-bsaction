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
 * @Description 好友关系缓存事件
 * @author mojianli
 * @date 2016年7月21日 下午7:50:23
 */
@Data
@ToString
public class FriendshipCacheEvent implements Serializable {

    private static final long serialVersionUID = 5463634408824797362L;

    // 动作类型
    private String action;
    // 用户id
    private Long userId;
    // 好友id
    private Long friendId;

}
