package com.gomeplus.oversea.bi.service.item.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.gomeplus.oversea.bi.service.item.conf.ItemConfig;
import com.gomeplus.oversea.bi.service.item.dao.mysql.ItemDao;
import com.gomeplus.oversea.bi.service.item.dao.mysql.OuterCategoryDao;
import com.gomeplus.oversea.bi.service.item.dao.mysql.SkuDao;
import com.gomeplus.oversea.bi.service.item.dao.redis.ItemRedisDao;
import com.gomeplus.oversea.bi.service.item.dao.redis.OuterCategoryRedisDao;
import com.gomeplus.oversea.bi.service.item.entity.Item;
import com.gomeplus.oversea.bi.service.item.entity.Sku;
import com.gomeplus.oversea.bi.service.item.utils.CodeUtils;
import com.gomeplus.oversea.bi.service.item.vo.ItemDetailVo;
import com.gomeplus.oversea.bi.service.item.vo.ItemVo;
import com.gomeplus.oversea.bi.service.item.vo.PriceVo;
import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;

@Slf4j
@Component
public class ItemManager {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private SkuDao skuDao;
	@Autowired
	private OuterCategoryDao outerCategoryDao;
	@Autowired
	private OuterCategoryRedisDao outerCategoryRedisDao;
	@Autowired
	private ItemRedisDao itemRedisDao;
	@Autowired
	private ItemConfig itemConfig;
	
	/**
	 * 校验itemVo
	 * @param itemVo
	 * @return
	 */
	public int checkUpdateItemSku(ItemVo itemVo) {
		// 校验标识(0：商品不需要创建 1：商品需创建 2：商品需更新)
		int flag = 0;
		
		Item item = itemVo.getItem();
		Sku sku = itemVo.getSku();
		// 商品来源
		String source = item.getSource();
		sku.setSource(source);
		
		// 商品外部id
		String itemOuterId = item.getOuterId();
		// 查询商品是否缓存
		ItemVo cacheVo = itemRedisDao.getItemDetail(item.getSource(), itemOuterId);
		boolean equalsItem = true;
		boolean equalsSku = true;
		if(cacheVo != null) {
			equalsItem = equalsItem(item,cacheVo.getItem());
			equalsSku = equalsSku(sku, cacheVo.getSku());
			item.setId(cacheVo.getItem().getId());
			sku.setId(cacheVo.getSku().getId());
		} else {
			flag = 1;
			// 查询商品是否已创建
			Item existItem = itemDao.findByOuterIdAndSource(item.getOuterId(), source);
			if(existItem != null) {
				flag = 0;
				log.info("Item already exist, id:{}", existItem.getId()) ;
				equalsItem = equalsItem(item, existItem);
				
				Sku existSku = skuDao.findByCriteria(existItem.getId(), sku.getOuterId(), source);
				equalsSku = equalsSku(sku, existSku);
				item.setId(existItem.getId());
				sku.setId(existSku.getId());
				if(cacheVo == null) {
					// 更新详情缓存
					try {
						itemRedisDao.upsertItemDetail(source, itemOuterId, itemVo);
					} catch (Exception e) {
						log.warn("Save item detail cache failed, itemVo:{}", itemVo, e);
					}
				}
			} 
		}
		if(!equalsItem) {
			itemDao.update(item);
		}
		if(!equalsSku){
			skuDao.update(sku);
		}
		if(!equalsItem || !equalsSku) {
			flag = 2;
			// 更新详情缓存
			try {
				itemRedisDao.upsertItemDetail(source, itemOuterId, itemVo);
			} catch (Exception e) {
				log.warn("Update item detail cache failed, itemVo:{}", itemVo, e);
			}
		}
		return flag;
	}
	
	/**
	 * 商品详情信息转换
	 * @param item
	 * @return
	 */
	public ItemDetailVo getItemDetail(Item item) {
		// 商品详情vo
		ItemDetailVo vo = new ItemDetailVo();
		vo.setId(item.getId());
		vo.setName(item.getName());
		vo.setMainImage(item.getMainImage());
		vo.setOuterUrl(item.getOuterUrl());
		// 商品外部id
		//vo.setOuterId(item.getOuterId());
		// 商品来源
		vo.setSource(item.getSource());
		// 商品类目
		vo.setCategoryId(item.getCategoryId());
//		vo.setOuterCategoryId(item.getOuterCategoryId());
		// 商品状态
		vo.setStatus(item.getStatus());
		List<String> images = new ArrayList<String>();
		// 商品轮播图列表
		if(StringUtils.isNotBlank(item.getImages())) {
			images = JSON.parseArray(item.getImages(), String.class);
		}
		vo.setImages(images);
		PriceVo originPriceVo = new PriceVo();
		originPriceVo.setAmount(item.getOriginPrice());
		PriceVo sellPriceVo = new PriceVo();
		sellPriceVo.setAmount(item.getSellingPrice());
		// 预期返利
		PriceVo cashBack = new PriceVo();
		// 先预设
		cashBack.setAmount(1000L);
				
		// 货币类型
		String unit = item.getCurrencyUnit();
		originPriceVo.setCurrency(unit);
		sellPriceVo.setCurrency(unit);
		cashBack.setCurrency(unit);
		
		// 获取商品货币配置
		String symbols = itemConfig.getSymbols();
		if(StringUtils.isNotBlank(symbols)) {
			try {
				Map<String, String>  currencySymbols = JSON.parseObject(symbols, Map.class);
				String symbol = currencySymbols.get(unit+".symbol");
				String align = currencySymbols.get(unit+".align");
				if("INR".equals(unit)) {
					// 备选货币符号
					String alternative = currencySymbols.get(unit+".alternative");
					originPriceVo.setAlternative(alternative != null? alternative : "");
					sellPriceVo.setAlternative(alternative != null? alternative : "");
				}
				originPriceVo.setSymbol(symbol);
				originPriceVo.setAlign(align);
				sellPriceVo.setSymbol(symbol);
				sellPriceVo.setAlign(align);
				cashBack.setSymbol(symbol);
				cashBack.setAlign(align);
			} catch (Exception e) {
				log.warn("Currency symbols json error for itemId:{}", item.getId(), e);
				throw new C422Exception(CodeUtils.CURRENCY_NOT_EXIST);
			}
		}
		vo.setOriginPrice(originPriceVo);
		vo.setSellingPrice(sellPriceVo);
		vo.setDiscountPercentage(item.getDiscountPercentage());
		vo.setExpectedCashBack(cashBack);
		return vo;
	}
	
