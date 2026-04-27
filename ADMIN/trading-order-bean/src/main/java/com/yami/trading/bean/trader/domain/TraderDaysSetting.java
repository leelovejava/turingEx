package com.yami.trading.bean.trader.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 交易跟单利息设置
 */
@Data
@TableName("T_TRADER_DAYS_SETTING")
@Slf4j
public class TraderDaysSetting extends BaseEntity {

	private static final long serialVersionUID = -1617033543659508052L;

	/**
	 * 跟单天数
	 */
	@TableField("DAYS")
	private int days;
	/**
	 * 杠杆
	 */
	@TableField("LEVEL")
	private int level;
	/**
	 * 借款一天利率
	 */
	@TableField("DAY_RATE")
	private double dayRate;
}
