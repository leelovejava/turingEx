package com.yami.trading.huobi.task;


import com.yami.trading.bean.data.domain.Trade;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.TradeTimeObject;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DataFrequencyServer implements Runnable {

	@Autowired
	private HobiDataService hobiDataService;
	@Autowired
	private ItemService itemService;

	public void start() {
		new Thread(this, "DataFrequencyServer").start();
		if (log.isInfoEnabled()) log.info("启动DataFrequencyServer！");
	}

	public void run() {
		while (true) {
			try {
				List<Item> item_list = new ArrayList<>(itemService.list()).stream().filter(i->i.getType().equalsIgnoreCase(Item.cryptos)).collect(Collectors.toList());
				for (int i = 0; i < item_list.size(); i++) {
					try {
						Item item = item_list.get(i);
						this.trade(item);
					} catch (Exception e) {
						log.error("trade fail", e);
					} finally {
						ThreadUtils.sleep(3000);
					}

				}
			} catch (Throwable e) {
				log.error("DataFrequencyServer fail", e);
			} finally {
				ThreadUtils.sleep(1000 * 10);
			}
		}
	}

	private void trade(Item item) {

		Trade trade = hobiDataService.tradeDecorator(item.getRemarks(), 0);
		if (trade != null) {
			TradeTimeObject timeObject = DataCache.getTrade(item.getSymbol());
			if (timeObject == null) {
				timeObject = new TradeTimeObject();
			}
			timeObject.put(item.getSymbol(), trade.getData());
			DataCache.putTrade(item.getSymbol(), timeObject);
		}
	}

	public void setHobiDataService(HobiDataService hobiDataService) {
		this.hobiDataService = hobiDataService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

}