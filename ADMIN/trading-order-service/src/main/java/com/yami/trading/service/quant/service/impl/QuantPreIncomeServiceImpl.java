package com.yami.trading.service.quant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.quant.QuantPreIncome;
import com.yami.trading.common.util.Arith;
import com.yami.trading.dao.quant.QuantPreIncomeMapper;
import com.yami.trading.service.quant.service.QuantPreIncomeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 量化预收益服务实现类
 *
 * 负责管理量化订单的预收益记录，包括生成、查询、标记使用等操作。
 *
 * 收益记录状态说明：
 *   0 - 未使用（预生成，尚未展示给用户）
 *   1 - 已使用（已展示给用户，计入收益统计）
 */
@Service
@Transactional
public class QuantPreIncomeServiceImpl extends ServiceImpl<QuantPreIncomeMapper, QuantPreIncome> implements QuantPreIncomeService {

	/**
	 * 查询指定量化订单的所有未使用收益记录（status=0），按开始时间升序排列
	 *
	 * @param quantOrderId 量化订单ID
	 * @return 未使用的预收益记录列表
	 */
	@Override
	public List<QuantPreIncome> findUnusedByQuantOrderId(String quantOrderId) {
		QueryWrapper<QuantPreIncome> queryWrapper = new QueryWrapper<>();
		if (quantOrderId != null) {
			queryWrapper.eq("quant_order_id", quantOrderId);
		}
		// 只查未使用的记录
		queryWrapper.eq("status", 0);
		queryWrapper.orderByAsc("start_time");
		return this.list(queryWrapper);
	}

	/**
	 * 从今日未使用的收益记录中随机取一条（用于前端实时展示当日交易波动）
	 *
	 * 条件：status=0（未使用）且 end_time 在今日范围内，随机排序取第一条
	 *
	 * @param quantOrderId 量化订单ID
	 * @return 随机一条今日未使用收益记录，不存在则返回 null
	 */
	@Override
	public QuantPreIncome findTodayRandomUnusedByQuantOrderId(String quantOrderId) {
		LocalDate today = LocalDate.now();
		// 今日开始时间（00:00:00）
		Date dayStart = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
		// 明日开始时间（即今日结束边界）
		Date nextDayStart = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		QueryWrapper<QuantPreIncome> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("quant_order_id", quantOrderId)
				// 未使用
				.eq("status", 0)
				// end_time 在今日范围内
				.ge("end_time", dayStart)
				.lt("end_time", nextDayStart)
				// 随机取一条
				.last("ORDER BY RAND() LIMIT 1");
		return this.getOne(queryWrapper, false);
	}

	/**
	 * 批量生成预收益记录（按固定10分钟间隔生成时间窗口）
	 *
	 * 生成规则：
	 *   - 前 profitCount 条为盈利记录，收益 = amount × (0.8~1.2) 随机倍率
	 *   - 后 lossCount 条为亏损记录，收益 = amount × -(0.5~0.8) 随机倍率
	 *   - 生成后打乱顺序，使盈亏分布随机
	 *
	 * @param quantOrderId 量化订单ID
	 * @param totalCount   总记录数
	 * @param profitCount  盈利记录数
	 * @param lossCount    亏损记录数
	 * @param amount       每笔基础金额
	 */
	@Override
	public void generatePreIncome(String quantOrderId, int totalCount, int profitCount, int lossCount, Double amount) {
		List<QuantPreIncome> incomeList = new ArrayList<>();

		LocalDateTime now = LocalDateTime.now();

		// 按每10分钟一条生成时间窗口
		for (int i = 0; i < totalCount; i++) {
			QuantPreIncome income = new QuantPreIncome();
			income.setQuantOrderId(quantOrderId);
			income.setNumber(amount);
			// 初始状态：未使用
			income.setStatus(0);

			LocalDateTime startTime = now.plusMinutes(i * 10);
			LocalDateTime endTime = startTime.plusMinutes(10);

			income.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
			income.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

			incomeList.add(income);
		}

		// 打乱顺序，使盈亏分布随机
		Collections.shuffle(incomeList);

		int profitIndex = 0;
		int lossIndex = profitCount;

		// 前 profitCount 条设为盈利，其余设为亏损
		for (int i = 0; i < incomeList.size(); i++) {
			QuantPreIncome income = incomeList.get(i);
			if (i < profitCount) {
				// 盈利：收益倍率 0.8~1.2
				double profitRatio = 0.8 + Math.random() * 0.4;
				income.setIncome(Arith.mul(amount, profitRatio));
			} else {
				// 亏损：收益倍率 -(0.5~0.8)
				double lossRatio = 0.5 + Math.random() * 0.3;
				income.setIncome(Arith.mul(amount, -lossRatio));
			}
		}

		this.saveBatch(incomeList);
	}

	/**
	 * 将指定收益记录标记为已使用（status: 0 → 1）
	 *
	 * 用于前端展示后标记该条记录已被消费，后续计入收益统计
	 *
	 * @param id 收益记录主键ID
	 */
	@Override
	public void markAsUsed(Integer id) {
		QuantPreIncome income = this.getById(id);
		if (income != null) {
			// 标记为已使用
			income.setStatus(1);
			this.updateById(income);
		}
	}

	/**
	 * 查询指定量化订单今日已使用收益总和（即今日收益）
	 *
	 * 条件：status=1（已使用）且 end_time 在今日范围内
	 *
	 * @param quantOrderId 量化订单ID
	 * @return 今日收益总和
	 */
	@Override
	public double selectDayIncome(String quantOrderId) {
		Date dayStart = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date dayEnd = Date.from(LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
		QueryWrapper<QuantPreIncome> qw = new QueryWrapper<QuantPreIncome>()
				.eq("quant_order_id", quantOrderId)
				.eq("status", 1)
				.apply("end_time BETWEEN {0} AND {1}", dayStart, dayEnd);
		return this.list(qw).stream().mapToDouble(o -> o.getIncome() == null ? 0 : o.getIncome()).sum();
	}

	/**
	 * 查询指定量化订单累计已使用收益总和（即总收益）
	 *
	 * 条件：status=1（已使用），不限时间范围
	 * 注意：必须过滤 status=1，否则会将未使用的预生成记录（status=0）也计入，导致数值虚高
	 *
	 * @param quantOrderId 量化订单ID
	 * @return 累计收益总和
	 */
	@Override
	public double selectTotalIncome(String quantOrderId) {
		return baseMapper.sumIncomeByOrderId(quantOrderId);
	}
}
