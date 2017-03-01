package com.gomeplus.bs.service.lol.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.interfaces.lol.entity.PublishEntry;
import com.gomeplus.bs.service.lol.mongo.impl.BaseMongoDaoImpl;

@Repository("publishEntryDao")
public class PublishEntryDao extends BaseMongoDaoImpl<PublishEntry> {

	@Autowired
	@Qualifier("mongoTemplate")
	
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
}