package com.gomeplus.bs.service.lol.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gomeplus.bs.interfaces.lol.service.ReplyResource;
import com.gomeplus.bs.interfaces.lol.vo.PublishReplyVo;
import com.gomeplus.bs.service.lol.LOLApplication;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)  
@SpringApplicationConfiguration(classes=LOLApplication.class)
public class PublishReplyResourceTest {

	@Autowired
	private ReplyResource publishReplyResource;
	
	@Test
	public void testPostOk() {
		for(int i=5; i>0; i--) {
			Long userId = 9528L;
			PublishReplyVo vo = new PublishReplyVo();
			vo.setContents("I L U"+i);
			vo.setEntryId("6");
			publishReplyResource.doPost(vo, userId);
		}
	}
	
	@Test
	public void testPost_NoEntry() {
		Long userId = 9528L;
		PublishReplyVo vo = new PublishReplyVo();
		vo.setContents("I L U");
		vo.setEntryId("999");
		publishReplyResource.doPost(vo, userId);
	}
	
	@Test
	public void testPost_NoContents() {
		Long userId = 9528L;
		PublishReplyVo vo = new PublishReplyVo();
		vo.setEntryId("6");
		publishReplyResource.doPost(vo, userId);
	}
	
	@Test
	public void testDelete_NoReply() {
		String id = "999";
		Long userId = 9528L;
		publishReplyResource.doDelete(id, userId);
	}
	
	@Test
	public void testDelete_CreateUserError() {
		String id = "3";
		Long userId = 9528L;
		publishReplyResource.doDelete(id, userId);
	}
	
	@Test
	public void testDelete_IsDeleted() {
		String id = "6";
		Long userId = 9528L;
		publishReplyResource.doDelete(id, userId);
	}

}
