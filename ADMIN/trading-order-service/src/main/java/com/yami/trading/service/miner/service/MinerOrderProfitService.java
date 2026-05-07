package com.yami.trading.service.miner.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.miner.MinerOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface MinerOrderProfitService {
	/**
	 * 分页获取计息中的矿机订单
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	Page pagedQueryComputeOrder(int pageNo, int pageSize);

	/**
	 * 计算订单收益
	 * 
	 * @param orders                 订单列表
	 * @param miner_profit_symbol    指定币种
	 * @param realtime               币种行情
	 * @param miner_bonus_parameters 推荐人收益参数
	 */
	void saveComputeOrderProfit(List orders, String miner_profit_symbol, Realtime realtime,
									   String miner_bonus_parameters);
	
	void saveComputeOrderProfit(List orders, String miner_profit_symbol, Realtime realtime,
			String miner_bonus_parameters,Date systemTime);
	/**
	 * 计算单个订单收益（赎回时调用）
	 * 
	 * @param order 矿机订单
	 */
	void saveComputeOrderProfit(MinerOrder order);
	
	/**
	 * 推荐人收益清空
	 */
	void cacheRecomProfitClear();

	/**
	 * 推荐人收益持久化数据库
	 */
	void saveRecomProfit();

	void saveRecomProfit(Date systemTime);
}
