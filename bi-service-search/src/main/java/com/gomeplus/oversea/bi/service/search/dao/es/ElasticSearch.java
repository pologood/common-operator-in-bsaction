package com.gomeplus.oversea.bi.service.search.dao.es;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

@Slf4j
@Component
@Configuration
public class ElasticSearch {
	

	private static final String style = "color:#f60" ;
	
	@Value("${es.cluster.name}")
	private String clusterName;
	
	@Value("${es.cluster.address}")
	private String address;
	
    private Client client;
    
    public ElasticSearch() {
	}
    
    public ElasticSearch(String clusterName, String address, Client client) {
		super();
		this.clusterName = clusterName;
		this.address = address;
		this.client = client;
	}


	@PostConstruct
    public void init(){
    	 log.debug("clusterName:[{}],address:[{}]",clusterName,address);
	     client = createClient(clusterName, address); 
    }

    public  Client getClient() {
        return client;
    }
 
    /**
     * 删除
     * @param indexName
     * @param mappingType
     * @param id
     * @return 是否删除成功，不成功可能是没有找到对应的数据或者索引
     */
	public boolean delField(String indexName, String mappingType, String id) {
		if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(mappingType)) {
			log.error("delField 必填字段为空:indexName=" + indexName
					+ " mappingType=" + mappingType);
			throw new RuntimeException("delField 必填字段为空:indexName=" + indexName
					+ " mappingType=" + mappingType);
		}
		if (StringUtils.isEmpty(id)||"0".endsWith(id.trim())) {
			log.error("delField id必填填写:indexName=" + indexName
					+ " mappingType=" + mappingType);
			throw new RuntimeException("delField id必填填写:indexName=" + indexName
					+ " mappingType=" + mappingType);
		}
		DeleteResponse d = client
				.prepareDelete(indexName, mappingType, id)
				.execute().actionGet();
		return d.isFound();
	}
    
    /**
     * 更新
     * @param indexName
     * @param mappingType
     * @param id
     * @param map 如果你需要清除一个属性的值，传入null会导致ES认为你不需要更新这个属性，所以清除一个属性的值我们需要传入一个""
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
	public void updateField(String indexName, String mappingType, String id,
			Map<String, Object> map) {
		if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(mappingType)) {
			log.error("addField 必填字段为空:indexName=" + indexName
					+ " mappingType=" + mappingType);
			throw new RuntimeException("addField 必填字段为空:indexName=" + indexName
					+ " mappingType=" + mappingType);
		}
		if (StringUtils.isEmpty(id)||"0".endsWith(id.trim())) {
			log.error("addField id必填填写:indexName=" + indexName
					+ " mappingType=" + mappingType);
			throw new RuntimeException("addField id必填填写:indexName=" + indexName
					+ " mappingType=" + mappingType);
		}
		try {
			UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.index(indexName);
			updateRequest.type(mappingType);
			updateRequest.id(id);

			XContentBuilder builder = XContentFactory.jsonBuilder()
					.startObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getKey().equalsIgnoreCase("serialVersionUID") ){
					continue ;
				}
				if(entry.getValue() instanceof Date){ //保持和json里一样的格式
					Date temp = (Date)entry.getValue() ;
					builder.field(entry.getKey(),temp.getTime());
					continue ;
				}
				builder.field(entry.getKey(), entry.getValue());
			}
			builder.endObject();
			updateRequest.doc(builder);
			client.update(updateRequest).get();
		} catch (Exception e) {
			log.error("updateField  fail!", e);//忽略此异常，一个值刚创建，然后更新，会报错，但实际插入值是正确的
		}
    }
    
    /**
     * 新加类型索引
     * @param indexName 类似库名
     * @param indexType 表名
     * @param id  相同的id多次 add会替换掉旧的值
     * @param beanJson  JSON.toJSONString(bean/List<bean<)
     */
    public  void addField(String indexName, String mappingType, String id ,String beanJson) {
    	  if(StringUtils.isEmpty(indexName)||StringUtils.isEmpty(mappingType)){
    		  log.error("addField 必填字段为空:indexName="+indexName+" mappingType="+mappingType); 
    		  throw new RuntimeException("addField 必填字段为空:indexName="+indexName+" mappingType="+mappingType) ;
    	  }
    	  if(StringUtils.isEmpty(beanJson)){
    		  log.error("addField beanJson为空:indexName="+indexName+" mappingType="+mappingType); 
    		  throw new RuntimeException("addField beanJson为空:indexName="+indexName+" mappingType="+mappingType) ;
    	  }
    	  
		Map<String, Object> map = JSON.parseObject(beanJson,
				new TypeReference<Map<String, Object>>() {
				});
		if (StringUtils.isEmpty(id)||"0".endsWith(id.trim())) {
			log.error("addField id必填填写:indexName="+indexName+" mappingType="+mappingType);
    		  throw new RuntimeException("addField id必填填写:indexName="+indexName+" mappingType="+mappingType) ;
    	  }
        try {
        	XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getKey().equalsIgnoreCase("serialVersionUID") ){
					continue ;
				}
				builder.field(entry.getKey(), entry.getValue()) ;
			}
            builder.endObject();
           
			IndexResponse response = client
					.prepareIndex(indexName, mappingType, id)
					.setSource(builder).execute().actionGet();
			log.debug("response:" + response.toString()+" response id:" + response.getId() +" input id :"+id);
        } catch (IOException e) {
        	log.error("add filed fail!", e);
            throw new RuntimeException("add filed fail!", e);
        } 
    }
    

	/**
	 * 简单搜索
	 * @param index 索引名
	 * @param mappingType mapping名，可为空，查整个index
	 * @param queryWord 查询关键词
	 * @param fieldNameList 查询具体的字段
	 * @param start 从0开始
	 * @param pageSize
	 * @return
	 */
    public SearchResponse query(String index,String mappingType,String queryWord,List<String> fieldNameList,int start,int pageSize){
    	
		if (fieldNameList==null||fieldNameList.isEmpty()) {
			log.error("query 必填字段fieldNameList");
			throw new RuntimeException("query 必填字段fieldNameList");
		}
		if (StringUtils.isEmpty(index)) {
			log.error("query 必填字段index");
			throw new RuntimeException("query 必填字段index");
		}
    	if(StringUtils.isEmpty(queryWord)){
    		  log.error("query 必填字段queryWord"); 
    		  throw new RuntimeException("query 必填字段queryWord") ;
    	}
    	QueryStringQueryBuilder  builder =	QueryBuilders.queryStringQuery(QueryParser.escape(queryWord));
    		for(String fieldName:fieldNameList)	{
    			builder.field(fieldName);
    		}
    	SearchResponse response ;

		if (StringUtils.isEmpty(mappingType)) {
			SearchRequestBuilder srb = client.prepareSearch(index)
					.setQuery(builder).setFrom(start).setSize(pageSize)
					.setExplain(true);
			for (String fieldName : fieldNameList) {
				srb.addHighlightedField(fieldName);
			}
			// 设置高亮显示
			response = srb
					.setHighlighterPreTags("<span style=\"" + style + "\">")
					.setHighlighterPostTags("</span>")
					// 设置高亮结束
					.execute().actionGet();
		} else {

			SearchRequestBuilder srb = client.prepareSearch(index)
					.setTypes(mappingType).setQuery(builder).setFrom(start)
					.setSize(pageSize).setExplain(true);
			for (String fieldName : fieldNameList) {
				srb.addHighlightedField(fieldName);
			}
			// 设置高亮显示
			response = srb
					.setHighlighterPreTags("<span style=\"" + style + "\">")
					.setHighlighterPostTags("</span>")
					// 设置高亮结束
					.execute().actionGet();
		}
		return response;
    }
    
    /**
     * 前缀查询--不分词
     * @param index
     * @param mappingType 必须输入
     * @param queryWord
     * @param column 查询的字段列名
     * @param size 查询几条
     * @return
     */
    public SearchResponse prefixQuery(String index,String mappingType,String queryWord,String column,int size){
		QueryBuilder query = QueryBuilders.prefixQuery(column, QueryParser.escape(queryWord)) ;
	       SearchResponse response = client.prepareSearch(index).setTypes(mappingType) 
	                .setQuery(query).setFrom(0).setSize(size)
	                .execute().actionGet();
		return response;
    }
    
    /**
     * 根据id查询
     * @param index
     * @param mappingType
     * @param id
     * @return
     */
    public SearchHit queryById(String index,String mappingType,String id){
    	QueryStringQueryBuilder  builder =	QueryBuilders.queryStringQuery(id).field("id") ;
	       SearchResponse response = client.prepareSearch(index).setTypes(mappingType).setQuery(builder)
	                .execute().actionGet();
			SearchHits  hits  = response.getHits() ;
	    	if(hits.getTotalHits()==0 || hits.getTotalHits() > 1){
	    		log.error("index:"+index+"  mappingType :"+mappingType+" id:"+id+" 找到对应的数据异常，为:"+hits.getTotalHits() +" 个"); 
	    		return null ;
	    	}
	    	Iterator<SearchHit> it = hits.iterator();  
		  return  it.next() ;
    }
    
    	//"client.transport.sniff", true  集群下用
	    private Client createClient(String cluster, String addresses) {
			Settings settings = Settings  
		            .settingsBuilder()  
		            .put("cluster.name",cluster)  
		            .put("client.transport.sniff", true)  
		            .build();  
			try {
				String[] addressArray = addresses.split(",");
	            
	            for (String address : addressArray) {
	            	String[] addrArr = address.split(":");
//	            	new InetSocketTransportAddress(InetAddress.getByName(addrArr[0]), Integer.valueOf(addrArr[1]));
	            	client = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(addrArr[0]), Integer.valueOf(addrArr[1])));
	            }
	        } catch (UnknownHostException e) {  
	            e.printStackTrace();  
	        }
	        return client;
	    }
    
	/**
	 * 得到高亮的词
	 * @param result
	 * @param fildName
	 * @return
	 */
    public String getHitField(SearchHit hit,String fildName){
    	HighlightField  hf = hit.getHighlightFields().get(fildName) ;
    	if(hf!=null){
            String description = "";  
            for(Text text : hf.getFragments()){     
          	  description += text;   
            }
            return description ;
    	}
    	return	(String)hit.getSource().get(fildName) ;
    }
    
    public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
    
}
