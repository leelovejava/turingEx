package com.yami.trading.admin.controller.loanOrder;

public class LoanConstants {

	/**
	 * 初始质押率
	 */
	public static final double PLEDGE_RATE_INITIAL = 0.75;
	
	/**
	 * 提醒质押率
	 */
	public static final double PLEDGE_RATE_NOTICE = 0.6;
	
	/**
	 * 强平质押率
	 */
	public static final double PLEDGE_RATE_CLOSEOUT = 0.83;
	
	/**
	 * 质押订单类型 
	 * 借款
	 */
	public static final int PLEDGE_ORDER_TYPE_LOAN = 1;
	
	/**
	 * 补增质押
	 */
	public static final int PLEDGE_ORDER_TYPE_REPLENISH = 2;
	
	/**
	 * 续借
	 */
	public static final int PLEDGE_ORDER_TYPE_REFURBISH = 3;
	
	/**
	 * 还款
	 */
	public static final int PLEDGE_ORDER_TYPE_REPAY = 4;
	
	/**
	 * 强平结清
	 */
	public static final int PLEDGE_ORDER_TYPE_CLOSEOUT = 5;
	
	/**
	 * 计息中
	 */
	public static final int PLEDGE_ORDER_STATE_CALCULATE = 1;
	
	/**
	 * 已结清
	 */
	public static final int PLEDGE_ORDER_STATE_SETTLE = 2;
	
	/**
	 * 强平结清
	 */
	public static final int PLEDGE_ORDER_STATE_CLOSEOUT = 3;
	
	/**
	 * 已逾期
	 */
	public static final int PLEDGE_ORDER_STATE_OVERDUE = 4;
	
	public static final int PLEDGE_TYPE = 1;
	
}
