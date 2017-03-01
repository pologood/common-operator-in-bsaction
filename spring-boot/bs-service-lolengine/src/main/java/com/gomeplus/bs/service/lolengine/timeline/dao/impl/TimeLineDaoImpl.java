/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年12月26日 下午6:26:00
 */
package com.gomeplus.bs.service.lolengine.timeline.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.interfaces.lol.entity.TimeLine;
import com.gomeplus.bs.service.lolengine.mongo.impl.BaseMongoDaoImpl;
import com.gomeplus.bs.service.lolengine.mongo.vo.OutputObject;
import com.gomeplus.bs.service.lolengine.timeline.dao.TimeLineDao;

/**
 * @Description TODO
 * @author mojianli
 * @date 2016年12月26日 下午6:26:00
 */
@Repository("timeLineDao")
public class TimeLineDaoImpl extends BaseMongoDaoImpl<TimeLine> implements TimeLineDao {

    @Autowired
    @Qualifier("mongoTemplate")

    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public AggregationResults<OutputObject> aggregate(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, TimeLine.class, OutputObject.class);
    }
}
