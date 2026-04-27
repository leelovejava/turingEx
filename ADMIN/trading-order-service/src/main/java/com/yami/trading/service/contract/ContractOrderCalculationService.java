package com.yami.trading.service.contract;

import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.data.domain.Realtime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.List;

/**
 * 合约盈亏计算
 */
public interface ContractOrderCalculationService {

	/*
	 * 订单盈亏计算
	 */
	public void saveCalculation(String order_no);
	
	public void setOrder_close_line(BigDecimal order_close_line);

	public void setOrder_close_line_type(int order_close_line_type);
	public BigDecimal calculateAllProfit(ContractOrder order);
	public BigDecimal calculateTodayProfit(ContractOrder order, ZoneId zoneId);
}
