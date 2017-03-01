package com.gomeplus.oversea.bi.service.item.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 2017/2/14
 * SKU model
 */
@Data
public class Sku implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 761641253248890092L;
	/**
	 * 主键id
	 */
	private String id;
	/**
	 * 外部ID
	 */
	private String outerId;
	/**
     * 商品来源
     */
    private String source;
	/**
	 * 商品ID
	 */
	private String itemId;
	/**
	 * 名称
	 */
    private String name;
    /**
     * 价格
     */
    private Long originPrice;
    /**
     * 商品卖价
     */
    private Long sellingPrice;
    /**
     * 货币单位
     */
    private String currencyUnit;
    /**
     * 图片url
     */
    private String image;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 修改人
     */
    private String updater;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;
    
}
