package com.yami.trading.huobi.hobi;


import com.yami.trading.bean.data.domain.*;

import java.util.List;
import java.util.Map;

public interface HobiDataService {
    List<Realtime> realtime(String symbols);
	List<Realtime> realtimeCryptos(String symbols);
	List<Realtime> realtimeXueQiu(String remarks);
    List<Realtime> realtimeXinLang(String symbols);
    public List<Realtime> realtimeTw(String symbols);
    List<Map<String, List<Kline>>> getKlines(String symbols);
    /**
     * 1day       历史数据  ： 周期 1年
     * 1week,1mon 历史数据  ： 周期 5年
     * 请求 350次
     */
	public Map<String, List<Kline>> getDailyWeekMonthHistory(String symbol);
    /**
     * 获取分钟数据
     */
    public Map<String, List<Kline>> getHourlyAndMinuteHistory(String symbol);
    /**
     * Hourly
     * 4hourly 3月
     */
    public List<Kline> getTimeseriesForFourHourly(String currency);
    
    /**
     * Hourly
     * 1hourly 1月
     */
    public List<Kline> getTimeseriesForOneHourly(String currency);
    /**
     * Hourly
     * 1hourly 2个小时
     */
    public List<Kline> getTimeseriesForTwoHourly(String currency);
    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesOneMinute(String currency);
    
    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesFiveMinute(String currency);
    
    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesFifteenMinute(String currency);
    
    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesThirtyMinute(String currency);
    /**
     * K线
     *
     * @param period 1day, 1mon, 1week, 1year
     *
     */
    public List<Kline> kline(String symbol, String period, Integer num, int maximum);

    /**
     * 市场深度数据（20档）
     */

    public Depth depth(String symbol, int maximum);

    /**
     * 市场深度数据（20档）,包装，数据本地化处理
     */
    Depth depthDecorator(String symbol, int maximum);

    /**
     * 获得近期交易记录
     */
    public Trade trade(String symbol, int maximum);

    /**
     * 获得近期交易记录,包装，数据本地化处理
     */
    public Trade tradeDecorator(String symbol, int maximum);

    public List<Symbols> symbols();

}
