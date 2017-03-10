package com.gomeplus.oversea.bi.service.spider.serviceImpl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.minlog.Log;
import com.gomeplus.oversea.bi.service.spider.common.Config;
import com.gomeplus.oversea.bi.service.spider.entity.Attributes;
import com.gomeplus.oversea.bi.service.spider.entity.Item;
import com.gomeplus.oversea.bi.service.spider.entity.ItemVo;
import com.gomeplus.oversea.bi.service.spider.entity.ProductInfo;
import com.gomeplus.oversea.bi.service.spider.entity.Sku;
import com.gomeplus.oversea.bi.service.spider.exception.AffiliateAPIException;
import com.gomeplus.oversea.bi.service.spider.mq.MessageProducer;
import com.gomeplus.oversea.bi.service.spider.mq.SubmitAction;
import com.gomeplus.oversea.bi.service.spider.service.DataParser;

@Slf4j
@Service
public class SpiderService extends DataParser{
		@Value("${spider.flipkart.affiliateId}")
	    private String affiliateId;
		@Value("${spider.flipkart.affiliateToken}")
	    private String affiliateToken;
		@Value("${spider.flipkart.affiliateBaseUrl}")
	    private String affiliateBaseUrl;
		@Value("${spider.flipkart.searchProductUrl}")
		private String searchProductUrl;
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2*Runtime.getRuntime().availableProcessors());
		//ExecutorService newFixedThreadPool = Executors.newCachedThreadPool();2*Runtime.getRuntime().availableProcessors()
		private Queue<String> nextPageQueue = new ConcurrentLinkedQueue<String>();
	    @Autowired
	    private MessageProducer  messageProducer;
	    DecimalFormat decimalFormat = new DecimalFormat("###################.###########");  
	 public Map<String, String> initializeProductDirectory() {
	        Map<String, String> productDirectory = new HashMap<String, String>();
	        try {
	        	Log.info("get FLIPKART category url[{}]", affiliateBaseUrl);
	            String jsonData = queryService(affiliateBaseUrl);
	            JSONObject obj = new JSONObject(jsonData);
	            System.out.println(jsonData);
	            JSONObject listing = obj.getJSONObject("apiGroups").getJSONObject("affiliate").getJSONObject("apiListings");
	            Iterator keys = listing.keys();
	            while(keys.hasNext()) {

	                String category_name = (String)keys.next();
	                JSONObject variants = listing.getJSONObject(category_name).getJSONObject("availableVariants");

	                // Sort the variants and get the latest version
	                Iterator v_iterator = variants.keys();
	                List<String> variant_keys = new ArrayList<String>();
	                while(v_iterator.hasNext()) {
	                    variant_keys.add((String)v_iterator.next());
	                }
	                Collections.sort(variant_keys);
	                String category_url = variants.getJSONObject(variant_keys.get(1)).getString("get");

	                productDirectory.put(category_name, category_url);
	            }
	        }
	        catch(Exception e) {
	        	Log.error("not find  categoryList categoryurl[{}]",affiliateBaseUrl,  e);
	        	//throw new C500Exception("not find  categoryList");
	        }

	        return productDirectory;
	    }
	
	public List<Item> getProductList(String productListUrl)
			 {
		 List<Item> plist = new ArrayList<Item>();
		 Log.info("fetch item list url itemsUrl[{}]", productListUrl);
	        	  String queryUrl = productListUrl+"&inStock=true";
	        	  nextPageQueue.add(queryUrl);
	            while(!nextPageQueue.isEmpty()&&plist.size()<=20000) {
	              try{ 
	            	  	String jsonData = queryService(nextPageQueue.poll());
	                	log.info("fetch onePage products data:{}",jsonData);
	                	JSONObject obj = new JSONObject(jsonData);
	                	String nextUrl = obj.optString("nextUrl", "");
	               if(StringUtils.isNotEmpty(nextUrl)){
	            	   nextPageQueue.add(nextUrl);
	               }
	           newFixedThreadPool.execute(new Runnable() {
					
					@Override
					public void run() {
						   JSONArray productArray = obj.getJSONArray("productInfoList");
			                ItemVo itemVo = new ItemVo();
			                for(int i =0; i < productArray.length(); i++) {
		                      ProductInfo pinfo = outProductToSku(productArray.getJSONObject(i),false);
			                   		if(pinfo==null) {
			                   			continue;
			                   		}
			                  		String categoryPath = pinfo.getCategoryPath();
			                  		if(StringUtils.isEmpty(categoryPath)) {
			                  			continue;
			                  		}
			                  		Item item = new Item();
			                  		List<String> images = new ArrayList<String>();
			                  		item.setBrandName(pinfo.getBrandName());
			                  		item.setCurrencyUnit(pinfo.getCurrencyUnit());
			                  		item.setDiscountPercentage(pinfo.getDiscountPercentage());
			                  		item.setMainImage(pinfo.getMainImage());
			                  		item.setName(pinfo.getName());
			                  		item.setOriginPrice(pinfo.getOriginPrice());
			                  		item.setSellingPrice(pinfo.getSellingPrice());
			                  		item.setOuterId(pinfo.getOuterId());
			                  		item.setOuterUrl(pinfo.getOuterUrl());
			                  		item.setSource(pinfo.getSource());
			                  		item.setStatus(pinfo.getStatus());
			                  		String[] categoryPaths = categoryPath.split(">");
			                  		itemVo.setOuterCategories(categoryPaths);
			                  		List<String> outerSkuIds = pinfo.getOutSkuIds();
			                  		outerSkuIds.remove(item.getOuterId());
			                  		List<Sku> skus = new ArrayList<Sku>();
			                  		Sku sku = packageSku(pinfo);
		                  			skus.add(sku);
			                  		for (String outerSkuId : outerSkuIds) {
			                  			if(StringUtils.isNotEmpty(outerSkuId)) {
			                  			try {
											pinfo = searchProductById(outerSkuId);
											if(pinfo!=null&&StringUtils.isNotEmpty(pinfo.getCategoryPath())) {
												if(images.size()<=5) {
													images.add(pinfo.getMainImage());
												}
												Sku skuInfo = packageSku(pinfo);
												skus.add(skuInfo);
											}
										} catch (Exception e) {
											continue;
										}
			                  			}
									}
			                  		item.setImages(JSON.toJSONString(images));
			                  		itemVo.setItem(item);
			                  		itemVo.setSkus(skus);
		 	                  		Log.info("fetch itemVoInfo itemVo[{}]", JSON.toJSONString(itemVo));
			                  		messageProducer.sendMessageToMq(Config.TOPIC_EXCHENAGER, Config.SPIDER_TYPE, SubmitAction.SPIDER_CREATE, itemVo);
			                  		plist.add(pinfo);
			                }
			               
					}
				});
	           		Thread.sleep(4000);
	              }catch(Exception e) {
	            	  Log.error("fetch itemsList fail", e);
	              }
	           	
                   /*queryUrl = obj.optString("nextUrl", "");
	               if(queryUrl != null && !queryUrl.isEmpty()&&plist.size()>20000) { queryUrl = ""; }*/
	            }
	            
	         /* newFixedThreadPool.shutdown(); 
	            boolean isFinished = newFixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
	                if(isFinished){ 
	                	log.info("线程池已关闭");
	                }*/ 

	        return plist;
	}
	private Sku packageSku(ProductInfo pinfo) {
		Sku sku = new Sku();
		sku.setName(pinfo.getName());
		sku.setOriginPrice(pinfo.getOriginPrice());
		sku.setSellingPrice(pinfo.getSellingPrice());
		sku.setStatus(pinfo.getStatus());
		sku.setCurrencyUnit(pinfo.getCurrencyUnit());
		sku.setDiscountPercentage(pinfo.getDiscountPercentage());
		sku.setImage(pinfo.getMainImage());
		sku.setGeneralAttributes(pinfo.getGeneralAttributes());
		sku.setOuterId(pinfo.getOuterId());
		sku.setSaleAttributes(StringUtils.isNotEmpty(pinfo.getSaleAttributes())?pinfo.getSaleAttributes():"[]");
		sku.setOuterUrl(pinfo.getOuterUrl());
		sku.setBrandName(pinfo.getBrandName());
		return sku;
	}

	public ProductInfo  searchProductById(String productId) throws AffiliateAPIException {
		StringBuilder buffBuilder = new StringBuilder(searchProductUrl);
		buffBuilder.append("?id=").append(productId);
		String url = buffBuilder.toString();
		try {
			String jsonData = this.queryService(url);
			log.info("searchProductById data:{}",jsonData);
			if(StringUtils.isNotEmpty(jsonData)) {
				JSONObject jsonObject = new JSONObject(jsonData);
				ProductInfo pinfo = outProductToSku(jsonObject, true);
				return pinfo;
			}
		} catch (Exception e) {
			log.error("fetch product info fail productId:{}",productId);
		}
		return null;
	}
	public ProductInfo outProductToSku(JSONObject object,boolean isSku) {
		ProductInfo pInfo = new ProductInfo();
		JSONObject productBaseInfoV1 = object.getJSONObject("productBaseInfoV1");
		JSONObject categorySpecificInfoV1 = object.getJSONObject("categorySpecificInfoV1");
		String productId = productBaseInfoV1.getString("productId");
		try {
			String mainImage = productBaseInfoV1.getJSONObject("imageUrls").getString("400x400");
			String outProductUrl = productBaseInfoV1.getString("productUrl");
		    String title =  productBaseInfoV1.getString("title");
		    String categoryPath = productBaseInfoV1.getString("categoryPath");
			String brand = productBaseInfoV1.getString("productBrand");
			 BigDecimal a=new BigDecimal(productBaseInfoV1.getJSONObject("maximumRetailPrice").getDouble("amount"));
	        String currencyUnit =  productBaseInfoV1.getJSONObject("maximumRetailPrice").getString("currency");
	         BigDecimal sellprice=new BigDecimal(productBaseInfoV1.getJSONObject("flipkartSellingPrice").getDouble("amount"));
	         Double discount = productBaseInfoV1.getDouble("discountPercentage");    
	         List<Attributes> saleAttributes = parseSaleAttributes(productBaseInfoV1.getJSONObject("attributes")); 
	         if(saleAttributes!=null&&saleAttributes.size()>0) {
	        	 String saleAttrs = JSON.toJSONString(saleAttributes);
	        	 pInfo.setSaleAttributes(saleAttrs);
	         }
	         String generalAttributes = parseGeneralAttributes(categorySpecificInfoV1.getJSONArray("specificationList"));
	         pInfo.setGeneralAttributes(generalAttributes);
	         pInfo.setBrandName(brand);
	         pInfo.setImages("[]");
	         pInfo.setCurrencyUnit(currencyUnit);
	         pInfo.setMainImage(mainImage);
	         pInfo.setDiscountPercentage( decimalFormat.format(discount));
	         pInfo.setName(title);
	         pInfo.setOriginPrice(a.multiply(new BigDecimal(100)).longValue());
	         pInfo.setSellingPrice(sellprice.multiply(new BigDecimal(100)).longValue());
	         pInfo.setStatus(Item.Status.ON_SHELF.value());
	         pInfo.setSource(Config.FLIPKART_SOURCE);
	         pInfo.setOuterId(productId);
	         pInfo.setOuterUrl(outProductUrl);
	         pInfo.setCategoryPath(categoryPath);
	         if(!isSku) {
	         JSONArray productFamily = productBaseInfoV1.getJSONArray("productFamily");
	         List<String> outSkuIds = new ArrayList<String>();
	         for (int i =0;i<productFamily.length();i++) {
				String skuId = productFamily.getString(i);
				outSkuIds.add(skuId);
			}
	         pInfo.setOutSkuIds(outSkuIds);
	         }
		} catch (JSONException e) {
			System.out.println(object.toString());
			log.error("parse product fail productInfo{}",object.toString(), e);
			return null;
		}
		
		return pInfo;
	}
	
	public List<Attributes> parseSaleAttributes(JSONObject jsonObject) {
		List<Attributes> saleAttributes = new ArrayList<Attributes>();
		Map<String, Object> saleMap = JSON.parseObject(jsonObject.toString(), Map.class);
		for (Map.Entry<String, Object> entry:saleMap.entrySet()) {
			if(StringUtils.isNotEmpty(entry.getValue().toString())) {
				Attributes attributes = new Attributes();
				attributes.setKey(entry.getKey());
				attributes.setValue(entry.getValue().toString());
				saleAttributes.add(attributes);
			}
		}
		return saleAttributes;
	}
	
	public String parseGeneralAttributes(JSONArray jsonArray) {
		return jsonArray.toString();
	}
	
	 public String getAffiliateId() {
	        return affiliateId;
	    }
	    public String getAffiliateToken() {
	        return affiliateToken;
	    }

		public void sendOneItem(ItemVo itemVo) {
			 messageProducer.sendMessageToMq(Config.TOPIC_EXCHENAGER, Config.SPIDER_TYPE, SubmitAction.SPIDER_CREATE, itemVo);
			
		}
		
	
}
