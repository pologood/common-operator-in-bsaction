package com.gomeplus.oversea.bi.service.item.service;

import com.gomeplus.oversea.bi.service.item.entity.Category;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategoryBinding;


public interface CategoryWriteService {
	Long createAndCheckCategoryName(String[] outCategories, String source);
	
	public Boolean bindingCategory(OuterCategoryBinding outerCategoryBinding);
	
	public Boolean deleteCategoryBinding(String id);

	Category createCategory(Category category);

	Boolean updateCategory(Category category);
}
