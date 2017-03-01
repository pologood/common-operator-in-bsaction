package com.gomeplus.bs.service.lol.service;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gomeplus.bs.interfaces.lol.service.AuditLolListResource;
import com.gomeplus.bs.interfaces.lol.vo.AuditLolVo;
import com.gomeplus.bs.interfaces.lol.vo.AuditVo;
import com.gomeplus.bs.service.lol.LOLApplication;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)  
@SpringApplicationConfiguration(classes=LOLApplication.class)
public class AuditLolListResourceTest {

	@Autowired
	private AuditLolListResource auditLolListResource;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetContent() {
		AuditVo vo = new AuditVo();
		vo.setAuditState(1);
		vo.setBusinessId("1");
		vo.setBusinessContent("不错哦");
		vo.setBusinessType(1);
		Map<String,Object> map = auditLolListResource.doGet(vo);
		System.out.println(((List<AuditLolVo>)map.get("rows")).size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetReply() {
		AuditVo vo = new AuditVo();
		vo.setAuditState(1);
		vo.setBusinessContent("");
		vo.setBusinessType(2);
		Map<String,Object> map = auditLolListResource.doGet(vo);
		System.out.println(((List<AuditLolVo>)map.get("rows")).size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetContentLog() {
		AuditVo vo = new AuditVo();
		vo.setAuditState(1);
		vo.setBusinessContent("不错");
		vo.setBusinessType(3);
		Map<String,Object> map = auditLolListResource.doGet(vo);
		System.out.println(((List<AuditLolVo>)map.get("rows")).size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetReplyLog() {
		AuditVo vo = new AuditVo();
		vo.setAuditState(1);
		vo.setBusinessContent(" U4");
		vo.setBusinessType(4);
		Map<String,Object> map = auditLolListResource.doGet(vo);
		System.out.println(((List<AuditLolVo>)map.get("rows")).size());
	}
	
}
