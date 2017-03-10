package com.gomeplus.oversea.bi.service.spider.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class ItemVo implements Serializable{

	private static final long serialVersionUID = 7275886925827376772L;
	private Item item;
	private List<Sku> skus;
	private String[] outerCategories;
}
