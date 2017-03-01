package com.gomeplus.bs.interfaces.lol.service;



/**
 * @Description 好友动态- 点赞接口
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
public interface PraiseResource {
	
	/**
	 * @Description 点赞
	 * @author yanyuyu
	 * @date 2016年12月15日 下午4:14:14
	 * @param reply
	 * @param userId
	 * @return
	 */
	public void doPut(String entryId, Long userId);
	
	/**
	 * @Description 取消点赞
	 * @author yanyuyu
	 * @date 2016年12月15日 下午4:14:41
	 * @param id
	 * @param userId
	 */
	public void doDelete(String entryId, Long userId);
}
