package com.yami.trading.bean.trader.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 用户跟随交易员累计收益表
 */
@Data
@TableName("T_TRADER_USER")
@Slf4j
public class TraderUser extends BaseEntity {

	private static final long serialVersionUID = -1617033543659508052L;

	@TableField("PARTY_ID")
	private String partyId;
	

	/**
	 * 用户账号 邮箱号或手机号
	 */
	@TableField("NAME")
	private String name;

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

	@TableField(exist = false)
	private String userCode;

}
