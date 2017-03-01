package com.gomeplus.bs.service.lol.mongo.entity;

import java.io.Serializable;

public class PageParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6297178964005032338L;
	private int pageNum; // 当前页数
	private int numPerPage; // 每页记录数
	
	public PageParam(){
		super();
	}
	
	public PageParam(int pageNum, int numPerPage) {
		super();
		this.pageNum = pageNum;
		this.numPerPage = numPerPage;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getNumPerPage() {
		return numPerPage;
	}

	public void setNumPerPage(int numPerPage) {
		this.numPerPage = numPerPage;
	}

	public int getOffset() {
		//计算起始位置
		if(pageNum==0){
			pageNum =1;
		}
		return (pageNum-1)*numPerPage;
	}

}