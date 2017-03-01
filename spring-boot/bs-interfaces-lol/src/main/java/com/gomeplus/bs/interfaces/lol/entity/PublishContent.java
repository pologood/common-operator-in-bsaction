package com.gomeplus.bs.interfaces.lol.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * @Description 好友动态- 发布内容
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Data
public class PublishContent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1518877804550175655L;

	/**
	 * 片键
	 */
	private String id;

	/**
	 * 国美在线的userId
	 */
	private Long outUserId;
	
	private Object[] components;
	
	private Integer auditState;
	
	private String auditUserId;
	
	private Date auditTime;
	
	private String noThroughReason;
	
	private Boolean audited;
	
	private Boolean isPublic;
	
	private Boolean isDelete;
	
	private Date createTime;
	
	private Date updateTime;
	
	/**
	 * 动态下的点赞最后操作时间
	 */
	private Date praiseUpdateTime;
	
	/**
	 * 动态下的评论最后操作时间
	 */
	private Date replyUpdateTime;
	
	private Long updateUserId;
	
}
