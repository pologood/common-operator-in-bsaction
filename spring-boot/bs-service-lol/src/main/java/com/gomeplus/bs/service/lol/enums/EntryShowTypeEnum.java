package com.gomeplus.bs.service.lol.enums;

public enum EntryShowTypeEnum {
	/**
	 * 公开的
	 */
	PUBLIC(0),
	
	/**
	 * 私密的
	 */
	PRIVATE(1),
	
	/**
	 * 部分可见
	 */
	INCLUSION(2),
	
	/**
	 * 不给谁看
	 */
	EXCLUSION(3);
	
	public int value;
	
	EntryShowTypeEnum(int value_) {
		this.value = value_;
	}
	
}
