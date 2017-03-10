package com.gomeplus.oversea.bi.service.spider.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 2017/2/13
 * 商品信息model
 */
@Data
public class Item implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3079173254623909570L;
	/**
	 * 主键id
	 */
	private String id;
	/**
	 * 外部商品ID
	 */
	private String outerId;
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
     */
    private String outerCategoryId;
    /**
     * 原价
     */
    private Long originPrice;
    /**
     * 售价 
     */
    private Long sellingPrice;
    /**
     * 商品主图
     */
    private String mainImage;
    /**
     * 商品轮播图json
     */
    private String images;
   
    private String currencyUnit;
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
    /**
     * 折扣百分比
     */
    private String discountPercentage;
    
    /**
     * 商品状态
     */
    public static enum Status {
    	 ON_SHELF(1, "上架"),
         OFF_SHELF(-1, "下架");
    	
    	 private int value;
         private String desc;

         private Status(int value, String desc) {
             this.value = value;
             this.desc = desc;
         }

         public int value() {
             return value;
         }

         public static Status from(int number) {
             for (Status status : Status.values()) {
                 if (status.value == number) {
                     return status;
                 }
             }
             return null;
         }

         @Override
         public String toString() {
             return desc;
         }
    }
}
