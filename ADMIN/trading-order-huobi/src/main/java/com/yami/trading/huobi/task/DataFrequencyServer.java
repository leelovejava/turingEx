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

/**
 * DataFrequencyServer - 虚拟货币交易数据频率服务
 *
 * 功能说明：
 * 该服务负责定时获取虚拟货币的交易行情数据，并缓存到内存中供其他模块使用
 * 主要采集买卖盘口、成交记录等实时交易数据
 *
 * 数据流向：
 * 外部数据源 → HobiDataService.tradeDecorator() → DataCache缓存 → 供WebSocket推送使用
 *
 * 处理范围：仅处理虚拟货币（Item.cryptos）类型的数据
 */
@Component
@Slf4j
public class DataFrequencyServer implements Runnable {

	@Autowired
	private HobiDataService hobiDataService;

	@Autowired
	private ItemService itemService;

	/**
	 * 启动数据频率服务
	 *
	 * 创建一个独立线程来运行数据采集任务
	 * 线程名称为 "DataFrequencyServer"
	 */
	public void start() {
		new Thread(this, "DataFrequencyServer").start();
		if (log.isInfoEnabled()) log.info("启动DataFrequencyServer！");
	}

	/**
	 * 核心运行方法 - 定时采集虚拟货币交易数据
	 *
	 * 执行流程：
	 * 1. 从数据库获取所有虚拟货币品种列表
	 * 2. 遍历每个虚拟货币品种，调用trade()获取交易数据
	 * 3. 每个品种处理完成后休眠3秒，避免请求过于频繁
	 * 4. 整个品种列表处理完成后休眠10秒，然后重新开始
	 *
	 * 异常处理：
	 * - 单个品种处理异常不会影响其他品种，继续处理下一个
	 * - 整个循环异常会记录日志，但不会停止服务
	 */
	public void run() {
		while (true) {
			try {
				// 获取所有虚拟货币品种列表
				List<Item> item_list = new ArrayList<>(itemService.list())
						.stream()
						.filter(i->i.getType().equalsIgnoreCase(Item.cryptos))
						.collect(Collectors.toList());

				// 遍历每个虚拟货币品种
				for (int i = 0; i < item_list.size(); i++) {
					try {
						Item item = item_list.get(i);
						// 获取该品种的交易数据
						this.trade(item);
					} catch (Exception e) {
						// 单个品种处理异常，记录日志但不中断其他品种
						log.error("trade fail", e);
					} finally {
						// 每个品种处理完成后休眠3秒，避免请求频率过高
						ThreadUtils.sleep(3000);
					}
				}
			} catch (Throwable e) {
				// 整体循环异常，记录日志但不停止服务
				log.error("DataFrequencyServer fail", e);
			} finally {
				// 整个品种列表处理完成后休眠10秒，然后重新开始采集
				ThreadUtils.sleep(1000 * 10);
			}
		}
	}

	/**
	 * 获取并缓存单个品种的交易数据
	 *
	 * 处理逻辑：
	 * 1. 调用HobiDataService获取装饰后的交易数据（包含买卖盘口信息）
	 * 2. 从DataCache获取该币种的现有交易时间对象
	 * 3. 更新交易数据到TradeTimeObject中
	 * 4. 重新放入DataCache缓存
	 *
	 * @param item 交易品种对象
	 */
	private void trade(Item item) {
		// 调用数据服务获取交易数据（包含盘口数据）
		Trade trade = hobiDataService.tradeDecorator(item.getRemarks(), 0);
		if (trade != null) {
			// 从缓存获取该币种的交易时间对象
			TradeTimeObject timeObject = DataCache.getTrade(item.getSymbol());
			if (timeObject == null) {
				// 如果缓存中没有，则创建新的对象
				timeObject = new TradeTimeObject();
			}
			// 更新交易数据
			timeObject.put(item.getSymbol(), trade.getData());
			// 重新放入缓存
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