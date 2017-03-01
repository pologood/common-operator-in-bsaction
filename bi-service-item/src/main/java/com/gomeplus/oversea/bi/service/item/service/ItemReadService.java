package com.gomeplus.oversea.bi.service.item.service;

import com.gomeplus.oversea.bi.service.item.entity.Item;
import com.gomeplus.oversea.bi.service.item.utils.Paging;
import com.gomeplus.oversea.bi.service.item.vo.ItemDetailVo;

/**
 * 2017/2/13
 * 商品read接口
 */
public interface ItemReadService {
	
    Item findItemById(String id);
    
    ItemDetailVo findItemDetailById(String id);
    
    /**
     * 提供搜索可用商品列表
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    Paging<Item> findItemsForSearch(Integer status, Integer pageNum,
			Integer pageSize);
}
