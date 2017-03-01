package com.gomeplus.bs.service.lol.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gomeplus.bs.interfaces.lol.service.PraiseResource;
import com.gomeplus.bs.service.lol.LOLApplication;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)  
@SpringApplicationConfiguration(classes=LOLApplication.class)
public class PraiseResourceTest {

	@Autowired
	private PraiseResource praiseResource;
	@Test
	public void testPostOk() {
		Long userId = 9528L;
		String entryId = "6";
		praiseResource.doPut(entryId, userId);
	}
	
	@Test
	public void testPost_Existed() {
		Long userId = 9528L;
		String entryId = "6";
		praiseResource.doPut(entryId, userId);
	}
	
	@Test
	public void testDeleteOk() {
		Long userId = 9528L;
		String entryId = "6";
		praiseResource.doDelete(entryId, userId);
	}
	
	@Test
	public void testDelete_NotExisted() {
		Long userId = 9528L;
		String entryId = "6";
		praiseResource.doDelete(entryId, userId);
	}
}
