package com.yami.trading.huobi.data.job;

import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 最高最低修正
 *
 */
@Component
public class HighLowHandleJob implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(HighLowHandleJob.class);

	/**
	 * 数据接口调用间隔时长(毫秒)
	 */
	private int interval;
	public static boolean first = true;
	@Autowired
	private ItemService itemService;

	public void start() {
		new Thread(this, "HighLowHandleJob").start();
	}

	@Override
	public void run() {
		ThreadUtils.sleep(1000 * 60 * 3);
		while (true) {
			bulidHighLow();
			ThreadUtils.sleep(1000 * 60 * 3);
		}
	}

	public void bulidHighLow() {
		try {
			if (first) {
				// data数据保存间隔时长(毫秒)
				this.interval = 3;
				first = false;
			}
			// 秒
			int num = (24 * 60 * 60) / this.interval;
			List<Item> item_list = itemService.findByType(Item.cryptos);
			for (int i = 0; i < item_list.size(); i++) {
				Item item = item_list.get(i);
				try {

					List<Realtime> history = bulidNum(DataCache.getCryptosRealtimeHistory(item.getSymbol()), num);
					if (history == null || history.size() == 0) {
						continue;
					}
					Double high = null;
					Double low = null;

					for (int j = 0; j < history.size(); j++) {
						Realtime realtime = history.get(j);

						if (high == null || high < realtime.getClose()) {
							high = realtime.getClose();
						}

						if ((low == null || low > realtime.getClose()) && realtime.getClose() > 0) {
							low = realtime.getClose();
						}
					}
					if (item == null || item.getSymbol() == null) {
						logger.error("run fail");
					}
					if (high != null) {
						DataCache.putRealtimeHigh(item.getSymbol(), high);
					}
					if (low != null && low > 0) {
						DataCache.putRealtimeLow(item.getSymbol(), low);
					}

					Collections.sort(history);
					DataCache.putRealtime24HBeforeOpen(item.getSymbol(), history.get(0).getClose());

				} catch (Exception e) {
					logger.error("run fail", e);
				}
			}

		} catch (Exception e) {
			logger.error("run fail", e);
		}
	}

	private List<Realtime> bulidNum(List<Realtime> cacheList, int num) {
		List<Realtime> list = new ArrayList<>();
		if (cacheList == null) {
			return list;
		}
		if (num > cacheList.size()) {
			num = cacheList.size();
		}

		for (int i = cacheList.size() - num; i < cacheList.size(); i++) {
			list.add(cacheList.get(i));
		}

		return list;
	}
}
