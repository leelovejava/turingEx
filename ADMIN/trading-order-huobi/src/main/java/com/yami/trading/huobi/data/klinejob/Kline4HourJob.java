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
 * 4小时K线
 */
@Slf4j
@Component
public class Kline4HourJob {
    @Autowired
    private KlineService klineService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CryptosKlineService cryptosKlineService;
    @Autowired
    private KlineContext klineContext;
    @Scheduled(cron = "0 0 */4 ? * *")
    public void taskJob() {
        StopWatch stopWatch = new StopWatch();
        log.info("Kline4HourJob start ... ");
        stopWatch.start();
        List<Item> item_list = new ArrayList<>(itemService.list());
        for (int i = 0; i < item_list.size(); i++) {
            Item item = item_list.get(i);
            if (!klineContext.isInitSuccess(item.getSymbol())) {
                continue;
            }
            if (MarketOpenChecker.isMarketOpenByItemCloseType(item.getOpenCloseType())) {
                if (Item.cryptos.equalsIgnoreCase(item.getType())) {
                    cryptosKlineService.saveOne(item.getSymbol(), Kline.PERIOD_4HOUR);
                } else {
                    klineService.saveKline4Hour(item.getSymbol(), Kline.PERIOD_4HOUR);
                }
            }
        }
        stopWatch.stop();
        log.info("Kline4HourJob end ,耗时 {} ", stopWatch.getTotalTimeMillis());
    }

}
