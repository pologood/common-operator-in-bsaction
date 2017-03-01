package com.gomeplus.bs.service.lol.service;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gomeplus.bs.interfaces.lol.common.Constants;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.entity.TimeLine;
import com.gomeplus.bs.service.lol.LOLApplication;
import com.gomeplus.bs.service.lol.dao.PublishContentDao;
import com.gomeplus.bs.service.lol.dao.TimeLineDao;

/**
 *
 * @author yanyuyu
 * @date   2016年12月23日
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)  
@SpringApplicationConfiguration(classes=LOLApplication.class)
public class TimeLineTest {

	@Autowired
	private PublishContentDao publishContentDao;
	
	@Autowired
	private TimeLineDao timeLineDao;
	
	@Test
	public void add() {
		Long timeLineOwner = 1177L;
		Query query = new Query(Criteria.where("isPublic").is(true).and("isDelete").is(false));
		List<PublishContent> contentList = publishContentDao.findAll(query);
		TimeLine line = null;
		for( PublishContent content : contentList) {
			Date current = new Date();
			line = new TimeLine();
			line.setCreateTime(current);
			line.setEntryCreateTime(content.getCreateTime());
			line.setEntryId(content.getId());
			line.setOutFriendId(content.getOutUserId());
			line.setId(timeLineDao.getNewId(Constants.CollectionName.TIME_LINE));
			line.setState(0);
			line.setUpdateTime(current);
			line.setOutUserId(timeLineOwner);
			
			timeLineDao.save(line);
		}
		
	}
}


