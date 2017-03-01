/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年12月23日 下午5:11:22
 */
package com.gomeplus.bs.service.lolengine.authority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishReply;
import com.gomeplus.bs.service.lolengine.publishreply.dao.PublishReplyDao;

/**
 * @Description 好友动态参与者权限
 * @author mojianli
 * @date 2016年12月23日 下午5:11:22
 */
@Component
public class ParticipantAuthority {

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @Autowired
    private PublishReplyDao publishReplyDao;

    /**
     * @Description 根据好友动态获取该动态的参与者（点赞人和评论者）
     * @author mojianli
     * @date 2016年12月23日 下午5:13:11
     * @param entryId
     * @return
     */
    public Set<Long> generateParticipant(String entryId) {

        String praiseSetKey = Constants.RedisKey.praiseSet(entryId);

        // 该动态的所有点赞用户
        Set<Long> praiseSet = redisTemplate.opsForSet().members(praiseSetKey);

        // 动态参与人集合
        Set<Long> participantSet = new HashSet<Long>();
        // 如果不是动态创建人给自己点赞,给包含创建人在内的好友参与者发提醒消息
        participantSet.addAll(praiseSet);

        Query replyQuery =
                new Query(Criteria.where("entryId").is(entryId).and("isPublic").is(true).and("isDelete").is(false));
        replyQuery.fields().exclude("_id");
        replyQuery.fields().include("userId");
        replyQuery.fields().include("outUserId");
        List<PublishReply> replyList = publishReplyDao.findAll(replyQuery);
        if (null != replyList && replyList.size() > 0) {
            for (PublishReply publishReply : replyList) {
                Long replyUserId = publishReply.getOutUserId();
                participantSet.add(replyUserId);
            }
        }

        return participantSet;
    }

}
