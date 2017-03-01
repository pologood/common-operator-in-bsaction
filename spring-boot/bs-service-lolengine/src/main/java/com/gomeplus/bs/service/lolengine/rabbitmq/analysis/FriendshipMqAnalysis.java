/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年7月21日 下午3:38:41
 */
package com.gomeplus.bs.service.lolengine.rabbitmq.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lolengine.entity.FriendshipRelation;
import com.gomeplus.bs.service.lolengine.event.FriendshipCacheEvent;
import com.gomeplus.bs.service.lolengine.event.TimeLineUpdateEvent;

import io.terminus.ecp.common.event.CoreEventDispatcher;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description 好友关系mq消息解析
 * @author mojianli
 * @date 2016年11月21日 下午4:40:08
 */
@Slf4j
@Service
public class FriendshipMqAnalysis {


    @Autowired
    private CoreEventDispatcher coreEventDispatcher;

    /**
     * @Description 解析好友关系mq
     * @author mojianli
     * @date 2016年11月22日 下午4:50:23
     * @param action
     * @param gomeUserExpertVo
     */
    public void dealFriendshipEvent(String action, FriendshipRelation friendshipRelation) {

        try {
            Long userId = friendshipRelation.getUserId();
            Long friendId = friendshipRelation.getFriendUserId();

            // 发布好友关系缓存更新事件
            FriendshipCacheEvent friendshipCacheEvent = new FriendshipCacheEvent();
            friendshipCacheEvent.setAction(action);
            friendshipCacheEvent.setUserId(userId);
            friendshipCacheEvent.setFriendId(friendId);
            coreEventDispatcher.publish(friendshipCacheEvent);

            TimeLineUpdateEvent timeLineUpdateEvent = new TimeLineUpdateEvent();
            timeLineUpdateEvent.setUserId(userId);
            timeLineUpdateEvent.setFriendId(friendId);
            if (Constants.Action.ACTION_CREATE.equals(action) || Constants.Action.ACTION_UPDATE.equals(action)) {
                // 成为好友,时间线里好友动态改为有效
                timeLineUpdateEvent.setValid(true);

            } else if (Constants.Action.ACTION_DELETE.equals(action)) {
                // 删除好友,时间线里好友动态改为无效
                timeLineUpdateEvent.setValid(false);
            }
            coreEventDispatcher.publish(timeLineUpdateEvent);
        } catch (Exception e) {
            log.error("解析好友关系异常.action={},friendshipRelation={}", action, friendshipRelation, e);
        }

    }
}
