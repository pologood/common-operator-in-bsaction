package com.gomeplus.bs.interfaces.lol.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @Description  动态列表查询-查询条件VO
 * @author yanyuyu
 * @date   2016年12月22日
 */
@ToString
@Getter @Setter
public class ContentsVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2170663218154706674L;

	/**
	 * 增量接口 ： 增量开始时间
	 */
	private Date increStartTime;
	
	/**
	 * 增量接口：增量查询限制条数
	 */
	private Integer increMaxSize;
	
	/**
	 * 被查看用户ID
	 */
	private Long ownerUserId;
	
	/**
	 * 全量接口：页大小
	 */
	private Integer pageSize;
	
	/**
	 * 全量接口  起始时间
	 */
	private Long startTime;
	
}


