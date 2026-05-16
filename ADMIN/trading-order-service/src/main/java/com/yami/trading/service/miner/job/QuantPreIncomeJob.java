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
		// 每日目标收益 = 投资金额 × 日利率%
		double dailyProfitTarget = Arith.mul(amount, dailyRate / 100);
		int profitCount = (int) (DAILY_COUNT * PROFIT_RATIO); // 每天盈利条数
		int lossCount = DAILY_COUNT - profitCount;            // 每天亏损条数
		double avgPerTrade = dailyProfitTarget / DAILY_COUNT; // 每条平均收益基准

		log.info("开始异步生成预收益记录，订单ID：{}，金额：{}，周期：{}天，日利率：{}%，每日目标：{}",
			quantOrderId, amount, cycleDays, dailyRate, dailyProfitTarget);

		try {
			List<com.yami.trading.bean.quant.QuantPreIncome> allList = new ArrayList<>();
			LocalDateTime now = LocalDateTime.now();

			for (int day = 0; day < cycleDays; day++) {
				// 生成当天 DAILY_COUNT 条记录（时间窗口连续）
				List<com.yami.trading.bean.quant.QuantPreIncome> dayList = new ArrayList<>();
				for (int i = 0; i < DAILY_COUNT; i++) {
					com.yami.trading.bean.quant.QuantPreIncome income = new com.yami.trading.bean.quant.QuantPreIncome();
					income.setQuantOrderId(quantOrderId);
					income.setNumber(amount);
					income.setStatus(0);
					long minuteOffset = (long) day * DAILY_COUNT * 10 + i * 10L;
					LocalDateTime startTime = now.plusMinutes(minuteOffset);
					income.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
					income.setEndTime(Date.from(startTime.plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant()));
					dayList.add(income);
				}

				// 打乱顺序，使盈亏分布随机
				Collections.shuffle(dayList);

				// 按胜率分配盈亏，前 profitCount 条盈利，其余亏损
				double dayTotal = 0;
				for (int i = 0; i < DAILY_COUNT; i++) {
					double currentIncome;
					if (i < profitCount) {
						double multiplier = 1.5 + Math.random() * 1.5; // 1.5~3.0
						currentIncome = Arith.mul(avgPerTrade, multiplier);
					} else {
						double multiplier = 0.5 + Math.random() * 0.3; // 0.5~0.8（亏损）
						currentIncome = Arith.mul(avgPerTrade, -multiplier);
					}
					dayList.get(i).setIncome(currentIncome);
					dayTotal = Arith.add(dayTotal, currentIncome);
				}

				// 调整最后一条，使当天总和精确等于 dailyProfitTarget
				double diff = Arith.sub(dailyProfitTarget, dayTotal);
				com.yami.trading.bean.quant.QuantPreIncome last = dayList.get(DAILY_COUNT - 1);
				last.setIncome(Arith.add(last.getIncome(), diff));

				allList.addAll(dayList);
			}

			quantPreIncomeService.saveBatch(allList);
			log.info("异步生成预收益记录成功，订单ID：{}，总记录数：{}，每日目标：{}",
				quantOrderId, allList.size(), dailyProfitTarget);
		} catch (Exception e) {
			log.error("异步生成预收益记录失败，订单ID：{}", quantOrderId, e);
		}
	}
}
