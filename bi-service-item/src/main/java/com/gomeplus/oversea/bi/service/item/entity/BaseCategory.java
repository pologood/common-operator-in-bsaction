package com.gomeplus.oversea.bi.service.item.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 2017/2/22
 * 类目基础类
 */
@Data
public class BaseCategory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -955903660955926977L;
	/**
	 * 主键id
	 */
	private String id;
	/**
	 * 父级id
	 */
	private String parentId;
	/**
	 * 名称
	 */
    private String name;
    /**
     * 级别
     */
    private Integer level;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 是否有子类目
     */
    private Boolean hasChildren;
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
