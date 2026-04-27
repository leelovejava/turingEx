package com.yami.trading.huobi.hobi.internal;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.data.domain.DepthEntry;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.data.domain.StockMarket;
import com.yami.trading.bean.data.domain.TradeDetails;
import com.yami.trading.bean.data.domain.TradeEntry;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DepthTimeObject;
import com.yami.trading.huobi.data.internal.KlineConstant;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.huobi.data.internal.TradeTimeObject;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.huobi.hobi.http.HttpMethodType;
import com.yami.trading.service.cms.InfomationService;
import com.yami.trading.service.item.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 完成需求k线图采集
 */
@Component
public class TWDataServiceImpl {
    //   https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=TSLA&begin=1682695800000&period=120m&type=before&count=-500&indicator=kline";
    public final static String klineUrl = "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution={}&symbol={}&from={}&to={}";

    public final static String klineOneMinuteUrl = "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution=1&symbol={}&quote=1";


    @Autowired
    private InfomationService infomationService;
    /**
     * live
     */
    public final static String live = "https://onjdo.com/stock/api/live/getLiveRates";
    public final static String markets = "https://onjdo.com/stock/api/live/getMarkets";

    public final static String pankou = "https://onjdo.com/stock/api/live/getPanKou";
    public final static String tradeList = "https://onjdo.com/stock/api/live/getTrade";

    private static Logger logger = LoggerFactory.getLogger(TWDataServiceImpl.class);

    @Autowired
    private KlineService klineService;
    @Autowired
    private ItemService itemService;

