package com.yami.trading.bean.trader.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户跟随交易员详情表
 */
@Data
@TableName("T_TRADER_FOLLOW_USER")
@Slf4j
public class TraderFollowUser extends BaseEntity {

	private static final long serialVersionUID = -1617033543659508052L;
	
	@TableField("PARTY_ID")
	private String partyId;
	/**
	 * 用户名
	 */
	@TableField("USERNAME")
	private String username;

	/**
	 * 交易员partyId TRADER_PARTYID
	 */
	@TableField("TRADER_PARTY_ID")
	private String traderPartyId;

	/**
	 * 跟随购买品种 symbol
	 */
	@TableField("SYMBOL")
	private String symbol;

	/**
	 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
	 */
	@TableField("FOLLOW_TYPE")
	private String followType;
	/**
	 * 状态 是否还在跟随状态 1,跟随，2，取消跟随
	 */
	@TableField("STATE")
	private String state;

	/**
	 * 跟单张数或比例---具体值
	 */
	@TableField("VOLUME")
	private double volume;
	/**
	 * 最大持仓张数
	 */
	@TableField("VOLUME_MAX")
	private double volumeMax;
	
	/**
	 * 累计跟单收益 PROFIT
	 */
	@TableField("PROFIT")
	private double profit;
	/**
	 * 累计跟单本金 AMOUNT_SUM
	 */
	@TableField("AMOUNT_SUM")
	private double amountSum;

	/**
	 * 止盈百分比
	 */
	@TableField("STOP_PFOFIT")
	private double stopProfit;
	/**
	 * 止损百分比
	 */
	@TableField("STOP_LOSS")
	private double stopLoss;

	/**
	 * 止盈百分比
	 */
	@TableField("DAYS_SETTING")
	private String daysSetting;

	@TableField(exist = false)
	private String userCode;

	@TableField(exist = false)
	private String traderUserCode;
}
