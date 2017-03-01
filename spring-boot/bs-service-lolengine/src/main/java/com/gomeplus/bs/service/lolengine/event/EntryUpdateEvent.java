/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年7月21日 下午7:50:23
 */
package com.gomeplus.bs.service.lolengine.event;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.ToString;

/**
 * @Description 动态更新事件定义
 * @author mojianli
 * @date 2016年7月21日 下午7:50:23
 */
@ToString
public class EntryUpdateEvent extends EntryEvent implements Serializable {

    private static final long serialVersionUID = 6390763301568105889L;
    // 动态发布人
    private Long userId;
    // 动态id
    private String entryId;
    // 动态发布人的好友集合
    private Set<Long> friendSet = new HashSet<Long>();

}
