package com.yami.trading.huobi.data.job;

import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KlineCacheJob implements Runnable {


	@Autowired
	private HobiDataService hobiDataService;

	@Autowired
	private ItemService itemService;

	public void start() {
		new Thread(this, "KlineCacheJob").start();
		log.info("启动KlineCacheJob！");
	}

	@Override
	public void run() {
		while (true) {
			try {
				List<Item> item_list = itemService.findByType(Item.cryptos);
				for (int i = 0; i < item_list.size(); i++) {
					Item item = item_list.get(i);

					this.addCache(item, Kline.PERIOD_1MIN);
					this.addCache(item, Kline.PERIOD_5MIN);
					this.addCache(item, Kline.PERIOD_15MIN);
					this.addCache(item, Kline.PERIOD_30MIN);
					this.addCache(item, Kline.PERIOD_60MIN);
					this.addCache(item, Kline.PERIOD_4HOUR);
					this.addCache(item, Kline.PERIOD_1DAY);
					this.addCache(item, Kline.PERIOD_1MON);
					this.addCache(item, Kline.PERIOD_1WEEK);

				}

			} catch (Throwable e) {
				log.error("KlineCacheJob  fail", e);
			} finally {
				ThreadUtils.sleep(1000);
			}
		}

	}

	public void addCache(Item item, String line) {
		List<Kline> hobikline_list = hobiDataService.kline(item.getRemarks(), line, 1, 0);
		if (hobikline_list != null && hobikline_list.size() > 0) {

			String key = item.getSymbol() + "_" + line;
			DataCache.putKlineHoBi(key, hobikline_list.get(0));
		}
		ThreadUtils.sleep(2000);
	}

}
