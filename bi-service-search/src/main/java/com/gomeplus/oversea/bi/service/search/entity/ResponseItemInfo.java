package com.gomeplus.oversea.bi.service.search.entity;

import lombok.Data;

import com.alibaba.fastjson.JSONObject;

@Data
public class ResponseItemInfo {
	private String id;
	private String ext;
	// 商品搜索相关type为1
	private Integer type;
	
	public ResponseItemInfo(){
		
	}

	public ResponseItemInfo(String id, String ext, Integer type) {
		this.id = id;
		this.ext = ext;
		this.type = type;
	}

	public String toString() {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("ext", ext);
		obj.put("type", type);
		return obj.toString();
	}

}
