package com.gomeplus.modifyjsonp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigUtil {
	static Map<String,String> map = new HashMap<>();
	static{
		Properties prop = new Properties();
		InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream("conf.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			log.error("fail to load properties!", e);
			throw new RuntimeException(e);
		}
		map.put("MX_URLPREFIX", prop.getProperty("MX_URLPREFIX"));
		map.put("ONLINE_URLPREFIX", prop.getProperty("ONLINE_URLPREFIX"));
		map.put("ENV", prop.getProperty("ENV"));
		map.put("originGomeImgHost", prop.getProperty("originGomeImgHost"));
	}
	public static String getProperty(String key){
		return map.get(key);
	}
	
	public static void main(String[] args) {
		String property = ConfigUtil.getProperty("ENV");
		System.err.println(property);
	}
}
