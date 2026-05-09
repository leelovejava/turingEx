package com.yami.trading.huobi.data.internal;

import com.yami.trading.huobi.data.model.AdjustmentValue;

import java.math.BigDecimal;

public interface AdjustmentValueService {
	/**
	 * 调整
	 */
	void adjust(String symbol, double value, double second);

	default void adjust(String symbol, double value, double second, Double durationSecond) {
		adjust(symbol, value, second);
	}

	double getCurrentValue(String symbol);

	AdjustmentValue getDelayValue2(String symbol);

}
