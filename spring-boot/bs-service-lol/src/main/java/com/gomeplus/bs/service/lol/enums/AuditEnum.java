package com.gomeplus.bs.service.lol.enums;


/**
 * @Description 审核枚举
 * @author yanyuyu
 * @date 2016年12月16日 上午10:06:47
 */
public enum AuditEnum {
	
	// 未审核
	UNAUDITED(1),
	// 审核通过
	PASS(2),
	// 未通过
	NOTPASS(3);
	
	public int value;
	
	AuditEnum(int value_) {
		this.value = value_;
	}
}
