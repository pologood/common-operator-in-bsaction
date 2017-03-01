package com.gomeplus.bs.interfaces.lol.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * @Description 好友动态- 发布信息
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Data
public class PublishEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5235081442155833183L;

	private String id;
	
	/**
	 * 发布地点：经纬度
	 */
	private Object place;
	
	/**
	 * 动态展示类型 0公开 1私密 2部分可见 3不给谁看
	 */
	private Integer showType;
	
	/**
	 * 是否提醒
	 */
	private Boolean isRemind;
	
	/**
	 * 国美在线的userId
	 */
	private Long outUserId;
	
	private Object[] showFriends;
	
	private Object[] remindFriends;
	
	private Date createTime;
	
}
