package com.yami.trading.admin.controller.loan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.loan.LoanParam;
import com.yami.trading.bean.loan.SimpleLoanOrder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JORGE
 * @description 借贷服务接口
 */
public interface LoanService {
	/**
	 * 删除借贷申请单
	 * @param orderId 申请单ID
	 * @return 删除是否成功
	 */
	public boolean deleteLoanOrder(String orderId);
	
	/**
	 * 获取借贷申请单列表
	 * @param queryParams 参数字典
	 * @return 页面对象
	 */
	public Page pagedQuery(Map<String,Object> queryParams);
	
	/**
	 * 获取借贷参数
	 * @param uuid 参数配置ID
	 * @return 参数配置字典
	 */
	public HashMap<String,Object> getLoanParams(String uuid);
	
	/**
	 * 获取配置集合
	 * @param uuid 参数配置ID
	 * @return 参数配置字典
	 */
	public List<LoanParam> getLoanParamList(String uuid);
	
	/**
	 * 添加借贷申请单
	 * @param simpleLoanOrder 申请单
	 * @return 添加是否成功
	 */
	public Boolean addLoanOrder(SimpleLoanOrder simpleLoanOrder);
	
	/**
	 * 修改借贷申请单
	 * @param simpleLoanOrder 申请单
	 * @return 修改是否成功
	 */
	public Boolean modLoanOrder(SimpleLoanOrder simpleLoanOrder);
	
	/**
	 * 修改借贷申请单状态
	 * @param orderId 申请单ID
	 * @param status 申请单状态
	 * @return 修改是否成功
	 */
	public boolean updateLoanOrderState(String orderId,String status,String reason);
	
	/**
	 * 获取单个借贷申请单
	 * @param orderId 用户ID
	 * @param orderId 申请单ID
	 * @return 申请单对象
	 */
	public SimpleLoanOrder getLoanOrder(String partyId,String orderId);
	
	/**
	 * 获取用户借贷订单列表
	 * @return 订单列表
	 */
	public List<SimpleLoanOrder> getUserOrders(Map<String,Object> paramMap);

	public void setParamMap(HashMap<String,Object> params);
	public HashMap<String,Object> getParamMap();

	void partialRepay(String orderId, BigDecimal amount);

}
