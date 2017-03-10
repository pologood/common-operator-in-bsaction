package com.gomeplus.oversea.bi.service.search.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 搜索相关透传字段
 * @author fanhailong
 *
 */
@Configuration
public class ABSearchInfo {
	// 搜索规则
	private String rule;
	// 搜索版本(读取配置文件)
	@Value("${search.abinfo.version}")
	private String version;
	
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
    public String toString() {
       return rule + "|" + version;
    }
}
