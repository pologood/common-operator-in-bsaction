package com.gomeplus.oversea.bi.service.item.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 商品配置
 * 2017/2/17
 */
@RefreshScope
@Configuration
public class ItemConfig {
	// 货币标识
	@Value("${currency.symbols}")
	private String symbols ;

	public String getSymbols() {
		return symbols;
	}

	public void setSymbols(String symbols) {
		this.symbols = symbols;
	}

}
