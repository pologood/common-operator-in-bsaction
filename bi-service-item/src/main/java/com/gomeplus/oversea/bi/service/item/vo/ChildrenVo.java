package com.gomeplus.oversea.bi.service.item.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhaozhou
 * 2016/10/13 18:17
 */
@Data
@ToString
public class ChildrenVo implements Serializable {

	private static final long serialVersionUID = 5271883986460997739L;

	private String name;//二级菜单名
	private String uri;// 二级菜单uri
	private List<String> actionKeys;//对应的二级菜单按钮权限
}
