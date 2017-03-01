package com.gomeplus.oversea.bi.service.item.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
@Data
public class RichCategory implements Serializable{

	private static final long serialVersionUID = -7923588440566588872L;
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
     * 是否有子类目
     */
    private Boolean hasChildren;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    private List<RichCategory> children;
}
