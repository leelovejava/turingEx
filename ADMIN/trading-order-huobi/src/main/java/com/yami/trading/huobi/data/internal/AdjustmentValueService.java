package com.yami.trading.huobi.data.internal;

import com.yami.trading.huobi.data.model.AdjustmentValue;

import java.math.BigDecimal;

public interface AdjustmentValueService {
	/**
	 * 调整
	 */
	void adjust(String symbol, double value, double second);

	double getCurrentValue(String symbol);

	AdjustmentValue getDelayValue(String symbol);

}
