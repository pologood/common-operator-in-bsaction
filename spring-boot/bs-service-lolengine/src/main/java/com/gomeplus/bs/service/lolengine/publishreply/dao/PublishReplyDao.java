/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年12月15日 下午4:55:26
 */
package com.gomeplus.bs.service.lolengine.publishreply.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.interfaces.lol.entity.PublishReply;
import com.gomeplus.bs.service.lolengine.mongo.impl.BaseMongoDaoImpl;

/**
 * @Description 动态的回复dao
 * @author mojianli
 * @date 2016年12月15日 下午4:55:26
 */
@Repository("publishReplyDao")
public class PublishReplyDao extends BaseMongoDaoImpl<PublishReply> {

    @Autowired
    @Qualifier("mongoTemplate")

    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
