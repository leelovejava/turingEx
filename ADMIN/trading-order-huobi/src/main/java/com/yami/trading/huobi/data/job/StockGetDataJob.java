package com.yami.trading.huobi.data.job;

import cn.hutool.core.util.StrUtil;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.*;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.huobi.hobi.internal.SpiderService;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StockGetDataJob extends AbstractGetDataJob {

    private static Logger logger = LoggerFactory.getLogger(StockGetDataJob.class);

    public static volatile boolean first = true;

    public static volatile boolean stockFirstFetch = true;
    @Autowired
    private HobiDataService hobiDataService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private SpiderService spiderService;
    @Override
    public void start() {
        new Thread(this, "StockGetDataJob").start();
    }

    @Override
    public void run() {

        if (first) {
            // data数据保存间隔时长(毫秒)
            this.interval = 3000;
            first = false;
        }
        while (true) {
            try {
                List<Item> list = new ArrayList<>(itemService.list()).stream().filter(i->"0".equalsIgnoreCase(i.getFake())).collect(Collectors.toList());
                // etf 和A股开盘时间是一样的
                String aStockRemarks = list.stream().filter(item -> item.isActive() &&  item.getOpenCloseType() != null &&  item.getOpenCloseType().equalsIgnoreCase(Item.A_STOCKS))
                        .map(Item::getSymbol).collect(Collectors.joining(","));
                String hkStockRemarks = list.stream().filter(item -> item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.HK_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String usStockRemarks = list.stream().filter(item ->  item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.US_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String twStockRemarks = list.stream().filter(item ->  item.isActive() && item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.TW_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String jpStockRemarks = list.stream().filter(item ->  item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.JP_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String indiaStockRemarks = list.stream().filter(item ->  item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.INDIA_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String ukStockRemarks = list.stream().filter(item ->  item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.UK_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String deStockRemarks = list.stream().filter(item ->  item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.DE_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String bzStockRemarks = list.stream().filter(item ->  item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.BZ_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String cadStockRemarks = list.stream().filter(item ->  item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.CAD_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String frStockRemarks = list.stream().filter(item ->  item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.FR_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String sgStockRemarks = list.stream().filter(item ->  item.isActive() &&  item.getOpenCloseType() != null &&item.getOpenCloseType().equalsIgnoreCase(Item.SG_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));




                if(stockFirstFetch){
                    this.realtimeHandle(aStockRemarks);
                    this.realtimeHandle(hkStockRemarks);
                    this.realtimeHandle(usStockRemarks);
                    this.realtimeHandleTW(twStockRemarks);
                    this.realtimeHandleCommon(jpStockRemarks);
                    this.realtimeHandleCommon(indiaStockRemarks);
                    this.realtimeHandleCommon(ukStockRemarks);
                    this.realtimeHandleCommon(deStockRemarks);
                    this.realtimeHandleCommon(bzStockRemarks);
                    this.realtimeHandleCommon(cadStockRemarks);
                    this.realtimeHandleCommon(frStockRemarks);
                    this.realtimeHandleCommon(sgStockRemarks);

                    stockFirstFetch = false;
                }
                if(MarketOpenChecker.isMarketOpen(Item.A_STOCKS, 30)){
                    this.realtimeHandle(aStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.HK_STOCKS, 30)){
                    this.realtimeHandle(hkStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.US_STOCKS, 30)){
                    this.realtimeHandle(usStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.TW_STOCKS, 30)){
                    this.realtimeHandleTW(twStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.JP_STOCKS, 30)){
                    this.realtimeHandleCommon(jpStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.INDIA_STOCKS, 30)){
                    this.realtimeHandleCommon(indiaStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.UK_STOCKS, 30)){
                    this.realtimeHandleCommon(ukStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.DE_STOCKS, 30)){
                    this.realtimeHandleCommon(deStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.BZ_STOCKS, 30)){
                    this.realtimeHandleCommon(bzStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.CAD_STOCKS, 30)){
                    this.realtimeHandleCommon(cadStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.FR_STOCKS, 30)){
                    this.realtimeHandleCommon(frStockRemarks);
                }
                if(MarketOpenChecker.isMarketOpen(Item.SG_STOCKS, 30)){
                    this.realtimeHandleCommon(frStockRemarks);
                }
//                String emptyRealTimeSymbols = list.stream().filter(i -> DataCache.getRealtime(i.getSymbol()) == null).map(Item::getSymbol).collect(Collectors.joining(","));
//                if(StringUtils.isNotEmpty(emptyRealTimeSymbols)){
//                    realtimeHandle(emptyRealTimeSymbols);
//                }
            } catch (Exception e) {
                logger.error("run fail", e);
            } finally {
                ThreadUtils.sleep(this.interval);
            }
        }

    }

    @Override
    public String getName() {
        return "股票实时数据采集";
    }

    public void realtimeHandleCommon(String remarks ) {
        if (StrUtil.isEmpty(remarks)) {
            log.error("当前没有行情数据可以采集");
            return;
        }
        List<Realtime> realtimeList = this.spiderService.fetchRealtimeList(remarks);
        if(stockFirstFetch){
            log.info("首次采集，采集回数据[{}]个", realtimeList.size());
        }
        super.handleRealTimeList(realtimeList);

    }

    public void realtimeHandleTW(String remarks ) {
        if (StrUtil.isEmpty(remarks)) {
            log.error("台股当前没有行情数据可以采集");
            return;
        }
        List<Realtime> realtimeList = this.spiderService.fetchRealtimeList(remarks);
        super.handleRealTimeList(realtimeList);

    }
    @Override
    public void realtimeHandle(String remarks ) {
        if (StrUtil.isEmpty(remarks)) {
            log.error("当前没有行情数据可以采集");
            return;
        }
        List<Realtime> realtimeList = this.spiderService.fetchRealtimeList(remarks);
        super.handleRealTimeList(realtimeList);
    }
}
