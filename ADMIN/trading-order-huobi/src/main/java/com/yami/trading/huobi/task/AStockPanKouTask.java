package com.yami.trading.huobi.task;

import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DepthTimeObject;
import com.yami.trading.huobi.hobi.internal.SpiderService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Lazy(value = false)
@Slf4j
public class AStockPanKouTask {
    @Autowired
    private ItemService itemService;

    @Autowired
    private SpiderService spiderService;

    private volatile boolean isAstockInit = false;

    @Scheduled(cron = "*/5 * * * * ?")
    public void sendTask() {
        String remarks = new ArrayList<Item>(itemService.list()).stream().filter(t-> t.isActive() && Item.A_STOCKS.equalsIgnoreCase(t.getOpenCloseType())).map(Item::getRemarks).collect(Collectors.joining(","));
        List<Depth> pankous = spiderService.pankous(remarks);
        pankous.stream().forEach(d -> {
            String symbol = d.getSymbol();
            // 暂不使用
//            Item curItem = itemService.findBySymbol(symbol);
//            if (curItem == null) {
//                return;
//            }
//            boolean isOpen = MarketOpenChecker.isMarketOpenByItemCloseType(curItem.getOpenCloseType());
//            if (isAstockInit && !isOpen) {
//                log.warn("---> AStockPanKouTask.sendTask 当前 symbol:{} 对应的股票处于未开盘状态，忽略采集的深度数据", symbol);
//                return;
//            }

            DepthTimeObject timeObject = new DepthTimeObject();
            timeObject.setDepth(d);

            DataCache.putDepth(itemService.getSymbolByRemarks(symbol), timeObject);
        });

        isAstockInit = true;
    }
}
