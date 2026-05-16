package com.yami.trading.service.miner.job;

import cn.hutool.core.util.RandomUtil;
import com.yami.trading.bean.quant.QuantBotOrder;
import com.yami.trading.service.quant.service.QuantBotOrderService;
import com.yami.trading.service.quant.service.QuantPreIncomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class QuantPreIncomeJob {

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
		BigDecimal amountBD = BigDecimal.valueOf(amount);
		// 每日目标收益 = 投资金额 × 日利率%
		BigDecimal dayIncome = amountBD.multiply(BigDecimal.valueOf(dailyRate / 100)).setScale(4, RoundingMode.DOWN);

		log.info("开始异步生成预收益记录，订单ID：{}，金额：{}，周期：{}天，日利率：{}%，每日目标：{}",
			quantOrderId, amount, cycleDays, dailyRate, dayIncome);

		try {
			List<com.yami.trading.bean.quant.QuantPreIncome> allList = new ArrayList<>();
			LocalDateTime now = LocalDateTime.now();

			for (int day = 0; day < cycleDays; day++) {
				// 随机涨幅 0.4~0.6，决定当天总亏损
				BigDecimal change = RandomUtil.randomBigDecimal(BigDecimal.valueOf(0.4), BigDecimal.valueOf(0.6))
						.setScale(4, RoundingMode.DOWN);
				BigDecimal totalDeficit = dayIncome.multiply(change).setScale(4, RoundingMode.DOWN);
				BigDecimal totalProfit = dayIncome.add(totalDeficit);

				BigDecimal aveProfit = totalProfit.divide(BigDecimal.valueOf(176), 4, RoundingMode.DOWN);
				BigDecimal aveDeficit = totalDeficit.divide(BigDecimal.valueOf(44), 4, RoundingMode.DOWN);

				log.info("第{}天，每日目标：{}，总盈利：{}，总亏损：{}", day + 1, dayIncome, totalProfit, totalDeficit);

				List<BigDecimal> incomeList = new ArrayList<>();
				BigDecimal totalProfitSum = BigDecimal.ZERO;
				BigDecimal totalDeficitSum = BigDecimal.ZERO;

				// 176次盈利
				for (int j = 0; j < 176; j++) {
					BigDecimal num;
					if (j == 175) {
						// 最后一次平账
						num = totalProfit.subtract(totalProfitSum).setScale(4, RoundingMode.DOWN);
					} else if (RandomUtil.randomInt(1, 3) == 1) {
						num = aveProfit.subtract(RandomUtil.randomBigDecimal(BigDecimal.valueOf(0.0001), BigDecimal.valueOf(0.003)))
								.setScale(4, RoundingMode.DOWN);
					} else {
						num = aveProfit.add(RandomUtil.randomBigDecimal(BigDecimal.valueOf(0.0001), BigDecimal.valueOf(0.004)))
								.setScale(4, RoundingMode.DOWN);
					}
					totalProfitSum = totalProfitSum.add(num);
					incomeList.add(num);
				}

				// 44次亏损
				for (int j = 0; j < 44; j++) {
					BigDecimal num;
					if (j == 43) {
						// 最后一次平账
						num = totalDeficit.subtract(totalDeficitSum).setScale(4, RoundingMode.DOWN);
						if (num.compareTo(BigDecimal.ZERO) > 0) {
							num = num.negate();
						}
					} else {
						BigDecimal random = RandomUtil.randomBigDecimal(BigDecimal.valueOf(0.0015), BigDecimal.valueOf(0.0022))
								.setScale(4, RoundingMode.DOWN);
						num = RandomUtil.randomInt(1, 3) == 1
								? aveDeficit.negate().subtract(random)
								: aveDeficit.negate().add(random);
					}
					totalDeficitSum = totalDeficitSum.add(num.abs());
					incomeList.add(num);
				}

				// 打乱顺序，保证第一条为正数
				Collections.shuffle(incomeList);
				if (!incomeList.isEmpty() && incomeList.get(0).compareTo(BigDecimal.ZERO) < 0) {
					for (int j = 1; j < incomeList.size(); j++) {
						if (incomeList.get(j).compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal temp = incomeList.get(0);
							incomeList.set(0, incomeList.get(j));
							incomeList.set(j, temp);
							break;
						}
					}
				}

				LocalDateTime startTime = now.plusDays(day);
				LocalDateTime endTime = startTime.plusDays(1);
				for (BigDecimal inc : incomeList) {
					com.yami.trading.bean.quant.QuantPreIncome record = new com.yami.trading.bean.quant.QuantPreIncome();
					record.setQuantOrderId(quantOrderId);
					record.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
					record.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
					record.setIncome(inc.doubleValue());
					record.setStatus(0);
					record.setNumber(amountBD.multiply(
							RandomUtil.randomBigDecimal(BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3))
									.setScale(4, RoundingMode.DOWN)).doubleValue());
					allList.add(record);
				}
			}

			quantPreIncomeService.saveBatch(allList);
			log.info("异步生成预收益记录成功，订单ID：{}，总记录数：{}", quantOrderId, allList.size());
		} catch (Exception e) {
			log.error("异步生成预收益记录失败，订单ID：{}", quantOrderId, e);
		}
	}
}
