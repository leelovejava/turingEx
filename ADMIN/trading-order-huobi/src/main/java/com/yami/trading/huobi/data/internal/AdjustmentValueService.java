package com.yami.trading.huobi.data.internal;

import com.yami.trading.huobi.data.model.AdjustmentValue;

import java.math.BigDecimal;

/**
 * 行情调整值服务接口
 * 负责K线行情的价格调整，支持即时生效、延时生效和持续时间自动清除三种模式
 */
public interface AdjustmentValueService {

	/**
	 * 调整行情价格
	 *
	 * @param symbol 交易品种代码
	 * @param value  调整值（正数加价，负数降价）
	 * @param second 生效延迟时间（秒），0 表示即时生效
	 */
	void adjust(String symbol, double value, double second);

	/**
	 * 调整行情价格，并在持续时间到期后自动清除调整值
	 *
	 * @param symbol         交易品种代码
	 * @param value          调整值（正数加价，负数降价）
	 * @param second         生效延迟时间（秒），0 表示即时生效
	 * @param durationSecond 持续时间（秒），0 或 null 表示不限制
	 */
	void adjust(String symbol, double value, double second, Double durationSecond);

	/**
	 * 启动持续时间定时清除任务（插针应用完毕后由 handleRealTimeList 调用）
	 *
	 * @param symbol         交易品种代码
	 * @param durationSecond 持续时间（秒），0 或 null 表示取消定时清除
	 */
	void scheduleClear(String symbol, Double durationSecond);

	/**
	 * 获取指定品种当前已生效的调整值
	 *
	 * @param symbol 交易品种代码
	 * @return 当前调整值
	 */
	double getCurrentValue(String symbol);

	/**
	 * 获取指定品种待生效的延时调整值
	 *
	 * @param symbol 交易品种代码
	 * @return 延时调整值对象，无待生效值时返回 null
	 */
	AdjustmentValue getDelayValue2(String symbol);

}
