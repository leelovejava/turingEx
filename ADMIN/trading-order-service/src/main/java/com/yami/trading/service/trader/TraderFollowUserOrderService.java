package com.yami.trading.service.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.trader.domain.TraderFollowUserOrder;
import com.yami.trading.service.contract.ContractApplyOrderService;
import com.yami.trading.service.contract.ContractOrderService;

import java.util.List;
import java.util.Map;

/**
 * 用户跟随交易员详情
 */
public interface TraderFollowUserOrderService {
	/**
	 *交易员进入市场后的持仓单
	 */

	public void traderOpen(ContractOrder order, ContractApplyOrderService contractApplyOrderService, ContractOrderService contractOrderService, int follow);
	
	/**
	 * 平仓，按订单进行平仓
	 */
	public void traderClose(ContractOrder order, ContractOrderService contractOrderService);
	
	
	/**
	 * @param partyId  用户partyId
	 * @param apply_oder_no 委托单订单号
	 */
	public TraderFollowUserOrder findByPartyIdAndOrderNo(String partyId, String apply_oder_no);
	
	public void update(TraderFollowUserOrder entity);
	
	/**
	 * APP查询订单列表
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getPaged(Page page, String partyId, String state);


}
