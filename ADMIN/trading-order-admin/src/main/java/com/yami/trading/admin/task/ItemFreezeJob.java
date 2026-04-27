package com.yami.trading.admin.task;

import com.yami.trading.service.item.ItemService;
import com.yami.trading.admin.facade.ItemVisitFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ItemFreezeJob {
    @Autowired
    private ItemVisitFacade itemVisitFacade;

    @Autowired
    private ItemService itemService;

    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void freezeItem() {
        log.info("正在扫描需要解冻的币对");
        List<String> symbolsOlderThanNDay = itemVisitFacade.getSymbolsOlderThanNDay(7);
        for (String symbol : symbolsOlderThanNDay){
            itemService.crawFreeze(symbol);
        }

    }


}
