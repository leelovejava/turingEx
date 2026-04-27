package com.yami.trading.service.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.trader.domain.Trader;

import java.util.List;
import java.util.Map;

/**
 * 交易员
 *
 */
public interface TraderService {

	public Trader findById(String id);
	
	public Trader findByPartyId(String partyId);

	public void update(Trader entity);
	
	/**
	 * 每次下单和平仓计算一次交易员自身的数据
	 */
	public void updateTrader(Trader entity, List<ContractOrder> orders);
	

	/**
	 * APP查询交易员列表
	 * 
	 * @param page
	 * @param name
	 * @param state    状态
	 * orderBy_type  按什么排序
	 */
	public List<Map<String, Object>> getPaged(Page<Trader> page, String name, String state);

	void save(Trader trader);

	Trader findByPartyIdAndChecked(String partyId, int checked);
}
