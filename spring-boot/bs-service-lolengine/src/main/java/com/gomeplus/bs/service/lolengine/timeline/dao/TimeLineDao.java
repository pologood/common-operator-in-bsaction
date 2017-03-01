/**
 * gomeo2o.com Copyright (c) 2015-2025 All Rights Reserved.
 * 
 * @Description TODO
 * @author mojianli
 * @date 2016年12月26日 下午6:25:16
 */
package com.gomeplus.bs.service.lolengine.timeline.dao;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import com.gomeplus.bs.interfaces.lol.entity.TimeLine;
import com.gomeplus.bs.service.lolengine.mongo.BaseMongoDao;
import com.gomeplus.bs.service.lolengine.mongo.vo.OutputObject;

/**
 * @Description TODO
 * @author mojianli
 * @date 2016年12月26日 下午6:25:16
 */
public interface TimeLineDao extends BaseMongoDao<TimeLine> {
    
    public AggregationResults<OutputObject> aggregate(Aggregation aggregation); 

}
