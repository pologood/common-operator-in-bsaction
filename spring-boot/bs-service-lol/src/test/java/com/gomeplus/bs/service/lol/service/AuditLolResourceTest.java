package com.gomeplus.bs.service.lol.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gomeplus.bs.interfaces.lol.service.AuditLolResource;
import com.gomeplus.bs.interfaces.lol.vo.AuditVo;
import com.gomeplus.bs.service.lol.LOLApplication;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)  
@SpringApplicationConfiguration(classes=LOLApplication.class)
public class AuditLolResourceTest {

	@Autowired
	private AuditLolResource auditLolResource;
	
	@Test
	public void testContentPass() {
		AuditVo vo = new AuditVo();
		vo.setBusinessId("1");
		vo.setAuditState(2);
		vo.setAuditUserId("99999");
		vo.setBusinessType(1);
		auditLolResource.doPut(vo);
	}
	
	
	@Test
	public void testContentNotPass() {
		
	}
	
	@Test
	public void testReplyPass() {
		AuditVo vo = new AuditVo();
		vo.setBusinessId("3");
		vo.setAuditState(2);
		vo.setAuditUserId("56d79dbfe3ae6f7ad6198108");
		vo.setBusinessType(2);
		auditLolResource.doPut(vo);
	}
	
	@Test
	public void testReplyNotPass() {
		
	}
}
