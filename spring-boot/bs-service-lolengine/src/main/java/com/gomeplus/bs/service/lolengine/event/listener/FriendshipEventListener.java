/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年7月21日 下午7:31:31
 */
package com.gomeplus.bs.service.lolengine.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gomeplus.bs.service.lolengine.event.FriendshipCacheEvent;
import com.gomeplus.bs.service.lolengine.friendship.cache.FriendshipCache;
import com.google.common.eventbus.Subscribe;

import io.terminus.ecp.common.event.EventListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 好友关系事件监听类
 * @author mojianli
 * @date 2016年7月21日 下午7:31:31
 */
@Slf4j
@Component
public class FriendshipEventListener implements EventListener {

    @Autowired
    private FriendshipCache friendshipCache;

    /**
     * @Description 时间线事件监听方法
     * @author mojianli
     * @date 2016年7月21日 下午8:26:28
     * @param event
     */
    @Subscribe
    public void onFriendshipCacheEvent(FriendshipCacheEvent event) {

        try {
            log.info("处理好友关系缓存事件,FriendshipCacheEvent=" + event.toString());

            Long userId = event.getUserId();
            String action = event.getAction();
            Long friendId = event.getFriendId();

            friendshipCache.updateFriendCache(action, userId, friendId);
        } catch (Exception e) {
            log.error("处理好友关系缓存事件异常 ",e);
        }
    }
}
