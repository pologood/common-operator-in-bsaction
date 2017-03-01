package com.gomeplus.oversea.bi.service.item.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gomeplus.oversea.bi.service.item.dao.mysql.ItemDao;
import com.gomeplus.oversea.bi.service.item.entity.Item;
import com.gomeplus.oversea.bi.service.item.manager.ItemManager;
import com.gomeplus.oversea.bi.service.item.service.ItemReadService;
import com.gomeplus.oversea.bi.service.item.utils.CodeUtils;
import com.gomeplus.oversea.bi.service.item.utils.PageInfo;
import com.gomeplus.oversea.bi.service.item.utils.Paging;
import com.gomeplus.oversea.bi.service.item.vo.ItemDetailVo;
import com.gomeplus.oversea.bs.common.exception.code4xx.C404Exception;
/**
 * 商品read服务
 * 2017/2/16
 */
@Slf4j
@Service
public class ItemReadServiceImpl implements ItemReadService{
	@Autowired
	private ItemManager itemManager;
	@Autowired
	private ItemDao itemDao;

	@Override
	public Item findItemById(String id) {
		return itemDao.findAtomicById(id);
	}

	@Override
	public ItemDetailVo findItemDetailById(String id) {
		Item item = itemDao.load(id);
		if(item == null) {
        	throw new C404Exception(CodeUtils.ITEM_NOT_EXIST);
        }
		// 商品信息
		ItemDetailVo vo = itemManager.getItemDetail(item);
		// 后台类目id接口文档未返回
		vo.setCategoryId(null);
		return vo;
	}
	
	@Override
	public Paging<Item> findItemsForSearch(Integer status, Integer pageNum,
			Integer pageSize) {
		PageInfo page = new PageInfo(pageNum, pageSize);
		return itemDao.findItemsForSearch(status, page.getOffset(), page.getLimit());
	}
}
