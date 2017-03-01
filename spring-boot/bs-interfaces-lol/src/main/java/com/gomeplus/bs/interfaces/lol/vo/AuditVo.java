package com.gomeplus.bs.interfaces.lol.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 审核相关服务条件VO
 * @author yanyuyu 
 * @date   2016年12月19日
 */
@ToString()
@Getter @Setter
public class AuditVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5251909079878567310L;

	/**
	 * 业务类型
	 * <p> 1: 好友动态  2：好友动态评论 3：好友动态日志 4：好友动态评论日志</p>
	 */
	private Integer businessType;
	
	/**
	 * 实体ID
	 * <p>如果是审核服务，则代表将此ID的实体进行审核通过/不通过操作</p>
	 * <p>如果是查询服务，则代表查询此ID的实体</p>
	 */
	private String businessId;
	
	/**
	 * 用户ID
	 * <p>如果是审核服务，则忽略此字段</p>
	 * <p>如果是查询服务，则代表查询此ID的实体</p>
	 */
	private Long businessUserId;

	/**
	 * 实体内容
	 * <p>如果是审核服务，则此字段不处理</p>
	 * <p>如果是查询服务，则在查询列表时进行内容的模糊匹配</p>
	 */
	private String businessContent;
	
	/**
	 * 是否为二审
	 * <p>如果是审核服务，则表示审核通过/不通过是否是二审：目前只有审核通过的支持二审</p>
	 * <p>如果是查询服务，则此字段不处理</p>
	 */
	private Boolean isSecondAudit;
	
	/**
	 * 审核状态：
	 * <p>如果是审核服务，则代表将此实体进行审核通过/不通过操作</p>
	 * <p>如果是查询服务，则代表查询审核通过/不通过/待审核的列表</p>
	 */
	private Integer auditState;
	
	/**
	 * 审核人：
	 * <p>如果是审核服务，则代表当前审核操作的审核人ID</p>
	 * <p>如果是查询服务，则此字段不处理</p>
	 */
	private String auditUserId;
	
	/**
	 * 审核不通过原因
	 */
	private String noThroughReason;
	
	/**
	 * 查询列表时的页号
	 */
	private Integer pageNum;
	
	/**
	 * 查询列表时的页大小
	 */
	private Integer pageSize;
}


