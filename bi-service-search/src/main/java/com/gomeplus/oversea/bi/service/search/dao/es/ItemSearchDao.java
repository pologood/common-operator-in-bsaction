package com.gomeplus.oversea.bi.service.search.dao.es;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.gomeplus.oversea.bi.service.search.entity.ItemSearchQuery;

@Component
public class ItemSearchDao {
	@Value("${es.index.name}")
	private String index ;
	@Value("${es.index.type}")
    private String mappingType ;
    
    @Autowired
	private ElasticSearch elasticSearch;
	
	/**
	 * 构建搜索对象
	 * @return SearchRequestBuilder
	 * */
	public SearchRequestBuilder createSearch(){
		return elasticSearch.getClient().prepareSearch(index);//.setTypes(mappingType);
	}
	
	//过滤特殊字符
	public static String StringFilter(String orgStr) throws PatternSyntaxException {
		// 只允许字母和数字
		// 清除掉所有特殊字符
		if (null!=orgStr&&!"".equals(orgStr.trim())) {  
            String regEx="[\\s~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";  
            Pattern p = Pattern.compile(regEx);  
            Matcher m = p.matcher(orgStr);  
            return m.replaceAll(" ");
        }
        return null;
	}
	
	/**
	 * 构建关键字查询条件
	 * @param keyWord 搜索关键字
	 * @param fieldNameList 搜索的字段
	 * @return QueryStringQueryBuilder
	 * */
	public QueryBuilder searchKeyWord(String keyword,List<String> fieldNameList){
		// 特殊字符转义
		if (StringUtils.isEmpty(keyword) || "".equals(keyword.trim())) {
			keyword = "*";
		}
		if(keyword.equals("*")){
			return QueryBuilders.matchAllQuery();
		}else{
			BoolQueryBuilder bool =	QueryBuilders.boolQuery() ;
			/**
			 * 布尔查询采用越多匹配越好的方法，所以每个match子句的得分会被加起来变成最后的每个文档的得分。
			 * 匹配两个子句的文档的得分会比只匹配了一个文档的得分高
			 * */
			for(String fieldName:fieldNameList){
				bool.should(QueryBuilders.matchQuery(fieldName, keyword));
			}
			return bool;
		}
	}
	
	public BoolQueryBuilder searchFiledFilter(ItemSearchQuery query){
		BoolQueryBuilder bool =	QueryBuilders.boolQuery() ;
		
		Integer categoryId = query.getCategoryId();
		String filterHideIds = query.getFilterHideIds();
		
		if(categoryId != null){
			bool.filter(QueryBuilders.termQuery("categoryId",categoryId));
		}
		if(!StringUtils.isEmpty(filterHideIds)){
			String[] ids = filterHideIds.split(",");
			for (String id : ids) {
				bool.mustNot(QueryBuilders.termQuery("id",id));
			}
		}
		
		return bool;
	}
	
	/**
	 * 获得搜索的数据
	 * @param SearchResponse
	 * @param Class<T>
	 * @return 
	 * */
	public <T> List<T> searchData(SearchResponse response,Class<T> class1){
		SearchHits hits = response.getHits(); 
		List<T> list = new ArrayList<T>();
        for (SearchHit hit : hits) {  
            String json = hit.getSourceAsString();  
            list.add( JSON.parseObject(json, class1));
        }  
		return list;
	}
	
}
