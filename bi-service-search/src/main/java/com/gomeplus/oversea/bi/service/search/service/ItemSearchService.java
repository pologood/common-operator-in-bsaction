package com.gomeplus.oversea.bi.service.search.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gomeplus.oversea.bi.service.search.client.ItemRemoteClient;
import com.gomeplus.oversea.bi.service.search.dao.es.ItemIndexDao;
import com.gomeplus.oversea.bi.service.search.dao.es.ItemSearchDao;
import com.gomeplus.oversea.bi.service.search.entity.ABSearchInfo;
import com.gomeplus.oversea.bi.service.search.entity.Item;
import com.gomeplus.oversea.bi.service.search.entity.ItemSearchQuery;
import com.gomeplus.oversea.bi.service.search.entity.ItemSearchVo;
import com.gomeplus.oversea.bi.service.search.entity.ResponseItemInfo;
import com.gomeplus.oversea.bi.service.search.entity.ResponseSearchInfo;
import com.gomeplus.oversea.bi.service.search.thread.EsIndexCreateTask;
import com.gomeplus.oversea.bi.service.search.utils.Paging;

@Service
@Slf4j
public class ItemSearchService {

	public static final String SEARCH_TYPE = "items";

	// 检索fileds
	public static final List<String> SEARCH_FIELDS = Arrays.asList(new String[] { "name" });

	@Autowired
	private ItemRemoteClient itemRemoteClient;

	@Autowired
	private ItemSearchDao itemSearchDao;
	
	@Autowired
	private ItemIndexDao itemIndexDao;

	@Autowired
	private ABSearchInfo aBSearchInfo;

	/**
	 * 商品主搜
	 * 
	 * @param query
	 * @return
	 */
	public ResponseSearchInfo search(ItemSearchQuery query) {

		String keyword = query.getKeyword();
		Integer pageNum = query.getPageNum();
		Integer pageSize = query.getPageSize();

		if (pageNum == null) {
			query.setPageNum(1);
		}
		if (pageSize == null || pageSize <= 0) {
			query.setPageSize(10);
		}
		
		// 执行搜索逻辑
		// 只针对 name 字段进行查询
		QueryBuilder qb = itemSearchDao.searchKeyWord(keyword, SEARCH_FIELDS);

		// 针对类目id 和 推荐商品id不显示的过滤
		BoolQueryBuilder filedFilter = itemSearchDao.searchFiledFilter(query);

		// 设置索引类型(相当于数据库items)
		SearchRequestBuilder srb = itemSearchDao.createSearch().setTypes(SEARCH_TYPE);

		SearchRequestBuilder searchRequestBuilder = srb
				.setQuery(qb)
				.setPostFilter(filedFilter)
				.setSearchType(SearchType.DEFAULT)
				.setFrom(((query.getPageNum() <= 0 ? 1 : query.getPageNum()) - 1) * query.getPageSize())
				.setSize(query.getPageSize());

		log.info("search request body : [{}]", searchRequestBuilder);

		SearchResponse sr = searchRequestBuilder.execute().actionGet();
		// 解析返回搜索文档,获得搜到的数据
		List<ItemSearchVo> list = itemSearchDao.searchData(sr,ItemSearchVo.class);

		// 召回总个数
		long totalCount = sr.getHits().getTotalHits();
		int count = new Long(totalCount).intValue();
		// 总页码
		int pageCount = new Long((totalCount + query.getPageSize() - 1) / query.getPageSize()).intValue();
		// 构建搜索返回对象
		return buildResponseSearchInfo(pageCount, count, list);
	}

	public ResponseSearchInfo buildResponseSearchInfo(Integer pageCount,Integer count, List<ItemSearchVo> list) {

		ResponseSearchInfo rsi = new ResponseSearchInfo();
		List<ResponseItemInfo> items = new ArrayList<ResponseItemInfo>();
		rsi.setPageCount(pageCount);
		rsi.setCount(count);

		if (list != null && list.size() > 0) {
			for (ItemSearchVo itemSearchVo : list) {
				String id = itemSearchVo.getId();
				aBSearchInfo.setRule("A01");
				//商品type为1
				ResponseItemInfo rii = new ResponseItemInfo(id,aBSearchInfo.toString(),1);
				rii.setType(1);
				items.add(rii);
			}
		}
		rsi.setItems(items);
		return rsi;
	}

	private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	/**
	 * 全量索引
	 * 
	 * @return
	 */
	public String saveAllIndex() {
		Paging<Item> paging = itemRemoteClient.findItemsForSearch(1, 0, 10);

		Long total = paging.getTotal();

		int x = (int) ((total + 999) / 1000);

		log.info(
				"Batch tasks executor thread pool:{} starting.................",
				executorService);

		for (int i = 1; i <= x; i++) {

			EsIndexCreateTask task = new EsIndexCreateTask(itemRemoteClient,itemIndexDao, i);

			executorService.execute(task);

			try {
				log.info("thread name : [{}] , wait 5s !", Thread.currentThread().getName());
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.info("Batch tasks executor thread pool:{}", executorService);
		}

		// 关闭启动线程
		executorService.shutdown();

		try {
			// 等待子线程结束，再继续执行下面的代码
			boolean isFinished = executorService.awaitTermination(
					Long.MAX_VALUE, TimeUnit.DAYS);

			if (isFinished) {
				log.info("all thread complete");
			}

		} catch (Exception e) {
			log.info("thread error or thread timeout");
			return "error";
		}
		log.info("Batch tasks executor thread pool:{} end.................",executorService);
		return "success";
	}

}
