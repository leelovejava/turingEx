package com.yami.trading.huobi.hobi.internal;

import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.huobi.data.internal.KlineConstant;
import com.yami.trading.huobi.data.internal.KlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class XinLangDataServiceImpl {

    @Autowired
    private KlineService klineService;

    @Autowired
    private FreeForexRateService freeForexRateService;

    @Autowired
    private XueQiuDataServiceImpl xueQiuDataService;

    public List<Realtime> realtimeSingle(String symbols) {
        return freeForexRateService.fetchRealtime(symbols);
    }

    public Map<String, List<Kline>> getDailyWeekMonthHistory(String symbol) {
        Map<String, List<Kline>> all = xueQiuDataService.getKlineFromTwelveData(symbol);
        Map<String, List<Kline>> map = new HashMap<>();
        map.put(Kline.PERIOD_1DAY,   all.getOrDefault(KlineConstant.PERIOD_1DAY,  new ArrayList<>()));
        map.put(Kline.PERIOD_1WEEK,  all.getOrDefault(KlineConstant.PERIOD_1WEEK, new ArrayList<>()));
        map.put(Kline.PERIOD_1MON,   all.getOrDefault(KlineConstant.PERIOD_1MON,  new ArrayList<>()));
        map.put(Kline.PERIOD_5DAY,   all.getOrDefault(KlineConstant.PERIOD_5DAY,  new ArrayList<>()));
        map.put(Kline.PERIOD_QUARTER,all.getOrDefault(KlineConstant.PERIOD_QUARTER,new ArrayList<>()));
        map.put(Kline.PERIOD_YEAR,   all.getOrDefault(KlineConstant.PERIOD_YEAR,  new ArrayList<>()));
        return map;
    }

    public List<Kline> buildOneDayPeriod(String currency) {
        return xueQiuDataService.getKlineFromTwelveData(currency)
                .getOrDefault(KlineConstant.PERIOD_1DAY, new ArrayList<>());
    }

    public List<Kline> getTimeseriesForFourHourly(String currency) {
        return xueQiuDataService.getKlineFromTwelveData(currency)
                .getOrDefault(KlineConstant.PERIOD_4HOUR, new ArrayList<>());
    }

    public List<Kline> getTimeseriesForTwoHourly(String currency) {
        return xueQiuDataService.getKlineFromTwelveData(currency)
                .getOrDefault(KlineConstant.PERIOD_2HOUR, new ArrayList<>());
    }

    public List<Kline> getTimeseriesForOneHourly(String currency) {
        return xueQiuDataService.getKlineFromTwelveData(currency)
                .getOrDefault(KlineConstant.PERIOD_60MIN, new ArrayList<>());
    }

    public List<Kline> getTimeseriesOneMinute(String currency) {
        return xueQiuDataService.getKlineFromTwelveData(currency)
                .getOrDefault(KlineConstant.PERIOD_1MIN, new ArrayList<>());
    }

    public List<Kline> getTimeseriesFiveMinute(String currency) {
        return xueQiuDataService.getKlineFromTwelveData(currency)
                .getOrDefault(KlineConstant.PERIOD_5MIN, new ArrayList<>());
    }

    public List<Kline> getTimeseriesFifteenMinute(String currency) {
        return xueQiuDataService.getKlineFromTwelveData(currency)
                .getOrDefault(KlineConstant.PERIOD_15MIN, new ArrayList<>());
    }

    public List<Kline> getTimeseriesThirtyMinute(String currency) {
        return xueQiuDataService.getKlineFromTwelveData(currency)
                .getOrDefault(KlineConstant.PERIOD_30MIN, new ArrayList<>());
    }
}
