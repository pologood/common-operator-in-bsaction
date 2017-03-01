/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年7月21日 下午3:38:41
 */
package com.gomeplus.bs.service.lolengine.rabbitmq.analysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gomeplus.bs.common.dubbor.rabbitmq.SendMqProducer;
import com.gomeplus.bs.common.dubbor.rabbitmq.common.SubmitAction;
import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.vo.mq.PraiseMqVo;
import com.gomeplus.bs.interfaces.lolengine.vo.MsgAlertVo;
import com.gomeplus.bs.interfaces.lolengine.vo.MsgParamVo;
import com.gomeplus.bs.service.lolengine.authority.ParticipantAuthority;
import com.gomeplus.bs.service.lolengine.friendship.cache.FriendshipCache;
import com.gomeplus.bs.service.lolengine.publishcontent.dao.PublishContentDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description 好友动态的点赞mq消息解析
 * @author mojianli
 * @date 2016年11月21日 下午4:40:08
 */
@Slf4j
@Service
public class PraiseMqAnalysis {

    @Autowired
    private FriendshipCache friendshipCache;

    @Autowired
    private SendMqProducer sendMqProducer;

    @Autowired
    private ParticipantAuthority participantAuthority;

    @Autowired
    private PublishContentDao publishContentDao;

    /**
     * @Description 解析好友动态的点赞mq
     * @author mojianli
     * @date 2016年11月22日 下午4:50:23
     * @param action
     * @param praise
     */
    public void dealPraiseEvent(String action, PraiseMqVo praiseMqVo) {

        try {
            String praiseId = praiseMqVo.getId();
            String entryId = praiseMqVo.getEntryId();
            Long userId = praiseMqVo.getUserId();
            Date praiseCreateTime = praiseMqVo.getCreateTime();
            // 动态创建人
            Long entryUserId = praiseMqVo.getEntryUserId();

            if (Constants.Action.ACTION_CREATE.equals(action)) {

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

                Set<Long> friendIdSet = friendshipCache.getFriendIds(userId);

                // 动态参与人集合
                Set<Long> participantSet = participantAuthority.generateParticipant(entryId);
                // 与好友集合求交集
                participantSet.retainAll(friendIdSet);

                // 参与人排除本次点赞人
                participantSet.remove(userId);
                // 如果是好友点赞，则把动态发布人加上
                if (!userId.equals(entryUserId)) {
                    participantSet.add(entryUserId);
                }

                MsgAlertVo msgAlertVo = new MsgAlertVo();
                List<MsgParamVo> msgList = new ArrayList<MsgParamVo>();
                for (Long participant : participantSet) {
                    Map<String, Object> extMap = new HashMap<String, Object>();
                    extMap.put("reminderType", "1011");
                    extMap.put("userId", userId);
                    extMap.put("createTime", String.valueOf(praiseCreateTime.getTime()));
                    extMap.put("entryContent", entryContent);
                    extMap.put("entryImg", entryImg);
                    extMap.put("entryId", entryId);
                    extMap.put("praiseId", praiseId);

                    MsgParamVo msgParamVo = new MsgParamVo();
                    msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_REPLY_NOTIFICATION);
                    msgParamVo.setMsgType(0);
                    msgParamVo.setReceiveUserId(String.valueOf(participant));
                    msgParamVo.setReceiveUserRole(0);
                    msgParamVo.setIsNotCount(1);
                    msgParamVo.setSendMessage("收到新的点赞提醒！");
                    msgParamVo.setExtInfoMap(extMap);
                    msgList.add(msgParamVo);
                }
                msgAlertVo.setBatchFlag("batch");
                msgAlertVo.setList(msgList);
                // 发送mq消息给好友推送好友动态消息
                sendMqProducer.sendVo2Mq(entryId, Constants.RoutingKey.IM_REPLY_MESSAGE, SubmitAction.CREATE,
                        msgAlertVo);

            } else if (Constants.Action.ACTION_UPDATE.equals(action)) {

            } else if (Constants.Action.ACTION_DELETE.equals(action)) {

            }
        } catch (Exception e) {
            log.error("解析好友动态的点赞异常.action={},praiseMqVo={}", action, praiseMqVo, e);
        }
    }
}
