/**   
 * Copyright © 2016 mx. All rights reserved.
 * 
 * @Title: Paging.java 
 * @Prject: bs-service-permission-api
 * @Package: com.gomeplus.bs.service.permission.common 
 * @author: sunyizhong
 * @date: 2016年10月10日 上午11:17:46 
 * @version: V1.0   
 */
package com.gomeplus.bs.framework.dubbor.vo;

import java.io.Serializable;
import java.util.List;

public class PageCollection<T> implements Serializable {

	private static final long serialVersionUID = -1538688104127441010L;
	
	private Long total;

    private List<T> rows;

    public PageCollection() {
    }

    public PageCollection(Long total, List<T> rows) {
        this.rows = rows;
        this.total = total;
    }

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	
}
