package com.gomeplus.oversea.bi.service.item.vo;

import java.io.Serializable;

import lombok.Data;

import com.gomeplus.oversea.bi.service.item.entity.Item;
import com.gomeplus.oversea.bi.service.item.entity.Sku;

@Data
public class ItemVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7275886925827376772L;
	private Item item;
	private Sku sku;
	/**
	 * 商品的外部类目
	 */
	private String[] outerCategories;
}
