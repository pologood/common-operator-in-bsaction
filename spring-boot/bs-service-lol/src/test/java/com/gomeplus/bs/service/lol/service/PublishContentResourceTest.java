package com.gomeplus.bs.service.lol.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.gomeplus.bs.interfaces.lol.service.EntryResource;
import com.gomeplus.bs.interfaces.lol.vo.PublishContentVo;
import com.gomeplus.bs.interfaces.lol.vo.PublishEntryVo;
import com.gomeplus.bs.service.lol.LOLApplication;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)  
@SpringApplicationConfiguration(classes=LOLApplication.class)
public class PublishContentResourceTest {

	@Autowired
	private EntryResource contentResource;
	
	
	@Test
	public void testGet() {
		PublishContentVo vo = contentResource.doGet("6",9527L);
		System.out.println(vo);
	}
	
	@Test
	public void testPost() {
		int i = 5;
		while (i-- > 0) {
			Long userId = 9527L;
			PublishEntryVo vo = new PublishEntryVo();
			JSONObject json = new JSONObject();
			json.put("type", "text");
			json.put("text", "哎呦不错哦"+i);
			JSONObject json1 = new JSONObject();
			json1.put("type", "image");
			json1.put("url", "https://i-pre.meixincdn.com/v1/img/T1GXJTB5bT1R4cSCrK.png");
			JSONObject json2 = new JSONObject();
			json2.put("type", "image");
			json2.put("url", "https://i-pre.meixincdn.com/v1/img/T1GXJTB5bT1R4cSCrK.png");
			JSONObject json3 = new JSONObject();
			json3.put("type", "text");
			json3.put("text", "哎呦不错哦"+i);
			Object[] array = {json,json1,json2,json3};
			vo.setComponents(array);
			contentResource.doPost(vo, userId);
		}
	}
	
	@Test
	public void testDelete() {
		Long userId = 9527L;
		String id = "2";
		contentResource.doDelete(id, userId);
	}

}
