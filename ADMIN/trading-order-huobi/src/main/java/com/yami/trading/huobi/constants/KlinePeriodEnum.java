package com.yami.trading.huobi.constants;

import cn.hutool.core.util.StrUtil;

public enum KlinePeriodEnum {

    PERIOD_1MIN("1min", 60L, 2880, 300),

    PERIOD_5MIN("5min", 300L, 1152, 200),

    PERIOD_15MIN("15min", 900L, 576, 200),

    PERIOD_30MIN("30min", 1800L, 576, 200),

    PERIOD_60MIN("60min", 3600L, 576, 200),

    PERIOD_2HOUR("120min", 7200L, 576, 100),

    PERIOD_4HOUR("4hour", 14400L, 576, 100),

    PERIOD_1DAY("1day", 24L * 3600L, 500, 100),

    PERIOD_5DAY("5day", 5L * 24L * 3600L, 500, 100),

    PERIOD_1MON("1mon", 30L * 24L * 3600L, 500, 50),

    PERIOD_1WEEK("1week", 7L * 24L * 3600L, 500, 50),

    PERIOD_QUARTER("quarter", 3L * 30L * 24L * 3600L, 100, 50),

    PERIOD_YEAR("year", 365L * 24L * 3600L, 50, 30),

    ;

    private String line;

    private long seconds;

    // 缓存中保持最近的记录数量，超过的旧记录可以删除
    private int maxSize;

    // 列表中最多展示出来的记录数量
    private int showSize;

    private KlinePeriodEnum(String line, long seconds, int maxSize, int showSize) {
        this.line = line;
        this.seconds = seconds;
        this.maxSize = maxSize;
        this.showSize = showSize;
    }

    public static KlinePeriodEnum lineOf(String line) {
        if (StrUtil.isBlank(line)) {
            return null;
        }

        KlinePeriodEnum[] values = KlinePeriodEnum.values();
        for (KlinePeriodEnum oneValue : values) {
            if (oneValue.getLine().equalsIgnoreCase(line)) {
                return oneValue;
            }
        }

        return null;
    }

    public String getLine() {
        return line;
    }

    public long getSeconds() {
        return seconds;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getShowSize() {
        return showSize;
    }
}
