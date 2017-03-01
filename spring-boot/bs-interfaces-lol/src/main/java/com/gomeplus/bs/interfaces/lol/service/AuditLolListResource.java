package com.gomeplus.bs.interfaces.lol.service;

import java.util.Map;

import com.gomeplus.bs.interfaces.lol.vo.AuditVo;

/**
 * @Description 审核 - 好友动态相关查询服务
 * @author yanyuyu
 * @date   2016年12月19日
 */
public interface AuditLolListResource {
	/**
	 * 查询列表
	 * 
	 * @return map : <key> rows 分页列表 
	 * 				 <key> total 列表总数
	 */
	public Map<String,Object> doGet(AuditVo vo);
}


