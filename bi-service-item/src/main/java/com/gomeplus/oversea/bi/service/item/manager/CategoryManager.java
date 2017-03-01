package com.gomeplus.oversea.bi.service.item.manager;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.oversea.bi.service.item.dao.mysql.CategoryDao;
import com.gomeplus.oversea.bi.service.item.entity.Category;

@Slf4j
@Component
public class CategoryManager {
	@Autowired
	private CategoryDao categoryDao;
	
	/**
     * 创建后台类目及更新其父类目
     * @param category 后台类目
     */
	@Transactional
	public void create(Category category) {
		Category parent = categoryDao.load(category.getParentId());
		category.setLevel(parent.getLevel() + 1);
		categoryDao.create(category);
		
		parent.setHasChildren(true);
		parent.setUpdater(category.getCreator());
		categoryDao.update(parent);
	}
	
	
}
