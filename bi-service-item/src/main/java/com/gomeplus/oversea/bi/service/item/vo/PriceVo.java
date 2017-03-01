package com.gomeplus.oversea.bi.service.item.vo;

import java.io.Serializable;

import lombok.Data;
/**
 * 价格vo
 * 2017/2/17
 */
@Data
public class PriceVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4937000843924415911L;
	/**
	 * 金额
	 */
	private Long amount;
	/**
	 * 币种
	 */
	private String currency;
	/**
	 * 货币符号
	 */
	private String symbol;
	/**
	 * 备选货币符号
	 */
	private String alternative;
	/**
	 * 符号位置
	 */
	private String align;
}
