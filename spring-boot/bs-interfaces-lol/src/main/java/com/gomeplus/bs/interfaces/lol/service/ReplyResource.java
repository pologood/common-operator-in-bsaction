package com.gomeplus.bs.interfaces.lol.service;

import com.gomeplus.bs.interfaces.lol.vo.PublishReplyVo;


/**
 * @Description 好友动态- 评论接口
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
public interface ReplyResource {
	
	/**
	 * @Description 创建评论 
	 * @author yanyuyu
	 * @date 2016年12月15日 下午4:14:14
	 * @param reply
	 * @param userId
	 * @return
	 */
	public PublishReplyVo doPost(PublishReplyVo vo, Long userId);
	
	/**
	 * @Description 删除评论
	 * @author yanyuyu
	 * @date 2016年12月15日 下午4:14:41
	 * @param id
	 * @param userId
	 */
	public void doDelete(String id, Long userId);
}
