package com.yami.trading.huobi.task;

import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.data.domain.Trade;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DepthTimeObject;
import com.yami.trading.huobi.data.internal.TradeTimeObject;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 加密货币 定时器
 *
 * @author Jerry
 * @date 2023/11/12
 */
@Component
@Lazy(value = false)
@Slf4j
public class CryptoCurrencyDataTask {
    @Resource
    ItemService itemService;

    @Resource
    HobiDataService hobiDataService;

    private volatile boolean isHuobiInit = false;

    /**
     * 深度数据、近期交易记录 火币采集任务
     */
    @Async
    @Scheduled(cron = "*/3 * * * * ?")
    public void DepthTradeCollectionTask() {
        try {
            List<Item> items = itemService.findByType(Item.cryptos);
            for (Item item : items) {
                // 暂不使用
//                boolean isOpen = MarketOpenChecker.isMarketOpenByItemCloseType(item.getOpenCloseType());
//                if (isHuobiInit && !isOpen) {
//                    log.warn("---> CryptoCurrencyDataTask.DepthTradeCollectionTask 当前 symbol:{} 对应的股票处于未开盘状态，" +
//                            "忽略采集的深度数据", item.getSymbol());
//                    continue;
//                }

                collection(item);
            }
            isHuobiInit = true;
        } catch (Throwable e) {
            log.error("深度数据、近期交易记录 火币采集任务，异常信息：", e);
        }
    }

    private void collection(Item item) {
        Depth depth = hobiDataService.depthDecorator(item.getRemarks(), 0);
        if (depth != null) {
            DepthTimeObject timeObject = new DepthTimeObject();
            timeObject.setDepth(depth);
            DataCache.putDepth(item.getSymbol(), timeObject);
        }

        Trade trade = hobiDataService.tradeDecorator(item.getRemarks(), 0);
        if (trade != null) {
            TradeTimeObject timeObject = new TradeTimeObject();
            timeObject.put(item.getSymbol(), trade.getData());
            DataCache.putTrade(item.getSymbol(), timeObject);
        }
    }

}
