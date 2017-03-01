package com.gomeplus.oversea.bi.service.item.service;

import com.gomeplus.oversea.bi.service.item.entity.Item;
import com.gomeplus.oversea.bi.service.item.vo.ItemVo;

/**
 * 2017/2/13
 * 商品write接口
 */
public interface ItemWriteService {
	
    String create(Item item);
    
    void createForSpider(ItemVo itemVo);
    
    Boolean update(Item item);
}
