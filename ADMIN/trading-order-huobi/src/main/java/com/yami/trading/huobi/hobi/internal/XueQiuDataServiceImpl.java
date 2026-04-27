package com.yami.trading.huobi.hobi.internal;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yami.trading.bean.cms.Infomation;
import com.yami.trading.bean.data.domain.*;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.*;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.huobi.hobi.http.HttpMethodType;
import com.yami.trading.service.cms.InfomationService;
import com.yami.trading.service.item.ItemService;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 完成需求k线图采集
 */
@Component
public class XueQiuDataServiceImpl {
    //   https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=TSLA&begin=1682695800000&period=120m&type=before&count=-500&indicator=kline";
    public final static String klineUrl = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol={}&begin={}&period={}&type=before&count=-500&indicator=kline";
    ExecutorService executor = Executors.newFixedThreadPool(12); // 根据需要执行的任务数量调整线程池大小
    @Autowired
    private InfomationService infomationService;

    @Qualifier("redissonClientSpider")
    @Autowired
    private RedissonClient redissonClientSpider;

    /**
     * live
     */
    public final static String live = "https://onjdo.com/stock/api/live/getLiveRates";
    public final static String markets = "https://onjdo.com/stock/api/live/getMarkets";

    public final static String pankou = "https://onjdo.com/stock/api/live/getPanKou";
    public final static String tradeList = "https://onjdo.com/stock/api/live/getTrade";
    public final static String lease = "https://onjdo.com/stock/api/live/lease";

    private static Logger logger = LoggerFactory.getLogger(XueQiuDataServiceImpl.class);

    @Autowired
    private KlineService klineService;
    @Autowired
    private ItemService itemService;

