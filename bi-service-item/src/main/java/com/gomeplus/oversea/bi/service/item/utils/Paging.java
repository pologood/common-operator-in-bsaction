package com.gomeplus.oversea.bi.service.item.utils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Paging <T> implements Serializable {
	private static final long serialVersionUID = 755183539178076901L;
	private Long total;
	private List<T> data;

	public Paging() {
	
	}

	public Paging(Long total, List<T> data) {
		this.data = data;
		this.total = total;
	}

	public List<T> getData() {
		return this.data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public Long getTotal() {
		return this.total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public static <T> Paging<T> empty(Class<T> clazz) {
		List emptyList = Collections.emptyList();
		return new Paging(Long.valueOf(0L), emptyList);
	}

	public static <T> Paging<T> empty() {
		return new Paging(Long.valueOf(0L), Collections.emptyList());
	}
	}