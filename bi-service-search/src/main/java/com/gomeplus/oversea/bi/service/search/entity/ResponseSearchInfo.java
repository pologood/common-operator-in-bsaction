package com.gomeplus.oversea.bi.service.search.entity;

import java.util.List;

import lombok.Data;


@Data
public class ResponseSearchInfo {
	private Integer pageCount;
    private Integer count;
    private List<ResponseItemInfo> items;
	
}
