package com.gomeplus.bs.interfaces.lol.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * @Description 审核日志 - 评论
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Data
public class AuditReplyLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6276706393259609748L;

	private String id;
	
	/**
	 * 动态内容ID  片键
	 */
	private String entryId;
	
	/**
	 * 评论ID
	 */
	private String replyId;
	
	private String contents;
	
	/**
	 * 国美在线的userId
	 */
	private Long outUserId;
	
	private Long outBeReplyedUserId;
	
	private Integer auditState;
	
	private String auditUserId;
	
	private Date auditTime;
	
	private String noThroughReason;
	
	private Boolean audited;
	
	private Boolean isPublic;
	
	private Boolean isDelete;
	
	private Date createTime;
	
	private Date updateTime;
}
