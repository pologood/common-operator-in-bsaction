package com.gomeplus.bs.interfaces.lol.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 好友动态- 动态详情VO
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Getter
@Setter
public class PublishContentVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2397490888873573591L;

	private String id;

	private Long userId;
	
	private Object[] components;
	
	private Date createTime;
	
	private Date updateTime;
	
	/**
	 * 是否已经点赞
	 */
	private Boolean isPraise;
	
	/**
	 * 点赞列表
	 */
	private List<UserVo> praiseUsers;
	
	/**
	 * 点赞数
	 */
	private Integer praiseQuantity;
	
	/**
	 * 评论列表
	 */
	private List<PublishReplyVo> replys;
	
	/**
	 * 评论数
	 */
	private Integer replyQuantity;
	
}
