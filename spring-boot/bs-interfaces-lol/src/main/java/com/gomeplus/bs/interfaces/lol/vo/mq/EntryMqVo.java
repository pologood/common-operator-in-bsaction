package com.gomeplus.bs.interfaces.lol.vo.mq;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 好友动态- 推计算引擎MQ的VO对象
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Getter
@Setter
public class EntryMqVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5605543018670340973L;

	private String id;
	
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
	
	private Boolean isPublic;
	
	private Date createTime;

	private Date updateTime;
	
}
