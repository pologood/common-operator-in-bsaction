package com.gomeplus.bs.interfaces.lol.service;

import com.gomeplus.bs.interfaces.lol.vo.AuditVo;

/**
 * @Description 审核 - 好友动态相关审核服务
 * @author yanyuyu
 * @date   2016年12月19日
 */
public interface AuditLolResource {
	/**
	 * 审核
	 * @param vo
	 */
	public void doPut(AuditVo vo);
}


