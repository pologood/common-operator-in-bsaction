package com.gomeplus.bs.interfaces.lol.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 好友动态- 发布VO
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Getter
@Setter
public class PublishEntryVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5605543018670340973L;

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
	
	private Long userId;
	
	private Object[] showFriends;
	
	private Object[] remindFriends;
	
	/**
	 * 发布内容
	 */
	private Object[] components;
	
	private Date createTime;

	private Date updateTime;
	
}
