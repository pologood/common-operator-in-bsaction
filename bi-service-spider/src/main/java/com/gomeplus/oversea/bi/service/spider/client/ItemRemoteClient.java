package com.gomeplus.oversea.bi.service.spider.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gomeplus.oversea.bi.service.spider.entity.Item;

@FeignClient("bi-service-item")
public interface ItemRemoteClient {
	  @RequestMapping(value="/item/item",method = RequestMethod.POST)
	  public String doPost(@RequestBody Item item);

}
