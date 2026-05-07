package com.yami.trading.bean.quant;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_quant_bot_orders")
public class QuantBotOrder {
	private static final long serialVersionUID = 1L;


	/**
	 * 实体主键
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 用户id
	 */
	@TableField("user_id")
	private String userId;

	/**
	 * 交易对id
	 */
	@TableField("match_id")
	private Integer matchId;

	/**
	 * 支付的币种
	 */
	@TableField("currency_id")
	private Integer currencyId;

	/**
	 * 买卖类型1.买涨,2.买跌
	 */
	private Integer type;

	/**
	 * 订单险种:0.无,1.正向，2反向。
	 */
	@TableField("is_insurance")
	private Integer isInsurance;

	/**
	 * 秒数
	 */
	private Integer seconds;

	/**
	 * 下单数量
	 */
	private BigDecimal number;

	/**
	 * 开仓价
	 */
	@TableField("open_price")
	private BigDecimal openPrice;

	/**
	 * 收盘价
	 */
	@TableField("end_price")
	private BigDecimal endPrice;

	/**
	 * 手续费
	 */
	private BigDecimal fee;

	/**
	 * 收益率
	 */
	@TableField("profit_ratio")
	private BigDecimal profitRatio;

	/**
	 * 最终收益
	 */
	@TableField("fact_profits")
	private BigDecimal factProfits;

	/**
	 * 状态:1.交易中,2.平仓中,3.已平仓
	 */
	private Integer status;

	/**
	 * 预设盈利状态:-1.亏损,0.未设置,1.盈利
	 */
	@TableField("pre_profit_result")
	private Integer preProfitResult;

	/**
	 * 盈利结果:-1.亏损,0.平,1.盈利
	 */
	@TableField("profit_result")
	private Integer profitResult;

	/**
	 * 提交日期
	 */
	@TableField("created_at")
	private Date createdAt;

	/**
	 * 更新日期
	 */
	@TableField("updated_at")
	private Date updatedAt;

	/**
	 * 平仓时间
	 */
	@TableField("handled_at")
	private Date handledAt;

	/**
	 * 完成时间
	 */
	@TableField("complete_at")
	private Date completeAt;

	/**
	 * 返还手续费的时间
	 */
	@TableField("return_at")
	private Date returnAt;

	/**
	 * 代理商关系
	 */
	@TableField("agent_path")
	private String agentPath;

	/**
	 * 机器人名称
	 */
	@TableField("bot_name")
	private String botName;

	/**
	 * 机器人ID
	 */
	@TableField("bot_id")
	private String botId;

	/**
	 * 用户ID
	 */
	@TableField("member_id")
	private String memberId;

	/**
	 * 是否首单
	 */
	@TableField("is_first")
	private Integer isFirst;

	/**
	 * 订单ID
	 */
	@TableField("bot_order_id")
	private Integer botOrderId;
}
