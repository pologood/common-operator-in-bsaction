package com.gomeplus.bs.interfaces.lol.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * @Description 好友动态- 时间线
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Data
public class TimeLine implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3396087892428092724L;

	private String id;
	
	/**
	 * 动态内容ID
	 */
	private String entryId;
	
	/**
	 * 国美在线的userId --- 1.4.0结束后作为片键
	 */
	private Long outUserId;
	
	/**
	 * 国美在线的userId
	 */
	private Long outFriendId;
	
	/**
	 * 动态状态：0有效 1好友删除
	 */
	private Integer state;
	
	/**
	 * 动态创建时间
	 */
	private Date entryCreateTime;
	
	private Date createTime;
	
	private Date updateTime;
	
}
