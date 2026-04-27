package com.yami.trading.admin.controller.loanOrder;

import com.yami.trading.bean.loanOrder.LoanRelationOrder;

import java.util.List;
import java.util.Map;

public interface LoanRelationOrderService {

	/**
	 * 新增质押借币关联订单
	 */
	public void saveLoanRelationOrder(LoanRelationOrder order);
	
	/**
	 * 质押记录
	 */
	public List<Map<String, Object>> pagedQueryPledge(int pageNo, int pageSize, String loanOrderId);

	/**
	 * 根据订单关联ID获取订单列表
	 */
	public List<LoanRelationOrder> queryOrders(String loanRelationOrderId, int orderType);
	
	/**
	 * 后台质押记录
	 */
	public List<Map<String,Object>> queryLoanRelation(String loanRelationOrderId);


	public void insertLoanRelationOrder(LoanRelationOrder order);
}
