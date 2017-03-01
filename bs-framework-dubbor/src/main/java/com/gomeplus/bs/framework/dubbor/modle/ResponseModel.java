package com.gomeplus.bs.framework.dubbor.modle;

import com.alibaba.fastjson.annotation.JSONField;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Description 返回实体
 * @author wanggang-ds6  update by zhaozhou
 * @date 2016年1月22日 下午3:15:54
 */
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel<T> {
	// 返回消息
	@JSONField(ordinal = 1)
	private String message = "";
	// 返回对象
	@JSONField(ordinal = 2)
	private T data;
	// debug模式，需要在配置开关，开发测试模式可以开启

	@JSONField(ordinal = 3)
	private T error;

	@JSONField(ordinal = 4)
	private String debug;

	public ResponseModel() {
	}

	public ResponseModel(T data) {
			this.data = data;
	}

	public ResponseModel(String message) {
		if (null != message) {
			this.message = message;
		}
	}

	public void setMessage(String message) {
		if (null != message) {
			this.message = message;
		}
	}

	public void setData(T data) {
		if (null != data) {
			this.data = data;
		}
	}
	public void setError(T error){
		if (null != error){
			this.error =  error;
		}
	}
}
