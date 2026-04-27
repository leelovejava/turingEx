package com.yami.trading.huobi.data.klinejob;

import cn.hutool.core.date.StopWatch;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.item.domain.Item;
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
public class Kline5DayJob {
    @Autowired
    private KlineService klineService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private KlineContext klineContext;
    @Scheduled(cron = "0 0 0 */5 * ?")
    public void taskJob() {
        StopWatch stopWatch = new StopWatch();
        log.info("Kline5DayJob start ... ");
        stopWatch.start();
        List<Item> item_list = new ArrayList<>(itemService.list());
        for (int i = 0; i < item_list.size(); i++) {
            Item item = item_list.get(i);
            if(!klineContext.isInitSuccess(item.getSymbol())){
                continue;
            }
            if (!Item.cryptos.equalsIgnoreCase(item.getType())) {
                klineService.saveKline5Day(item.getSymbol(), Kline.PERIOD_5DAY);
            }

        }
        stopWatch.stop();
        log.info("Kline15DayJob end ,耗时 {} ", stopWatch.getTotalTimeMillis());
    }

}
