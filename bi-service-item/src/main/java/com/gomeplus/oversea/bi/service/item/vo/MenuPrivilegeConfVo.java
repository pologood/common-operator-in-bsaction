package com.gomeplus.oversea.bi.service.item.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/10/13 17:44
 */
public class MenuPrivilegeConfVo implements Serializable {
	
	private static final long serialVersionUID = 4848026701010729842L;
	
	/**
	 * 模块名
	 */
	private String module;
	/**
	 * 模块名
	 */
	private String name;
	/**
	 * 排序号
	 */
	private Integer order;
	/**
	 * 1代表admin用户权限 0代表普通用户权限
	 */
	private Integer type;
	/**
	 * 版本号
	 */
	private String version;
	/**
	 * 菜单列表 --一级
	 */
	private List<MenuVo> menus;
	/**
	 * 二级菜单列表
	 */
	private List<ResourceVo> resources;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public List<MenuVo> getMenus() {
		return menus;
	}

	public void setMenus(List<MenuVo> menus) {
		this.menus = menus;
	}

	public List<ResourceVo> getResources() {
		return resources;
	}

	public void setResources(List<ResourceVo> resources) {
		this.resources = resources;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "MenuPrivilegeConfVo [module=" + module + ", name=" + name
				+ ", order=" + order + ", type=" + type + ", version="
				+ version + ", menus=" + menus + ", resources=" + resources
				+ "]";
	}
}
