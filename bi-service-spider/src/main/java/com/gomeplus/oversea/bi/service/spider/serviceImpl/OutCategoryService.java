package com.gomeplus.oversea.bi.service.spider.serviceImpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.gomeplus.oversea.bi.service.spider.mq.OuterCategory;

@Service
public class OutCategoryService {

	
	
	public String  handleItemOuterCategory(String categoryPath) {
		if(StringUtils.isNotEmpty(categoryPath)) {
			if(false) {
				String[] outCategorys = categoryPath.split(">");
				for (int i = 0;i<outCategorys.length;i++) {
					String id = getCategoryPathToId(outCategorys[i]);
					if(i==outCategorys.length-1) {
						return id;
					}
				}
			}
			
		}
		return null;
	}

	private OuterCategory findCategoryBbyName(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	String getCategoryPathToId(String categoryName) {
		OuterCategory category = findCategoryBbyName(categoryName);
		if(category==null) {
			category = createOuterCategory();
		}
		
		return category.getId();
	}

	private OuterCategory createOuterCategory() {
		return null;
	}
}
