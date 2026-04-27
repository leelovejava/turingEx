package com.yami.trading.common.util;

import com.yami.trading.common.constants.RedisKeys;

public class RealTimeUtil {

    public static String getKlineInfoCacheKey(String symbol, String line) {
        return RedisKeys.SORTED_KLINE_INFO + symbol + ":" + line;
    }

    public static String getKlineDataCacheKey(String symbol, String line) {
        return RedisKeys.MAP_KLINE_DATA + symbol + ":" + line;
    }

    public static String getKlineInfoElementKey(String symbol, String line, String klineId) {
        return symbol + ":" + line + ":" + klineId;
    }

    public static String getRealtimeInfoCacheKey(String symbol) {
        return RedisKeys.SORTED_REALTIME_INFO + symbol;
    }

    public static String getRealtimeDataCacheKey(String symbol) {
        return RedisKeys.MAP_REALTIME_DATA + symbol;
    }

    public static String getRealtimeInfoElementKey(String symbol, String realtimeId) {
        return symbol + ":" + realtimeId;
    }

}
