/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年7月21日 下午3:38:41
 */
package com.gomeplus.bs.service.lolengine.rabbitmq.analysis;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.TimeLine;
import com.gomeplus.bs.interfaces.lol.vo.mq.EntryMqVo;
import com.gomeplus.bs.service.lolengine.event.EntryAuditDeleteEvent;
import com.gomeplus.bs.service.lolengine.event.EntryCreateEvent;
import com.gomeplus.bs.service.lolengine.event.EntryDeleteEvent;
import com.gomeplus.bs.service.lolengine.event.EntryUpdateEvent;
import com.gomeplus.bs.service.lolengine.friendship.cache.FriendshipCache;
import com.gomeplus.bs.service.lolengine.timeline.dao.TimeLineDao;

import io.terminus.ecp.common.event.CoreEventDispatcher;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description 好友动态mq消息解析
 * @author mojianli
 * @date 2016年11月21日 下午4:40:08
 */
@Slf4j
@Service
public class PublishEntryMqAnalysis {

    @Autowired
    private FriendshipCache friendshipCache;

    @Autowired
    private TimeLineDao timeLineDao;

    @Autowired
    private CoreEventDispatcher coreEventDispatcher;

    /**
     * @Description 解析好友动态mq
     * @author mojianli
     * @date 2016年11月22日 下午4:50:23
     * @param action
     * @param publishEntry
     */
    public void dealPublishEntryEvent(String action, EntryMqVo entryMqVo) {

        try {
            Long userId = entryMqVo.getUserId();
            String entryId = entryMqVo.getId();
            Date entryCreateTime = entryMqVo.getCreateTime();
            Boolean isPublic = entryMqVo.getIsPublic();
            Date entryUpdateTime = entryMqVo.getUpdateTime();

            if (Constants.Action.ACTION_CREATE.equals(action)) {

                // 本人时间线里追加该动态
                TimeLine selfTimeLine = new TimeLine();
                selfTimeLine.setId(String.valueOf(timeLineDao.getNewId(Constants.CollectionName.TIME_LINE)));
                selfTimeLine.setOutUserId(userId);
                selfTimeLine.setOutFriendId(userId);
                selfTimeLine.setEntryId(entryId);
                selfTimeLine.setEntryCreateTime(entryCreateTime);
                selfTimeLine.setState(0);
                Date selfDate = new Date();
                selfTimeLine.setCreateTime(selfDate);
                selfTimeLine.setUpdateTime(selfDate);
                timeLineDao.save(selfTimeLine);

                EntryCreateEvent timeLineCreateEvent = new EntryCreateEvent();
                timeLineCreateEvent.setUserId(userId);
                timeLineCreateEvent.setEntryId(entryId);
                // 动态为公开,则在好友的时间线里追加该动态
                if (isPublic) {

                    Set<Long> friendSet = friendshipCache.getFriendIds(userId);
                    if (!CollectionUtils.isEmpty(friendSet)) {
                        for (Long friendUserId : friendSet) {
                            TimeLine timeLine = new TimeLine();
                            timeLine.setId(String.valueOf(timeLineDao.getNewId(Constants.CollectionName.TIME_LINE)));
                            timeLine.setOutUserId(friendUserId);
                            timeLine.setOutFriendId(userId);
                            timeLine.setEntryId(entryId);
                            timeLine.setEntryCreateTime(entryCreateTime);
                            timeLine.setState(0);
                            Date date = new Date();
                            timeLine.setCreateTime(date);
                            timeLine.setUpdateTime(date);
                            timeLineDao.save(timeLine);
                        }
                        timeLineCreateEvent.setFriendSet(friendSet);
                    }
                }

                // 发布时间线新增事件
                coreEventDispatcher.publish(timeLineCreateEvent);

            } else if (Constants.Action.ACTION_UPDATE.equals(action)) {

                // 好友动态审核通过,保存时间线并给好友发消息
                if (isPublic) {

                    // 好友集合
                    Set<Long> friendSet = friendshipCache.getFriendIds(userId);

                    EntryUpdateEvent entryUpdateEvent = new EntryUpdateEvent();
                    entryUpdateEvent.setUserId(userId);
                    entryUpdateEvent.setEntryId(entryId);

                    if (!CollectionUtils.isEmpty(friendSet)) {

                        for (Long friendUserId : friendSet) {
                            TimeLine timeLine = new TimeLine();
                            timeLine.setId(String.valueOf(timeLineDao.getNewId(Constants.CollectionName.TIME_LINE)));
                            timeLine.setOutUserId(userId);
                            timeLine.setOutFriendId(friendUserId);
                            timeLine.setEntryId(entryId);
                            timeLine.setEntryCreateTime(entryCreateTime);
                            timeLine.setState(0);
                            Date date = new Date();
                            timeLine.setCreateTime(date);
                            timeLine.setUpdateTime(date);
                            timeLineDao.save(timeLine);
                        }
                        entryUpdateEvent.setFriendSet(friendSet);
                        // 发布时间线审核更新事件
                        coreEventDispatcher.publish(entryUpdateEvent);

                    }
                } else {

                    EntryAuditDeleteEvent entryAuditDeleteEvent = new EntryAuditDeleteEvent();
                    entryAuditDeleteEvent.setUserId(userId);
                    entryAuditDeleteEvent.setEntryId(entryId);
                    entryAuditDeleteEvent.setEntryUpdateTime(entryUpdateTime);
                    // 发布时间线审核更新事件
                    coreEventDispatcher.publish(entryAuditDeleteEvent);
                }

            } else if (Constants.Action.ACTION_DELETE.equals(action)) {

                EntryDeleteEvent entryDeleteEvent = new EntryDeleteEvent();
                entryDeleteEvent.setUserId(userId);
                entryDeleteEvent.setEntryId(entryId);
                // 发布时间线审核更新事件
                coreEventDispatcher.publish(entryDeleteEvent);
            }
        } catch (Exception e) {
            log.error("解析好友动态异常.action={},EntryMqVo={}", action, entryMqVo, e);
        }
    }
}
