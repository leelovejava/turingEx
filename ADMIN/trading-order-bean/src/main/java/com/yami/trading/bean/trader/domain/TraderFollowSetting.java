package com.yami.trading.bean.trader.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 交易员带单设置
 */
@Data
@TableName("T_TRADER_FOLLOW_SETTING")
@Slf4j
public class TraderFollowSetting extends BaseEntity {

	private static final long serialVersionUID = -1617033543659508052L;
	
	@TableField("PARTY_ID")
	private String partyId;

	/**
	 * 用户名
	 */
	@TableField("USERNAME")
	private String username;

	/**
	 * 借款天数设置
	 */
	@TableField("DAYS_SETTING")
	private String daysSetting;

	/**
	 * 借款金额上限 （已废弃）
	 */
	@TableField("AMOUNT")
	private BigDecimal amount;

	/**
	 * 带单佣金比例
	 */
	@TableField("RATE")
	private double rate;
}
