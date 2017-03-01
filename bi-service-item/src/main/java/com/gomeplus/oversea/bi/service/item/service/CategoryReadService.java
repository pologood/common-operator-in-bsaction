package com.gomeplus.oversea.bi.service.item.service;

import java.util.List;
import java.util.Map;

import com.gomeplus.oversea.bi.service.item.entity.Category;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategory;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategoryBinding;
import com.gomeplus.oversea.bi.service.item.vo.RichCategory;

public interface CategoryReadService {
	
	public List<Category> findCategorisByParentId(String parentId);
	
	public List<OuterCategoryBinding> findCategoryBindingsByCriteria(Map<String, Object> criteria);

	public Category findCategoryById(String id);

	public List<RichCategory> findFrontCategoryTreeByParentId(String parentId, Integer depth);

	public List<OuterCategory> findOuterCategorisByParentId(String parentId);
}
