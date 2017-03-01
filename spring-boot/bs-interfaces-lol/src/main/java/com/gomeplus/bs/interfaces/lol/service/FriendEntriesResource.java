package com.gomeplus.bs.interfaces.lol.service;

import com.gomeplus.bs.framework.dubbor.vo.PageCollection;
import com.gomeplus.bs.interfaces.lol.vo.ContentsVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishContentVo;


/**
 * @Description 好友动态- 好友动态列表 全量接口
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
public interface FriendEntriesResource {
	public PageCollection<PublishContentVo> doGet(Long userId, ContentsVo vo);
}
