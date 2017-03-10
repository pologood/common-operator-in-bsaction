package com.gomeplus.oversea.bi.service.spider.entity;

import java.io.Serializable;



public class Attributes implements Serializable {
	
	

	private static final long serialVersionUID = 6285102013311404781L;
	private String key;          //属性名
	private String value;         //属性值


	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
