package com.yami.trading.bean.loanOrder;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 借币订单实体
 */
@TableName("t_loan_relation_order")
public class LoanRelationOrder implements Serializable {

	private static final long serialVersionUID = -1309085104344395345L;

	/**
	 * 主键
	 */

	@Getter
	private String uuid;
	
	/**
	 * 借款关联订单ID
	 */
	private String loan_relation_order_id;
	
	/**
	 * 用户ID
	 */
	@Getter
	@Setter
	private String party_id;
	
	/**
	 * 订单类型: 1 借款;2新增质押;3续借;4还款;5强平结清
	 */
	@Getter
	@Setter
	private int order_type;
	
	/**
	 * 借款金额
	 */
	@Getter
	@Setter
	private double loan_amount;
	
	/**
	 * 贷款币种
	 */
	@Getter
	@Setter
	private String loan_currency;
	
	/**
	 * 质押的币种
	 */
	@Getter
	@Setter
	private String pledge_currency;
	
	/**
	 * 质押金额
	 */
	@Getter
	@Setter
	private double pledge_amount;
	
	/**
	 * 质押类型:1币质押
	 */
	@Getter
	@Setter
	private int pledge_type;
	
	/**
	 * 创建时间
	 */
	@Getter
	@Setter
	private Date create_time;

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLoanRelationOrderId() {
		return loan_relation_order_id;
	}

	public void setLoanRelationOrderId(String loanRelationOrderId) {
		this.loan_relation_order_id = loanRelationOrderId;
	}

	public int getPledge_type() {
		return pledge_type;
	}

	public void setPledgeType(int pledgeType) {
		this.pledge_type = pledgeType;
	}

//	public String getPartyId() {
//		return party_id;
//	}

	public void setPartyId(String partyId) {
		this.party_id = partyId;
	}

//	public int getOrderType() {
//		return order_type;
//	}

//	public void setOrder_type(int orderType) {
//		this.order_type = orderType;
//	}

	public double getLoan_amount() {
		return loan_amount;
	}

	public void setLoanAmount(double loanAmount) {
		this.loan_amount = loanAmount;
	}

	public String getLoan_currency() {
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

	public double getPledge_amount() {
		return pledge_amount;
	}

	public void setPledge_amount(double pledgeAmount) {
		this.pledge_amount = pledgeAmount;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	
}
