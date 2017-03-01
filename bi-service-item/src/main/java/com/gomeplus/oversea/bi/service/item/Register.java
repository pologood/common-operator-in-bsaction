package com.gomeplus.oversea.bi.service.item;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gomeplus.oversea.bi.service.item.clients.MenuPrivilegeConfResource;
import com.gomeplus.oversea.bi.service.item.vo.MenuPrivilegeConfVo;

/**
 * @Description 注册文件地址
 * @author yuanchangjun
 * @date 2016年10月21日 下午3:27:16
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class Register implements CommandLineRunner  {
	
	@Autowired
	private MenuPrivilegeConfResource menuPrivilegeConfResource;
	
	@Override
	public void run(String... arg0) throws Exception {
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("menu_privileges.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			StringBuilder builder = new StringBuilder();

			String line;
			try {
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					builder.append("\n");
				}
			} catch (IOException e) {
				log.error("Failed to read menu_privileges",e);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("Read menu_privileges off flow failed",e);
				}
			}
			JSONObject menuJsonObj = JSON.parseObject(builder.toString());
			MenuPrivilegeConfVo conf = JSON.toJavaObject(menuJsonObj, MenuPrivilegeConfVo.class);
			menuPrivilegeConfResource.doPut(conf.getModule(), conf);
		} catch (Exception e) {
			log.error("Failed to start the registered rights tree", e);
			System.exit(1);
		}
	}
}