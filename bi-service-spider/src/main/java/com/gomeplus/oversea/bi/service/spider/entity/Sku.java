package com.gomeplus.oversea.bi.service.spider.entity;

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
	private static final long serialVersionUID = 5135570582129073478L;
	/**
	 * 主键id
	 */
	private String id;
	/**
	 * 外部ID
	 */
	private String outerId;
	/**
	 * 外部url
	 */
	private String outerUrl;
	/**
	 * 商品ID
	 */
	private String itemId;
	/**
	 * 名称
	 */
    private String name;
    /**
     * 原价
     */
    private Long originPrice;
    /**
     * 售价 
     */
    private Long sellingPrice;
    
    private String currencyUnit;
    /**
     * 图片url
     */
    private String image;
    
    /**
     * sku状态(1:上架,-1:下架)
     */
    private Integer status;
    /**
     * sku 销售属性
     */
    private String saleAttributes;
    /**
     * sku普通属性
     */
    private String generalAttributes;
    /**
     * 
     */
    /**
     * 折扣百分比
     */
    private String discountPercentage;
    /**
     * 品牌名称
     */
    private String brandName;
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
