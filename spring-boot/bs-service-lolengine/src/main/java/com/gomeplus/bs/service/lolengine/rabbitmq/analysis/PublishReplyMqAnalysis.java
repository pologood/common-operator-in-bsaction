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
import com.gomeplus.bs.interfaces.lol.vo.mq.ReplyMqVo;
import com.gomeplus.bs.interfaces.lolengine.vo.MsgAlertVo;
import com.gomeplus.bs.interfaces.lolengine.vo.MsgParamVo;
import com.gomeplus.bs.service.lolengine.authority.ParticipantAuthority;
import com.gomeplus.bs.service.lolengine.friendship.cache.FriendshipCache;
import com.gomeplus.bs.service.lolengine.publishcontent.dao.PublishContentDao;

import io.terminus.ecp.config.center.ConfigCenter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description 好友动态回复mq消息解析
 * @author mojianli
 * @date 2016年11月21日 下午4:40:08
 */
@Slf4j
@Service
public class PublishReplyMqAnalysis {

    @Autowired
    private SendMqProducer sendMqProducer;

    @Autowired
    private FriendshipCache friendshipCache;

    @Autowired
    private ParticipantAuthority participantAuthority;

    @Autowired
    private PublishContentDao publishContentDao;

    @Autowired
    private ConfigCenter configCenter;

