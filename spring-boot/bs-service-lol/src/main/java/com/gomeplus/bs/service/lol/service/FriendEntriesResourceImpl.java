package com.gomeplus.bs.service.lol.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gomeplus.bs.framework.dubbor.annotations.PublicParam;
import com.gomeplus.bs.framework.dubbor.vo.PageCollection;
import com.gomeplus.bs.interfaces.lol.entity.TimeLine;
import com.gomeplus.bs.interfaces.lol.service.FriendEntriesResource;
import com.gomeplus.bs.interfaces.lol.vo.ContentsVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishContentVo;
import com.gomeplus.bs.service.lol.dao.TimeLineDao;


@RestController("friendEntriesResource")
@RequestMapping("/lol/friendEntries")
public class FriendEntriesResourceImpl implements FriendEntriesResource{
	
	@Autowired
	private TimeLineDao timeLineDao;
	
	/**
	 * @Description 好友动态列表 全量
	 * @author yanyuyu
	 * @date 2016年12月15日 下午5:50:39
	 * @param userId
	 * @return
	 */
	@RequestMapping(method = GET)
	@Override
	public PageCollection<PublishContentVo> doGet(@PublicParam(name = "userId") Long userId, ContentsVo vo) {
		PageCollection<PublishContentVo> pageData = new PageCollection<PublishContentVo>();
		Integer pageSize = vo.getPageSize();
		Long startTime = vo.getStartTime();
		if(pageSize == null) {
			pageSize = 10;
		}
		Query query = new Query();
		Criteria criteria = Criteria.where("state").is(0)
									.and("outUserId").is(userId);
		if(startTime != null && !startTime.equals(0L)) {
			criteria.and("entryCreateTime").lt(new Date(startTime));
		}
		query.addCriteria(criteria);
		query.with(new Sort(Direction.DESC, "entryCreateTime"));
		query.limit(pageSize);
		List<TimeLine> timeLineList = timeLineDao.findAll(query);
		List<PublishContentVo> contentList = new ArrayList<PublishContentVo>();
		if(timeLineList != null && timeLineList.size() > 0) {
			PublishContentVo contentVo = null;
			for(TimeLine line : timeLineList) {
				contentVo = timeLineDao.lineDetailToContentVo(line, 1, userId);
				contentList.add(contentVo);
			}
		}
		pageData.setRows(contentList);
		return pageData;
	}
	
}
