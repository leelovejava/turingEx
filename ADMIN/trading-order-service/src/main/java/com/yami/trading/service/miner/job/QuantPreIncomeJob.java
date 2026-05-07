package com.yami.trading.service.miner.job;

import com.yami.trading.bean.quant.QuantBotOrder;
import com.yami.trading.common.util.Arith;
import com.yami.trading.service.quant.service.QuantBotOrderService;
import com.yami.trading.service.quant.service.QuantPreIncomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class QuantPreIncomeJob {

	private static final int DAILY_COUNT = 220;
	private static final double PROFIT_RATIO = 0.8; // 80%盈利

	@Autowired
	private QuantBotOrderService quantBotOrderService;

	@Autowired
	private QuantPreIncomeService quantPreIncomeService;

	/**
	 * 下单后同步创建机器人订单
	 * @param userId 用户ID
	 * @param amount 下单金额
	 * @param minerName 矿机名称
	 * @param minerId 矿机ID
	 * @return 机器人订单ID
	 */
	public Integer generatePreIncomeSync(String userId, Double amount, String minerName,String minerId) {
		log.info("开始同步创建机器人订单，用户ID：{}，金额：{}，矿机：{}", userId, amount, minerName);
		try {
			QuantBotOrder botOrder = new QuantBotOrder();
			botOrder.setUserId(userId);
			botOrder.setNumber(BigDecimal.valueOf(amount));
			botOrder.setStatus(1);
			botOrder.setCreatedAt(new Date());
			botOrder.setUpdatedAt(new Date());
			botOrder.setBotName(minerName);
			botOrder.setBotId(minerId);
			botOrder.setMemberId(userId);

			QuantBotOrder savedBotOrder = quantBotOrderService.createBotOrder(botOrder);
			log.info("创建机器人订单成功，订单ID：{}", savedBotOrder.getId());
			return savedBotOrder.getId();
		} catch (Exception e) {
			log.error("创建机器人订单失败，用户ID：{}", userId, e);
			return null;
		}
	}

	/**
	 * 异步生成预收益记录
	 * 盈利+亏损总和符合日收益金额
	 * @param quantOrderId 机器人订单ID
	 * @param amount 下单金额
	 * @param cycleDays 矿机周期天数
	 * @param dailyRate 日利率(%)
	 */
	@Async
	public void generatePreIncomeRecordsAsync(String quantOrderId, Double amount, int cycleDays, double dailyRate) {
		// 计算每日目标收益 = 投资金额 × 日利率
		double dailyProfitTarget = Arith.mul(amount, dailyRate / 100);
		// 计算总记录数：220条/天 × 周期天数
		int totalCount = DAILY_COUNT * cycleDays;
		// 计算盈利记录数：80% × 总记录数
		int profitCount = (int) (totalCount * PROFIT_RATIO);
		// 计算亏损记录数：剩余部分
		int lossCount = totalCount - profitCount;
		
		log.info("开始异步生成预收益记录，机器人订单ID：{}，金额：{}，周期天数：{}，日利率：{}%，每日目标收益：{}", 
			quantOrderId, amount, cycleDays, dailyRate, dailyProfitTarget);
		
		try {
			List<com.yami.trading.bean.quant.QuantPreIncome> incomeList = new ArrayList<>();
			LocalDateTime now = LocalDateTime.now();

			for (int i = 0; i < totalCount; i++) {
				com.yami.trading.bean.quant.QuantPreIncome income = new com.yami.trading.bean.quant.QuantPreIncome();
				income.setQuantOrderId(quantOrderId);
				income.setNumber(amount);
				income.setStatus(0);

				LocalDateTime startTime = now.plusMinutes(i * 10L);
				LocalDateTime endTime = startTime.plusMinutes(10);

				income.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
				income.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

				incomeList.add(income);
			}

			Collections.shuffle(incomeList);

			// 平均每次收益（用于确保总和符合目标）
			double avgPerTrade = dailyProfitTarget / DAILY_COUNT;
			double totalGenerated = 0;

			for (int i = 0; i < incomeList.size(); i++) {
				com.yami.trading.bean.quant.QuantPreIncome income = incomeList.get(i);
				double currentIncome;
				
				if (i < profitCount) {
					// 盈利记录：基于平均收益的1.5~3倍
					double multiplier = 1.5 + Math.random() * 1.5;
					currentIncome = Arith.mul(avgPerTrade, multiplier);
				} else {
					// 亏损记录：基于平均收益的-2~-0.5倍
					double multiplier = -2 + Math.random() * 1.5;
					currentIncome = Arith.mul(avgPerTrade, multiplier);
				}
				
				income.setIncome(currentIncome);
				totalGenerated = Arith.add(totalGenerated, currentIncome);
			}

			// 调整最后一条记录，使每日总和精确等于目标收益
			if (totalCount > 0) {
				double dailyTotalTarget = Arith.mul(dailyProfitTarget, cycleDays);
				double diff = Arith.sub(dailyTotalTarget, totalGenerated);
				
				com.yami.trading.bean.quant.QuantPreIncome lastIncome = incomeList.get(totalCount - 1);
				lastIncome.setIncome(Arith.add(lastIncome.getIncome(), diff));
			}

			quantPreIncomeService.saveBatch(incomeList);
			log.info("异步生成预收益记录成功，机器人订单ID：{}，记录数：{}，每日目标收益：{}", 
				quantOrderId, incomeList.size(), dailyProfitTarget);
		} catch (Exception e) {
			log.error("异步生成预收益记录失败，机器人订单ID：{}", quantOrderId, e);
		}
	}
}
