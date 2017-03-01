package com.gomeplus.bs.interfaces.lol.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 好友动态- 点赞
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Getter @Setter
public class Praise implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3478686705126878839L;

	private String id;
	
	//是否取消点赞
	private Boolean isDelete;
	
	// 动态ID    片键
	private String entryId;

	/**
	 * 国美在线的userId
	 */
	private Long outUserId;

	// 创建时间
	private Date createTime;

	// 修改时间
	private Date updateTime;
}