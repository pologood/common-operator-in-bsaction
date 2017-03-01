package com.gomeplus.bs.interfaces.lol.vo.mq;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 好友动态- 评论详情VO
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Getter @Setter
public class ReplyMqVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2920975962008247347L;

	private String id;
	
	private String entryId;
	
	private String contents;
	
	/**
	 * 创建人
	 */
	private Long userId;
	
	/**
	 * 动态创建人
	 */
	private Long entryUserId;
	
	/**
	 * 是否可见
	 */
	private Boolean isPublic;
	
	/**
	 * 是否二审
	 */
	private Boolean isSecondAudit;
	
	/**
	 * 被评论人ID
	 */
	private Long beReplyedUserId;
	
	private Date createTime;
	
	private Date updateTime;
}
