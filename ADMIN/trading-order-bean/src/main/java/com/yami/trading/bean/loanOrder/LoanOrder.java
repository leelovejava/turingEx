package com.yami.trading.bean.loanOrder;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;


/**
 * 借币订单实体
 */
@Getter
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_loan_order")
public class LoanOrder implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = -8634950101772123796L;
	
	/**
	 * 主键
	 */
	@Getter
	private String uuid;

	/**
	 * 订单号
	 */
//	@TableField("ORDER_NO")
	private String order_no;
	
	/**
	 * 用户ID
	 */
//	@TableField("PARTY_ID")
	private String party_id;
	
	/**
	 * 订单类型: 1 借款;2新增质押;3续借;4还款;5强平结清
	 */
//	@TableField("ORDER_TYPE")
	@Getter
	private int order_type;
	
	/**
	 * 借款金额
	 */
//	@TableField("LOAN_AMOUNT")
	private double loan_amount;
	
	/**
	 * 订单状态: 1计息中;2已结清;3强平结清;4已逾期
	 */
	@Getter
	private int state;
	
	/**
	 * 贷款币种
	 */
//	@TableField("LOAN_CURRENCY")
	private String loan_currency;
	
	/**
	 * 质押的币种
	 */
//	@TableField("PLEDGE_CURRENCY")
	private String pledge_currency;
	
	/**
	 * 质押金额
	 */
//	@TableField("PLEDGE_AMOUNT")
	@Getter
	private double pledge_amount;
	
	/**
	 * 质押率
	 */
	@Getter
	private double pledge_rate;
	
	/**
	 * 质押类型:1币质押
	 */
	@Getter
	private int pledge_type;
	
	/**
	 * 总负债
	 */
	@Getter
	private double debt_amount;
	
	/**
	 * 总利息
	 */
	@Getter
	private double interest_amount;
	
	/**
	 * 总逾期
	 */
	@Getter
	private double overdue_amount;
	
	/**
	 * 逾期时利率
	 */
	@Getter
	private double overdue_rate;
	
	/**
	 * 时利率
	 */
	@Getter
	private double hourly_rate;
	
	/**
	 * 借款周期
	 */
	@Getter
	private int loan_cycle;
	
	/**
	 * 借款时间
	 */
	@Getter
	private Date create_time;
	
	/**
	 * 到期时间
	 */
	@Getter
	private Date expire_time;

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOrderNo() {
		return order_no;
	}

	public void setOrderNo(String orderNo) {
		this.order_no = orderNo;
	}

	public String getPartyId() {
		return party_id;
	}

	public void setPartyId(String partyId) {
		this.party_id = partyId;
	}

	public void setOrder_type(int order_type) {
		this.order_type = order_type;
	}

	public double getLoanAmount() {
		return loan_amount;
	}

	public void setLoanAmount(double loanAmount) {
		this.loan_amount = loanAmount;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getLoanCurrency() {
		return loan_currency;
	}

	public void setLoanCurrency(String loanCurrency) {
		this.loan_currency = loanCurrency;
	}
	
	public String getPledgeCurrency() {
		return pledge_currency;
	}

	public void setPledgeCurrency(String pledgeCurrency) {
		this.pledge_currency = pledgeCurrency;
	}

	public void setPledge_amount(double pledge_amount) {
		this.pledge_amount = pledge_amount;
	}

	public void setPledgeRate(double pledgeRate) {
		this.pledge_rate = pledgeRate;
	}

	public void setDebtAmount(double debtAmount) {
		this.debt_amount = debtAmount;
	}

	public void setInterestAmount(double interestAmount) {
		this.interest_amount = interestAmount;
	}

	public void setOverdueAmount(double overdueAmount) {
		this.overdue_amount = overdueAmount;
	}

	public void setOverdueRate(double overdueRate) {
		this.overdue_rate = overdueRate;
	}

	public void setHourlyRate(double hourlyRate) {
		this.hourly_rate = hourlyRate;
	}

	public void setLoanCycle(int loanCycle) {
		this.loan_cycle = loanCycle;
	}

	public void setCreateTime(Date createTime) {
		this.create_time = createTime;
	}

	public void setExpire_time(Date expire_time) {
		this.expire_time = expire_time;
	}

	public void setPledgeType(int pledgeType) {
		this.pledge_type = pledgeType;
	}
	
}
