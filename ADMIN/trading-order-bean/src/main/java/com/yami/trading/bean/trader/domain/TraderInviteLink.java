package com.yami.trading.bean.trader.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 交易员生成邀请链接
 */
@Data
@TableName("T_TRADER_INVITE_LINK")
@Slf4j
public class TraderInviteLink extends BaseEntity {

	private static final long serialVersionUID = -1617033543659508052L;

	/**
	 * 交易员ID
	 */
	@TableField("TRADER_ID")
	private String traderId;

	/**
	 * 杠杆
	 */
	@TableField("LEVEL")
	private int level;

	/**
	 * 佣金比例
	 */
	@TableField("PROFIT_SHARE_RATIO")
	private double profitShareRatio;

	/**
	 * 邀请跟单ID
	 */
	@TableField("IDS")
	private String ids;

	/**
	 * 邀请链接
	 */
	@TableField("LINK")
	private String link;

	/**
	 * 邀请链接状态 0-失效 1-有效
	 */
	@TableField("STATUS")
	private int status;
}
