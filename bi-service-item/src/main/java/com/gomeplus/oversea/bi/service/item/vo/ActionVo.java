package com.gomeplus.oversea.bi.service.item.vo;

import java.io.Serializable;

/**
 * 
 * @ClassName: MenusVo
 * @Description: 运营后台菜单Vo
 * @author: sunyizhong
 * @date: 2016年10月12日 上午10:58:27
 */
public class ActionVo implements Serializable {

	private static final long serialVersionUID = 6112110913647790156L;
	/**
	 * 按钮名称
	 */
	private String name;
	/**
	 * 按钮对应的key值
	 */
	private String key;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "ActionVo [name=" + name + ", key=" + key + "]";
	}

}