package com.gomeplus.bs.service.lol.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.gomeplus.bs.interfaces.lol.entity.AuditContentLog;
import com.gomeplus.bs.interfaces.lol.vo.AuditLolVo;
import com.gomeplus.bs.interfaces.lol.vo.AuditVo;
import com.gomeplus.bs.service.lol.mongo.entity.PageParam;
import com.gomeplus.bs.service.lol.mongo.impl.BaseMongoDaoImpl;
import com.gomeplus.bs.service.lol.util.ValidateUtil;

@Repository("auditContentLogDao")
public class AuditContentLogDao extends BaseMongoDaoImpl<AuditContentLog> {

	@Autowired
	@Qualifier("mongoTemplate")
	
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	
	/**
	 * 根据条件查询审核动态日志列表
	 * @param vo
	 * @return
	 */
	public Map<String,Object> findAuditList(AuditVo vo) {
		Criteria criteria = new Criteria();
		Map<String,Object> map = new HashMap<String,Object>();
		if(!ValidateUtil.isNull(vo.getBusinessId())) {
			criteria.and("id").is(vo.getBusinessId());
		}
		if(!ValidateUtil.isNull(vo.getBusinessUserId())) {
			criteria.and("outUserId").is(vo.getBusinessUserId());
		}
		if(!ValidateUtil.isNull(vo.getBusinessContent())) { //内容模糊查询
			String patternStr = "^.*"+vo.getBusinessContent()+".*$";
			Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
			criteria.and("components.text").regex(pattern);
		}
		Query query = new Query(criteria);
		query.with(new Sort(Sort.Direction.ASC,"createTime"));
		int total = this.count(query);
		Integer pageSize = vo.getPageSize() == null ? 10 : vo.getPageSize();
		Integer pageNum = vo.getPageNum() == null ? 1 : vo.getPageNum();
		PageParam page = new PageParam(pageNum, pageSize);
		List<AuditContentLog> list = this.findPage(page, query);
		List<AuditLolVo> auditList = new ArrayList<AuditLolVo>();
		if(list != null && list.size() > 0) {
			AuditLolVo auditVo = null;
			for(AuditContentLog log : list) {
				auditVo = new AuditLolVo();
				BeanUtils.copyProperties(log, auditVo);
				auditVo.setLogId(log.getId());   // auditVo的logId为 logId
				// 融合业务：outUserId赋值到userId输出
				auditVo.setUserId(log.getOutUserId());
				auditList.add(auditVo);
			}
		}
		map.put("rows", auditList);
		map.put("total", total);
		return map;
	}
}