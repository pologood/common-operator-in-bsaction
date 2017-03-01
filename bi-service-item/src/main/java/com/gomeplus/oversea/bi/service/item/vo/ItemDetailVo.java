package com.gomeplus.oversea.bi.service.item.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 2017/2/17
 * 商品详情vo
 */
@Data
public class ItemDetailVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3667094043617889988L;
	/**
	 * 主键id
	 */
	private String id;
	/**
	 * 外部商品ID
	private String outerId;
	 */
	/**
	 * 商品名称
	 */
    private String name;
    /**
     * 后台类目id
     */
    private String categoryId;
    /**
     * 外部后台叶子类目ID
    private String outerCategoryId;
     */
    /**
     * 原价
     */
    private PriceVo originPrice;
    /**
     * 商品卖价
     */
    private PriceVo sellingPrice;
    /**
     * 预期返利
     */
    private PriceVo expectedCashBack;
    /**
     * 折扣百分比
     */
    private String discountPercentage;
    /**
     * 商品主图
     */
    private String mainImage;
    /**
     * 商品轮播图列表
     */
    private List<String> images;
    /**
     * 外部url
     */
    private String outerUrl;
    /**
     * 商品来源
     */
    private String source;
    /**
     * 商品状态(1:上架,-1:下架)
     */
    private Integer status;
    
}
