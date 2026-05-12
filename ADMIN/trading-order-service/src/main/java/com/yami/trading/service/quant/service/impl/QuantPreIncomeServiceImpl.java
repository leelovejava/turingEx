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

@Service
@Transactional
public class QuantPreIncomeServiceImpl extends ServiceImpl<QuantPreIncomeMapper, QuantPreIncome> implements QuantPreIncomeService {

	@Override
	public List<QuantPreIncome> findUnusedByQuantOrderId(String quantOrderId) {
		QueryWrapper<QuantPreIncome> queryWrapper = new QueryWrapper<>();
		if (quantOrderId != null) {
			queryWrapper.eq("quant_order_id", quantOrderId);
		}
		queryWrapper.eq("status", 0);
		queryWrapper.orderByAsc("start_time");
		return this.list(queryWrapper);
	}

	@Override
	public QuantPreIncome findTodayRandomUnusedByQuantOrderId(String quantOrderId) {
		LocalDate today = LocalDate.now();
		Date dayStart = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date nextDayStart = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		QueryWrapper<QuantPreIncome> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("quant_order_id", quantOrderId)
				.eq("status", 1)
				.ge("end_time", dayStart)
				.lt("end_time", nextDayStart)
				.last("ORDER BY RAND() LIMIT 1");
		return this.getOne(queryWrapper, false);
	}

	@Override
	public void generatePreIncome(String quantOrderId, int totalCount, int profitCount, int lossCount, Double amount) {
		List<QuantPreIncome> incomeList = new ArrayList<>();

		LocalDateTime now = LocalDateTime.now();

		for (int i = 0; i < totalCount; i++) {
			QuantPreIncome income = new QuantPreIncome();
			income.setQuantOrderId(quantOrderId);
			income.setNumber(amount);
			income.setStatus(0);

			LocalDateTime startTime = now.plusMinutes(i * 10);
			LocalDateTime endTime = startTime.plusMinutes(10);

			income.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
			income.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

			incomeList.add(income);
		}

		Collections.shuffle(incomeList);

		int profitIndex = 0;
		int lossIndex = profitCount;

		for (int i = 0; i < incomeList.size(); i++) {
			QuantPreIncome income = incomeList.get(i);
			if (i < profitCount) {
				double profitRatio = 0.8 + Math.random() * 0.4;
				income.setIncome(Arith.mul(amount, profitRatio));
			} else {
				double lossRatio = 0.5 + Math.random() * 0.3;
				income.setIncome(Arith.mul(amount, -lossRatio));
			}
		}

		this.saveBatch(incomeList);
	}

	@Override
	public void markAsUsed(Integer id) {
		QuantPreIncome income = this.getById(id);
		if (income != null) {
			income.setStatus(1);
			this.updateById(income);
		}
	}

	@Override
	public boolean markAsUsedFromUnused(Integer id) {
		QuantPreIncome updateEntity = new QuantPreIncome();
		updateEntity.setStatus(2);
		QueryWrapper<QuantPreIncome> updateWrapper = new QueryWrapper<>();
		updateWrapper.eq("id", id).eq("status", 1);
		return this.update(updateEntity, updateWrapper);
	}

	@Override
	public double selectDayIncome(String quantOrderId) {
		Date dayStart = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date dayEnd = Date.from(LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
		QueryWrapper<QuantPreIncome> qw = new QueryWrapper<QuantPreIncome>()
				.eq("quant_order_id", quantOrderId)
				.between("end_time", dayStart, dayEnd);
		return this.list(qw).stream().mapToDouble(o -> o.getIncome() == null ? 0 : o.getIncome()).sum();
	}

	@Override
	public double selectTotalIncome(String quantOrderId) {
		QueryWrapper<QuantPreIncome> qw = new QueryWrapper<QuantPreIncome>()
				.eq("quant_order_id", quantOrderId);
		return this.list(qw).stream().mapToDouble(o -> o.getIncome() == null ? 0 : o.getIncome()).sum();
	}
}
