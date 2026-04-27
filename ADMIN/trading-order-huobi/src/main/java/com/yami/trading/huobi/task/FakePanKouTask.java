package com.yami.trading.huobi.task;


import com.google.common.collect.Lists;
import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DepthTimeObject;
import com.yami.trading.service.etf.EtfSecKLineService;
import com.yami.trading.service.etf.MarketService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Component
//@Lazy(value = false)
@Slf4j
public class FakePanKouTask {
    @Autowired
    private DepthPushJob depthPushJob;
    @Autowired
    private ItemService itemService;
    @Autowired
    private MarketService marketService;

    @Autowired
    private EtfSecKLineService etfSecKLineService;

    private boolean isFirstInit = true;
    @Scheduled(cron = "*/20 * * * * ?")
    public void deleteOverdueEtfKLine() throws InterruptedException {
        etfSecKLineService.deleteOverdueEtfKLine();

    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void sendTask() throws InterruptedException {
        List<Item> symbols = new ArrayList<>(itemService.list()).stream().filter(i -> "1".equalsIgnoreCase(i.getFake())).collect(Collectors.toList());
        List<Depth> pankous = Lists.newArrayList();
        for (Item item : symbols) {
            if( !isFirstInit  && !MarketOpenChecker.isMarketOpenByItemCloseType(item.getOpenCloseType())){
                continue;
            }
            Depth d = marketService.queryDepth(item.getSymbol());
            if (d != null) {
                DepthTimeObject timeObject = new DepthTimeObject();
                timeObject.setDepth(d);
                DataCache.putDepth(d.getSymbol(), timeObject);
                pankous.add(d);
                DataCache.depthToTrade(d);
            }
            isFirstInit = false;
        }


//        Set<String> symbolSet = pankous.stream().map(Depth::getSymbol).collect(Collectors.toSet());
//        Map<String, String> depthResultMap = new HashMap<>();
//        // 数据处理
//        ResultObject depthResult = new ResultObject();
//        for (String symbol : symbolSet) {
//            DepthTimeObject depth = DataCache.getDepth().get(symbol);
//            if (null != depth && null != depth.getDepth()) {
//                Depth depthData = depth.getDepth();
//                Realtime realtime = DataCache.getRealtime(symbol);
//                if (realtime == null) {
//                    continue;
//                }
//                depthResult.setData(depthPushJob.depthRevise(depthData, symbol, realtime.getClose().doubleValue()));
//            }
//            depthResultMap.put(symbol, JSONObject.toJSONString(depthResult));
//        }
//
//        if (depthResultMap.isEmpty()) {
//            return;
//        }
//        ConcurrentHashMap.KeySetView<String, WebSocketSession> stringWebSocketSessionKeySetView = WebSocketServer.depthMap.keySet();
//        for (String socketKey : stringWebSocketSessionKeySetView) {
//            WebSocketSession webSocketSession = WebSocketServer.depthMap.get(socketKey);
//            // WebSocketServer server = WebSocketServer.depthMap.get(socketKey);
//            String type = webSocketSession.getType();
//            String symbolKey = webSocketSession.getParam();
//            String message = depthResultMap.get(symbolKey);
//            if(StringUtils.isEmptyString(message)){
//                continue;
//            }
//            WebSocketServer.sendToMessageById(socketKey, message, type);
//        }

    }
}
