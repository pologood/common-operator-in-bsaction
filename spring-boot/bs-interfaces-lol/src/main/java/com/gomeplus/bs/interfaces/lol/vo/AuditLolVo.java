package com.gomeplus.bs.interfaces.lol.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 审核相关服务Vo
 * @author yanyuyu
 * @date 2016年12月15日 下午2:55:00
 */
@ToString
@Getter
@Setter
public class AuditLolVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8421375793996955284L;

	/**
	 * 评论ID
	 */
	private String replyId;
	
	/**
	 * 动态ID
	 */
	private String entryId;
	
	/**
	 * 日志ID
	 */
	private String logId;

	/**
	 * 创建人
	 */
	private Long userId;
	
	/**
	 * 动态内容
	 */
	private Object[] components;
	
	/**
	 * 评论内容
	 */
	private String contents;
	
	/**
	 * 审核状态
	 */
	private Integer auditState;
	
	/**
	 * 审核人
	 */
	private String auditUserId;
	
	/**
	 * 审核不通过原因
	 */
	private String noThroughReason;
	
	/**
	 * 审核时间
	 */
	private Date auditTime;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
}
