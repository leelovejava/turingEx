package com.yami.trading.huobi.task;


import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.huobi.hobi.internal.SpiderService;
import com.yami.trading.huobi.hobi.internal.TWDataServiceImpl;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@Lazy(value = false)
@Slf4j
public class AStockTradeListTask {
    @Autowired
    private DepthPushJob depthPushJob;
    @Autowired
    private ItemService itemService;
    @Autowired
    private SpiderService spiderService;
    @Autowired
    private TWDataServiceImpl twDataService;
    private volatile  boolean isAStockInit = false;
    private volatile  boolean isUsStockInit = false;

    private volatile  boolean isTwStockInit = false;
    @Scheduled(cron = "*/5 * * * * ?")
    public void sendTask() throws InterruptedException {
        if(!isAStockInit){
            fetchAStock();
            isAStockInit = true;
        }
        if(MarketOpenChecker.isMarketOpenByItemCloseType(Item.A_STOCKS)){
            fetchAStock();
        }

        if(!isUsStockInit){
            fetchUs();

            isUsStockInit = true;
        }
        if(MarketOpenChecker.isMarketOpenByItemCloseType(Item.US_STOCKS)){
            fetchUs();

        }


        if(!isTwStockInit){
            fetchTW();

            isTwStockInit = true;
        }
        if(MarketOpenChecker.isMarketOpenByItemCloseType(Item.TW_STOCKS)){
            fetchTW();

        }

    }

    private void fetchAStock() {
        String remarks = new ArrayList<>(itemService.list()).stream().filter(t->t.isActive() && Item.A_STOCKS.equalsIgnoreCase(t.getOpenCloseType())).map(Item::getRemarks).collect(Collectors.joining(","));
        spiderService.tradeList(remarks, false);
    }

    private void fetchUs() {
        String remarks = new ArrayList<>(itemService.list()).stream().filter(t->t.isActive() && Item.US_STOCKS.equalsIgnoreCase(t.getOpenCloseType())).map(Item::getRemarks).collect(Collectors.joining(","));
        spiderService.tradeList(remarks, true);
    }
    private void fetchTW() {
        String remarks = new ArrayList<>(itemService.list()).stream().filter(t->t.isActive() && Item.TW_STOCKS.equalsIgnoreCase(t.getOpenCloseType())).map(Item::getRemarks).collect(Collectors.joining(","));
        spiderService.tradeList(remarks, true);
    }

}
