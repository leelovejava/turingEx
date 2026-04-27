package com.yami.trading.common.constants;

public interface RedisKeyConstants {
    String TRADING_PREFIX = "trading:";
    /**
     * 邮箱验证码
     */
    String USER_EMAILL_PREFIX = TRADING_PREFIX + ":email_code:";
    /**
     * 手机号验证码
     */
    String USER_MOBILE_PREFIX = TRADING_PREFIX + ":mobile_code:";
    /**
     * K线
     */
    String KLINE = TRADING_PREFIX + "kline";
    /**
     * 分时
     */
    String TREND = TRADING_PREFIX + "trend";
    /**
     * 市场深度数据
     */
    String TRADE_LIST = TRADING_PREFIX + "trend:list";
    /**
     * 最新的实时价格信息缓存
     */
    String LATEST_REALTIME = TRADING_PREFIX + "latest:realtime";
    /**
     * 股票时区暂存
     */
    String REAL_MARKET = TRADING_PREFIX + "real:market";
    /**
     * 市场深度数据
     */
    String DEPTH = TRADING_PREFIX + "depth";
    /**
     * 近期交易记录
     */
    String TRADE = TRADING_PREFIX + "trade";
    /**
     * 最高
     */
    String REALTIME_HIGH = TRADING_PREFIX + "realtime:high";
    /**
     * 最低
     */
    String REALTIME_LOW = TRADING_PREFIX + "realtime:low";
    /**
     * 火币K线
     */
    String KLINE_HOBI = TRADING_PREFIX + "kline:hobi";
    /**
     * 向前24小时时间点的开盘价
     */
    String REALTIME_24H_BEFORE_OPEN = TRADING_PREFIX + "realtime:24h:before:open";
    /**
     * 24小时的历史记录
     */
    String CRYPTOS_REALTIME_HISTORY = TRADING_PREFIX + "cryptos:realtime:history";
    /**
     * 最近60s实时价格信息列表
     */
    String LATEST_REALTIME_60S = TRADING_PREFIX + "latest:realtime:60s";

}
