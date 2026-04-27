package com.yami.trading.huobi.data;

import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.data.domain.DepthEntry;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.data.domain.StockMarket;
import com.yami.trading.bean.data.domain.TradeDetails;
import com.yami.trading.bean.data.domain.TradeEntry;
import com.yami.trading.common.constants.RedisKeyConstants;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.huobi.data.internal.DepthTimeObject;
import com.yami.trading.huobi.data.internal.KlineTimeObject;
import com.yami.trading.huobi.data.internal.TradeTimeObject;
import com.yami.trading.huobi.data.internal.TrendTimeObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class DataCache {
    /**
     * 24小时的历史记录
     */
    private volatile static Map<String, List<Realtime>> cryptosRealtimeHistory = new ConcurrentHashMap<>();


    public static Realtime getLatestRealTime(String symbol) {
        return RedisUtil.get(RedisKeyConstants.LATEST_REALTIME + symbol);
    }

    public static void depthToTrade(Depth depth) {
        String symbol = depth.getSymbol();
        TradeTimeObject timeObject = DataCache.getTrade(symbol);
        if (timeObject == null) {
            timeObject = new TradeTimeObject();
        }

        List<TradeEntry> data = new ArrayList<>();
        List<DepthEntry> asks = depth.getAsks();
        List<DepthEntry> bids = depth.getBids();
        List<TradeEntry> sell = asks.stream().map(a -> {
            TradeEntry tradeEntry = new TradeEntry();
            tradeEntry.setDirection("sell");
            tradeEntry.setAmount(a.getAmount());
            tradeEntry.setPrice(a.getPrice());
            tradeEntry.setTs(depth.getTs());
            return tradeEntry;
        }).collect(Collectors.toList());
        List<TradeEntry> buy = bids.stream().map(a -> {
            TradeEntry tradeEntry = new TradeEntry();
            tradeEntry.setDirection("buy");
            tradeEntry.setAmount(a.getAmount());
            tradeEntry.setPrice(a.getPrice());
            tradeEntry.setTs(depth.getTs());
            return tradeEntry;
        }).collect(Collectors.toList());
        data.addAll(sell);
        data.addAll(buy);
        timeObject.put(symbol, data);
        DataCache.putTrade(symbol, timeObject);
    }

    public static KlineTimeObject getKline(String symbol, String line) {
        String key = symbol;
        if (!StringUtils.isBlank(line)) {
            key = key + "_" + line;
        }
        return RedisUtil.hGet(RedisKeyConstants.KLINE, key);
    }

    public static TrendTimeObject getTrend(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.KLINE, symbol);
    }

    public static void putTrend(String symbol, TrendTimeObject model) {
        RedisUtil.hSet(RedisKeyConstants.TREND, symbol, model);
    }

    public static void putKline(String symbol, String line, KlineTimeObject model) {
        String key = symbol;
        if (!StringUtils.isBlank(line)) {
            key = key + "_" + line;
        }
        RedisUtil.hSet(RedisKeyConstants.KLINE, key, model);
    }

    public static List<TradeDetails> getStockTradeList(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.TRADE_LIST, symbol);
    }

    public static void putStockTradeList(String symbol, List<TradeDetails> model) {
        RedisUtil.hSet(RedisKeyConstants.TRADE_LIST, symbol, model);
    }

    public static Realtime getRealtime(String symbol) {
        Realtime realtime = RedisUtil.hGet(RedisKeyConstants.LATEST_REALTIME, symbol);
        if (realtime == null) {
            if (StringUtils.isAllLowerCase(symbol)) {
                symbol = symbol.toUpperCase();
            } else if (StringUtils.isAllUpperCase(symbol)) {
                symbol = symbol.toLowerCase();
            }
            return RedisUtil.hGet(RedisKeyConstants.LATEST_REALTIME, symbol);
        } else {
            return realtime;
        }
    }

    public static void putLatestRealTime(String symbol, Realtime model) {
        RedisUtil.hSet(RedisKeyConstants.LATEST_REALTIME, symbol, model);
    }

    public static void putRealtime(String symbol, Realtime model) {
        RedisUtil.hSet(RedisKeyConstants.LATEST_REALTIME, symbol, model);
    }

    public static void putMarket(String symbol, StockMarket market) {
        RedisUtil.hSet(RedisKeyConstants.REAL_MARKET, symbol, market);
    }

    public static StockMarket getMarket(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.REAL_MARKET, symbol);
    }

    public static void putDepth(String symbol, DepthTimeObject model) {
        RedisUtil.hSet(RedisKeyConstants.DEPTH, symbol, model);
    }

    public static DepthTimeObject getDepth(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.DEPTH, symbol);
    }

    public static void putTrade(String symbol, TradeTimeObject model) {
        RedisUtil.hSet(RedisKeyConstants.TRADE, symbol, model);
    }

    public static TradeTimeObject getTrade(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.TRADE, symbol);
    }

    public static void putRealtimeHigh(String symbol, Double high) {
        RedisUtil.hSet(RedisKeyConstants.REALTIME_HIGH, symbol, high);
    }

    public static Double getRealtimeHigh(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.REALTIME_HIGH, symbol);
    }

    public static void putRealtimeLow(String symbol, Double low) {
        RedisUtil.hSet(RedisKeyConstants.REALTIME_LOW, symbol, low);
    }

    public static Double getRealtimeLow(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.REALTIME_LOW, symbol);
    }

    public static void putKlineHoBi(String symbol, Kline model) {
        RedisUtil.hSet(RedisKeyConstants.KLINE_HOBI, symbol, model);
    }

    public static Kline getKlineHoBi(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.KLINE_HOBI, symbol);
    }

    public static void putRealtime24HBeforeOpen(String symbol, Double open) {
        RedisUtil.hSet(RedisKeyConstants.REALTIME_24H_BEFORE_OPEN, symbol, open);
    }

    public static Double getRealtime24HBeforeOpen(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.REALTIME_24H_BEFORE_OPEN, symbol);
    }

    public static void putCryptosRealtimeHistory(String symbol, List<Realtime> realtimes) {
        DataCache.cryptosRealtimeHistory.put(symbol, realtimes);
    }

    public static List<Realtime> getCryptosRealtimeHistory(String symbol) {
        return DataCache.cryptosRealtimeHistory.get(symbol);
    }

    public static void clearCryptosRealtimeHistory(String symbol) {
        RedisUtil.hDel(RedisKeyConstants.CRYPTOS_REALTIME_HISTORY, symbol);
    }

    public static void putLatestRealTime60s(String symbol, List<Realtime> realtimes) {
        RedisUtil.hSet(RedisKeyConstants.LATEST_REALTIME_60S, symbol, realtimes);
    }

    public static void clearLatestRealTime60s(String symbol) {
        RedisUtil.hDel(RedisKeyConstants.LATEST_REALTIME_60S, symbol);
    }

    public static List<Realtime> getLatestRealTime60s(String symbol) {
        return RedisUtil.hGet(RedisKeyConstants.LATEST_REALTIME_60S, symbol);
    }
}
