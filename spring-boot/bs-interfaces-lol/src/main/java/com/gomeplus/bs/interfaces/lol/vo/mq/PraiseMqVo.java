package com.gomeplus.bs.interfaces.lol.vo.mq;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 好友动态- 点赞VO
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Getter @Setter
public class PraiseMqVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2754439093540251033L;

	private String id;
	
	// 是否取消点赞
	private Boolean isDelete;
	
	// 动态ID 
	private String entryId;

	// 创建人
	private Long userId;
	
	// 动态创建人
	private Long entryUserId;

	// 创建时间
	private Date createTime;
	
	// 修改时间
	private Date updateTime;
	
	// 是否初次点赞
	private Boolean isFirst;
}