package com.yami.trading.service.quant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.quant.QuantPreIncome;

import java.util.List;

public interface QuantPreIncomeService extends IService<QuantPreIncome> {

	/**
	 * 根据矿机订单ID查询未使用的预收益记录
	 */
	List<QuantPreIncome> findUnusedByQuantOrderId(String quantOrderId);

	/**
	 * 生成预收益记录
	 * @param quantOrderId 机器人订单ID
	 * @param totalCount 总次数
	 * @param profitCount 盈利次数
	 * @param lossCount 亏损次数
	 * @param amount 每次下单数量
	 */
	void generatePreIncome(String quantOrderId, int totalCount, int profitCount, int lossCount, Double amount);

	/**
	 * 标记预收益记录为已使用
	 */
	void markAsUsed(Integer id);

	/** 查询今日已结算收益 */
	double selectDayIncome(String quantOrderId);

	/** 查询累计已结算收益 */
	double selectTotalIncome(String quantOrderId);
}
