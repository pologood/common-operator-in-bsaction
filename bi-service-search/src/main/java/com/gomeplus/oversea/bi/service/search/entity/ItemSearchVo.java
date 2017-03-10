package com.gomeplus.oversea.bi.service.search.entity;

import java.util.Date;

import lombok.Data;

@Data
public class ItemSearchVo {
	private String id;
	private String name;
	private	Long sellingPrice;
	private Long originPrice;
	private Integer status;
	private String categoryId;
	private String source;
	private Date createDate;
}
