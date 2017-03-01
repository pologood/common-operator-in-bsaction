package com.gomeplus.oversea.bi.service.item.dao.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.gomeplus.oversea.bi.service.item.entity.Category;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategoryBinding;

/**
 * 2017/2/13 外站类目dao
 */
@Repository
public class OuterCategoryBindingDao extends BaseDao<OuterCategoryBinding> {

	/**
	 * 根据名称和来源查询外站类目
	 * 
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

	/**
	 * @param ovcCategoryId
	 * @return
	 */
	public List<OuterCategoryBinding> findCategoryBindingsByCriteria(
			Map<String, Object> criteriia) {
		return getSqlSession().selectList(
				sqlId("findCategoryBindingsByCriteria"), criteriia);
	}
}
