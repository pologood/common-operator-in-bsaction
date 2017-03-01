/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年7月21日 下午7:31:31
 */
package com.gomeplus.bs.service.lolengine.event.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.gomeplus.bs.common.dubbor.rabbitmq.SendMqProducer;
import com.gomeplus.bs.common.dubbor.rabbitmq.common.SubmitAction;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.entity.TimeLine;
import com.gomeplus.bs.interfaces.lolengine.vo.MsgAlertVo;
import com.gomeplus.bs.interfaces.lolengine.vo.MsgParamVo;
import com.gomeplus.bs.service.lolengine.event.EntryAuditDeleteEvent;
import com.gomeplus.bs.service.lolengine.event.EntryCreateEvent;
import com.gomeplus.bs.service.lolengine.event.EntryDeleteEvent;
import com.gomeplus.bs.service.lolengine.event.EntryUpdateEvent;
import com.gomeplus.bs.service.lolengine.mongo.vo.OutputObject;
import com.gomeplus.bs.service.lolengine.publishcontent.dao.PublishContentDao;
import com.gomeplus.bs.service.lolengine.timeline.dao.TimeLineDao;
import com.google.common.eventbus.Subscribe;

import io.terminus.ecp.common.event.EventListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 好友动态事件监听类
 * @author mojianli
 * @date 2016年7月21日 下午7:31:31
 */
@Slf4j
@Component
public class EntryEventListener implements EventListener {

    @Autowired
    private SendMqProducer sendMqProducer;

    @Autowired
    private TimeLineDao timeLineDao;

    @Autowired
    private PublishContentDao publishContentDao;


