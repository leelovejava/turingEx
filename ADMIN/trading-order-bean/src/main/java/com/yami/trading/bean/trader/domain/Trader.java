package com.yami.trading.bean.trader.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 交易员
 */
@Data
@TableName("T_TRADER")
@Slf4j
public class Trader extends BaseEntity {

	private static final long serialVersionUID = -1617033543659508052L;

	@TableField("PARTY_ID")
	private String partyId;

	/**
	 * 交易员名称
	 */
	@TableField("NAME")
	private String name;
	/**
	 * 交易员简介
	 */
	@TableField("REMARKS")
	private String remarks;
	/**
	 * 币种 带单币种（多品种的话用;隔开）
	 */
	@TableField("SYMBOLS")
	private String symbols;

	/**
	 * 利润分成比例
	 */
	@TableField("PROFIT_SHARE_RATIO")
	private double profitShareRatio;

	/**
	 * 状态（是否开启跟单）---STATE,0为未开启，1为开启
	 */
	@TableField("STATE")
	private String state;
	/**
	 * 此次跟单最多跟随人数
	 */
	@TableField("FOLLOWER_MAX")
	private int followerMax;

	/**
	 * 头像图片
	 */
	@TableField("IMG")
	private String img;


	/**
	 * 3周收益
	 */
	@TableField("WEEK_3_PROFIT")
	private double week3Profit;
	/**
	 * 3周累计金额
	 */
	@TableField("WEEK_3_ORDER_AMOUNT")
	private double week3OrderAmount;

	/**
	 * 3周收益率
	 */
	@TableField("WEEK_3_PROFIT_RATIO")
	private double week3ProfitRatio;
	/**
	 * 3周交易收益
	 */
	@TableField("WEEK_3_ORDER_PROFIT")
	private int week3OrderProfit;
	/**
	 * 3周交易笔数
	 */
	@TableField("WEEK_3_ORDER_SUM")
	private int week3OrderSum;
	/**
	 * 累计金额
	 */
	@TableField("ORDER_AMOUNT")
	private double orderAmount;

	/**
	 * 累计收益
	 */
	@TableField("PROFIT")
	private double profit;

	/**
	 * 累计收益率
	 */
	@TableField("PROFIT_RATIO")
	private double profitRatio;

	/**
	 * 累计盈利笔数
	 */
	@TableField("ORDER_PROFIT")
	private int orderProfit;

	/**
	 * 累计亏损笔数
	 */
	@TableField("ORDER_LOSS")
	private int orderLoss;
	/**
	 * 累计交易笔数
	 */
	@TableField("ORDER_SUM")
	private int orderSum;

	/**
	 * 累计跟随人数
	 */
	@TableField("FOLLOWER_SUM")
	private int followerSum;

	/**
	 * 当前跟随人数
	 */
	@TableField("FOLLOWER_NOW")
	private int followerNow;

	/**
	 * 3周收益偏差
	 */
	@TableField("DEVIATION_WEEK_3_PROFIT")
	private double deviationWeek3Profit;
	/**
	 * 3周累计金额-偏差
	 */
	@TableField("DEVIATION_WEEK_3_ORDER_AMOUNT")
	private double deviationWeek3OrderAmount;

	/**
	 * 3周收益率-偏差
	 */
	@TableField("DEVIATION_WEEK_3_PROFIT_RATIO")
	private double deviationWeek3ProfitRatio;
	/**
	 * 3周盈利笔数-偏差
	 */
	@TableField("DEVIATION_WEEK_3_ORDER_PROFIT")
	private Integer deviationWeek3OrderProfit;
	/**
	 * 3周交易笔数-偏差
	 */
	@TableField("DEVIATION_WEEK_3_ORDER_SUM")
	private Integer deviationWeek3OrderSum;
	/**
	 * 累计金额-偏差
	 */
	@TableField("DEVIATION_ORDER_AMOUNT")
	private double deviationOrderAmount;

	/**
	 * 累计收益-偏差
	 */
	@TableField("DEVIATION_PROFIT")
	private double deviationProfit;

	/**
	 * 累计收益率-偏差
	 */
	@TableField("DEVIATION_PROFIT_RATIO")
	private double deviationProfitRatio;

	/**
	 * 累计盈利笔数-偏差
	 */
	@TableField("DEVIATION_ORDER_PROFIT")
	private int deviationOrderProfit;

	/**
	 * 累计亏损笔数-偏差
	 */
	@TableField("DEVIATION_ORDER_LOSS")
	private int deviationOrderLoss;
	/**
	 * 累计交易笔数-偏差
	 */
	@TableField("DEVIATION_ORDER_SUM")
	private int deviationOrderSum;

	/**
	 * 累计跟随人数-偏差
	 */
	@TableField("DEVIATION_FOLLOWER_SUM")
	private int deviationFollowerSum;

	/**
	 * 当前跟随人数-偏差
	 */
	@TableField("DEVIATION_FOLLOWER_NOW")
	private int deviationFollowerNow;
	/**
	 * 跟单最小下单数
	 */
	@TableField("FOLLOW_VOLUMN_MIN")
	private int followVolumnMin;

	/**
	 * 审核: 0-待审核,1-审核通过,-1审核不通过
	 */
	@TableField("CHECKED")
	private int checked;

	/**
	 * 用户编码
	 */
	@TableField(exist = false)
	private String userCode;

	@TableField(exist = false)
	private String path;
}
