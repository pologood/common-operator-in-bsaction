package com.gomeplus.oversea.bi.service.search.utils;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.gomeplus.oversea.bi.service.search.dao.es.ElasticSearch;


@Slf4j
public class ItemMappingManager {
	/**
	 * 创建mapping(field("indexAnalyzer","ik")该字段分词IK索引
	 * field("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
	 * 
	 * @param indices
	 *            索引名称
	 * @param mappingType
	 *            索引类型
	 * @throws Exception
	 */
	public static void createAnalysisMapping(ElasticSearch elasticSearch,String indices, String mappingType)
			throws Exception {
		IndicesExistsResponse ier = elasticSearch.getClient().admin().indices().exists(new IndicesExistsRequest(indices)).actionGet();
		//如果索引存在就不创建了
        if (!ier.isExists()) {
        	
        	// 索引未创建，创建一个空索引
		 	elasticSearch.getClient().admin().indices().prepareCreate(indices).execute().actionGet();
	    
			XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(mappingType).startObject("properties")	 
			.startObject("id").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()//唯一主键
			.startObject("name").field("type", "string").field("store", "yes").field("analyzer", "standard").endObject()////商品名称,搜索的关键词
			.startObject("sellingPrice").field("type", "long").field("store", "yes").field("index", "not_analyzed").endObject()// 
			.startObject("originPrice").field("type", "long").field("store", "yes").field("index", "not_analyzed").endObject()// 
			.startObject("status").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()// 
			.startObject("categoryId").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()// 
			.startObject("source").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()// 
			.startObject("createDate").field("type", "date").field("store", "yes").endObject()// 
			.endObject().endObject().endObject();
			 
			PutMappingRequest mapping = Requests.putMappingRequest(indices)
					.type(mappingType).source(builder);
			elasticSearch.getClient().admin().indices().putMapping(mapping).actionGet();
			log.info("Mapping has created,indexName：[{}],mappingType:[{}]",indices,mappingType);
        }else{
        	log.info("Mapping has exist,indexName：[{}],mappingType:[{}]",indices,mappingType);
        }
	}
	
}
