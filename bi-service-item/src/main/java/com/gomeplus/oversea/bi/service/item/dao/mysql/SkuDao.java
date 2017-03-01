package com.gomeplus.oversea.bi.service.item.dao.mysql;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.gomeplus.oversea.bi.service.item.entity.Sku;

/**
 * 2017/2/13
 * SKU dao
 */
@Repository
public class SkuDao extends BaseDao<Sku>{

	public Sku findByCriteria(String itemId, String outerId, String source) {
		Map<String, String> params = new HashMap<String,String>();
		params.put("itemId", itemId);
		params.put("outerId", outerId);
		params.put("source", source);
		return getSqlSession().selectOne(sqlId("findByCriteria"), params);
	}

}
