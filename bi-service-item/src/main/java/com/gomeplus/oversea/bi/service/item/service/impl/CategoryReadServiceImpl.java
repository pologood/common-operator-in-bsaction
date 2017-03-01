package com.gomeplus.oversea.bi.service.item.service.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gomeplus.oversea.bi.service.item.dao.mysql.CategoryDao;
import com.gomeplus.oversea.bi.service.item.dao.mysql.OuterCategoryBindingDao;
import com.gomeplus.oversea.bi.service.item.dao.mysql.OuterCategoryDao;
import com.gomeplus.oversea.bi.service.item.entity.Category;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategory;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategoryBinding;
import com.gomeplus.oversea.bi.service.item.service.CategoryReadService;
import com.gomeplus.oversea.bi.service.item.vo.RichCategory;
import com.gomeplus.oversea.bs.common.exception.code5xx.C500Exception;
import com.google.common.collect.ImmutableList;

/**
 * 类目read服务
 * 2017/2/16
 */
@Slf4j
@Service
public class CategoryReadServiceImpl implements CategoryReadService {

	@Autowired
	public CategoryDao categoryDao;
	
	@Autowired
	public OuterCategoryDao outerCategoryDao;
	
	@Autowired
	public OuterCategoryBindingDao outerCategoryBindingDao;
	
	@Override
	public List<Category> findCategorisByParentId(String parentId) {
		return categoryDao.findCategoriesByParentId(parentId);
	}

	@Override
	public List<OuterCategoryBinding> findCategoryBindingsByCriteria(Map<String, Object> criteria) {
		return outerCategoryBindingDao.findCategoryBindingsByCriteria(criteria);
	}

	@Override
	public Category findCategoryById(String id) {
		return categoryDao.load(Long.valueOf(id));
	}

	@Override
	public List<OuterCategory> findOuterCategorisByParentId(String parentId) {
		return outerCategoryDao.findOuterCategoriesByParentId(parentId);
	}
	
	@Override
	public List<RichCategory> findFrontCategoryTreeByParentId(String parentId, Integer depth) {
		log.info("get categoryTree parentId:{}",parentId);
		List<RichCategory> richCategories = new ArrayList<RichCategory>();
		try {
			List<Category> categories =  categoryDao.findCategoriesByParentId(parentId);
			for (Category category : categories) {
				RichCategory richCategory = getBackTree(category, depth);
				richCategories.add(richCategory);
			}
		} catch (Exception e) {
			log.error("get categoryTree fail parentId:{}", parentId, e);
			throw new C500Exception("get categoryTree fail");
		}
		
		return richCategories;
	}
	public RichCategory getBackTree(Category root,Integer depth) {
	    RichCategory rootNode = new RichCategory();
        rootNode.setId(root.getId());
        rootNode.setLevel(root.getLevel());
        rootNode.setName(root.getName());
        rootNode.setParentId(root.getParentId());
        rootNode.setCreatedAt(root.getCreatedAt());
        rootNode.setUpdatedAt(root.getUpdatedAt());
        rootNode.setHasChildren(root.getHasChildren());
        rootNode.setChildren(new ArrayList<RichCategory>());

        Vector<Vector<RichCategory>> row = new Vector<>(2);
        row.add(0, new Vector<RichCategory>());
        row.add(1, new Vector<RichCategory>());

        int initLevel = root.getLevel() + 1;
        int curLevel = initLevel;
        row.get(curLevel % 2).add(rootNode);
        while (curLevel - initLevel < depth - 1 && !row.get(curLevel % 2).isEmpty()) {
            row.get((curLevel + 1) % 2).setSize(0); // empty next row
            Map<Long, RichCategory> view = new HashMap<>();
            for (RichCategory bc : row.get(curLevel % 2)) {
                view.put(Long.valueOf(bc.getId()), bc);
            }
            List<Category> bcs = findInPids(ImmutableList.copyOf(view.keySet()));
            for (Category bc : bcs) {
                RichCategory bcNode = new RichCategory();
                bcNode.setId(bc.getId());
                bcNode.setLevel(bc.getLevel());
                bcNode.setName(bc.getName());
                bcNode.setParentId(bc.getParentId());
                bcNode.setCreatedAt(bc.getCreatedAt());
                bcNode.setUpdatedAt(bc.getUpdatedAt());
                bcNode.setHasChildren(bc.getHasChildren());
                bcNode.setChildren(new ArrayList<RichCategory>());
                view.get(Long.valueOf(bc.getParentId())).getChildren().add(bcNode);
                row.get((curLevel + 1) % 2).add(bcNode);
            }
            ++ curLevel;
        }

        return rootNode;
	}
	public List<Category> findInPids(List<Long> ids) {
		return categoryDao.findInPids(ids);
	}
}