    public static List<Depth> pankous(String remarks) {
        Map<String, String> param = new HashMap<>();
        param.put("currency", remarks);

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
        XueQiuDataServiceImpl service = new XueQiuDataServiceImpl();
//        List<Kline> sz300750 = service.buildOneYearPeriod("AAPL");
//        System.out.println(sz300750.size());
        List<StockMarket> markets1 = service.getMarkets("AAPL,OIL");
        System.out.println(markets1);
    }
    public void lease(String symbol) {
        Item bySymbol = itemService.findBySymbol(symbol);
        if(Item.DEFAULT_ACTIVE.equalsIgnoreCase(bySymbol.getCrawlStatus())){
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("currency", symbol);
        param.put("type", bySymbol.getOpenCloseType());
        String result = HttpHelper.getJSONFromHttpNew(lease, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        if ("ok".equals(code)) {
            logger.info("{} 采集续约成功", symbol);
        }
    }
    public void getInformation() {
        String cookie = HttpHelper.getCookie("https://xueqiu.com/");
        String url = "https://xueqiu.com/statuses/livenews/list.json?since_id=-1&max_id=-1&count=15";
        String json = HttpHelper.sendGetHttp(url, null, cookie);
        JSONArray items = JSONObject.parseObject(json).getJSONArray("items");
        List<Infomation> infomations = new ArrayList<>();
        items.forEach(d -> {
            Infomation infom = new Infomation();
            JSONObject data = (JSONObject) d;
            String dataId = data.getString("id");
            infom.setDataId(dataId);
            String description = data.getString("text");
            infom.setDescription(description);
            String createAt = data.getString("created_at");
            infom.setCreatedAt(createAt);
            infom.setType("1");
            String originUrl = data.getString("target");
            infom.setOriginUrl(originUrl);
            String source = getSource(description);
            infom.setSource(source);
            infom.setLang("zh-CN");
            String key = "infomation" + originUrl;
            if (RedisUtil.get(key) == null) {
                infomations.add(infom);
                // url存一周
                RedisUtil.set(key, 1, 60 * 60 * 24 * 7);
            }
        });
        if (infomations.size() > 1) {
            infomationService.saveBatch(infomations);
        }
    }

    public Map<String, List<Kline>> getHourlyAndMinuteHistory(String symbol, String cookie)  {
        Map<String, List<Kline>> map = new HashMap<>();
        try {
            CompletableFuture<List<Kline>> fourHourlyFuture = CompletableFuture.supplyAsync(() -> getTimeseriesForFourHourly(symbol, cookie), executor);
            CompletableFuture<List<Kline>> oneHourlyFuture = CompletableFuture.supplyAsync(() -> getTimeseriesForOneHourly(symbol, cookie), executor);
            CompletableFuture<List<Kline>> twoHourlyFuture = CompletableFuture.supplyAsync(() -> getTimeseriesForTwoHourly(symbol, cookie), executor);
            CompletableFuture<List<Kline>> thirtyMinuteFuture = CompletableFuture.supplyAsync(() -> getTimeseriesThirtyMinute(symbol, cookie), executor);
            CompletableFuture<List<Kline>> fifteenMinuteFuture = CompletableFuture.supplyAsync(() -> getTimeseriesFifteenMinute(symbol, cookie), executor);
            CompletableFuture<List<Kline>> fiveMinuteFuture = CompletableFuture.supplyAsync(() -> getTimeseriesFiveMinute(symbol, cookie), executor);
            CompletableFuture<List<Kline>> oneMinuteFuture = CompletableFuture.supplyAsync(() -> getTimeseriesOneMinute(symbol, cookie), executor);

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    fourHourlyFuture, oneHourlyFuture, twoHourlyFuture, thirtyMinuteFuture, fifteenMinuteFuture, fiveMinuteFuture, oneMinuteFuture);

            // 等待所有任务完成
            allFutures.join();

            map.put(KlineConstant.PERIOD_4HOUR, fourHourlyFuture.get());
            map.put(KlineConstant.PERIOD_60MIN, oneHourlyFuture.get());

            // 特殊处理两小时数据
            List<Kline> twoHourlyList = twoHourlyFuture.get();
            if (twoHourlyList == null || twoHourlyList.isEmpty()) {
                try {
                    List<Kline> hourList = oneHourlyFuture.get();
                    if (hourList != null && !hourList.isEmpty()) {
                        map.put(Kline.PERIOD_2HOUR, klineService.calculateKline(symbol, 2, Kline.PERIOD_2HOUR, hourList));
                    }
                } catch (Exception e) {
                    logger.error("计算 120mink线图失败", e);
                }
            } else {
                map.put(KlineConstant.PERIOD_2HOUR, twoHourlyList);
            }

            map.put(KlineConstant.PERIOD_30MIN, thirtyMinuteFuture.get());
            map.put(KlineConstant.PERIOD_15MIN, fifteenMinuteFuture.get());
            map.put(KlineConstant.PERIOD_5MIN, fiveMinuteFuture.get());
            map.put(KlineConstant.PERIOD_1MIN, oneMinuteFuture.get());

            return map;
        }catch (Exception e){
            logger.error("getHourlyAndMinuteHistory 报错 :{}", symbol);
        }

        return map;
    }

    public static String getSource(String text) {

        String pattern = "（([^（）]*)）$"; // 匹配最后一个括号内的文本

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(text);

        if (matcher.find()) {
            String endText = matcher.group(1);
            return endText;
        } else {
            return "";
        }
    }

    /**
     * 可以多个
     */
    public void tradeList(String remarks, boolean isUSStock) {
        Map<String, String> param = new HashMap<>();
        param.put("currency", remarks);

        String result = HttpHelper.getJSONFromHttpNew(tradeList, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        if ("ok".equals(code)) {
            JSONArray dataArray = resultJson.getJSONArray("data");
            for (Object o : dataArray) {
                JSONArray d = (JSONArray) o;
                List<TradeDetails> tradeDetails = d.toJavaList(TradeDetails.class);
                if (tradeDetails.size() >= 1) {
                    String symbol = itemService.getSymbolByRemarks(tradeDetails.get(0).getSymbol());
                    DataCache.putStockTradeList(symbol, tradeDetails);
                    tradeListToTrade(symbol, tradeDetails);
                    if (isUSStock) {
                        setTradeListToDepth(symbol, tradeDetails);
                    }
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

    public List<Realtime> realtimeSingle(String symbol) {
        List<Realtime> list = new ArrayList<>();
        try {
            Map<String, String> param = new HashMap<>();
            param.put("currency", symbol);
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
                    String symbolByRemarks = itemService.getSymbolByRemarks(currency);
                    realtime.setSymbol(symbolByRemarks);
                    realtime.setName(symbolByRemarks);
                    Long timestamp = realtimeJson.getLong("timestamp");
                    if (timestamp.toString().length() > 13) {
                        timestamp = timestamp / 1000;
                    }
                    realtime.setTs(timestamp);
                    realtime.setOpen(realtimeJson.getBigDecimal("open").setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    if (realtimeJson.getBigDecimal("mid").compareTo(BigDecimal.ZERO) != 0) {
                        realtime.setClose(realtimeJson.getBigDecimal("mid").setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    } else {
                        realtime.setClose(realtimeJson.getBigDecimal("close").setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    }

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
                        realtime.setTurnoverRate(turnoverRate.setScale(2, RoundingMode.HALF_UP).doubleValue());
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

    /**
     * 1day       历史数据  ： 周期 1年
     * 1week,1mon 历史数据  ： 周期 5年
     * 请求 350次
     */
    public Map<String, List<Kline>> getDailyWeekMonthHistory(String symbol,String cookie) {
        Map<String, List<Kline>> map = new HashMap<>();
        try {
            CompletableFuture<List<Kline>> oneWeekFuture = CompletableFuture.supplyAsync(() -> buildOneWeekPeriod(symbol, cookie), executor);
            CompletableFuture<List<Kline>> oneMonthFuture = CompletableFuture.supplyAsync(() -> buildOneMonthPeriod(symbol, cookie), executor);
            CompletableFuture<List<Kline>> oneDayFuture = CompletableFuture.supplyAsync(() -> buildOneDayPeriod(symbol, cookie), executor);
            CompletableFuture<List<Kline>> fiveDayFuture = CompletableFuture.supplyAsync(() -> buildFiveDayPeriod(symbol, cookie), executor);
            CompletableFuture<List<Kline>> oneQuarterFuture = CompletableFuture.supplyAsync(() -> buildOneQuarterPeriod(symbol, cookie), executor);
            CompletableFuture<List<Kline>> oneYearFuture = CompletableFuture.supplyAsync(() -> buildOneYearPeriod(symbol, cookie), executor);

            // 等待所有 CompletableFuture 完成
            CompletableFuture.allOf(oneWeekFuture, oneMonthFuture, oneDayFuture, fiveDayFuture, oneQuarterFuture, oneYearFuture).join();
            map.put(Kline.PERIOD_1WEEK, oneWeekFuture.get());
            map.put(Kline.PERIOD_1MON, oneMonthFuture.get());
            map.put(Kline.PERIOD_1DAY, oneDayFuture.get());
            map.put(Kline.PERIOD_5DAY, fiveDayFuture.get());
            map.put(Kline.PERIOD_QUARTER, oneQuarterFuture.get());
            map.put(Kline.PERIOD_YEAR, oneYearFuture.get());
        }catch (Exception e){
           logger.info("初始化k线图报错：{}", symbol, e) ;
        }
        return map;
    }

    public List<Kline> buildOneDayPeriod(String currency, String cookie) {
        return getTimeseriesByPeriod(currency, "day", Kline.PERIOD_1DAY, 365, cookie);

    }


    public List<Kline> buildOneWeekPeriod(String currency, String cookie) {
        return getTimeseriesByPeriod(currency, "week", Kline.PERIOD_1WEEK, 365 * 5, cookie);

    }

    public List<Kline> buildOneMonthPeriod(String currency, String cookie) {
        return getTimeseriesByPeriod(currency, "month", Kline.PERIOD_1MON, 365 * 5, cookie);
    }

    public List<Kline> buildOneQuarterPeriod(String currency , String cookie) {
        return getTimeseriesByPeriod(currency, "quarter", Kline.PERIOD_QUARTER, 365 * 100, cookie);
    }

    public List<Kline> buildOneYearPeriod(String currency , String cookie) {
        return getTimeseriesByPeriod(currency, "year", Kline.PERIOD_YEAR, 365 * 100, cookie);
    }

    /**
     * 雪球支持120分钟的，讲120分钟的，
     * Hourly
     * 4hourly 3月
     */
    @Retryable(
            value = {RuntimeException.class},  // 需要重试的异常类型
            maxAttempts = 5,  // 最大重试次数
            backoff = @Backoff(delay = 2000)  // 退避策略：每次重试之间的延迟
    )
    public List<Kline> buildFiveDayPeriod(String currency, String cookie) {
        List<Kline> result = Lists.newArrayList();
        List<Kline> timeseriesByOneDay = getTimeseriesByPeriod(currency, "day", Kline.PERIOD_1DAY, 1000 , cookie);

        Collections.sort(timeseriesByOneDay, Kline::compareTo);
        int lastIndex = timeseriesByOneDay.size() - 1;
        for (int i = lastIndex; i >= 5; i = i - 5) {
            // 1天K线最新的5条数据
            List<Kline> klineOneTop5 = new ArrayList<>();
            klineOneTop5.add(timeseriesByOneDay.get(i));
            klineOneTop5.add(timeseriesByOneDay.get(i - 1));
            klineOneTop5.add(timeseriesByOneDay.get(i - 2));
            klineOneTop5.add(timeseriesByOneDay.get(i - 3));
            klineOneTop5.add(timeseriesByOneDay.get(i - 4));

            Double high = null;
            Double low = null;
            for (Kline kline : klineOneTop5) {
                if (high == null || high <= kline.getHigh()) {
                    high = kline.getHigh();
                }
                if (low == null || low >= kline.getLow()) {
                    low = kline.getLow();
                }
            }
            // 保存K线到数据库
            Kline kline = new Kline();
            if(itemService != null){
                kline.setSymbol(itemService.getSymbolByRemarks(currency));
            }else{
                kline.setSymbol(cookie);
            }

            kline.setTs(klineOneTop5.get(4).getTs());
            kline.setOpen(klineOneTop5.get(4).getOpen());
            kline.setHigh(high);
            kline.setLow(low);
            kline.setClose(klineOneTop5.get(0).getClose());
            kline.setPeriod(Kline.PERIOD_5DAY);
            // 格式化小数点位
            if(klineService != null){
                klineService.formatPoint(kline);
            }
            double sumAmount = klineOneTop5.stream()
                    .map(Kline::getAmount)
                    .filter(amount -> amount != 0)
                    .reduce(0D, Double::sum);
            double sumVolume = klineOneTop5.stream()
                    .map(Kline::getVolume)
                    .filter(volume -> volume != null)
                    .reduce(0D, Double::sum);
            kline.setAmount(sumAmount);
            kline.setVolume(sumVolume);
            if(klineService != null){
                klineService.repairKline(kline);
            }
            result.add(kline);
        }
        Collections.sort(result);
        return result;
    }


    /**
     * 雪球支持120分钟的，讲120分钟的，
     * Hourly
     * 4hourly 3月
     */
    @Retryable(
            value = {RuntimeException.class},  // 需要重试的异常类型
            maxAttempts = 5,  // 最大重试次数
            backoff = @Backoff(delay = 2000)  // 退避策略：每次重试之间的延迟
    )
    public List<Kline> getTimeseriesForFourHourly(String currency, String cookie) {
        if(itemService != null){
            currency = itemService.findBySymbol(currency).getRemarks();
        }

        List<Kline> result = Lists.newArrayList();
        List<Kline> timeseriesByMinute = getTimeseriesByMinute(currency, 120, 90, cookie);
        Collections.sort(timeseriesByMinute, Kline::compareTo);
        int lastIndex = timeseriesByMinute.size() - 1;
        for (int i = lastIndex; i >= 1; i = i - 2) {
            Kline first = timeseriesByMinute.get(i);
            Kline secnd = timeseriesByMinute.get(i - 1);
            Kline kline = new Kline();
            kline.setPeriod(Kline.PERIOD_4HOUR);
            if(itemService != null){
                kline.setSymbol(itemService.getSymbolByRemarks(currency));
            }else{
                kline.setSymbol(currency);
            }

            Long timestamp = first.getTs();
            if (timestamp.toString().length() > 13) {
                timestamp = timestamp / 1000;
            }
            kline.setTs(timestamp);
            kline.setOpen(secnd.getOpen());
            kline.setClose(first.getClose());
            kline.setHigh(Math.max(first.getHigh(), secnd.getHigh()));
            kline.setLow(Math.min(first.getLow(), secnd.getLow()));
            kline.setAmount(first.getAmount() + secnd.getAmount());
            kline.setVolume(first.getVolume() + secnd.getVolume());
            if(klineService != null){
                klineService.repairKline(kline);
            }
            result.add(kline);
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Hourly
     * 1hourly 2个小时
     */
    public List<Kline> getTimeseriesForTwoHourly(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 120, 300, cookie);
    }

    /**
     * Hourly
     * 1hourly 1月
     */
    public List<Kline> getTimeseriesForOneHourly(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 60, 300, cookie);
    }

    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesOneMinute(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 1, 15,cookie);

    }

    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesFiveMinute(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 5, 15, cookie);

    }

    /**
     * Minute
     * 30minute 10天
     * 15minute 5天
     * 5minute  2天
     * 1minute  1天
     */
    public List<Kline> getTimeseriesFifteenMinute(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 15, 15, cookie);
    }

    /**
     * Minute
     * 30minute 15
     * 15minute 15
     * 5minute  15
     * 1minute  15
     */
    public List<Kline> getTimeseriesThirtyMinute(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 30, 10, cookie);
    }

    @Retryable(
            value = {RuntimeException.class},  // 需要重试的异常类型
            maxAttempts = 5,  // 最大重试次数
            backoff = @Backoff(delay = 2000)  // 退避策略：每次重试之间的延迟
    )
    public List<Kline> getTimeseriesByPeriod(String currency, String periodXieQiu, String sysPeriod, long limitDays, String cookie) {
        List<Kline> resList = new ArrayList<>();
        long nowTs = System.currentTimeMillis();
        long startTs = System.currentTimeMillis() - limitDays * 24 * 60 * 60 * 1000;
        long begin = nowTs;
        if(cookie == null){
            cookie = HttpHelper.getCookie("https://xueqiu.com/");
        }
        if(itemService != null){
            currency = itemService.findBySymbol(currency).getRemarks();
        }
        Set<Long> tsSet = new HashSet<>();
        while (begin > startTs) {
            String url = StrUtil.format(klineUrl, currency, begin, periodXieQiu);
            String json = HttpHelper.sendGetHttp(url, null, cookie);
            JSONObject resultJson = JSON.parseObject(json);
            JSONArray dataArray = resultJson.getJSONObject("data").getJSONArray("item");
            List<List> lists = dataArray.toJavaList(List.class);
            long minTS = begin;
            for (List result : lists) {
                Kline kline = new Kline();
                if(itemService != null){
                    kline.setSymbol(itemService.getSymbolByRemarks(currency));
                }else{
                    kline.setSymbol(sysPeriod);
                }
                kline.setPeriod(sysPeriod);
                // 毫秒
                long ts = Long.parseLong(result.get(0).toString());
                if (Long.toString(ts).length() > 13) {
                    ts = ts / 1000;
                }
                if (tsSet.contains(ts)) {
                    continue;
                } else {
                    tsSet.add(ts);
                }
                kline.setTs(ts);
                kline.setOpen(new BigDecimal(result.get(2).toString()).doubleValue());
                kline.setClose(new BigDecimal(result.get(5).toString()).doubleValue());
                kline.setHigh(new BigDecimal(result.get(3).toString()).doubleValue());
                kline.setLow(new BigDecimal(result.get(4).toString()).doubleValue());
                kline.setVolume(new BigDecimal(result.get(1).toString()).doubleValue());
                kline.setAmount(new BigDecimal(result.get(9).toString()).doubleValue());
                if (klineService != null) {
                    klineService.repairKline(kline);
                }
                resList.add(kline);
                if (ts < minTS) {
                    minTS = ts;
                }
            }
            if (minTS == begin) {
                break;
            }
            begin = minTS;
            if (begin < startTs) {
                break;
            }
        }
        Collections.sort(resList);
        int len = resList.size();
        for (int i = 1; i < len; i++) {
            resList.get(i).setOpen(resList.get(i - 1).getClose());
        }
        return resList;
    }

    public List<Kline> getTimeseriesByMinute(String currency, int minute, long limitDays, String cookie) {
        return getTimeseriesByPeriod(currency, minute + "m", minute + "min", limitDays, cookie );
    }
}
