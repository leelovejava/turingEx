package com.yami.trading.huobi.hobi.internal;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.etf.domain.EtfMinuteKLine;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.data.internal.KlineConstant;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.service.etf.EtfMinuteKLineService;
import com.yami.trading.service.item.ItemService;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FakeKlineInitService {
    @Autowired
    private KlineService klineService;
    @Autowired
    private EtfMinuteKLineService etfMinuteKLineService;

    public final void saveInit(String symbol) {
        Item bySymbol = itemService.findBySymbol(symbol);
        // 非机器人的价格，无需初始化
        if ("0".equalsIgnoreCase(bySymbol.getFake())) {
            return;
        }
        QueryWrapper<EtfMinuteKLine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("symbol", symbol);
        queryWrapper.lt("ts", System.currentTimeMillis());
        queryWrapper.orderByAsc("ts");
        List<EtfMinuteKLine> list = etfMinuteKLineService.list(queryWrapper);
        List<Kline> klineList = list.stream().map(e -> {
            Kline kline = BeanUtil.copyProperties(e, Kline.class);
            kline.setPeriod(Kline.PERIOD_1MIN);
            return kline;
        }).collect(Collectors.toList());

        Map<String, List<Kline>> dailyWeekMonthHistoryMap = getDailyWeekMonthHistory(symbol, klineList);
        Map<String, List<Kline>> hourlyAndMinuteHistoryMap = getHourlyAndMinuteHistory(symbol, klineList);

        klineService.saveInit(symbol, dailyWeekMonthHistoryMap, hourlyAndMinuteHistoryMap);
    }

    @Autowired
    private ItemService itemService;

    /**
     * 获取分钟数据
     */
    public Map<String, List<Kline>> getHourlyAndMinuteHistory(String symbol , List<Kline> klineList) {
        Map<String, List<Kline>> map = new HashMap<>();

        List<Kline> fourHourlyList = getTimeseriesForFourHourly(symbol,  klineList);
        map.put(KlineConstant.PERIOD_4HOUR, fourHourlyList);

        List<Kline> twoHourlyList = getTimeseriesForTwoHourly(symbol,  klineList);
        map.put(KlineConstant.PERIOD_2HOUR, twoHourlyList);

        List<Kline> oneHourlyList = getTimeseriesForOneHourly(symbol,  klineList);
        map.put(KlineConstant.PERIOD_60MIN, oneHourlyList);

        List<Kline> thirtyMinuteList = getTimeseriesThirtyMinute(symbol,  klineList);
        map.put(KlineConstant.PERIOD_30MIN, thirtyMinuteList);

        List<Kline> fifteenMinuteList = getTimeseriesFifteenMinute(symbol,  klineList);
        map.put(KlineConstant.PERIOD_15MIN, fifteenMinuteList);

        List<Kline> fiveMinuteList = getTimeseriesFiveMinute(symbol,  klineList);
        map.put(KlineConstant.PERIOD_5MIN, fiveMinuteList);

        List<Kline> oneMinuteList = klineList;
        map.put(KlineConstant.PERIOD_1MIN, oneMinuteList);
        return map;
    }


    /**
     * 1day       历史数据  ： 周期 1年
     * 1week,1mon 历史数据  ： 周期 5年
     * 请求 350次
     */
    public Map<String, List<Kline>> getDailyWeekMonthHistory(String symbol , List<Kline> klineList) {
        Map<String, List<Kline>> map = new HashMap<>();
        map.put(Kline.PERIOD_1WEEK, buildOneWeekPeriod(symbol, klineList));
        map.put(Kline.PERIOD_1MON, buildOneMonthPeriod(symbol, klineList));
        map.put(Kline.PERIOD_1DAY, buildOneDayPeriod(symbol, klineList));
        map.put(Kline.PERIOD_5DAY, buildFiveDayPeriod(symbol, klineList));
        map.put(Kline.PERIOD_QUARTER, buildOneQuarterPeriod(symbol, klineList));
        map.put(Kline.PERIOD_YEAR, buildOneYearPeriod(symbol, klineList));

        return map;
    }

    public List<Kline> buildOneDayPeriod(String currency, List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 390, Kline.PERIOD_1DAY , klineList);

    }


    public List<Kline> buildOneWeekPeriod(String currency, List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 390*7, Kline.PERIOD_1WEEK , klineList);

    }

    public List<Kline> buildOneMonthPeriod(String currency, List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 390*22, Kline.PERIOD_1MON , klineList);
    }

    public List<Kline> buildOneQuarterPeriod(String currency,  List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 390*66, Kline.PERIOD_QUARTER , klineList);
    }

    public List<Kline> buildOneYearPeriod(String currency,  List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 390*220, Kline.PERIOD_YEAR , klineList);
    }

    /**
     * 雪球支持120分钟的，讲120分钟的，
     * Hourly
     * 4hourly 3月
     */
    public List<Kline> buildFiveDayPeriod(String currency,  List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 390*5, Kline.PERIOD_5DAY , klineList);


    }


    /**
     * 雪球支持120分钟的，讲120分钟的，
     * Hourly
     * 4hourly 3月
     */
    public List<Kline> getTimeseriesForFourHourly(String currency,  List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 240, Kline.PERIOD_4HOUR , klineList);


    }

    /**
     * Hourly
     * 1hourly 2个小时
     */
    public List<Kline> getTimeseriesForTwoHourly(String currency,  List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 120, Kline.PERIOD_2HOUR , klineList);
    }

    /**
     * Hourly
     * 1hourly 1月
     */
    public List<Kline> getTimeseriesForOneHourly(String currency,  List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 60, Kline.PERIOD_60MIN, klineList);
    }

    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesOneMinute(String currency,  List<Kline> klineList) {
        return klineList;

    }

    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesFiveMinute(String currency,  List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 5, Kline.PERIOD_5MIN, klineList);

    }

    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesFifteenMinute(String currency,  List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 15, Kline.PERIOD_15MIN, klineList);
    }

    /**
     * Minute
     * 30minute 15
     * 15minute 15
     * 5minute  15
     * 1minute  15
     */
    public List<Kline> getTimeseriesThirtyMinute(String currency, List<Kline> klineList) {
        return getTimeseriesByMinute(currency, 30, Kline.PERIOD_30MIN, klineList);
    }


    /**
     * 按多少分钟进行partition聚合
     * @param symbol
     * @param minute
     * @param klineList
     * @return
     */
    public List<Kline> getTimeseriesByMinute(String symbol, int minute, String period, List<Kline> klineList) {
        int decimal = itemService.getDecimal(symbol);
        List<Kline> result = new ArrayList<>();
        // 抽象成minute分钟图，方便前端显示
        List<List<Kline>> partition = Lists.partition(klineList, minute);
        for (List<Kline> list1Min : partition) {
            Double high = list1Min.get(0).getHigh();
            Double low = list1Min.get(0).getLow();
            for (Kline kline : list1Min) {
                if (high <= kline.getHigh()) {
                    high = kline.getHigh();
                }
                if (low >= kline.getLow()) {
                    low = kline.getLow();
                }
            }
            int lastIndex = list1Min.size() - 1;
            Kline kline = new Kline();
            kline.setSymbol(symbol);
            kline.setTs(list1Min.get(lastIndex).getTs());
            kline.setOpen(BigDecimal.valueOf(list1Min.get(0).getOpen()).setScale(decimal, BigDecimal.ROUND_DOWN).doubleValue());
            kline.setHigh(BigDecimal.valueOf(high).setScale(decimal, BigDecimal.ROUND_DOWN).doubleValue());
            kline.setLow(BigDecimal.valueOf(low).setScale(decimal, BigDecimal.ROUND_DOWN).doubleValue());
            kline.setClose(BigDecimal.valueOf(list1Min.get(lastIndex).getClose()).setScale(decimal, BigDecimal.ROUND_DOWN).doubleValue());
            kline.setPeriod(period);

            double sumAmount = klineList.stream()
                    .map(Kline::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(0D, Double::sum);
            double sumVolume = klineList.stream()
                    .map(Kline::getVolume)
                    .filter(Objects::nonNull)
                    .reduce(0D, Double::sum);
            kline.setAmount(sumAmount);
            kline.setVolume(sumVolume);
            result.add(kline);
        }
        return result;
    }
}
