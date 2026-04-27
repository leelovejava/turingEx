package com.yami.trading.bean.trader.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 交易员历史订单(保护虚假订单)
 */
@Data
@TableName("T_TRADER_ORDER")
@Slf4j
public class TraderOrder extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7332111188993920706L;

	@TableField("PARTY_ID")
	private String partyId;
	
	/**
	 * 品种
	 */
	@TableField("SYMBOL")
	private String symbol;
	/**
	 * 订单 号
	 */
	@TableField("ORDER_NO")
	private String orderNo;
	
	/**
	 * "buy":买(多) "sell":卖(空)
	 */
	@TableField("DIRECTION")
	private String direction;
	
	/**
	 * 收益
	 */
	@TableField("PROFIT")
	private double profit;

	/**
	 * 平仓时间
	 */
	@TableField("CLOSE_TIME")
	private Date closeTime;

	/**
	 * 杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
	 */
	@TableField("LEVER_RATE")
	private Double leverRate;
	
	/**
	 * 委托数量(张)
	 */
	@TableField("VOLUME_OPEN")
	private Double volumeOpen;
	/**
	 * 涨跌幅
	 */
	@TableField("CHANGE_RATIO")
	private double changeRatio;
	/**
	 * 状态。submitted 已提交（持仓）， created 完成（平仓）
	 */
	@TableField("STATE")
	private String state = "created";
	/**
	 * 成交均价(成本)
	 */
	@TableField("TRADE_AVG_PRICE")
	private Double tradeAvgPrice;

	/**
	 * 平仓均价
	 */
	@TableField("CLOSE_AVG_PRICE")
	private Double closeAvgPrice;

	@TableField(exist = false)
	private String usercode;
}
