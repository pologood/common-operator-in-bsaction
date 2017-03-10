package com.gomeplus.bs.service.lol.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.gomeplus.bs.service.lol.friendship.cache.FriendshipCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gomeplus.bs.framework.dubbor.annotations.PublicParam;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.framework.dubbor.vo.PageCollection;
import com.gomeplus.bs.interfaces.lol.entity.PublishContent;
import com.gomeplus.bs.interfaces.lol.service.UserEntriesResource;
import com.gomeplus.bs.interfaces.lol.vo.ContentsVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishContentVo;
import com.gomeplus.bs.service.lol.dao.PublishContentDao;
import com.gomeplus.bs.service.lol.util.LolMessageUtil;


@RestController("userEntriesResource")
@RequestMapping("/lol/userEntries")
public class UserEntriesResourceImpl implements UserEntriesResource{

	@Autowired
	private PublishContentDao publishContentDao;

	@Autowired
	private FriendshipCache friendshipCache;

	/**
	 * 个人动态列表 全量
	 */
	@RequestMapping(method = GET)
	@Override
	public PageCollection<PublishContentVo> doGet(@PublicParam(name = "userId") Long userId, ContentsVo vo) {
		PageCollection<PublishContentVo> pageData = new PageCollection<PublishContentVo>();
		Integer pageSize = vo.getPageSize();
		Long startTime = vo.getStartTime();
		Long ownerUserId = vo.getOwnerUserId();
		//新增逻辑：非好友不可见他人动态列表
		//好友列表
		Set<Long> friends = friendshipCache.getFriendIds(userId);
		if((friends == null || !friends.contains(ownerUserId)) && !userId.equals(ownerUserId)) {
			pageData.setTotal(0L);
			pageData.setRows(new ArrayList<PublishContentVo>());
			return pageData;
		}

		if(ownerUserId == null) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		if(pageSize == null) {
			pageSize = 10;
		}
		
		Query query = new Query();
		Criteria criteria = Criteria.where("isDelete").is(false)
									.and("outUserId").is(ownerUserId);
		if(startTime != null && !startTime.equals(0L)) {
			criteria.and("createTime").lt(new Date(startTime));
		}
		
		if(!userId.equals(ownerUserId)) {
			criteria.and("isPublic").is(true);
		}
		query.addCriteria(criteria);
		long totalQuantity = publishContentDao.count(query);
		query.with(new Sort(Direction.DESC, "createTime"));
		query.limit(pageSize);
		List<PublishContent> list = publishContentDao.findAll(query);
		List<PublishContentVo> volist = new ArrayList<PublishContentVo>();
		if(list != null && list.size()>0 ) {
			PublishContentVo contentVo = null;
			for(int i = 0; i < list.size(); i ++) {
				contentVo = publishContentDao.contentDetailToVo(list.get(i), 1, userId, friends);
				volist.add(contentVo);
			}
		}

		pageData.setTotal(totalQuantity);
		pageData.setRows(volist);
		return pageData;
	}

}
