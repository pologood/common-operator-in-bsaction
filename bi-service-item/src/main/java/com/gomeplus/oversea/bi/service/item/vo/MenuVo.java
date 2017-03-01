package com.gomeplus.oversea.bi.service.item.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaozhou
 * 2016/10/13 18:02
 */
@Data
@ToString
public class MenuVo implements Serializable {

	private static final long serialVersionUID = 8789631422425375891L;
	private String env; //环境变量
	private String name;//菜单名
	private String module;//模块名
	private Integer order;//一级菜单顺序
	private List<ChildrenVo> children;
}