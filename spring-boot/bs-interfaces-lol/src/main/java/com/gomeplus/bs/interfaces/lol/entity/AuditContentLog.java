package com.gomeplus.bs.interfaces.lol.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * @Description 审核日志 - 动态
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Data
public class AuditContentLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3160307207925895256L;

	/**
	 * 片键
	 */
	private String id;
	
	/**
	 * 动态ID
	 */
	private String entryId;

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
	
	private Long updateUserId;
	
}
