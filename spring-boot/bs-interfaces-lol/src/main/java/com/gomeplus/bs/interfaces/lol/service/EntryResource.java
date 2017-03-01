package com.gomeplus.bs.interfaces.lol.service;

import com.gomeplus.bs.interfaces.lol.vo.PublishContentVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishEntryVo;


/**
 * @Description 好友动态- 动态接口
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
public interface EntryResource {
	/**
	 * @Description 动态详情
	 * @author yanyuyu
	 * @date 2016年12月15日 下午4:12:38
	 * @param id
	 * @return
	 */
	public PublishContentVo doGet(String id, Long userId);
	
	/**
	 * @Description 发布动态
	 * @author yanyuyu
	 * @date 2016年12月15日 下午4:12:52
	 * @param content
	 * @param userId
	 * @return
	 */
	public PublishContentVo doPost(PublishEntryVo entry, Long userId);
	
	/**
	 * @Description 删除动态 
	 * @author yanyuyu
	 * @date 2016年12月15日 下午4:13:01
	 * @param id
	 * @param userId
	 */
	public void doDelete(String id, Long userId);
}
