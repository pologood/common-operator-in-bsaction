package com.gomeplus.bs.service.lol.service;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.interfaces.lol.service.AuditLolListResource;
import com.gomeplus.bs.interfaces.lol.vo.AuditVo;
import com.gomeplus.bs.service.lol.dao.AuditContentLogDao;
import com.gomeplus.bs.service.lol.dao.AuditReplyLogDao;
import com.gomeplus.bs.service.lol.dao.PublishContentDao;
import com.gomeplus.bs.service.lol.dao.PublishReplyDao;
import com.gomeplus.bs.service.lol.util.LolMessageUtil;

@Slf4j
@Service("auditLolListResource")
public class AuditLolListResourceImpl implements AuditLolListResource{
	
	@Autowired
	private AuditContentLogDao auditContentLogDao;
	
	@Autowired
	private AuditReplyLogDao auditReplyLogDao;
	
	@Autowired
	private PublishContentDao publishContentDao;
	
	@Autowired
	private PublishReplyDao publishReplyDao;
	
	@Override
	public Map<String,Object> doGet(AuditVo vo) {
		log.info("AuditLolList execute, {} ", vo);
		if(vo == null) {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		Map<String,Object> auditMap = new HashMap<String,Object>();
		//1: 好友动态  2：好友动态评论 3：好友动态日志 4：好友动态评论日志
		Integer type = vo.getBusinessType();
		if(type == null)  {
			throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		switch (type) {
			case 1:
				auditMap = publishContentDao.findAuditList(vo);
				break;
			case 2:
				auditMap = publishReplyDao.findAuditList(vo);
				break;
			case 3:
				auditMap = auditContentLogDao.findAuditList(vo);
				break;
			case 4:
				auditMap = auditReplyLogDao.findAuditList(vo);
				break;
			default:
				throw new C422Exception(LolMessageUtil.PARAM_CHECK_FAILED);
		}
		return auditMap;
	}
}