    /**
     * @Description 时间线新增事件监听方法
     * @author mojianli
     * @date 2016年7月21日 下午8:26:28
     * @param event
     */
    @Subscribe
    public void onEntryCreateEvent(EntryCreateEvent event) {

        try {
            Long userId = event.getUserId();
            String entryId = event.getEntryId();
            Set<Long> friendSet = event.getFriendSet();

            log.info("处理[动态新增]事件" + "EntryCreateEvent=" + event.toString());

            if (!CollectionUtils.isEmpty(friendSet)) {

                MsgAlertVo msgAlertVo = new MsgAlertVo();

                List<MsgParamVo> msgList = new ArrayList<MsgParamVo>();
                for (Long friendUserId : friendSet) {
                    Map<String, Object> extMap = new HashMap<String, Object>();
                    extMap.put("userId", userId);

                    MsgParamVo msgParamVo = new MsgParamVo();
                    msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_REDTIP_NOTIFICATION);
                    msgParamVo.setMsgType(0);
                    msgParamVo.setReceiveUserId(String.valueOf(friendUserId));
                    msgParamVo.setReceiveUserRole(0);
                    msgParamVo.setIsNotCount(1);
                    msgParamVo.setSendMessage("你的好友发新动态啦！");
                    msgParamVo.setExtInfoMap(extMap);
                    msgList.add(msgParamVo);
                }
                msgAlertVo.setBatchFlag("batch");
                msgAlertVo.setList(msgList);
                try {
                    // 发送mq消息给好友推送好友动态消息
                    sendMqProducer.sendVo2Mq(entryId, Constants.RoutingKey.IM_REDTIP_MESSAGE, SubmitAction.CREATE,
                            msgAlertVo);
                } catch (Exception e) {
                    log.error("send im message mq error!entryId={}", entryId, e);
                }
                // 时间线2000条以外数据删除
                friendSet.add(userId);
                removeTimeLine(friendSet);
            }
        } catch (Exception e) {
            log.error("处理[动态新增]事件异常 ", e);
        }
    }

    /**
     * @Description 维护时间线为2000队列
     * @author mojianli
     * @date 2016年12月27日 下午4:09:57
     * @param friendSet
     */
    private void removeTimeLine(Set<Long> friendSet) {

        for (Long friendUserId : friendSet) {

            AggregationOperation match = Aggregation.match(Criteria.where("outUserId").is(friendUserId));
            AggregationOperation group = Aggregation.group("outUserId").count().as("count");

            Aggregation agg = Aggregation.newAggregation(match, group);
            AggregationResults<OutputObject> result = timeLineDao.aggregate(agg);

            List<OutputObject> resultList = result.getMappedResults();
            for (OutputObject outputObject : resultList) {
                // 时间线动态数
                int count = outputObject.getCount();
                int diff = count - 2000;
                if (diff > 0) {
                    Query removeQuery = new Query(Criteria.where("outUserId").is(friendUserId))
                            .with(new Sort(Direction.ASC, "createTime")).limit(diff);
                    timeLineDao.remove(removeQuery);
                }
            }
        }
    }

    /**
     * @Description 好友动态更新事件监听方法
     * @author mojianli
     * @date 2016年7月21日 下午8:26:28
     * @param event
     */
    @Subscribe
    public void onEntryUpateEvent(EntryUpdateEvent event) {

        try {
            Long userId = event.getUserId();
            String entryId = event.getEntryId();
            Set<Long> friendSet = event.getFriendSet();

            log.info("处理[动态更新]事件" + "EntryUpdateEvent=" + event.toString());

            if (!CollectionUtils.isEmpty(friendSet)) {

                MsgAlertVo msgAlertVo = new MsgAlertVo();

                List<MsgParamVo> msgList = new ArrayList<MsgParamVo>();
                for (Long friendUserId : friendSet) {
                    Map<String, Object> extMap = new HashMap<String, Object>();
                    extMap.put("userId", userId);

                    MsgParamVo msgParamVo = new MsgParamVo();
                    msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_REDTIP_NOTIFICATION);
                    msgParamVo.setMsgType(0);
                    msgParamVo.setReceiveUserId(String.valueOf(friendUserId));
                    msgParamVo.setReceiveUserRole(0);
                    msgParamVo.setIsNotCount(1);
                    msgParamVo.setSendMessage("你的好友发新动态啦！");
                    msgParamVo.setExtInfoMap(extMap);
                    msgList.add(msgParamVo);
                }
                msgAlertVo.setBatchFlag("batch");
                msgAlertVo.setList(msgList);
                try {
                    // 发送mq消息给好友推送好友动态消息
                    sendMqProducer.sendVo2Mq(entryId, Constants.RoutingKey.IM_REDTIP_MESSAGE, SubmitAction.CREATE,
                            msgAlertVo);
                } catch (Exception e) {
                    log.error("send im message mq error!entryId={}", entryId, e);
                }
            }
        } catch (Exception e) {
            log.error("处理[动态更新]事件异常 ", e);
        }
    }

    /**
     * @Description 好友动态审核不通过事件监听方法
     * @author mojianli
     * @date 2016年7月21日 下午8:26:28
     * @param event
     */
    @Subscribe
    public void onEntryAuditDeleteEvent(EntryAuditDeleteEvent event) {

        try {
            Long userId = event.getUserId();
            String entryId = event.getEntryId();
            Date entryUpdateTime = event.getEntryUpdateTime();

            log.info("处理[动态审核不通过删除]事件" + "EntryDeleteEvent=" + event.toString());

            Map<String, String> entryContentMap = new HashMap<String, String>();

            // 获取动态的内容
            generateContentMap(entryId, entryContentMap);

            MsgAlertVo msgAlertVo = new MsgAlertVo();

            List<MsgParamVo> msgList = new ArrayList<MsgParamVo>();
            Map<String, Object> extMap = new HashMap<String, Object>();
            extMap.put("entryContent", entryContentMap.get("entryContent"));
            extMap.put("entryImg", entryContentMap.get("entryImg"));
            extMap.put("entryId", entryId);
            extMap.put("times", String.valueOf(entryUpdateTime.getTime()));
            extMap.put("reminderType", "231");

            MsgParamVo msgParamVo = new MsgParamVo();
            msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_SYSTEM_NOTIFICATION);
            msgParamVo.setMsgType(0);
            msgParamVo.setReceiveUserId(String.valueOf(userId));
            msgParamVo.setReceiveUserRole(0);
            msgParamVo.setSendMessage("您发布的动态审核未通过");
            msgParamVo.setExtInfoMap(extMap);
            msgList.add(msgParamVo);
            msgAlertVo.setBatchFlag("batch");
            msgAlertVo.setList(msgList);

            try {
                // 发送mq消息给好友推送好友动态消息
                sendMqProducer.sendVo2Mq(entryId, Constants.RoutingKey.IM_REPLY_MESSAGE, SubmitAction.CREATE,
                        msgAlertVo);
            } catch (Exception e) {
                log.error("send im message mq error!entryId={}", entryId, e);
            }

            // 好友动态删除
            Query query = new Query(Criteria.where("entryId").is(entryId));
            query.fields().include("_id");

            List<TimeLine> list = timeLineDao.findAll(query);

            if (null != list && list.size() > 0) {
                for (TimeLine timeLine : list) {
                    String primaryKey = timeLine.getId();
                    Query removeQuery = new Query(Criteria.where("_id").is(primaryKey));
                    timeLineDao.remove(removeQuery);
                }
            }
        } catch (Exception e) {
            log.error("处理[动态审核不通过删除]事件异常 ", e);
        }
    }

    /**
     * @Description 好友动态删除事件监听方法
     * @author mojianli
     * @date 2016年7月21日 下午8:26:28
     * @param event
     */
    @Subscribe
    public void onEntryDeleteEvent(EntryDeleteEvent event) {

        try {
            Long userId = event.getUserId();
            String entryId = event.getEntryId();

            log.info("处理[动态删除]事件" + "EntryDeleteEvent=" + event.toString());

            // 好友动态删除
            Query query = new Query(Criteria.where("entryId").is(entryId));
            query.fields().include("_id");

            List<TimeLine> list = timeLineDao.findAll(query);

            if (null != list && list.size() > 0) {
                for (TimeLine timeLine : list) {
                    String primaryKey = timeLine.getId();
                    Query removeQuery = new Query(Criteria.where("_id").is(primaryKey));
                    timeLineDao.remove(removeQuery);
                }
            }
        } catch (Exception e) {
            log.error("处理[动态删除]事件异常 ", e);
        }
    }

    /**
     * @Description 获取动态的内容
     * @author mojianli
     * @date 2017年1月19日 下午2:49:41
     * @param entryContentMap
     */
    private void generateContentMap(String entryId, Map<String, String> entryContentMap) {

        String entryContent = "";
        String entryImg = "";

        PublishContent publishContent = publishContentDao.findById(entryId);
        Object[] components = publishContent.getComponents();
        if (null != components) {
            for (int i = 0; i < components.length; i++) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) components[i];
                if (null != map && map.containsKey("type")) {
                    String type = String.valueOf(map.get("type"));
                    // 判断话题包含图片、商品或者视频
                    if ("image".equals(type) && StringUtils.isBlank(entryImg)) {
                        entryImg = String.valueOf(map.get("url"));
                    } else if ("text".equals(type) && StringUtils.isBlank(entryContent)) {
                        entryContent = String.valueOf(map.get("text"));
                    }
                }
            }
        }
        entryContentMap.put("entryContent", entryContent);
        entryContentMap.put("entryImg", entryImg);
    }
}
