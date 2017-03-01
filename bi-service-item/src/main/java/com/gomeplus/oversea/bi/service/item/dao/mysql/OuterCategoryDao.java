package com.gomeplus.oversea.bi.service.item.dao.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.gomeplus.oversea.bi.service.item.entity.Category;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategory;
import com.google.common.collect.ImmutableMap;

/**
 * 2017/2/13
 * 外站类目映射dao
 */
@Repository
public class OuterCategoryDao extends BaseDao<OuterCategory>{
	
	/**
	 * 根据名称和来源查询外站类目
	 * @param name
	 * @param source
	 * @return
	 */
	public Category findByNameAndSource(String name, String source) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("source", source);
		return getSqlSession().selectOne(sqlId("findByNameAndSource"), params);
	}

	public Long findCategoryIdByCriteria(Map<String, Object> criteria) {
		return getSqlSession().selectOne(sqlId("findCategoryIdByCriteria"), criteria);
	}
	
	/**
	 * 根据父级类目ID查询外站类目子类目列表
	 * @param parentId
	 * @return
	 */
	public List<OuterCategory> findOuterCategoriesByParentId(String parentId) {
		return getSqlSession().selectList(
				sqlId("findOuterCategoriesByParentId"),
				ImmutableMap.of("parentId", Long.parseLong(parentId)));
	}
}
