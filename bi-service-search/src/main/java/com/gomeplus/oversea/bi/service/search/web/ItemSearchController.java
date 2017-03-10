package com.gomeplus.oversea.bi.service.search.web;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gomeplus.oversea.bi.service.search.client.ItemRemoteClient;
import com.gomeplus.oversea.bi.service.search.dao.es.ElasticSearch;
import com.gomeplus.oversea.bi.service.search.entity.ItemSearchQuery;
import com.gomeplus.oversea.bi.service.search.entity.ResponseSearchInfo;
import com.gomeplus.oversea.bi.service.search.service.ItemSearchService;
import com.gomeplus.oversea.bi.service.search.utils.ItemMappingManager;
import com.gomeplus.oversea.bs.common.exception.code4xx.C400Exception;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
@Slf4j
@RequestMapping("/item/searchItems")
public class ItemSearchController {
	
	@Autowired
	private ItemRemoteClient itemRemoteClient;
	
	@Autowired
	private ItemSearchService itemSearchService;
	
	@Autowired
	private ElasticSearch elasticSearch;
	
	
	
	@ApiOperation(value = "根据关键字搜索商品")
    @ApiImplicitParam(name = "query", value = "查询商品参数", required = true, dataType = "ItemSearchQuery")
	@HystrixCommand(commandProperties={@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")},fallbackMethod="search")
	@RequestMapping(method = RequestMethod.GET)
	public Object search(HttpServletRequest request) {
		
		ItemSearchQuery query = getRequestInfo(request);	 
		log.info("begin to search for request:"+request.toString());
		ResponseSearchInfo searchInfo = itemSearchService.search(query);
		return searchInfo;
		
	}
	
	/**
     * 获取入参
     *
     * @param request
     * @return
     */
    public ItemSearchQuery getRequestInfo(HttpServletRequest request) {
    	try {
			ItemSearchQuery req = new ItemSearchQuery();
			String keyword = request.getParameter("keyword");
			String sortCriteria = request.getParameter("sortCriteria");
			String sort = request.getParameter("sort");
			String pageSize = request.getParameter("pageSize");
			String pageNum = request.getParameter("pageNum");
			String categoryId = request.getParameter("categoryId");
			String filterHideIds = request.getParameter("filterHideIds");
			//业务相关字段
			req.setKeyword(keyword);
			if (!StringUtils.isEmpty(sortCriteria)  && StringUtils.isNumeric(sortCriteria)) {
			    req.setSortCriteria(Integer.parseInt(sortCriteria));
			}
			if (!StringUtils.isEmpty(sort)  && StringUtils.isNumeric(sort)) {
			    req.setSort(Integer.parseInt(sort));
			}
			if (!StringUtils.isEmpty(pageSize)  && StringUtils.isNumeric(pageSize)) {
			    req.setPageSize(Integer.parseInt(pageSize));
			}
			if (!StringUtils.isEmpty(pageNum) && StringUtils.isNumeric(pageNum)) {
			    req.setPageNum(Integer.parseInt(pageNum));
			}
			if (!StringUtils.isEmpty(categoryId) && StringUtils.isNumeric(categoryId)) {
			    req.setCategoryId(Integer.parseInt(categoryId));
			}
			req.setFilterHideIds(filterHideIds);
			
			//埋点,搜索日志相关字段
			String lang = request.getHeader("X-Gomeplus-Lang");
			String device = request.getHeader("X-Gomeplus-Device");
			String traceId = request.getHeader("X-Gomeplus-Trace-Id");
			//String deviceId = request.getHeader("X-Gomeplus-Unique-Device-Id");
			String userId = request.getHeader("X-Gomeplus-User-Id");
			String timeZone = request.getHeader("X-Gomeplus-Time-Zone");
			String app = request.getHeader("X-Gomeplus-App");
			String pageType = request.getParameter("pageType");
			String pageModule = request.getParameter("pageModule");
			String pageTag = request.getParameter("pageTag");
			String itemId = request.getParameter("itemId");

			if(!StringUtils.isEmpty(device)){
			    String[] deviceStr=device.split("/");
			    if(deviceStr.length==4){
			    	req.setDevice(deviceStr[3]);
			    }
			}else{
			    req.setDevice("");
			}
			req.setLang(lang);
			req.setTraceId(traceId);
			//req.setDeviceId(deviceId);
			req.setUserId(userId);
			req.setTimeZone(timeZone);
			req.setApp(app);
			req.setPageType(pageType);
			req.setPageModule(pageModule);
			req.setPageTag(pageTag);
			req.setItemId(itemId);
			
			return req;
		} catch (Exception e) {
			 log.error("parse search request failed:", e);
	         throw new C400Exception("parse search request failed");
 
		}
    }
	
	@ApiOperation(value = "全量添加索引数据")
    @ApiImplicitParam(name = "saveIndexAll", value = "null", required = false, dataType = "NULL")
	@HystrixCommand(commandProperties={@HystrixProperty(name = "execution.timeout.enabled", value = "false")})
	@RequestMapping(method = RequestMethod.POST)
	public String saveIndexAll(){
		 return itemSearchService.saveAllIndex();
	}
	
	@ApiOperation(value = "构建索引Mapping结构")
    @ApiImplicitParam(name = "buildIndexMapping", value = "null", required = false, dataType = "NULL")
	@HystrixCommand(commandProperties={@HystrixProperty(name = "execution.timeout.enabled", value = "false")})
	@RequestMapping(value="/buildMapping",method = RequestMethod.POST)
	public String buildIndexMapping(){
		 try {
			ItemMappingManager.createAnalysisMapping(elasticSearch, "oversea_items_index", "items");
			return "SUCCESS";
		} catch (Exception e) {
			log.error("es create mapping fail", e);
			return "ERROR";
		}
	}
}
