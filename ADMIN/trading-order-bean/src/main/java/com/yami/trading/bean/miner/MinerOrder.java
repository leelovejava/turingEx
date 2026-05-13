package com.yami.trading.bean.miner;

import java.util.Comparator;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_miner_order")
public class MinerOrder extends UUIDEntity implements Comparator<MinerOrder> {
	private static final long serialVersionUID = 1L;

	@TableField("party_id")
	private String party_id;
	/**
	 * 订单 号
	 */
	private String order_no;

	/**
	 * 矿机产品Id
	 */
	@Getter
	private String miner_id;

	/**
	 * 金额
	 */
	private double amount;

	/**
	 * 买入时间
	 */
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date  create_time;

	/**
	 * 起息时间 从买入时间第二天开始算
	 */
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date  earn_time;

	/**
	 * 截止时间
	 */
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date  stop_time;

	/**
	 * 累计收益
	 */
	private double profit;

	/**
	 * 状态。0.正常赎回， 1 托管中 ,2提前赎回 (违约)3.取消
	 */
	private String state = "1";
	/**
	 * 上次结息日期纪录，（如遇服务中途停止，可根据该字段判定是否需要重新计算）
	 */
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date  compute_day;
	/**
	 * 赎回时间
	 */
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date close_time;
	/**
	 * 是否首次购买： 1：首次购买，0不是首次
	 */
	private String first_buy;
	/**
	 * 基础计息金额
	 */
	private double base_compute_amount;

	/**
	 * 体验矿机随机日利率(%) - 购买时随机生成，介于日利率开始和结束值之间
	 */
	private double random_daily_rate;

	@Setter
	@Getter
	private  double compute_profit;

	@Setter
	@Getter
	private double default_money;

	/**
	 * 预计总收益（整数，创建时根据随机日收益×周期天数生成）
	 */
	private long expected_total_income;

	/**
	 * 交易对，如 BTC/USDT、ETH/USDT、SOL/USDT
	 */
	@Setter
	@Getter
	private String symbol;

	/**
	 * 购买时传入的周期天数（不持久化，仅用于创建时计算stop_time）
	 */
	@Setter
	@Getter
	@TableField(exist = false)
	private int cycle;

	public String getPartyId() {
		return party_id;
	}

	public void setPartyId(String partyId) {
		this.party_id = partyId;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date  getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public Date getEarn_time() {
		return earn_time;
	}

	public void setEarn_time(Date earn_time) {
		this.earn_time = earn_time;
	}

	public Date getStop_time() {
		return stop_time;
	}

	public void setStop_time(Date stop_time) {
		this.stop_time = stop_time;
	}

	public void setMinerId(String minerId) {
		this.miner_id = minerId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getCompute_day() {
		return compute_day;
	}

	public void setCompute_day(Date compute_day) {
		this.compute_day = compute_day;
	}

	public Date getClose_time() {
		return close_time;
	}

	public void setClose_time(Date close_time) {
		this.close_time = close_time;
	}

	public String getFirst_buy() {
		return first_buy;
	}

	public void setFirst_buy(String first_buy) {
		this.first_buy = first_buy;
	}

	
	public double getBase_compute_amount() {
		return base_compute_amount;
	}

	public void setBase_compute_amount(double base_compute_amount) {
		this.base_compute_amount = base_compute_amount;
	}

	/**
	 * 获取体验矿机随机日利率(%)
	 * @return 随机日利率
	 */
	public double getRandom_daily_rate() {
		return random_daily_rate;
	}

	/**
	 * 设置体验矿机随机日利率(%)
	 * @param random_daily_rate 随机日利率
	 */
	public void setRandom_daily_rate(double random_daily_rate) {
		this.random_daily_rate = random_daily_rate;
	}

	public long getExpected_total_income() {
		return expected_total_income;
	}

	public void setExpected_total_income(long expected_total_income) {
		this.expected_total_income = expected_total_income;
	}

	@Override
	public int compare(MinerOrder arg0, MinerOrder arg1) {
		// TODO Auto-generated method stub
		return -arg0.getCreate_time().compareTo(arg1.getCreate_time());
	}

}
