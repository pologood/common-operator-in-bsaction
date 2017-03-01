package com.gomeplus.oversea.bi.service.item.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @ClassName: ResourcesVo
 * @Description: 运营后台资源Vo
 * @author: sunyizhong
 * @date: 2016年10月12日 上午10:57:11
 */
public class ResourceVo implements Serializable {

	private static final long serialVersionUID = 8775113406548121882L;
	/**
	 * 资源名
	 */
	private String name;
	/**
	 * 资源权限 列表--按钮权限
	 */
	private List<ActionVo> actions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ActionVo> getActions() {
		return actions;
	}

	public void setActions(List<ActionVo> actions) {
		this.actions = actions;
	}

	@Override
	public String toString() {
		return "ResourceVo [name=" + name + ", actions=" + actions + "]";
	}
}