package com.yami.trading.bean.item.query;

import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品DTO
 * @author lucas
 * @version 2023-03-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ItemQueryByScene {

	/**
	 * 领涨
	 */
	public static final String gains = "gains";
	/**
	 * 领跌
	 */
	public static final String decline = "decline";
	/**
	 * 盘前
	 */
	public static final String preMarket = "preMarket";
	/**
	 * 盘后
	 */
	public static final String afterHours = "afterHours";


	@ApiParam(value = "forex->外汇,commodities->大宗商品，指数->indices,  A-stocks->A股,  HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币 ")
	private String type;


	@ApiParam(value = "gains->领涨,decline->领跌，preMarket->盘前, afterHours->盘后")
	private String scene;

	private String marketIndex;

}
