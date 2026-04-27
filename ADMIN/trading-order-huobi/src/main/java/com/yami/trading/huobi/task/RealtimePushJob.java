package com.yami.trading.huobi.task;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.data.domain.StockMarket;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.TimeZoneConverterService;
import com.yami.trading.huobi.websocket.WebSocketServer;
import com.yami.trading.huobi.websocket.WebSocketSession;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 行情数据推送Job
 */
@Component
@Slf4j
public class RealtimePushJob implements Runnable {
    @Autowired
    private ItemService itemService;

    @Autowired
    private TimeZoneConverterService timeZoneConverterService;

    public void start() {
        new Thread(this, "realtimePushJob").start();
        log.info("启动realtimePushJob！");
    }

    @Override
    public void run() {

        while (true) {
            try {
                this.realtimeHandle();
            } catch (Exception e) {
                log.error("run fail", e);
            } finally {
                ThreadUtils.sleep(1000);
            }
        }
    }

    private void realtimeHandle() {
        try {
            Map<String, String> realtimeResultMap = new HashMap<>();

            // 行情实时价格
            if (!WebSocketServer.realtimeMap.isEmpty()) {

                // 客户端请求的所有币种，去重集合
                Set<String> symbolSet = new HashSet<String>();
                for (String socketKey : WebSocketServer.realtimeMap.keySet()) {
                    WebSocketSession webSocketSession = WebSocketServer.realtimeMap.get(socketKey);
                    String symbolKey = webSocketSession.getParam();
                    symbolSet.add(symbolKey);
                }

                for (String symbol : symbolSet) {
                    Realtime realtimeData = DataCache.getRealtime(symbol);
                    if (realtimeData == null) {
                        log.error("realtimeHandle 获取{} 数据为空", symbol);
                    }
                    this.realtimeRevise(realtimeResultMap, realtimeData, symbol);
                }

                if (realtimeResultMap.isEmpty()) {
                    return;
                }

                for (String socketKey : WebSocketServer.realtimeMap.keySet()) {
//					long timeMillins = System.currentTimeMillis();
                    //WebSocketServer server = WebSocketServer.realtimeMap.get(socketKey);
//					if (server.getTimeStr() != 0 && timeMillins > server.getTimeStr()) {
//						server.onClose();
//						return;
//					}
                    WebSocketSession webSocketSession = WebSocketServer.realtimeMap.get(socketKey);

                    String type = webSocketSession.getType();
                    String symbolKey = webSocketSession.getParam();
                    WebSocketServer.sendToMessageById(socketKey, realtimeResultMap.get(symbolKey), type);
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    /**
     * 行情实时价格解析
     */
    private void realtimeRevise(Map<String, String> realtimeResultMap, Realtime realtime, String symbol) {

        ResultObject realtimeResult = new ResultObject();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (realtime == null) {
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Item bySymbol = itemService.findBySymbol(symbol);

        Integer decimal = itemService.getDecimal(symbol);

        map.put("symbol", symbol);
        // 是否需要做时区转换？ TODO
        map.put("timestamp", realtime.getTs());
        map.put("current_time", realtime.getCurrentTime());
        map.put("name", bySymbol.getSourceName());
        map.put("enName", bySymbol.getEnName());

        map.put("change_ratio", realtime.getChangeRatio());
        map.put("netChange", realtime.getNetChange());
        map.put("open", realtime.getOpen());
        map.put("close", realtime.getClose());
        map.put("high", realtime.getHigh());
        map.put("low", realtime.getLow());
        map.put("volume", realtime.getVolume());
        map.put("amount", realtime.getAmount());
        map.put("chg", realtime.getChg());
        map.put("percent", realtime.getPercent());
        map.put("pb", realtime.getPb());
        map.put("navps", realtime.getNavps());
        map.put("turnoverRate", realtime.getTurnoverRate());
        map.put("amplitude", realtime.getAmplitude());
        map.put("eps", realtime.getEps());
        map.put("marketCapital", realtime.getMarketCapital());

//        if( realtime.getVolume() != null){
//            map.put("volume", realtime.getVolume().setScale(2, RoundingMode.HALF_UP));
//        }else{
//            map.put("volume", null);
//        }
//        if( realtime.getAmount() != null){
//            map.put("amount", realtime.getAmount().setScale(2, RoundingMode.HALF_UP));
//        }else{
//            map.put("amount", null);
//        }
//        if( realtime.getChg() != null){
//            map.put("chg", realtime.getChg().setScale(2, RoundingMode.HALF_UP));
//        }else{
//            map.put("chg", null);
//        }
//        if( realtime.getPercent() != null){
//            map.put("percent", realtime.getPercent().setScale(2, RoundingMode.HALF_UP));
//        }else{
//            map.put("percent", null);
//        }
//        if( realtime.getPb() != null){
//            map.put("pb", realtime.getPb().setScale(2, RoundingMode.HALF_UP));
//        }else{
//            map.put("pb", null);
//        }
//        if( realtime.getNavps() != null){
//            map.put("navps", realtime.getNavps().setScale(2, RoundingMode.HALF_UP));
//        }else{
//            map.put("navps", null);
//        }
//        if( realtime.getTurnoverRate() != null){
//            map.put("turnover_rate", realtime.getTurnoverRate().setScale(2, RoundingMode.HALF_UP));
//        }else{
//            map.put("turnover_rate", null);
//        }
//        if( realtime.getAmplitude() != null){
//            map.put("amplitude", realtime.getAmplitude().setScale(2, RoundingMode.HALF_UP));
//        }else{
//            map.put("amplitude", null);
//        }
//        if( realtime.getEps() != null){
//            map.put("eps", realtime.getEps().setScale(2, RoundingMode.HALF_UP));
//        }else{
//            map.put("eps", null);
//        }
        map.put("ask", realtime.getAsk());
        map.put("bid", realtime.getBid());
        StockMarket market = DataCache.getMarket(symbol);
        if ("1".equals(bySymbol.getFake())) {
            // 假ETF默认取美股
            market = DataCache.getMarket("AAPL");
        }
        if (market == null) {
            market = new StockMarket();
            market.setTime_zone(timeZoneConverterService.getTimeZoneByItemCloseType(bySymbol.getOpenCloseType()));
            if (MarketOpenChecker.isMarketOpenByItemCloseType(bySymbol.getOpenCloseType())) {
                market.setStatus("交易中");
            } else {
                market.setStatus("未开盘");
            }
            market.calculate();
            map.put("market", market);
        } else {
            market.calculate();
            map.put("market", market);
        }
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(realtime);
        for (String key : stringObjectMap.keySet()) {
            if (!map.containsKey(key)) {
                map.put(key, stringObjectMap.get(key));
                BigDecimal open = BigDecimal.valueOf(realtime.getOpen());
                map.put("open", open != null ? open.setScale(decimal, RoundingMode.HALF_UP) : null);
                BigDecimal close = BigDecimal.valueOf(realtime.getClose());
                map.put("close", close != null ? close.setScale(decimal, RoundingMode.HALF_UP) : null);
                BigDecimal high = BigDecimal.valueOf(realtime.getHigh());
                map.put("high", high != null ? high.setScale(decimal, RoundingMode.HALF_UP) : null);
                BigDecimal low = BigDecimal.valueOf(realtime.getLow());
                map.put("low", low != null ? low.setScale(decimal, RoundingMode.HALF_UP) : null);
            }
        }
        map.put("type", bySymbol.getType());

        list.add(map);
        realtimeResult.setData(list);
        realtimeResultMap.put(realtime.getSymbol(), JSONObject.toJSONString(realtimeResult));
    }

}
