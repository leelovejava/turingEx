package com.yami.trading.huobi.data.klinejob;

import cn.hutool.core.date.StopWatch;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.huobi.data.internal.CryptosKlineService;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.huobi.task.KlineContext;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 1天K线
 */
@Component
@Slf4j
public class Kline1DayJob {
    @Autowired
    private KlineService klineService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CryptosKlineService cryptosKlineService;
    @Autowired
    private KlineContext klineContext;

    @Scheduled(cron = "0 0 0 * * ?")
    public void taskJob() {
        StopWatch stopWatch = new StopWatch();
        log.info("Kline1DayJob start ... ");
        stopWatch.start();
        List<Item> item_list = new ArrayList<>(itemService.list());
        for (int i = 0; i < item_list.size(); i++) {
            Item item = item_list.get(i);
            if(!klineContext.isInitSuccess(item.getSymbol())){
                continue;
            }
            if (MarketOpenChecker.isMarketOpenByItemCloseType(item.getOpenCloseType())) {
                if (Item.cryptos.equalsIgnoreCase(item.getType())) {
                    cryptosKlineService.saveOne(item.getSymbol(), Kline.PERIOD_1DAY);
                } else {
                    klineService.saveKline1Day(item.getSymbol(), Kline.PERIOD_1DAY);
                }
            }
        }
        stopWatch.stop();
        log.info("Kline1DayJob end ,耗时 {} ", stopWatch.getTotalTimeMillis());
    }

    /**
     * 股票每天下午5点计算当日数据
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void taskStockJob() {
        StopWatch stopWatch = new StopWatch();
        log.info("Kline1DayJob start ... ");
        stopWatch.start();
        List<Item> item_list = new ArrayList<>(itemService.list());
        for (int i = 0; i < item_list.size(); i++) {
            Item item = item_list.get(i);
            if(!klineContext.isInitSuccess(item.getSymbol())){
                continue;
            }
            if (!Item.cryptos.equalsIgnoreCase(item.getOpenCloseType()) && MarketOpenChecker.isMarketOpen(item.getOpenCloseType(), 180)) {
                    klineService.saveKline1Day(item.getSymbol(), Kline.PERIOD_1DAY);
            }

        }
        stopWatch.stop();
        log.info("Kline1DayJob end ,耗时 {} ", stopWatch.getTotalTimeMillis());
    }

}
