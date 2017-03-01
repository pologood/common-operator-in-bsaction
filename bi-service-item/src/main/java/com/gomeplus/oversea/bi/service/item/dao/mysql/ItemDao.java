package com.gomeplus.oversea.bi.service.item.dao.mysql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.gomeplus.oversea.bi.service.item.entity.Item;
import com.gomeplus.oversea.bi.service.item.utils.Paging;

/**
 * 2017/2/13
 * 商品dao
 */
@Repository
public class ItemDao extends BaseDao<Item>{
	
	public Item findAtomicById(String id) {
		return getSqlSession().selectOne(sqlId("findAtomicById"), Long.valueOf(id));
	}
	
	/**
	 * 根据outerId和来源查询商品
	 * @param outerId
	 * @param source
	 * @return
	 */
	public Item findByOuterIdAndSource(String outerId, String source) {
		Map<String, String> params = new HashMap<String,String>();
		params.put("outerId", outerId);
		params.put("source", source);
		return getSqlSession().selectOne(sqlId("findByOuterIdAndSource"), params);
	}
	
	/**
	 * 查询有后台类目绑定的商品列表
	 * @param status 商品状态，不传则查询所有状态
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Paging<Item> findItemsForSearch(Integer status, Integer offset, Integer limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		Long total = getSqlSession().selectOne(sqlId("countItemsForSearch"), params);
        if (total == 0L){
            return new Paging<Item>(0L, new ArrayList<Item>());
        }
		params.put("offset", offset);
		params.put("limit", limit);
		List<Item> items = getSqlSession().selectList(sqlId("findItemsForSearch"), params);
		return new Paging<Item>(total, items);
	}
}
