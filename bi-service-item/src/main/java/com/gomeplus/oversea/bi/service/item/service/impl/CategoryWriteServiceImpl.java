package com.gomeplus.oversea.bi.service.item.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gomeplus.oversea.bi.service.item.dao.mysql.CategoryDao;
import com.gomeplus.oversea.bi.service.item.dao.mysql.OuterCategoryBindingDao;
import com.gomeplus.oversea.bi.service.item.dao.mysql.OuterCategoryDao;
import com.gomeplus.oversea.bi.service.item.dao.redis.OuterCategoryRedisDao;
import com.gomeplus.oversea.bi.service.item.entity.Category;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategory;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategoryBinding;
import com.gomeplus.oversea.bi.service.item.manager.CategoryManager;
import com.gomeplus.oversea.bi.service.item.service.CategoryWriteService;
import com.gomeplus.oversea.bi.service.item.utils.CodeUtils;
import com.gomeplus.oversea.bi.service.item.utils.ValidateUtils;
import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;

/**
 * 
 * 类目write服务
 * 2017/2/16
 *
 */
@Service
public class CategoryWriteServiceImpl implements CategoryWriteService {
	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private OuterCategoryDao outerCategoryDao;
	@Autowired
	private OuterCategoryRedisDao outerCategoryRedisDao;
	@Autowired
	private OuterCategoryBindingDao outerCategoryBindingDao;
	@Autowired
	private CategoryManager categoryManager;
	
	@Override
	public Long createAndCheckCategoryName(String[] outCategories,String source) {
		if(outCategories != null && outCategories.length > 0) {
			StringBuilder stringBuilder = new StringBuilder("source:").append(source);
			for (String categoryName : outCategories) {
				stringBuilder.append(":").append(categoryName);
			}
			String categoryId =  outerCategoryRedisDao.getCategoryIdByCategoryPath(stringBuilder.toString());
			if(StringUtils.isNotEmpty(categoryId)) {
				return Long.valueOf(categoryId);
			}
			Long pid = 0L;
			for(int i=0;i<outCategories.length;i++) {
				Map<String, Object> criteria = new HashMap<String, Object>();
				criteria.put("parentId", pid);
				criteria.put("name", outCategories[i]);
				criteria.put("level", i);
				criteria.put("source", source);
				Long id = outerCategoryDao.findCategoryIdByCriteria(criteria);
				if(id==null) {
					OuterCategory outerCategory = new OuterCategory();
					outerCategory.setName(outCategories[i]);
					outerCategory.setParentId(String.valueOf(pid));
					outerCategory.setLevel(i);
					outerCategory.setSource(source);
					outerCategory.setStatus(OuterCategory.Status.ENABLED.value());
					outerCategory.setHasChildren(i==outCategories.length-1?false:true);
					outerCategoryDao.create(outerCategory);
					pid = Long.valueOf(outerCategory.getId());
				}else {
					pid = id;
				}
				
			}
			outerCategoryRedisDao.saveFinalCategeryPathId(stringBuilder.toString(),pid);
			return pid;
		}
		return null;
	}
	
	@Override
	public Boolean bindingCategory(OuterCategoryBinding outerCategoryBinding) {
		return outerCategoryBindingDao.create(outerCategoryBinding);
	}
	
	@Override
	public Boolean deleteCategoryBinding(String id) {
		return outerCategoryBindingDao.delete(Long.parseLong(id));
	}

	@Override
	public Category createCategory(Category category) {
		String name = category.getName();
		if(StringUtils.isBlank(name)) {
			throw new C422Exception(CodeUtils.CATEGORY_NAME_EMPTY);
		}
		category.setName(name.trim());
		Category exist = categoryDao.findByName(category.getName());
		if (exist != null){
			throw new C422Exception(CodeUtils.CATEGORY_ALREADY_EXIST);
		} 
        // 后台类目不存在则创建
		category.setStatus(Category.Status.ENABLED.value());
		category.setHasChildren(false);
		String parentId = category.getParentId();
		if(parentId == null || Long.valueOf(parentId) == 0) {
			// 该类目为父级类目
			category.setParentId("0");
			category.setLevel(Category.LevelData.LEVELONE.getFlag());
			categoryDao.create(category);
		} else {
			// 有父级类目, 需更新父级类目hasChildren属性
			categoryManager.create(category);
		}
		return category;
	}
	
	@Override
	public Boolean updateCategory(Category category) {
		Category exist = categoryDao.load(category.getId());
		// 非空校验
		ValidateUtils.checkNotNull(exist, CodeUtils.CATEGORY_NOT_EXIST);
		// 新类目名称
		String name = category.getName();
		if(StringUtils.isBlank(name)) {
			throw new C422Exception(CodeUtils.CATEGORY_NAME_EMPTY);
		}
		category.setName(name.trim());
        if (!category.getName().equals(exist.getName())) {
            Category other = categoryDao.findByNameExcludeId(Long.valueOf(category.getId()), category.getName());
            if (other != null) {
            	// 新名称对应的后台类目已存在
            	throw new C422Exception(CodeUtils.CATEGORY_ALREADY_EXIST);
            }
            // 更新类目
            categoryDao.update(category);
        }
		return true;
	}
	
}
