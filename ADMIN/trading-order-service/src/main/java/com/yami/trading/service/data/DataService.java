package com.yami.trading.service.data;


import com.yami.trading.bean.data.domain.*;

import java.util.List;

public interface DataService {

	/**
	 * 根据币种分类 获取实时价格数据
	 */
	List<Realtime> realtimeByType(String symbolType);

	/**
	 * 实时价格
	 *
	 * @param symbol 指定产品代码，多个用逗号分割，最大100个
	 *
	 */
	List<Realtime> realtime(String symbol);

	/**
	 * 市场深度数据
	 */
	Depth depth(String symbol);

	/**
	 * 分时
	 */
	List<Trend> trend(String symbol);

	/**
	 * K线
	 */
	List<Kline> kline(String symbol, String line);

	/**
	 * 获得近期交易记录
	 */
	Trade trade(String symbol);

}
