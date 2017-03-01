/**
 * gomeo2o.com 
 * Copyright (c) 2015-2025 All Rights Reserved.
 * @Description TODO 
 * @author chaizhilei
 * @date 2017年2月22日 下午2:59:31
 */
package com.gomeplus.bs.image_web.common;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description 第三方账号
 * @author chaizhilei
 * @date 2017年2月22日 下午2:59:31
 */
@Document
public class ThirdAccount implements Serializable {

	private static final long serialVersionUID = -8267382582962871835L;

	// 主键
	private String id;

	// 第三方账号
	private String account;

	// 第三方账号id
	private Long accountId;

	// 第三方来源id
	private String code;

	// 第三方账号状态 0 ：有效 1：无效
	private int state;

	// 第三方描述
	private String desc;

	private Date createTime;

	private Date updateTime;

	// 最后请求时间
	private Long lastRequestTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getLastRequestTime() {
		return lastRequestTime;
	}

	public void setLastRequestTime(Long lastRequestTime) {
		this.lastRequestTime = lastRequestTime;
	}

	@Override
	public String toString() {
		return "ThirdAccount [id=" + id + ", account=" + account + ", accountId=" + accountId + ", code=" + code
				+ ", state=" + state + ", desc=" + desc + ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", lastRequestTime=" + lastRequestTime + "]";
	}

}
