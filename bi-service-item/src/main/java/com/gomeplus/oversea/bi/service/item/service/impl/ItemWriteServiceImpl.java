package com.gomeplus.oversea.bi.service.item.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gomeplus.oversea.bi.service.item.dao.mysql.ItemDao;
import com.gomeplus.oversea.bi.service.item.entity.Item;
import com.gomeplus.oversea.bi.service.item.enums.SubmitAction;
import com.gomeplus.oversea.bi.service.item.manager.ItemManager;
import com.gomeplus.oversea.bi.service.item.mq.MessageProducer;
import com.gomeplus.oversea.bi.service.item.service.CategoryWriteService;
import com.gomeplus.oversea.bi.service.item.service.ItemWriteService;
import com.gomeplus.oversea.bi.service.item.vo.ItemDetailVo;
import com.gomeplus.oversea.bi.service.item.vo.ItemVo;

/**
 * 2017/2/13
 * 商品service实现
 *
 */
@Slf4j
@Service
public class ItemWriteServiceImpl implements ItemWriteService{
	
	private static final String TOPIC_EXCHENAGE = "bi.topic";
	private static final String TYPE_ITEM = "item.item";
	
	@Autowired
	private CategoryWriteService categoryWriteService;
	@Autowired
	private MessageProducer messageProducer;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemManager itemManager;
	
	@Override
	public String create(Item item) {
		if(item.getStatus() == null) {
			item.setStatus(Item.Status.OFF_SHELF.value());
		}
		itemDao.create(item);
		return item.getId();
	}

	@Override
	public Boolean update(Item item) {
		itemDao.update(item);
		return true;
	}

	/**
	 * 创建抓取商品数据，若存在则更新
	 */
	@Override
	public void createForSpider(ItemVo itemVo) {
		if(itemVo == null) {
			return;
		}
		Item item = itemVo.getItem();
		if(item == null || itemVo.getSku() == null) {
			return;
		}
		try {
			//外站类目进行处理
			Long outCategoryId = categoryWriteService.createAndCheckCategoryName(itemVo.getOuterCategories(),item.getSource());
			if(outCategoryId==null) {
				log.error("Get outCategoryId fail:{}", itemVo);
				return;
			}
			item.setOuterCategoryId(String.valueOf(outCategoryId));
		} catch (Exception e) {
			log.error("Find spider item outCategoryId fail:{}", itemVo, e);
			return;
		}
		int flag = 0;
		try {
			// 校验商品是否创建或更新
			flag = itemManager.checkUpdateItemSku(itemVo);
		} catch (Exception e) {
			log.error("Fail to update spider item:{}", itemVo, e);
			return;
		}
		if(flag == 1) {
			// 创建商品和sku
			try {
				itemManager.createItemSku(itemVo);
			} catch (Exception e) {
				log.error("Fail to create spider item:{}", itemVo, e);
				return;
			}
		} else if(flag == 2) {
			if(item.getCategoryId() != null) {
				try {
					// 获取商品详情
					ItemDetailVo itemDetail = itemManager.getItemDetail(itemVo.getItem());
					// 推送消息
					messageProducer.sendMessageToMq(item.getId(), TOPIC_EXCHENAGE, TYPE_ITEM, SubmitAction.UPDATE, itemDetail);
				
				} catch (Exception e) {
					log.error("Fail to send rabbitmq spider item:{}", itemVo, e);
					return;
				}
			}
		}
		return;
	}
}