    public static List<Depth> pankous(String symbols) {
        Map<String, String> param = new HashMap<>();
        param.put("currency", symbols);

        String result = HttpHelper.getJSONFromHttpNew(pankou, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        if ("ok".equals(code)) {
            JSONArray dataArray = resultJson.getJSONArray("data");
            return dataArray.toJavaList(Depth.class);

        }
        return Lists.newArrayList();
    }

    public static List<StockMarket> getMarkets(String symbols) {
        Map<String, String> param = new HashMap<>();
        param.put("currency", symbols);

        String result = HttpHelper.getJSONFromHttpNew(markets, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        if ("ok".equals(code)) {
            JSONArray dataArray = resultJson.getJSONArray("data");
            return dataArray.toJavaList(StockMarket.class);

        }
        return Lists.newArrayList();
    }

    public static void main(String[] args) {
        TWDataServiceImpl service = new TWDataServiceImpl();
        List<Kline> timeseriesThirtyMinute = service.getTimeseriesOneMinute("5434");
        System.out.println(timeseriesThirtyMinute);
    }


    /**
     * 可以多个
     *
     * @param symbols
     */
    public void tradeList(String symbols) {
        Map<String, String> param = new HashMap<>();
        param.put("currency", symbols);

        String result = HttpHelper.getJSONFromHttpNew(tradeList, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        if ("ok".equals(code)) {
            JSONArray dataArray = resultJson.getJSONArray("data");
            for (Object o : dataArray) {
                JSONArray d = (JSONArray) o;
                List<TradeDetails> tradeDetails = d.toJavaList(TradeDetails.class);
                if (tradeDetails.size() >= 1) {
                    String symbol = tradeDetails.get(0).getSymbol();
                    symbol = itemService.getSymbolByRemarks(symbol);
                    DataCache.putStockTradeList(symbol, tradeDetails);
                    tradeListToTrade(symbol, tradeDetails);
                    setTradeListToDepth(symbol, tradeDetails);

                }
            }
        }
    }


    public static void setTradeListToDepth(String symbol, List<TradeDetails> tradeDetails) {
        Depth depth = new Depth();
        depth.setTs(tradeDetails.get(0).getTimestamp() / 1000);
        depth.setSymbol(symbol);
        List<DepthEntry> asks = tradeDetails.stream().filter(t -> t.getSide() == -1).map(t -> {
            DepthEntry depthEntry = new DepthEntry();
            depthEntry.setAmount((double) t.getTrade_volume());
            depthEntry.setPrice(t.getCurrent());
            return depthEntry;
        }).collect(Collectors.toList());
        List<DepthEntry> bids = tradeDetails.stream().filter(t -> t.getSide() == 1).map(t -> {
            DepthEntry depthEntry = new DepthEntry();
            depthEntry.setAmount((double) t.getTrade_volume());
            depthEntry.setPrice(t.getCurrent());
            return depthEntry;
        }).collect(Collectors.toList());
        depth.setAsks(asks);
        depth.setBids(bids);

        DepthTimeObject timeObject = new DepthTimeObject();
        timeObject.setDepth(depth);
        DataCache.putDepth(depth.getSymbol(), timeObject);
    }


    public static void tradeListToTrade(String symbol, List<TradeDetails> tradeDetails) {
        TradeTimeObject timeObject = DataCache.getTrade(symbol);
        if (timeObject == null) {
            timeObject = new TradeTimeObject();
        }

        List<TradeEntry> data = tradeDetails.stream().map(a -> {
            TradeEntry tradeEntry = new TradeEntry();
            tradeEntry.setDirection(a.getSide() == 1 ? "sell" : "buy");
            tradeEntry.setAmount((double) a.getTrade_volume());
            tradeEntry.setPrice(a.getCurrent());
            tradeEntry.setTs(a.getTimestamp() / 1000);
            return tradeEntry;
        }).collect(Collectors.toList());
        timeObject.put(symbol, data);
        DataCache.putTrade(symbol, timeObject);
    }

    /**
     * 获取原始的K线图数据
     *
     * @param symbol
     * @param cookie
     * @return
     */
    public String getRawTimeseriesByMinute(String symbol, String cookie) {
        long nowTs = System.currentTimeMillis();
        long begin = nowTs;
        String url = StrUtil.format(klineUrl, symbol, begin, "1m");
        return HttpHelper.sendGetHttp(url, null, cookie);
    }

    public List<Realtime> realtimeSingle(String remakrs) {
        List<Realtime> list = new ArrayList<Realtime>();
        try {
            Map<String, String> param = new HashMap<>();
            param.put("currency", remakrs);
            String result = HttpHelper.getJSONFromHttpNew(live, param, HttpMethodType.GET);
            JSONObject resultJson = JSON.parseObject(result);
            String code = resultJson.getString("code");
            if ("ok".equals(code)) {
                JSONArray dataArray = resultJson.getJSONArray("data");
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONObject realtimeJson = dataArray.getJSONObject(i);
                    Realtime realtime = new Realtime();
                    // realtime.setUuid(ApplicationUtil.getCurrentTimeUUID());
                    String currency;
                    currency = realtimeJson.getString("currency");
                    int decimal = itemService.getDecimal(currency);
                    realtime.setSymbol(itemService.getSymbolByRemarks(currency));
                    realtime.setName(currency);
                    Long timestamp = realtimeJson.getLong("timestamp");
                    if (timestamp.toString().length() > 13) {
                        timestamp = timestamp / 1000;
                    }
                    realtime.setTs(timestamp);
                    realtime.setOpen(realtimeJson.getBigDecimal("open").setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setClose(realtimeJson.getBigDecimal("mid").setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setHigh(realtimeJson.getBigDecimal("high").setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setLow(realtimeJson.getBigDecimal("low").setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setMarketCapital(realtimeJson.getLong("marketCapital"));
                    realtime.setFloatMarketCapital(realtimeJson.getLong("floatMarketCapital"));
                    BigDecimal peForecast = realtimeJson.getBigDecimal("peForecast");
                    if (peForecast != null) {
                        realtime.setPeForecast(peForecast.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal volumeRatio = realtimeJson.getBigDecimal("volumeRatio");
                    if (volumeRatio != null) {
                        realtime.setVolumeRatio(volumeRatio.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal turnoverRate = realtimeJson.getBigDecimal("turnoverRate");
                    if (turnoverRate != null) {
                        realtime.setTurnoverRate(turnoverRate.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal navps = realtimeJson.getBigDecimal("navps");
                    if (navps != null) {
                        realtime.setNavps(navps.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal pb = realtimeJson.getBigDecimal("pb");
                    if (pb != null) {
                        realtime.setPb(pb.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal amplitude = realtimeJson.getBigDecimal("amplitude");
                    if (amplitude != null) {
                        realtime.setAmplitude(amplitude.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal eps = realtimeJson.getBigDecimal("eps");
                    if (eps != null) {
                        realtime.setEps(eps.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal chg = realtimeJson.getBigDecimal("chg");
                    if (chg != null) {
                        realtime.setChg(chg.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal percent = realtimeJson.getBigDecimal("percent");
                    if (percent != null) {
                        realtime.setPercent(percent.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal amount = realtimeJson.getBigDecimal("amount");
                    if (amount == null) {
                        amount = BigDecimal.ZERO;
                    }
                    realtime.setAmount(amount.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    BigDecimal volume = realtimeJson.getBigDecimal("volume");
                    if (volume == null) {
                        volume = BigDecimal.ZERO;
                    }
                    BigDecimal averagePrice = realtimeJson.getBigDecimal("averagePrice");
                    if (averagePrice != null) {
                        realtime.setAveragePrice(averagePrice.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    BigDecimal earningsRatio = realtimeJson.getBigDecimal("earningsRatio");
                    if (earningsRatio != null) {
                        realtime.setEarningsRatio(earningsRatio.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    Long issuedShares = realtimeJson.getLong("issuedShares");
                    if (issuedShares != null) {
                        realtime.setIssuedShares(issuedShares);
                    }
                    BigDecimal dailyLimit = realtimeJson.getBigDecimal("dailyLimit");
                    if (dailyLimit != null) {
                        realtime.setDailyLimit(dailyLimit.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal limitDown = realtimeJson.getBigDecimal("limitDown");
                    if (limitDown != null) {
                        realtime.setLimitDown(limitDown.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal w52High = realtimeJson.getBigDecimal("w52High");
                    if (w52High != null) {
                        realtime.setW52High(w52High.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal w52Low = realtimeJson.getBigDecimal("w52Low");
                    if (w52Low != null) {
                        realtime.setW52Low(w52Low.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal internalVolume = realtimeJson.getBigDecimal("internalVolume");
                    if (internalVolume != null) {
                        realtime.setInternalVolume(internalVolume.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal externalVolume = realtimeJson.getBigDecimal("externalVolume");
                    if (externalVolume != null) {
                        realtime.setExternalVolume(externalVolume.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal epsYear = realtimeJson.getBigDecimal("epsYear");
                    if (epsYear != null) {
                        realtime.setEpsYear(epsYear.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal epsQuarter = realtimeJson.getBigDecimal("epsQuarter");
                    if (epsQuarter != null) {
                        realtime.setEpsQuarter(epsQuarter.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal grossProfitMargin = realtimeJson.getBigDecimal("grossProfitMargin");
                    if (grossProfitMargin != null) {
                        realtime.setGrossProfitMargin(grossProfitMargin.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal netValuePerShare = realtimeJson.getBigDecimal("netValuePerShare");
                    if (netValuePerShare != null) {
                        realtime.setNetValuePerShare(netValuePerShare.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal priceToNetRatio = realtimeJson.getBigDecimal("priceToNetRatio");
                    if (priceToNetRatio != null) {
                        realtime.setPriceToNetRatio(priceToNetRatio.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal profitMargin = realtimeJson.getBigDecimal("profitMargin");
                    if (profitMargin != null) {
                        realtime.setProfitMargin(profitMargin.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal annualDividend = realtimeJson.getBigDecimal("annualDividend");
                    if (annualDividend != null) {
                        realtime.setAnnualDividend(annualDividend.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal yield = realtimeJson.getBigDecimal("yield");
                    if (yield != null) {
                        realtime.setYield(yield.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

                    BigDecimal netProfitMargin = realtimeJson.getBigDecimal("netProfitMargin");
                    if (netProfitMargin != null) {
                        realtime.setNetProfitMargin(netProfitMargin.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }
                    realtime.setVolume(volume.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setAsk(realtimeJson.getBigDecimal("ask").setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setBid(realtimeJson.getBigDecimal("bid").setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    list.add(realtime);
                }
            } else {
                logger.error(" realtime()error, resultJson [ " + resultJson.toJSONString() + " ]");
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
        return list;
    }

    public List<Realtime> realtime(String symbols) {
        List<Realtime> realtimeList = realtimeSingle(symbols);
        return realtimeList;
    }


    public List<Kline> buildOneDayPeriod(String currency) {
        return getTimeseriesByPeriod(currency, "D", Kline.PERIOD_1DAY, 365);
    }

    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesOneMinute(String currency) {
        return getTimeseriesByPeriod(currency, "1", "1min", 100);
    }


    @Retryable(
            value = {RuntimeException.class},  // 需要重试的异常类型
            maxAttempts = 5,  // 最大重试次数
            backoff = @Backoff(delay = 2000)  // 退避策略：每次重试之间的延迟
    )
    public List<Kline> getTimeseriesByPeriod(String currency, String periodXieQiu, String sysPeriod, long limitDays) {
        List<Kline> resList = new ArrayList<>();
        long nowTs = System.currentTimeMillis() / 1000;
        long startTs = (System.currentTimeMillis() - limitDays * 24 * 60 * 60 * 1000) / 1000;
        long begin = nowTs;

        Set<Long> tsSet = new HashSet<>();
        String url = StrUtil.format(klineUrl, periodXieQiu, itemService.findBySymbol(currency).getRemarks(), nowTs, startTs);
        if (periodXieQiu.equals("1")) {
            url = StrUtil.format(klineOneMinuteUrl, itemService.findBySymbol(currency).getRemarks());
        }
        // "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution={}&symbol=TWS:{}:STOCK&from={}&to={}";
        logger.info("采集台股k线图， {} {}", currency, url);
        String cookie = HttpHelper.getCookie("https://www.cnyes.com/twstock/" + itemService.getSymbolByRemarks(currency));

        String json = HttpHelper.sendGetHttp(url, null, cookie);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String statusCode = jsonObject.getString("statusCode");
        if ("200".equalsIgnoreCase(statusCode)) {
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray opens = data.getJSONArray("o");
            JSONArray closes = data.getJSONArray("c");
            JSONArray highs = data.getJSONArray("h");
            JSONArray lows = data.getJSONArray("l");
            JSONArray tss = data.getJSONArray("t");
            JSONArray volumes = data.getJSONArray("v");
            int len = tss.size();
            for (int i = 0; i < len; i++) {
                Kline kline = new Kline();
                kline.setSymbol(currency);
                kline.setPeriod(sysPeriod);
                // 毫秒
                long ts = Long.parseLong(tss.get(i).toString());
                if (Long.toString(ts).length() > 13) {
                    ts = ts / 1000;
                }
                kline.setTs(ts);
                kline.setOpen(new BigDecimal(opens.get(i).toString()).doubleValue());
                kline.setClose(new BigDecimal(closes.get(i).toString()).doubleValue());
                kline.setHigh(new BigDecimal(highs.get(i).toString()).doubleValue());
                kline.setLow(new BigDecimal(lows.get(i).toString()).doubleValue());
                if (CollectionUtil.isNotEmpty(volumes)) {
                    Object o = volumes.get(i);
                    if (o != null) {
                        kline.setVolume(new BigDecimal(o.toString()).doubleValue());
                        kline.setAmount(kline.getVolume() * kline.getClose());
                    }
                }
                if (klineService != null) {
                    klineService.repairKline(kline);
                }
                resList.add(kline);
            }
        }
        Collections.sort(resList);
        return resList;
    }

    public List<Kline> getTimeseriesBy5Minute(String currency, int minute, long limitDays) {
        return getTimeseriesByPeriod(currency, "5c", minute + "min", limitDays);
    }

    /**
     * 1day       历史数据  ： 周期 1年
     * 1week,1mon 历史数据  ： 周期 5年
     * 请求 350次
     */
    public Map<String, List<Kline>> getDailyWeekMonthHistory(String symbol) {
        Map<String, List<Kline>> map = new HashMap<>();
        List<Kline> oneDayPeriod = buildOneDayPeriod(symbol);

//  	public List<Kline> calculateKline(String symbol, int seq, String period, List<Kline> klineList) ;
        map.put(Kline.PERIOD_1WEEK, klineService.calculateKline(symbol, 7, Kline.PERIOD_1WEEK, oneDayPeriod));
        map.put(Kline.PERIOD_1MON, klineService.calculateKline(symbol, 30, Kline.PERIOD_1MON, oneDayPeriod));
        map.put(Kline.PERIOD_1DAY, oneDayPeriod);
        map.put(Kline.PERIOD_5DAY, klineService.calculateKline(symbol, 5, Kline.PERIOD_5DAY, oneDayPeriod));
        map.put(Kline.PERIOD_QUARTER, klineService.calculateKline(symbol, 90, Kline.PERIOD_QUARTER, oneDayPeriod));
        map.put(Kline.PERIOD_YEAR, klineService.calculateKline(symbol, 365, Kline.PERIOD_YEAR, oneDayPeriod));

        return map;
    }

    public Map<String, List<Kline>> getHourlyAndMinuteHistory(String symbol) {
        Map<String, List<Kline>> map = new HashMap<>();
        List<Kline> minKline5 = getTimeseriesBy5Minute(symbol, 5, 12);
        map.put(KlineConstant.PERIOD_1MIN, getTimeseriesOneMinute(symbol));
        map.put(KlineConstant.PERIOD_5MIN, minKline5);
        map.put(KlineConstant.PERIOD_15MIN, klineService.calculateKline(symbol, 3, Kline.PERIOD_15MIN, minKline5));
        map.put(KlineConstant.PERIOD_30MIN, klineService.calculateKline(symbol, 6, Kline.PERIOD_30MIN, minKline5));
        map.put(KlineConstant.PERIOD_60MIN, klineService.calculateKline(symbol, 12, Kline.PERIOD_60MIN, minKline5));
        map.put(KlineConstant.PERIOD_2HOUR, klineService.calculateKline(symbol, 24, Kline.PERIOD_2HOUR, minKline5));
        map.put(KlineConstant.PERIOD_4HOUR, klineService.calculateKline(symbol, 48, Kline.PERIOD_4HOUR, minKline5));
        return map;
    }
}
