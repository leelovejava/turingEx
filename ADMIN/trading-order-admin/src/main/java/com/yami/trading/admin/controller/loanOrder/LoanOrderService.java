package com.yami.trading.admin.controller.loanOrder;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.loanOrder.LoanOrder;

/**
 * 质押借币service
 *
 */
public interface LoanOrderService {

	/**
	 * 新增借币订单
	 */
	public void saveLoanOrder(LoanOrder order);
	
	/**
	 * 修改质押借币订单
	 */
	public void updateLoanOrder(LoanOrder order);
	
	/**
	 * 质押借币订单
	 */
	public List<Map<String, Object>> pagedQuery(int pageNo, int pageSize, String partyId);
	
	public List<String> queryOrdersNoticeList(String partyId);
	
	/**
	 * 页面订单详情
	 */
	public Map<String, Object> getLoanOrder(String loanOrderId);
	
	/**
	 * 订单
	 */
	public LoanOrder getLoanOrderParam(String loanOrderId);
	
	/**
	 * 获取配置
	 */
	public Map<String, Object> getLoanConfig();
	
	/**
	 * 分页查询 计息中、已逾期的订单
	 */
	public Page pagedQueryInterestOrder(int pageNo, int pageSize, Date date);
	
	/**
	 * 定时计算利息
	 */
	public void updateInterest(List<LoanOrder> orders);
	
	/**
	 * 根据订单总负债 计算质押率
	 */
	public Map<String, Double> calculatePledgeRate(String pledgeCurrency, double debtAmount, double pledgeAmount);
	
	/**
	 * 系统强平
	 */
	public void updateCloseout(LoanOrder order, double pledgeRate);
	
	/**
	 * 后台质押借币订单
	 */
	public Page pagedQueryAdmin(int pageNo, int pageSize, String userParam, String orderNo, String roleName, String state , List<String> children);
	
	/**
	 * 从缓存中获取订单
	 */
	public List<LoanOrder> cacheOrders();
	
	/**
	 * 存入缓存
	 */
	public void addCacheOrder(LoanOrder order);
	
	/**
	 * 从缓存中移除订单
	 */
	public void removeCacheOrder(String orderId);
	
	/**
	 * 从缓存中移除订单
	 */
	public void cacheRemoveOrders(List<String> orderIds);


	//
	/**
	 * 补增质押
	 */
	public void saveReplenishOrder(String loanOrderId, double pledgeAmount, String partyId);

	/**
	 * 续借
	 */
	public Object saveRefurbishOrder(String loanOrderId, double loanAmount, String partyId);

	/**
	 * 还款
	 */
	public void saveRepayOrder(String loanOrderId, double repayAmount, String partyId);
	
}
