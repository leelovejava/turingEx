package com.yami.trading.huobi.task.future;

import cn.hutool.core.date.DateUtil;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.future.domain.FuturesOrder;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.service.future.FuturesOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class FuturesOrderCalculationService {
	@Autowired
	private FuturesOrderService futuresOrderService;

	public void saveCalculation(FuturesOrder order) {

		try {
			Realtime realtime = DataCache.getRealtime(order.getSymbol());
			if (null == realtime) {
				return;
			}
			// 更新订单信息并纪录到缓存
			futuresOrderService.refreshCache(order, realtime.getClose());
			if (order.getSettlementTime()< DateUtil.currentSeconds()) {
				futuresOrderService.saveClose(order);
			}
		} catch (Throwable e) {
			log.error("FuturesOrderCalculationServiceImpl run fail", e);
		}
	}
}
