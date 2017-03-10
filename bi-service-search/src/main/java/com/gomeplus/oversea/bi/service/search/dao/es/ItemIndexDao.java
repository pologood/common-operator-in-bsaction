package com.gomeplus.oversea.bi.service.search.dao.es;

import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.gomeplus.oversea.bi.service.search.entity.ItemSearchVo;
import com.gomeplus.oversea.bi.service.search.utils.ItemMappingManager;

@Slf4j
@Component
@Configuration
public class ItemIndexDao {
	
	@Autowired
	private ElasticSearch elasticSearch ;
	@Value("${es.index.name}")
	private String indexName ;
	@Value("${es.index.type}")
	private String mappingType ;
	/**
	 * 新增或者整个更换
	 * @param q
	 */
    public void add(ItemSearchVo o){
    	String id="";
    	String json=""; 
    	if(o instanceof ItemSearchVo){
    		ItemSearchVo isv=(ItemSearchVo)o;
    		json = JSON.toJSONString(isv);
    		id = isv.getId();
    	}
    	if(StringUtils.isBlank(id)||StringUtils.isBlank(json)){
    		log.error("obj is not item index model,can't create elasticsearch index");
    		throw new RuntimeException("vo id is null or obj is not item index model,can't create elasticsearch index");
    	}
    	elasticSearch.addField(indexName, mappingType, id, json);
    	
    }
	/**
     * 更新 
     * @param id
     * @param map key为类属性名 如果你需要清除一个属性的值，传入null会导致ES认为你不需要更新这个属性，所以清除一个属性的值我们需要传入一个""
     */
	public void update(String id, Map<String, Object> map) {
		try {
			elasticSearch.updateField(indexName, mappingType, id, map);
		} catch (Exception e) {
			log.error(" update elasticsearch item index error,ID：[{}]",id,e);
		} 
	}
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public boolean delete(String id){
		return elasticSearch.delField(indexName, mappingType, id) ;
	}
	/**
	 * 查询
	 * @param id
	 * @return
	 */
	public SearchHit query(String id){
		return elasticSearch.queryById(indexName, mappingType, id) ;
	}
	
	/**
	 * 容器初始化的时候创建Mapping
	 * @param id
	 * @return
	 */
	@PostConstruct
	public void createMapping(){
		try {
			ItemMappingManager.createAnalysisMapping(elasticSearch,indexName,mappingType);
		} catch (Exception e) {
			log.error("elasticsearch create AnalysisMapping error,indexName：[{}],mappingType:[{}]",indexName,mappingType,e);
		}
	}

	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public String getMappingType() {
		return mappingType;
	}

	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}

	public ElasticSearch getElasticSearch() {
		return elasticSearch;
	}

	public void setElasticSearch(ElasticSearch elasticSearch) {
		this.elasticSearch = elasticSearch;
	}
}
