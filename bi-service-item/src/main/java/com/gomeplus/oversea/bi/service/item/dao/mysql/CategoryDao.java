package com.gomeplus.oversea.bi.service.item.dao.mysql;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.gomeplus.oversea.bi.service.item.entity.Category;
import com.google.common.collect.ImmutableMap;

/**
 * 2017/2/13
 * 后台类目dao
 */
@Repository
public class CategoryDao extends BaseDao<Category>{
	
	public List<Category> findCategoriesByParentId(String parentId) {
		return getSqlSession().selectList(sqlId("findCategoriesByParentId"), ImmutableMap.of("parentId", Long.parseLong(parentId)));
	}

	public List<Category> findInPids(List<Long> ids) {
		return getSqlSession().selectList(sqlId("findInPids"), ids);
	}
	
    /**
     * 通过名称查询类目
     * @param name 类目名称
     * @return 类目
     */
    public Category findByName(String name) {
        return getSqlSession().selectOne(sqlId("findByName"), ImmutableMap.of("name", name));
    }
    
    /**
     * 通过名称查询类目,排除特定id
     * @param name 类目名称
     * @return 类目
     */
    public Category findByNameExcludeId(Long id,String name) {
        return getSqlSession().selectOne(sqlId("findByNameExcludeId"), ImmutableMap.of("name", name, "id", id));
    }
}
