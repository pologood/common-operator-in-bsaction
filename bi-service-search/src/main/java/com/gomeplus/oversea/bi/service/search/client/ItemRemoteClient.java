package com.gomeplus.oversea.bi.service.search.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gomeplus.oversea.bi.service.search.entity.Item;
import com.gomeplus.oversea.bi.service.search.utils.Paging;

@FeignClient("bi-service-item")
public interface ItemRemoteClient {

	 @RequestMapping(value="/item/items/search", method = RequestMethod.GET)
	 public Paging<Item> findItemsForSearch(@RequestParam(value="status")Integer status, 
										  @RequestParam(value="pageNum")Integer pageNum, 
										  @RequestParam(value="pageSize")Integer pageSize);
	
}
