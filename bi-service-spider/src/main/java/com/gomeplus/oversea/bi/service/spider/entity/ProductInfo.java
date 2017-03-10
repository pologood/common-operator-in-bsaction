package com.gomeplus.oversea.bi.service.spider.entity;
import java.lang.String;
import java.util.List;

import lombok.Data;
@Data
public class ProductInfo extends Item {

	private static final long serialVersionUID = 339926682141808879L;
	
    private String categoryPath;
    
    private List<String> outSkuIds;
    private String saleAttributes;
    private String generalAttributes;
    
    
}