	/**
	 * 创建商品,sku和详情缓存
	 * @param itemVo
	 */
	@Transactional
	public void createItemSku(ItemVo itemVo){
		Item item = itemVo.getItem();
				
		Sku sku = itemVo.getSku();
		if(item.getStatus() == null) {
			item.setStatus(Item.Status.ON_SHELF.value());
		}
		itemDao.create(item);
		// 保存sku
		sku.setItemId(item.getId());
		sku.setSource(item.getSource());
		skuDao.create(sku);
		try {
			// 创建商品详情缓存
			itemRedisDao.upsertItemDetail(item.getSource(), item.getOuterId(), itemVo);
		} catch (Exception e) {
			log.warn("Create item detail cache failed, itemVo:{}", itemVo, e);
		}
	}
	
	/**
	 * 比较sku主要属性是否变化
	 * @param sku
	 * @param other
	 * @return
	 */
	public boolean equalsSku(Sku sku, Sku other) {
		if (sku == other)
			return true;
		if (other == null)
			return false;
		if (sku.getCurrencyUnit() == null) {
			if (other.getCurrencyUnit() != null)
				return false;
		} else if (!sku.getCurrencyUnit().equals(other.getCurrencyUnit()))
			return false;
		if (sku.getImage() == null) {
			if (other.getImage() != null)
				return false;
		} else if (!sku.getImage().equals(other.getImage()))
			return false;
		if (sku.getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!sku.getName().equals(other.getName()))
			return false;
		if (sku.getOriginPrice() == null) {
			if (other.getOriginPrice() != null)
				return false;
		} else if (!sku.getOriginPrice().equals(other.getOriginPrice()))
			return false;
		if (sku.getOuterId() == null) {
			if (other.getOuterId() != null)
				return false;
		} else if (!sku.getOuterId().equals(other.getOuterId()))
			return false;
		if (sku.getSellingPrice() == null) {
			if (other.getSellingPrice() != null)
				return false;
		} else if (!sku.getSellingPrice().equals(other.getSellingPrice()))
			return false;
		return true;
	}
	
	/**
	 * 比较item主要属性是否变化
	 * @param item
	 * @param other
	 * @return
	 */
	public boolean equalsItem(Item item, Item other) {
		if (item == other)
			return true;
		if (other == null)
			return false;
		if (item.getCurrencyUnit() == null) {
			if (other.getCurrencyUnit() != null)
				return false;
		} else if (!item.getCurrencyUnit().equals(other.getCurrencyUnit()))
			return false;
		if (item.getImages() == null) {
			if (other.getImages() != null)
				return false;
		} else if (!item.getImages().equals(other.getImages()))
			return false;
		if (item.getMainImage() == null) {
			if (other.getMainImage() != null)
				return false;
		} else if (!item.getMainImage().equals(other.getMainImage()))
			return false;
		if (item.getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!item.getName().equals(other.getName()))
			return false;
		if (item.getOriginPrice() == null) {
			if (other.getOriginPrice() != null)
				return false;
		} else if (!item.getOriginPrice().equals(other.getOriginPrice()))
			return false;
		if (item.getOuterId() == null) {
			if (other.getOuterId() != null)
				return false;
		} else if (!item.getOuterId().equals(other.getOuterId()))
			return false;
		if (item.getOuterUrl() == null) {
			if (other.getOuterUrl() != null)
				return false;
		} else if (!item.getOuterUrl().equals(other.getOuterUrl()))
			return false;
		if (item.getSellingPrice() == null) {
			if (other.getSellingPrice() != null)
				return false;
		} else if (!item.getSellingPrice().equals(other.getSellingPrice()))
			return false;
		if (item.getDiscountPercentage() == null) {
			if (other.getDiscountPercentage() != null)
				return false;
		} else if (!item.getDiscountPercentage().equals(other.getDiscountPercentage()))
			return false;
		if (item.getSource() == null) {
			if (other.getSource() != null)
				return false;
		} else if (!item.getSource().equals(other.getSource()))
			return false;
		return true;
	}
}
