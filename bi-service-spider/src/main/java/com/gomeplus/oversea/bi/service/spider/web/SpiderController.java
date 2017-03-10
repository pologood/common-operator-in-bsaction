package com.gomeplus.oversea.bi.service.spider.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gomeplus.oversea.bi.service.spider.entity.Item;
import com.gomeplus.oversea.bi.service.spider.entity.ItemVo;
import com.gomeplus.oversea.bi.service.spider.exception.AffiliateAPIException;
import com.gomeplus.oversea.bi.service.spider.serviceImpl.SpiderService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
@Slf4j
@RequestMapping("/spider/fetchCategoryItem")
public class SpiderController {

	@Autowired
	private SpiderService spiderService;

	@ApiOperation(value = "start spider fetchCategoryItem", notes = "")
	@RequestMapping(method = RequestMethod.GET)
	//@HystrixCommand(commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") }, threadPoolProperties = { @HystrixProperty(name = "coreSize", value = "100") })
	public String doGet(@RequestParam String categoryName)
			throws AffiliateAPIException {
		log.info("start spider fetch categoryName[{}]", categoryName);
		Map<String, String> categoryUrls = spiderService
				.initializeProductDirectory();
		List<Item> items = new ArrayList<Item>();
		if (categoryUrls != null && categoryUrls.size() > 0) {
			String queryUrl = categoryUrls.get(categoryName);
			if (StringUtils.isEmpty(queryUrl)) {
				return "没有此类目！！！";
			}
			items = spiderService.getProductList(queryUrl);
		}
		return "抓取结束，共抓取商品数量：" + items.size();
	}

	@ApiOperation(value = "send one item test", notes = "")
	@RequestMapping(method = RequestMethod.POST)
	public String doPost(@RequestBody ItemVo ItemVo)
			throws AffiliateAPIException {
		
		spiderService.sendOneItem(ItemVo);
		return "发送完成";
	}

}
