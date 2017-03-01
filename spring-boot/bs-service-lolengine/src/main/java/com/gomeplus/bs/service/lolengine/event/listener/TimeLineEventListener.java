/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年7月21日 下午7:31:31
 */
package com.gomeplus.bs.service.lolengine.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.gomeplus.bs.service.lolengine.event.TimeLineUpdateEvent;
import com.gomeplus.bs.service.lolengine.timeline.dao.TimeLineDao;
import com.google.common.eventbus.Subscribe;

import io.terminus.ecp.common.event.EventListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 时间线有效性更新事件监听类
 * @author mojianli
 * @date 2016年7月21日 下午7:31:31
 */
@Slf4j
@Component
public class TimeLineEventListener implements EventListener {

    @Autowired
    private TimeLineDao timeLineDao;


    /**
     * @Description 时间线更新事件监听方法
     * @author mojianli
     * @date 2016年7月21日 下午8:26:28
     * @param event
     */
    @Subscribe
    public void onTimeLineUpdateEvent(TimeLineUpdateEvent event) {

        try {
            boolean isValid = event.isValid();
            Long userId = event.getUserId();
            Long friendId = event.getFriendId();

            log.info("处理时间线更新事件,TimeLineUpdateEvent=" + event.toString());

            Update updateCase = new Update();
            if (isValid) {
                updateCase.set("state", 0);
            } else {
                updateCase.set("state", 1);
            }

            Query updateQuery = new Query(Criteria.where("outUserId").is(userId).and("outFriendId").is(friendId));

            // 更新用户的时间线
            timeLineDao.updateMulti(updateQuery, updateCase);

            Query friendUpdateQuery = new Query(Criteria.where("outUserId").is(friendId).and("outFriendId").is(userId));

            // 更新用户好友的时间线
            timeLineDao.updateMulti(friendUpdateQuery, updateCase);

        } catch (Exception e) {
            log.error("处理时间线更新事件异常 ", e);
        }

    }
}
