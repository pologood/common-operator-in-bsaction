package com.gomeplus.oversea.bi.service.item.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gomeplus.oversea.bi.service.item.entity.Category;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategory;
import com.gomeplus.oversea.bi.service.item.entity.OuterCategoryBinding;
import com.gomeplus.oversea.bi.service.item.service.CategoryReadService;
import com.gomeplus.oversea.bi.service.item.service.CategoryWriteService;
import com.gomeplus.oversea.bi.service.item.utils.CodeUtils;
import com.gomeplus.oversea.bi.service.item.utils.EscapeCharacterTransfer;
import com.gomeplus.oversea.bi.service.item.vo.RichCategory;
import com.gomeplus.oversea.bs.common.exception.code4xx.C404Exception;
import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;

@RestController
@RequestMapping("/item")
public class CategoryController {

	@Autowired
	public CategoryReadService categoryReadService;
	@Autowired
	public CategoryWriteService categoryWriteService;
	
    @RequestMapping(path = "/categories", method = RequestMethod.GET)
	public List<Category> findCategorisByParentId(@RequestParam(required=true) String parentId) {
		return categoryReadService.findCategorisByParentId(parentId);
	}
	
    @RequestMapping(path = "/categoryBindings", method = RequestMethod.GET)
	public List<OuterCategoryBinding> findCategoryBindingsByOvcCategoryId(@RequestParam(required=true) String ovcCategoryId) {
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("ovcCategoryId", ovcCategoryId);
		
		return categoryReadService.findCategoryBindingsByCriteria(criteria);
	}
	
    @RequestMapping(path = "/categoryBindings", method = RequestMethod.POST)
	public Boolean bindingOuterCategories(@RequestBody OuterCategoryBinding outerCategoryBinding) {
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("ovcCategoryId", outerCategoryBinding.getOvcCategoryId());
		criteria.put("categoryId", outerCategoryBinding.getCategoryId());
		
		if(categoryReadService.findCategoryBindingsByCriteria(criteria).size() > 0) {
			throw new C422Exception(CodeUtils.CATEGORYBINDING_ALREADY_EXIST);
		}
		//转义路径中的特殊字符
		outerCategoryBinding.setPath(EscapeCharacterTransfer.forward(outerCategoryBinding.getPath()));
		return categoryWriteService.bindingCategory(outerCategoryBinding);
	}
	
    @RequestMapping(path = "/categoryBindings", method = RequestMethod.DELETE)
	public Boolean bindingOuterCategories(@RequestParam(required=true) String id) {
		return categoryWriteService.deleteCategoryBinding(id);
	}
	
    @RequestMapping(path = "/category", method = RequestMethod.GET)
	public Category findCategoryById(@RequestParam(required=true) String id) {
		Category category = categoryReadService.findCategoryById(id);
		if(category == null) {
			throw new C404Exception(CodeUtils.CATEGORY_NOT_EXIST);
		}
		return category;
	}
	
    @RequestMapping(path = "/outerCategories", method = RequestMethod.GET)
	public List<OuterCategory> findOuterCategorisByParentId(@RequestParam(required=true) String parentId) {
		return categoryReadService.findOuterCategorisByParentId(parentId);
	}
    
    @RequestMapping(path = "/categoryTree", method = RequestMethod.GET)
	public Map<String, List<RichCategory>> findCategoryTree(@RequestParam(required=true) String parentId,
			@RequestParam(required=false) Integer depth) {
		Map<String, List<RichCategory>> returnMap = new HashMap<String, List<RichCategory>>();
		
			if(depth==null || depth < 0 || depth > 4){
				depth = 4;
			}
			List<RichCategory> richCategories = categoryReadService.findFrontCategoryTreeByParentId(parentId,depth);
			returnMap.put("categories", richCategories);
			
		
		return returnMap;
	}
    
    /**
     * 创建类目
     * @param category
     * @return
     */
    @RequestMapping(path = "/category", method = RequestMethod.POST)
   	public Category createCategory(@RequestBody Category category) {
    	return categoryWriteService.createCategory(category);
   	}
    
    /**
     * 更新类目
     * @param id 类目主键
     * @param name 类目新名称
     * @return
     */
    @RequestMapping(path = "/category/{id}", method = RequestMethod.PUT)
   	public Boolean updateCategory(@PathVariable("id") String id, 
   			@RequestParam(name="name", required=true) String name) {
    	Category category = new Category();
    	category.setId(id);
    	category.setName(name);
    	return categoryWriteService.updateCategory(category);
   	}
}
