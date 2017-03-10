package com.gomeplus.oversea.bi.service.search.entity;

import lombok.Data;

@Data
public class ItemSearchQuery {

	private String keyword;
	//排序条件:0综合,1销量,2价格,3上新,4总销量,0默认
	private Integer sortCriteria;
	//排序规则:0升序asc,1降序desc;待更新
	private Integer sort;
	private Integer pageNum = 1;
	private Integer pageSize = 10;
	private Integer categoryId;
	private String  filterHideIds;
	
	/**
	 * 业务不相关(搜索业务埋点,透传相关)
	 */
	private String lang;
	private String device;
	private String traceId;
	private String deviceId;
	private String userId;
	private String timeZone;
	private String app;
	private String pageType;
	private String pageModule;
	private String pageTag;
	private String itemId;
    private String auid;
	
}
