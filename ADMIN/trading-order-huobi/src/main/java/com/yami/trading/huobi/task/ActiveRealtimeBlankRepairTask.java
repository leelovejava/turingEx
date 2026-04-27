package com.yami.trading.huobi.task;


import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.job.StockGetDataJob;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Lazy(value = false)
@Slf4j
public class ActiveRealtimeBlankRepairTask {
    @Autowired
    private StockGetDataJob stockGetDataJob;

    @Autowired
    private ItemService itemService;
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;
    @Scheduled(cron = "*/3 * * ? * *")
    @Async
    public void doStockTask() throws InterruptedException {
        log.debug("ActiveRealtimeBlankRepairTask 正在对已经激活的，重新补充实时价格数据");
        List<Item> stocks = itemService.listManualActive();
        for(Item item  : stocks){
            Realtime realtime = DataCache.getRealtime(item.getSymbol());
            if(realtime == null){
                log.info("ActiveRealtimeBlankRepairTask  重新补充实时价格数据 :{}", item.getSymbol());
                stockGetDataJob.realtimeHandle(item.getRemarks());
            }
        }


    }





}
