/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年12月15日 下午4:55:26
 */
package com.gomeplus.bs.service.lolengine.publishcontent.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.service.lolengine.mongo.impl.BaseMongoDaoImpl;

/**
 * @Description 发布内容dao
 * @author mojianli
 * @date 2016年12月15日 下午4:55:26
 */
@Repository("publishContentDao")
public class PublishContentDao extends BaseMongoDaoImpl<PublishContent> {

    @Autowired
    @Qualifier("mongoTemplate")

    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
