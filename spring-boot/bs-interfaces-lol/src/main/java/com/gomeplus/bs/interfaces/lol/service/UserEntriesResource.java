package com.gomeplus.bs.interfaces.lol.service;

import com.gomeplus.bs.framework.dubbor.vo.PageCollection;
import com.gomeplus.bs.interfaces.lol.vo.ContentsVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishContentVo;


/**
 * @Description 好友动态- 个人动态列表 接口
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
public interface UserEntriesResource {
	public PageCollection<PublishContentVo> doGet(Long userId, ContentsVo vo);
}
