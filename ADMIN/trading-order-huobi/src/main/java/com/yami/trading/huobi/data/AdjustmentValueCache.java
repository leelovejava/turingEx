package com.yami.trading.huobi.data;

import com.yami.trading.huobi.data.model.AdjustmentValue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 行情调整值缓存类
 * 
 * 该类用于存储和管理行情调整值,支持两种生效模式:
 * 1. 即时生效模式: 调整值立即应用到行情价格
 * 2. 延时生效模式: 调整值在指定时间内分批逐步应用
 * 
 * 注意: 该缓存是静态内存缓存,系统重启后数据会丢失。
 * 建议在系统启动时从数据库加载调整值到该缓存。
 * 
 * @author System
 */
public class AdjustmentValueCache {
    /**
     * 当前生效的调整值缓存
     * 
     * Key: 交易品种代码(如 BTCUSDT, ETHUSDT)
     * Value: 调整值(正数表示上调,负数表示下调)
     * 
     * 该缓存中的调整值会立即应用到行情价格上。
     * 调整值同时影响收盘价(close)、买价(ask)、卖价(bid)。
     */
    private static volatile Map<String, Double> currentValue = new ConcurrentHashMap();

    /**
     * 延时生效的调整值缓存
     * 
     * Key: 交易品种代码(如 BTCUSDT, ETHUSDT)
     * Value: AdjustmentValue 对象,包含剩余调整值和剩余生效时间
     * 
     * 延时调整值会在指定的时间内分批逐步应用:
     * - 每次采集时应用一部分调整值
     * - 更新剩余调整值和剩余时间
     * - 直到调整值全部应用完毕
     * 
     * 这种方式可以实现价格的平滑过渡,避免价格突然大幅波动。
     */
    private static volatile Map<String, AdjustmentValue> delayValue = new ConcurrentHashMap();

    /**
     * 插针应用完毕后需要执行的持续时间（秒）缓存
     * Key: 交易品种代码
     * Value: 持续时间（秒），插针分批应用完成后开始计时
     */
    private static volatile Map<String, Double> pendingDurationSecond = new ConcurrentHashMap();

    public static Map<String, Double> getCurrentValue() {
        return currentValue;
    }

    public static Map<String, AdjustmentValue> getDelayValue() {
        return delayValue;
    }

    public static Map<String, Double> getPendingDurationSecond() {
        return pendingDurationSecond;
    }

}
