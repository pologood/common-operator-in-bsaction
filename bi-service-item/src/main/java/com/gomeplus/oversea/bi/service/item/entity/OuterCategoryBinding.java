package com.gomeplus.oversea.bi.service.item.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 2017/2/13
 * 外站类目映射
 *
 */
@Data
public class OuterCategoryBinding implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4596487311918296954L;
	/**
	 * 主键id
	 */
	private String id;
	/**
	 * 外部叶子类目id
	 */
	private String ovcCategoryId;
	/**
	 * 后台叶子类目id
	 */
	private String categoryId;
	/**
	 * 后台类目全路径
	 */
    private String path;
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
