package com.yami.trading.bean.finance;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_finance_order")
public class FinanceOrder extends UUIDEntity {

	private static final long serialVersionUID = -726057340004619294L;

//	private String uuid;

	/**
	 * 用户ID
	 */
	private String partyId;
	
	/**
	 * 订单 号
	 */
	private String orderNo;
	
	/**
	 * 理财产品名称
	 */
	private String name;
	
	/**
	 * 理财产品名称繁体
	 */
	private String nameCn;
	
	/**
	 * 理财产品名称英文
	 */
	private String nameEn;

	/**
	 * 理财产品Id
	 */
	@TableField("`finance_id`")
	private String financeId;

	/**
	 * 金额
	 */
	private double amount;

	/**
	 * 买入时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**
	 * 起息时间 从买入时间第二天开始算
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date earnTime;

	/**
	 * 截止时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date stopTime;

	/**
	 * 赎回时间=截止时间+1天
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date closeTime;

	/**
	 * 收益
	 */
	private double profit;
	/**
	 * 之前或累计收益
	 */
	private double profitBefore;

	/**
	 * 状态。0.正常赎回， 1 托管中 ,2提前赎回 (违约)3.取消
	 */
	private String state = "1";
	/**
	 * 托管时间，周期
	 */
	private int cycle;

	/**
	 * 传回前端数据，数据库不保存
	 */

	/**
	 * 理财产品图片
	 */
	@TableField(exist = false)
	private String img;
	/**
	 * 剩余天数
	 * 
	 * @return
	 */
	@TableField(exist = false)
	private int days;
	/**
	 * 赎回时间=截止时间+1天
	 */
	@TableField(exist = false)
	private String closeTimeStr;
	/**
	 * 买入时间
	 */
	@TableField(exist = false)
	private String createTimeStr;
	/**
	 * 起息时间 从买入时间第二天开始算
	 */
	@TableField(exist = false)
	private String earnTimeStr;

	/**
	 * 截止时间
	 */
	@TableField(exist = false)
	private String stopTimeStr;

	/**
	 * 日利率(%)
	 */
	@TableField(exist = false)
	private String dailyRate;
	/**
	 * 预计收益
	 * 
	 * @return
	 */
	@TableField(exist = false)
	private double profitMay;

	/**
	 * 理财购买币种
	 */
	@TableField(exist = false)
	private String buyCurrency = "usdt";
	
	/**
	 * 理财购买币种
	 */
	@TableField(exist = false)
	private String outputCurrency = "usdt";

}