    /**
     * @Description 解析好友动态的回复mq
     * @author mojianli
     * @date 2016年11月22日 下午4:50:23
     * @param action
     * @param publishEntry
     */
    public void dealPublishReplyEvent(String action, ReplyMqVo replyMqVo) {

        try {
            Long userId = replyMqVo.getUserId();
            String entryId = replyMqVo.getEntryId();
            Boolean isPublic = replyMqVo.getIsPublic();
            Long entryUserId = replyMqVo.getEntryUserId();
            Date replyCreateTime = replyMqVo.getCreateTime();
            String replyContents = replyMqVo.getContents();
            Boolean isSecondAudit = replyMqVo.getIsSecondAudit();
            Date replyUpdateTime = replyMqVo.getUpdateTime();
            String replyId = replyMqVo.getId();

            // 动态参与人集合
            Set<Long> participantSet = participantAuthority.generateParticipant(entryId);

            Set<Long> friendIdSet = friendshipCache.getFriendIds(userId);

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
            Map<String, String> entryContentMap = new HashMap<String, String>();

            if (Constants.Action.ACTION_CREATE.equals(action)) {

                // 获取动态的内容
                generateContentMap(entryId, entryContentMap);

                for (Long participant : participantSet) {
                    Map<String, Object> extMap = new HashMap<String, Object>();
                    extMap.put("reminderType", "1021");
                    extMap.put("userId", userId);
                    extMap.put("createTime", String.valueOf(replyCreateTime.getTime()));
                    extMap.put("entryContent", entryContentMap.get("entryContent"));
                    extMap.put("entryImg", entryContentMap.get("entryImg"));
                    extMap.put("entryId", entryId);
                    extMap.put("replyId", replyId);

                    MsgParamVo msgParamVo = new MsgParamVo();
                    msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_REPLY_NOTIFICATION);
                    msgParamVo.setMsgType(0);
                    msgParamVo.setReceiveUserId(String.valueOf(participant));
                    msgParamVo.setReceiveUserRole(0);
                    msgParamVo.setIsNotCount(1);
                    msgParamVo.setSendMessage(replyContents);
                    msgParamVo.setExtInfoMap(extMap);
                    msgList.add(msgParamVo);
                }

            } else if (Constants.Action.ACTION_UPDATE.equals(action)) {

                // 评论是公开的,给参与人发消息
                if (isPublic) {

                    // 获取动态的内容
                    generateContentMap(entryId, entryContentMap);

                    for (Long participant : participantSet) {
                        Map<String, Object> extMap = new HashMap<String, Object>();
                        extMap.put("reminderType", "1021");
                        extMap.put("userId", userId);
                        extMap.put("createTime", String.valueOf(replyCreateTime.getTime()));
                        extMap.put("entryContent", entryContentMap.get("entryContent"));
                        extMap.put("entryImg", entryContentMap.get("entryImg"));
                        extMap.put("entryId", entryId);
                        extMap.put("replyId", replyId);

                        MsgParamVo msgParamVo = new MsgParamVo();
                        msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_REPLY_NOTIFICATION);
                        msgParamVo.setMsgType(0);
                        msgParamVo.setReceiveUserId(String.valueOf(participant));
                        msgParamVo.setReceiveUserRole(0);
                        msgParamVo.setIsNotCount(1);
                        msgParamVo.setSendMessage(replyContents);
                        msgParamVo.setExtInfoMap(extMap);
                        msgList.add(msgParamVo);
                    }

                } else {

                    // 获取动态的内容
                    generateContentMap(entryId, entryContentMap);

                    Boolean isPubFirst = Boolean.valueOf(configCenter.get(Constants.SOCIAL_AUDIT_PUBFIRST));

                    // 先发后审，给所有参与人发消息
                    if (isPubFirst) {
                        // 评论审核失败,给评论发布人发审核不通过提醒
                        Map<String, Object> extMapOwner = new HashMap<String, Object>();
                        extMapOwner.put("reminderType", "230");
                        extMapOwner.put("times", String.valueOf(replyUpdateTime.getTime()));
                        extMapOwner.put("entryId", entryId);
                        extMapOwner.put("entryReplyId", replyId);
                        extMapOwner.put("entryReplyContent", replyContents);

                        MsgParamVo msgParamVoOwner = new MsgParamVo();
                        msgParamVoOwner.setSendUserId(Constants.ImSysAccount.LOL_SYSTEM_NOTIFICATION);
                        msgParamVoOwner.setMsgType(0);
                        msgParamVoOwner.setReceiveUserId(String.valueOf(userId));
                        msgParamVoOwner.setReceiveUserRole(0);
                        msgParamVoOwner.setSendMessage("您发布的评论审核未通过");
                        msgParamVoOwner.setExtInfoMap(extMapOwner);
                        msgList.add(msgParamVoOwner);

                        // 评论审核失败,给参与人发删除评论消息
                        for (Long participant : participantSet) {
                            Map<String, Object> extMap = new HashMap<String, Object>();
                            extMap.put("reminderType", "1022");
                            extMap.put("userId", userId);
                            extMap.put("createTime", String.valueOf(replyCreateTime.getTime()));
                            extMap.put("entryContent", entryContentMap.get("entryContent"));
                            extMap.put("entryImg", entryContentMap.get("entryImg"));
                            extMap.put("entryId", entryId);
                            extMap.put("replyId", replyId);

                            MsgParamVo msgParamVo = new MsgParamVo();
                            msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_REPLY_NOTIFICATION);
                            msgParamVo.setMsgType(0);
                            msgParamVo.setReceiveUserId(String.valueOf(participant));
                            msgParamVo.setReceiveUserRole(0);
                            msgParamVo.setIsNotCount(1);
                            msgParamVo.setSendMessage("该评论已删除");
                            msgParamVo.setExtInfoMap(extMap);
                            msgList.add(msgParamVo);
                        }
                    } else {
                        // 先审后发模式下，二次审核不通过
                        if (isSecondAudit) {
                            // 给评论发布人发审核不通过提醒
                            Map<String, Object> extMapOwner = new HashMap<String, Object>();
                            extMapOwner.put("reminderType", "230");
                            extMapOwner.put("times", String.valueOf(replyUpdateTime.getTime()));
                            extMapOwner.put("entryId", entryId);
                            extMapOwner.put("entryReplyId", replyId);
                            extMapOwner.put("entryReplyContent", replyContents);

                            MsgParamVo msgParamVoOwner = new MsgParamVo();
                            msgParamVoOwner.setSendUserId(Constants.ImSysAccount.LOL_SYSTEM_NOTIFICATION);
                            msgParamVoOwner.setMsgType(0);
                            msgParamVoOwner.setReceiveUserId(String.valueOf(userId));
                            msgParamVoOwner.setReceiveUserRole(0);
                            msgParamVoOwner.setSendMessage("您发布的评论审核未通过");
                            msgParamVoOwner.setExtInfoMap(extMapOwner);
                            msgList.add(msgParamVoOwner);
                            // 给动态参与人发评论删除覆盖消息
                            for (Long participant : participantSet) {
                                Map<String, Object> extMap = new HashMap<String, Object>();
                                extMap.put("reminderType", "1022");
                                extMap.put("userId", userId);
                                extMap.put("createTime", String.valueOf(replyCreateTime.getTime()));
                                extMap.put("entryContent", entryContentMap.get("entryContent"));
                                extMap.put("entryImg", entryContentMap.get("entryImg"));
                                extMap.put("entryId", entryId);
                                extMap.put("replyId", replyId);

                                MsgParamVo msgParamVo = new MsgParamVo();
                                msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_REPLY_NOTIFICATION);
                                msgParamVo.setMsgType(0);
                                msgParamVo.setReceiveUserId(String.valueOf(participant));
                                msgParamVo.setReceiveUserRole(0);
                                msgParamVo.setIsNotCount(1);
                                msgParamVo.setSendMessage("该评论已删除");
                                msgParamVo.setExtInfoMap(extMap);
                                msgList.add(msgParamVo);
                            }
                        } else {
                            // 先审后发，给评论发布人发审核不通过消息
                            Map<String, Object> extMapOwner = new HashMap<String, Object>();
                            extMapOwner.put("reminderType", "230");
                            extMapOwner.put("times", String.valueOf(replyUpdateTime.getTime()));
                            extMapOwner.put("entryId", entryId);
                            extMapOwner.put("entryReplyId", replyId);
                            extMapOwner.put("entryReplyContent", replyContents);

                            MsgParamVo msgParamVo = new MsgParamVo();
                            msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_SYSTEM_NOTIFICATION);
                            msgParamVo.setMsgType(0);
                            msgParamVo.setReceiveUserId(String.valueOf(userId));
                            msgParamVo.setReceiveUserRole(0);
                            msgParamVo.setSendMessage("您发布的评论审核未通过");
                            msgParamVo.setExtInfoMap(extMapOwner);
                            msgList.add(msgParamVo);
                        }

                    }

                }
            } else if (Constants.Action.ACTION_DELETE.equals(action)) {

                // 获取动态的内容
                generateContentMap(entryId, entryContentMap);

                for (Long participant : participantSet) {
                    Map<String, Object> extMap = new HashMap<String, Object>();
                    extMap.put("reminderType", "1023");
                    extMap.put("userId", userId);
                    extMap.put("createTime", String.valueOf(replyCreateTime.getTime()));
                    extMap.put("entryContent", entryContentMap.get("entryContent"));
                    extMap.put("entryImg", entryContentMap.get("entryImg"));
                    extMap.put("entryId", entryId);
                    extMap.put("replyId", replyId);

                    MsgParamVo msgParamVo = new MsgParamVo();
                    msgParamVo.setSendUserId(Constants.ImSysAccount.LOL_REPLY_NOTIFICATION);
                    msgParamVo.setMsgType(0);
                    msgParamVo.setReceiveUserId(String.valueOf(participant));
                    msgParamVo.setReceiveUserRole(0);
                    msgParamVo.setSendMessage("该评论已删除");
                    msgParamVo.setExtInfoMap(extMap);
                    msgList.add(msgParamVo);
                }

            }
            msgAlertVo.setBatchFlag("batch");
            msgAlertVo.setList(msgList);
            // 发送mq消息给好友推送好友动态消息
            sendMqProducer.sendVo2Mq(entryId, Constants.RoutingKey.IM_REPLY_MESSAGE, SubmitAction.CREATE, msgAlertVo);
        } catch (Exception e) {
            log.error("解析好友动态的回复异常.action={},replyMqVo={}", action, replyMqVo, e);
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
