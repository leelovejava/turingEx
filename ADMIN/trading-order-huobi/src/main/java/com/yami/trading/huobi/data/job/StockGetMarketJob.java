package com.yami.trading.huobi.data.job;

import cn.hutool.core.util.StrUtil;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.data.domain.StockMarket;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.huobi.hobi.internal.SpiderService;
import com.yami.trading.huobi.hobi.internal.XueQiuDataServiceImpl;
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
public class StockGetMarketJob extends AbstractGetDataJob {

    private static Logger logger = LoggerFactory.getLogger(StockGetMarketJob.class);

    public static volatile boolean first = true;
    @Autowired
    private HobiDataService hobiDataService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private SpiderService spiderService;

    @Override
    public void start() {
        new Thread(this, "StockGetMarketJob").start();
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
                List<Item> list = new ArrayList<>(itemService.list()).stream().filter(i ->i.isActive() && "0".equalsIgnoreCase(i.getFake())).collect(Collectors.toList());
                // etf 和A股开盘时间是一样的
                String aStocSymbols = list.stream().filter(item -> item.getOpenCloseType() != null && item.getOpenCloseType().equalsIgnoreCase(Item.A_STOCKS)).map(Item::getSymbol).collect(Collectors.joining(","));
                String hkStocSymbols = list.stream().filter(item -> item.getOpenCloseType() != null && item.getOpenCloseType().equalsIgnoreCase(Item.HK_STOCKS)).map(Item::getSymbol).collect(Collectors.joining(","));
                String usStocSymbols = list.stream().filter(item -> item.getOpenCloseType() != null && item.getOpenCloseType().equalsIgnoreCase(Item.US_STOCKS)).map(Item::getRemarks).collect(Collectors.joining(","));
                String symbols = aStocSymbols + "," + hkStocSymbols + "," + usStocSymbols;
                List<StockMarket> markets = spiderService.getMarkets(symbols);
                markets.forEach(m -> DataCache.putMarket(itemService.getSymbolByRemarks(m.getSymbol()) , m));

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


    @Override
    public void realtimeHandle(String symbols) {
        if (StrUtil.isEmpty(symbols)) {
            log.error("当前没有行情数据可以采集");
            return;
        }
        List<Realtime> realtimeList = this.hobiDataService.realtime(symbols);
        super.handleRealTimeList(realtimeList);
    }
}
