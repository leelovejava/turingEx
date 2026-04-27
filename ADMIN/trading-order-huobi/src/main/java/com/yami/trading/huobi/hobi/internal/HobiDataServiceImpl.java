package com.yami.trading.huobi.hobi.internal;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yami.trading.bean.data.domain.*;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.common.util.UTCDateUtils;
import com.yami.trading.huobi.data.internal.KlineConstant;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.huobi.data.model.TimeseriesResult;
import com.yami.trading.huobi.hobi.Config;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.huobi.hobi.constant.TraderMadeOptions;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.huobi.hobi.http.HttpMethodType;
import com.yami.trading.service.item.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.yami.trading.huobi.hobi.internal.SpiderService.REALTIME_HASH_BAK;
import static com.yami.trading.huobi.hobi.internal.XueQiuDataServiceImpl.lease;
import static com.yami.trading.huobi.hobi.internal.XueQiuDataServiceImpl.live;

@Component
public class HobiDataServiceImpl implements HobiDataService {
    private static Logger logger = LoggerFactory.getLogger(HobiDataServiceImpl.class);

    @Autowired
    private XueQiuDataServiceImpl xueQiuDataService;
    @Autowired
    private XinLangDataServiceImpl xinLangDataService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private KlineService klineService;
    @Autowired
    private TWDataServiceImpl twDataService;

    @Autowired
    private FreeForexRateService freeForexRateService;

    @Autowired
    private SpiderService spiderService;
    /**
     *                                                                         ?     */
    private int interval = 100;

    private int sleep = 100;
    /**
     *                                                                            ?     */
    private volatile Date last_time = new Date();

    private volatile boolean lock = false;


    public static void main(String[] args) {
        HobiDataServiceImpl huobi = new HobiDataServiceImpl();
//        List<Kline> btcusdt = huobi.mexcKline("btcusdt", Kline.PERIOD_1MIN);
//        System.out.println(btcusdt);
//        Depth btcusdt1 = huobi.mexcdepth("btcusdt");
//        System.out.println(btcusdt1);
        Trade btcusdt = huobi.mexcTrade("btcusdt");
        System.out.println(btcusdt);


        //Map<String, List<Kline>> etcusd = huobi.getDailyWeekMonthHistory("ETCUSD");
       // System.out.println(etcusd);
    }

    @Override
    public List<Kline> kline(String symbolData, String period, Integer num, int maximum) {
        if(!spiderService.isHuobi()){
            return mexcKline(symbolData, period);
        }
        List<Kline> list = new ArrayList<Kline>();
        Item item = itemService.findByRemarks(symbolData);
        if (item == null) {
            return list;
        }
        boolean current_lock = false;
        if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
            ThreadUtils.sleep(sleep);
            if (maximum >= 100) {
                return list;
            } else {
                return this.kline(symbolData, period, num, ++maximum);
            }

        } else {
            try {
                current_lock = true;
                lock = true;
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("symbol", symbolData);
                param.put("period", period);
                if (num == null) {
                    if (Kline.PERIOD_1MIN.equals(period)) {
                        param.put("size", 1440);
                    }
                    if (Kline.PERIOD_5MIN.equals(period)) {
                        param.put("size", 576);
                    }
                    if (Kline.PERIOD_15MIN.equals(period)) {
                        param.put("size", 576);
                    }
                    if (Kline.PERIOD_30MIN.equals(period)) {
                        param.put("size", 576);
                    }
                    if (Kline.PERIOD_60MIN.equals(period)) {
                        param.put("size", 576);
                    }

                    if (Kline.PERIOD_4HOUR.equals(period)) {
                        param.put("size", 576);
                    }
                    if (Kline.PERIOD_1DAY.equals(period)) {
                        param.put("size", 500);
                    }
                    if (Kline.PERIOD_1MON.equals(period)) {
                        param.put("size", 500);
                    }
                    if (Kline.PERIOD_1WEEK.equals(period)) {
                        param.put("size", 500);
                    }

                } else {
                    param.put("size", num);
                }

                String result = HttpHelper.getJSONFromHttp(Config.url + Config.kline, param, HttpMethodType.GET);
                JSONObject resultJson = JSON.parseObject(result);
                String status = resultJson.getString("status");
                if ("ok".equals(status)) {
                    JSONArray dataArray = resultJson.getJSONArray("data");
                    /**
                     *                                                   ?                     */
                    int start = 1;
                    if (num != null && num == 1) {
                        start = 0;
                    }
                    for (int i = start; i < dataArray.size(); i++) {
                        JSONObject realtimeJson = dataArray.getJSONObject(i);
                        Kline kline = new Kline();
                        kline.setSymbol(item.getSymbol());
                        kline.setPeriod(period);
                        kline.setTs(Long.valueOf(realtimeJson.getString("id") + "000"));
                        kline.setOpen(realtimeJson.getDouble("open"));
                        kline.setClose(realtimeJson.getDouble("close"));
                        kline.setHigh(realtimeJson.getDouble("high"));
                        kline.setLow(realtimeJson.getDouble("low"));
                        kline.setVolume(realtimeJson.getDouble("vol"));
                        list.add(kline);
                    }
                }
            } catch (Exception e) {
                logger.error("error", e);
            } finally {
                if (current_lock) {
                    lock = false;
                    last_time = new Date();
                }
            }
        }

        return list;
    }



    public List<Kline> mexcKline(String symbolData, String period) {
        Map<String, String> map = new HashMap<>();
        map.put(Kline.PERIOD_1MIN, "1m");
        map.put(Kline.PERIOD_5MIN, "5m");
        map.put(Kline.PERIOD_15MIN, "15m");
        map.put(Kline.PERIOD_30MIN, "30m");
        map.put(Kline.PERIOD_60MIN, "60m");
        map.put(Kline.PERIOD_4HOUR, "4h");
        map.put(Kline.PERIOD_1DAY, "1d");
        map.put(Kline.PERIOD_1WEEK, "1W");
        map.put(Kline.PERIOD_1MON, "1M");

        List<Kline> list = new ArrayList<Kline>();
        String symbol = symbolData;
        Item item = null;
        if(itemService != null){
            item = itemService.findByRemarks(symbolData);
            if (item == null) {
                return list;
            }
            symbol = item.getRemarks().toUpperCase();
        }
        try {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("symbol", symbol.toUpperCase());
            param.put("interval", map.get(period));
            param.put("limit", 1000);
            String result = HttpHelper.getJSONFromHttp(Config.mexc_kline, param, HttpMethodType.GET);
            JSONArray dataArray = JSON.parseArray(result);
            if (!dataArray.isEmpty()) {
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONArray realtimeJson = dataArray.getJSONArray(i);
                    Kline kline = new Kline();
                    if(item == null){
                        kline.setSymbol(symbol);
                    }else{
                        kline.setSymbol(item.getSymbol());
                    }
                    kline.setPeriod(period);
                    kline.setTs(realtimeJson.getLong(0));
                    kline.setOpen(realtimeJson.getDouble(1));
                    kline.setClose(realtimeJson.getDouble(4));
                    kline.setHigh(realtimeJson.getDouble(2));
                    kline.setLow(realtimeJson.getDouble(3));
                    kline.setVolume(realtimeJson.getDouble(5));
                    kline.setAmount(realtimeJson.getDouble(7));
                    list.add(kline);
                }
            }
        } catch (Exception e) {
            logger.error("fetch mexcKline failed", e);
        }
        return list;
    }
    /**
     *                                                  ?0             ?                                                                         ?     */
    @Override
    public Depth depthDecorator(String symbol, int maximum) {
        Depth depth = this.depth(symbol, maximum);
        Item item = itemService.findByRemarks(symbol);
        if ((depth == null || item.getAdjustmentValue() == 0) && (item.getMultiple() == 0 || item.getMultiple() == 1)) {
            return depth;
        }

        List<DepthEntry> asks = depth.getAsks();
        for (int i = 0; i < asks.size(); i++) {
            DepthEntry depthEntry = asks.get(i);
            if (item.getMultiple() > 0) {
                depthEntry.setAmount(Arith.mul(depthEntry.getAmount(), item.getMultiple()));
            } else {
                depthEntry.setAmount(depthEntry.getAmount());
            }
            depthEntry.setPrice(Arith.add(depthEntry.getPrice(), item.getAdjustmentValue()));
        }

        List<DepthEntry> bids = depth.getBids();
        for (int i = 0; i < bids.size(); i++) {
            DepthEntry depthEntry = bids.get(i);
            if (item.getMultiple() > 0) {
                depthEntry.setAmount(Arith.mul(depthEntry.getAmount(), item.getMultiple()));
            } else {
                depthEntry.setAmount(depthEntry.getAmount());
            }
            depthEntry.setPrice(Arith.add(depthEntry.getPrice(), item.getAdjustmentValue()));
        }
        return depth;
    }

    @Override
    public Depth depth(String symbol, int maximum) {
        if(!spiderService.isHuobi()){
            return mexcdepth(symbol);
        }
        boolean current_lock = false;
        if (StringUtils.isNullOrEmpty(symbol)) {
            return null;
        }
        if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
            ThreadUtils.sleep(sleep);
            if (maximum >= 100) {
                return null;
            } else {
                return this.depth(symbol, ++maximum);
            }
        } else {
            try {
                current_lock = true;
                lock = true;
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("symbol", symbol);
                param.put("type", "step2");

                String result = HttpHelper.getJSONFromHttp(Config.url + Config.depth, param, HttpMethodType.GET);
                JSONObject resultJson = JSON.parseObject(result);
                String status = resultJson.getString("status");
                if ("ok".equals(status)) {
                    JSONObject dataJson = resultJson.getJSONObject("tick");
                    Long ts = resultJson.getLongValue("ts");
                    Depth depth = new Depth();

                    Item item = itemService.findByRemarks(symbol);
                    if (item == null) {
                        return null;
                    }
                    depth.setSymbol(item.getSymbol());
                    depth.setTs(ts);

                    JSONArray bidsArray = dataJson.getJSONArray("bids");
                    for (int i = 0; i < bidsArray.size(); i++) {

                        JSONArray object = (JSONArray) bidsArray.get(i);
                        DepthEntry depthEntry = new DepthEntry();
                        depthEntry.setPrice(object.getDouble(0));
                        depthEntry.setAmount(object.getDouble(1));
                        depth.getBids().add(depthEntry);

                    }

                    JSONArray asksArray = dataJson.getJSONArray("asks");
                    for (int i = 0; i < asksArray.size(); i++) {
                        JSONArray object = (JSONArray) asksArray.get(i);
                        DepthEntry depthEntry = new DepthEntry();
                        depthEntry.setPrice(object.getDouble(0));
                        depthEntry.setAmount(object.getDouble(1));
                        depth.getAsks().add(depthEntry);

                    }

                    return depth;
                }

            } catch (Exception e) {
                logger.error("error", e);
            } finally {
                if (current_lock) {
                    lock = false;
                    last_time = new Date();
                }

            }
        }
        return null;
    }


    public Depth mexcdepth(String symbol) {
        Item item = null;
        if(itemService != null){
            item = itemService.findByRemarks(symbol);
            if (item == null) {
                return null;
            }
            symbol = item.getRemarks().toUpperCase();
        }
        if (StringUtils.isEmptyString(symbol)) {
            return null;
        }

        try {
            lock = true;
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("symbol", symbol.toUpperCase());
            param.put("limit", "50");

            String result = HttpHelper.getJSONFromHttp(Config.mexc_depth, param, HttpMethodType.GET);
            JSONObject resultJson = JSON.parseObject(result);
            Long ts = resultJson.getLong("timestamp");
            if (ts != null) {
                Depth depth = new Depth();
                if (item != null){
                    depth.setSymbol(item.getSymbol());
                }else{
                    depth.setSymbol(symbol);
                }
                depth.setTs(ts);
                JSONArray bidsArray = resultJson.getJSONArray("bids");
                for (int i = 0; i < bidsArray.size(); i++) {
                    JSONArray object = (JSONArray) bidsArray.get(i);
                    DepthEntry depthEntry = new DepthEntry();
                    depthEntry.setPrice(object.getDouble(0));
                    depthEntry.setAmount(object.getDouble(1));
                    depth.getBids().add(depthEntry);
                }

                JSONArray asksArray = resultJson.getJSONArray("asks");
                for (int i = 0; i < asksArray.size(); i++) {
                    JSONArray object = (JSONArray) asksArray.get(i);
                    DepthEntry depthEntry = new DepthEntry();
                    depthEntry.setPrice(object.getDouble(0));
                    depthEntry.setAmount(object.getDouble(1));
                    depth.getAsks().add(depthEntry);

                }

                return depth;
            }

        } catch (Exception e) {
            logger.error("error", e);
        }
        return null;
    }

    /**
     *                                                            ?                                                                         ?     */
    @Override
    public Trade tradeDecorator(String symbol, int maximum) {
        Trade trade = this.trade(symbol, maximum);
        Item item = itemService.findByRemarks(symbol);
        if ((trade == null || item.getAdjustmentValue() == 0) && (item.getMultiple() == 0 || item.getMultiple() == 1)) {
            return trade;
        }
        List<TradeEntry> data = trade.getData();
        for (int i = 0; i < data.size(); i++) {
            TradeEntry tradeEntry = data.get(i);

            /**
             *                                                             ?                   ?             */
            if (item.getMultiple() > 0) {
                tradeEntry.setAmount(Arith.mul(tradeEntry.getAmount(), item.getMultiple()));
            } else {
                tradeEntry.setAmount(tradeEntry.getAmount());
            }
            tradeEntry.setPrice(Arith.add(tradeEntry.getPrice(), item.getAdjustmentValue()));
        }
        return trade;

    }

    @Override
    public Trade trade(String symbol, int maximum) {
        if(!spiderService.isHuobi()){
            return mexcTrade(symbol);
        }
        boolean current_lock = false;
        if (StringUtils.isNullOrEmpty(symbol)) {
            return null;
        }
        if (lock || (System.currentTimeMillis() - last_time.getTime()) < interval) {
            ThreadUtils.sleep(sleep);
            if (maximum >= 100) {

                return null;
            } else {
                return this.trade(symbol, ++maximum);
            }
        } else {
            try {
                current_lock = true;
                lock = true;
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("symbol", symbol);

                String result = HttpHelper.getJSONFromHttp(Config.url + Config.trade, param, HttpMethodType.GET);
                JSONObject resultJson = JSON.parseObject(result);
                String status = resultJson.getString("status");
                if ("ok".equals(status)) {
                    JSONObject dataJson = resultJson.getJSONObject("tick");
                    Long ts = resultJson.getLongValue("ts");

                    Trade trade = new Trade();

                    Item item = itemService.findByRemarks(symbol);
                    if (item == null) {
                        return null;
                    }
                    trade.setSymbol(item.getSymbol());
                    trade.setTs(ts);

                    JSONArray dataArray = dataJson.getJSONArray("data");
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject object = dataArray.getJSONObject(i);
                        TradeEntry tradeEntry = new TradeEntry();
                        tradeEntry.setTs(object.getLong("ts"));
                        tradeEntry.setPrice(object.getDouble("price"));
                        tradeEntry.setAmount(object.getDouble("amount"));
                        tradeEntry.setDirection(object.getString("direction"));
                        trade.getData().add(tradeEntry);

                    }
                    return trade;
                }

            } catch (Exception e) {
                logger.error("error", e);
            } finally {
                if (current_lock) {
                    lock = false;
                    last_time = new Date();
                }

            }
        }
        return null;
    }

    public Trade mexcTrade(String symbol) {
        Item item = null;
        if(itemService != null){
            item = itemService.findByRemarks(symbol);
            if (item == null) {
                return null;
            }
            symbol = item.getRemarks().toUpperCase();
        }
        if (StringUtils.isEmptyString(symbol)) {
            return null;
        }

        try {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("symbol", symbol.toUpperCase());
            param.put("limit", 50);
            String result = HttpHelper.getJSONFromHttp(Config.mexc_trade, param, HttpMethodType.GET);
            JSONArray dataArray  = JSONArray.parseArray(result);
            if (!dataArray.isEmpty()) {
                Long ts = System.currentTimeMillis();
                Trade trade = new Trade();
                if(item != null){
                    trade.setSymbol(item.getSymbol());
                }else{
                    trade.setSymbol(symbol);
                }
                trade.setTs(ts);
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONObject object = dataArray.getJSONObject(i);
                    TradeEntry tradeEntry = new TradeEntry();
                    tradeEntry.setTs(object.getLong("time"));
                    tradeEntry.setPrice(object.getDouble("price"));
                    tradeEntry.setAmount(object.getDouble("qty"));
                    String tradeType = object.getString("tradeType");
                    if (tradeType.equalsIgnoreCase("ASK")) {
                        tradeEntry.setDirection("sell");
                    }else{
                        tradeEntry.setDirection("buy");
                    }
                    trade.getData().add(tradeEntry);

                }
                return trade;
            }

        } catch (Exception e) {
            logger.error("error", e);
        }
        return null;
    }

    @Override
    public List<Symbols> symbols() {
        List<Symbols> list = new ArrayList<Symbols>();
        boolean current_lock = false;
        if (lock || (new Date().getTime() - last_time.getTime()) < interval) {

            return list;
        } else {
            try {
                current_lock = true;
                lock = true;
                Map<String, Object> param = new HashMap<String, Object>();
                String result = HttpHelper.getJSONFromHttp(Config.url + Config.symbols, param, HttpMethodType.GET);
                JSONObject resultJson = JSON.parseObject(result);
                String status = resultJson.getString("status");
                if ("ok".equals(status)) {
                    JSONArray dataArray = resultJson.getJSONArray("data");
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject realtimeJson = dataArray.getJSONObject(i);
                        Symbols symbols = new Symbols();
                        symbols.setBaseCurrency(realtimeJson.getString("base-currency"));
                        symbols.setQuoteCurrency(realtimeJson.getString("quote-currency"));
                        symbols.setLeverageRatio(realtimeJson.getBigDecimal("leverage-ratio"));
                        symbols.setPricePrecision(realtimeJson.getIntValue("price-precision"));
                        symbols.setState(realtimeJson.getString("state"));
                        symbols.setSymbol(realtimeJson.getString("symbol"));
                        list.add(symbols);
                    }

                } else {
                    logger.error(" symbols()error, resultJson [ " + resultJson.toJSONString() + " ]");
                }
            } catch (Exception e) {
                logger.error("error", e);
            } finally {
                if (current_lock) {
                    lock = false;
                    last_time = new Date();
                }

            }
        }
        return list;
    }

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    public boolean isXueQiu(String symbol) {
        //                                                                                               ?                                                             ?
        if("IYW,UUP".contains(symbol)){
            return true;
        }
        String type = itemService.findBySymbol(symbol).getType();
        return type.contains("stock") || (type.equalsIgnoreCase(Item.indices));
    }

    public boolean isTW(String symbol) {
        String type = itemService.findBySymbol(symbol).getType();
        return Item.TW_STOCKS.equalsIgnoreCase(type);
    }
    public boolean isXinlang(String symbol) {
        if("XAUUSD,XAGUSD,OIL".contains(symbol)){
            return false;

        }
        Item bySymbol = itemService.findBySymbol(symbol);
        String type = bySymbol.getType();
        return Item.forex.contains(type) && Item.forex.equals(bySymbol.getCategory());
    }

    public List<Realtime> realtimeSingle(String symbol) {
        if (isXueQiu(symbol)) {
            return xueQiuDataService.realtimeSingle(symbol);
        }
        if (isXinlang(symbol)) {
            return xinLangDataService.realtimeSingle(symbol);
        }
        return freeForexRateService.fetchRealtime(symbol);
    }

    @Override
    public List<Realtime> realtimeCryptos(String symbols) {
        if(spiderService.isHuobi()){
            return this.spiderService.fetchRealtimeList(symbols);
        }else{
             return spiderService.fetchRealtimeList(symbols, REALTIME_HASH_BAK);
        }
    }


    @Override
    public List<Realtime> realtime(String symbols) {
        List<Realtime> realtimeList = freeForexRateService.fetchRealtime(symbols);
        if (!realtimeList.isEmpty()) {
            return realtimeList;
        }
        List<Realtime> fallback = Collections.synchronizedList(new ArrayList<>());
        Splitter.on(",").omitEmptyStrings().splitToList(symbols).parallelStream().map(this::realtimeSingle).forEach(fallback::addAll);
        return fallback;
    }

    @Override
    public List<Realtime> realtimeXueQiu(String remarks) {
        return xueQiuDataService.realtimeSingle(remarks);
    }

    @Override
    public List<Realtime> realtimeXinLang(String symbols) {
        return xinLangDataService.realtimeSingle(symbols);
    }

    @Override
    public List<Realtime> realtimeTw(String remarks) {
        return twDataService.realtimeSingle(remarks);
    }

    @Override
    public List<Map<String, List<Kline>>> getKlines(String symbols) {
        List<String> remarksList = Splitter.on(",").omitEmptyStrings().splitToList(symbols).stream().map(s -> itemService.findBySymbol(s).getRemarks()).collect(Collectors.toList());;
        String remarks = remarksList.stream().collect(Collectors.joining(","));
        List<Map<String, List<Kline>>>  list  = new ArrayList<>();
        try {
            Map<String, String> param = new HashMap<>();
            param.put("currency", remarks);
            String result = HttpHelper.getJSONFromHttpNew(TraderMadeOptions.kline, param, HttpMethodType.GET);
            JSONObject resultJson = JSON.parseObject(result);
            String code = resultJson.getString("code");
            if ("ok".equals(code)) {
                JSONArray dataArray = resultJson.getJSONArray("data");
                for (int i = 0; i < dataArray.size(); i++) {
                    Map<String, List<Kline>> map = new HashMap<>();
                    JSONObject klineJson = dataArray.getJSONObject(i);
                    for(String s : remarksList){
                        if(klineJson.containsKey(s)){
                            JSONObject jsonObject = klineJson.getJSONObject(s);
                            Set<String> periods = jsonObject.keySet();
                            for(String period : periods){
                                List<Kline> javaList = jsonObject.getJSONArray(period).toJavaList(Kline.class);
                                javaList.forEach(k->k.setSymbol(itemService.getSymbolByRemarks(s)));
                                map.put(period, javaList);
                            }
                            list.add(map);
                        }
                    }

                }
            } else {
                logger.error(" getKlines error");
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
        return list;
    }

    /**
     * 1day                                   ?  ?             ?1 ?     * 1week,1mon                             ?  ?             ?5 ?     *              ?350 ?     */
    @Override
    public Map<String, List<Kline>> getDailyWeekMonthHistory(String symbol) {
        if (isTW(symbol)) {
            return twDataService.getDailyWeekMonthHistory(symbol);
        }
        if (isXinlang(symbol)) {
            return xinLangDataService.getDailyWeekMonthHistory(symbol);
        }

        Map<String, List<Kline>> map = new HashMap<>();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        f.setTimeZone(TimeZone.getTimeZone(UTCDateUtils.GMT_TIME_ZONE));
        Date now = new Date();
        for (int i = 0; i < TraderMadeOptions.weekAndMonthPeriod; i++) {
            String startDate = UTCDateUtils.addYear(now, -1);
            String endDate = f.format(now);
            now = UTCDateUtils.toDate(startDate, "yyyy-MM-dd");

            Map<String, String> param = new HashMap<>();
            param.put("currency", symbol);
            param.put("start_date", startDate);
            param.put("end_date", endDate);
            param.put("format", "records");
            param.put("api_key", TraderMadeOptions.apiKey);
            String json = null;
            try {
                json = HttpHelper.getJSONFromHttpNew(TraderMadeOptions.timeseries, param, HttpMethodType.GET);
            } catch (Exception e) {
                logger.error("                                                           ?{} ", param);
                continue;
            }

            JSONObject resultJson = JSON.parseObject(json);
            JSONArray dataArray = resultJson.getJSONArray("quotes");
            if (dataArray.size() > 0) {
                List<TimeseriesResult> list = JSONObject.parseArray(JSONObject.toJSONString(dataArray), TimeseriesResult.class);
                Map<String, List<TimeseriesResult>> monthMap = new LinkedHashMap<>();
                Map<String, List<TimeseriesResult>> weekMap = new LinkedHashMap<>();
                for (TimeseriesResult result : list) {
                    String month = result.getDate().substring(0, 7);
                    if (monthMap.containsKey(month)) {
                        monthMap.get(month).add(result);
                    } else {
                        List<TimeseriesResult> monthList = new ArrayList<>();
                        monthList.add(result);
                        monthMap.put(month, monthList);
                    }

                    String firstDateOfWeek = UTCDateUtils.getFirstDateOfWeek(UTCDateUtils.toDate(result.getDate(), "yyyy-MM-dd"));
                    if (weekMap.containsKey(firstDateOfWeek)) {
                        weekMap.get(firstDateOfWeek).add(result);
                    } else {
                        List<TimeseriesResult> weekList = new ArrayList<>();
                        weekList.add(result);
                        weekMap.put(firstDateOfWeek, weekList);
                    }
                }

                map.put(Kline.PERIOD_1WEEK, buildOneWeekPeriod(weekMap, symbol));
                map.put(Kline.PERIOD_1MON, buildOneMonthPeriod(monthMap, symbol));

                if (0 == i) {
                    map.put(Kline.PERIOD_1DAY, buildOneDayPeriod(list, symbol));
                }
            }
        }
        try {
            List<Kline> dayList = map.get(Kline.PERIOD_1DAY);
            if (CollectionUtil.isNotEmpty(dayList)) {
                map.put(Kline.PERIOD_5DAY, klineService.calculateKline(symbol, 5, Kline.PERIOD_5DAY, dayList));
            }
            List<Kline> montList = map.get(Kline.PERIOD_1MON);

            if (CollectionUtil.isNotEmpty(montList)) {
                map.put(Kline.PERIOD_QUARTER, klineService.calculateKline(symbol, 3, Kline.PERIOD_1MON, montList));
            }

        } catch (Exception e) {
            logger.error("calculate forex period data failed", e);
        }

        //return map;
        throw new RuntimeException("not supported");
    }


    /**
     *                                           ?     */
    @Override
    public Map<String, List<Kline>> getHourlyAndMinuteHistory(String symbol) {
        if(isTW(symbol)){
            return twDataService.getHourlyAndMinuteHistory(symbol);
        }
        Map<String, List<Kline>> map = new HashMap<>();

        List<Kline> fourHourlyList = getTimeseriesForFourHourly(symbol);
        map.put(KlineConstant.PERIOD_4HOUR, fourHourlyList);

        List<Kline> oneHourlyList = getTimeseriesForOneHourly(symbol);
        map.put(KlineConstant.PERIOD_60MIN, oneHourlyList);

        List<Kline> twoHourlyList = getTimeseriesForTwoHourly(symbol);
        if (CollectionUtil.isEmpty(twoHourlyList)) {
            try {
                List<Kline> hourList = map.get(Kline.PERIOD_60MIN);
                if (CollectionUtil.isNotEmpty(hourList)) {
                    map.put(Kline.PERIOD_2HOUR, klineService.calculateKline(symbol, 2, Kline.PERIOD_2HOUR, hourList));
                }
            } catch (Exception e) {
                logger.error("                            ?120mink                            ", e);
            }
        } else {
            map.put(KlineConstant.PERIOD_2HOUR, twoHourlyList);
        }

        List<Kline> thirtyMinuteList = getTimeseriesThirtyMinute(symbol);
        map.put(KlineConstant.PERIOD_30MIN, thirtyMinuteList);

        List<Kline> fifteenMinuteList = getTimeseriesFifteenMinute(symbol);
        map.put(KlineConstant.PERIOD_15MIN, fifteenMinuteList);

        List<Kline> fiveMinuteList = getTimeseriesFiveMinute(symbol);
        map.put(KlineConstant.PERIOD_5MIN, fiveMinuteList);

        List<Kline> oneMinuteList = getTimeseriesOneMinute(symbol);
        map.put(KlineConstant.PERIOD_1MIN, oneMinuteList);

        //return map;
        throw new RuntimeException("not supported");
    }

    public List<Kline> buildOneDayPeriod(List<TimeseriesResult> list, String currency) {
       if (isXinlang(currency)) {
            return xinLangDataService.buildOneDayPeriod(currency);
        }
        List<Kline> resList = new ArrayList<>();
        for (TimeseriesResult result : list) {
            Kline kline = new Kline();
            kline.setSymbol(currency);
            kline.setPeriod(Kline.PERIOD_1DAY);
            //                              ?TODO
            kline.setTs(UTCDateUtils.toDate(result.getDate(), "yyyy-MM-dd").getTime());
            kline.setOpen(result.getOpen());
            kline.setClose(result.getClose());
            kline.setHigh(result.getHigh());
            kline.setLow(result.getLow());
            kline.setVolume(0);
            resList.add(kline);
        }

        return resList;
    }

    public List<Kline> buildOneWeekPeriod(Map<String, List<TimeseriesResult>> weekMap, String currency) {

        List<Kline> resList = new ArrayList<>();
        for (List<TimeseriesResult> list : weekMap.values()) {
            double high = list.stream().map(TimeseriesResult::getHigh).filter(Objects::nonNull).max(Double::compare).orElse(0D);
            double low = list.stream().map(TimeseriesResult::getLow).filter(Objects::nonNull).min(Double::compare).orElse(0D);
            Kline kline = new Kline();
            kline.setSymbol(currency);
            kline.setPeriod(Kline.PERIOD_1WEEK);
            //              ?            kline.setTs(UTCDateUtils.toDate(list.get(list.size() - 1).getDate()).getTime());
            kline.setOpen(list.get(0).getOpen());
            kline.setClose(list.get(list.size() - 1).getClose());
            kline.setHigh(high);
            kline.setLow(low);
            kline.setVolume(0);
            resList.add(kline);
        }
        return resList;
    }

    public List<Kline> buildOneMonthPeriod(Map<String, List<TimeseriesResult>> monthMap, String currency) {
        List<Kline> resList = new ArrayList<>();
        for (List<TimeseriesResult> list : monthMap.values()) {
            double high = list.stream().map(TimeseriesResult::getHigh).filter(Objects::nonNull).max(Double::compare).orElse(0D);
            double low = list.stream().map(TimeseriesResult::getLow).filter(Objects::nonNull).min(Double::compare).orElse(0D);
            Kline kline = new Kline();
            kline.setSymbol(currency);
            kline.setPeriod(Kline.PERIOD_1MON);
            //              ?            kline.setTs(UTCDateUtils.toDate(list.get(list.size() - 1).getDate()).getTime());
            kline.setOpen(list.get(0).getOpen());
            kline.setClose(list.get(list.size() - 1).getClose());
            kline.setHigh(high);
            kline.setLow(low);
            kline.setVolume(0);
            resList.add(kline);
        }

        return resList;
    }

    public List<Kline> buildQuarterPeriod(Map<String, List<TimeseriesResult>> quarterMap, String currency) {
        List<Kline> resList = new ArrayList<>();
        for (List<TimeseriesResult> list : quarterMap.values()) {
            double high = list.stream().map(TimeseriesResult::getHigh).filter(Objects::nonNull).max(Double::compare).orElse(0D);
            double low = list.stream().map(TimeseriesResult::getLow).filter(Objects::nonNull).min(Double::compare).orElse(0D);
            Kline kline = new Kline();
            kline.setSymbol(currency);
            kline.setPeriod(Kline.PERIOD_QUARTER);
            //              ?            kline.setTs(UTCDateUtils.toDate(list.get(list.size() - 1).getDate()).getTime());
            kline.setOpen(list.get(0).getOpen());
            kline.setClose(list.get(list.size() - 1).getClose());
            kline.setHigh(high);
            kline.setLow(low);
            kline.setVolume(0);
            resList.add(kline);
        }
        return resList;
    }

    public List<Kline> buildYearPeriod(Map<String, List<TimeseriesResult>> yearMap, String currency) {
        List<Kline> resList = new ArrayList<>();
        for (List<TimeseriesResult> list : yearMap.values()) {
            double high = list.stream().map(TimeseriesResult::getHigh).filter(Objects::nonNull).max(Double::compare).orElse(0D);
            double low = list.stream().map(TimeseriesResult::getLow).filter(Objects::nonNull).min(Double::compare).orElse(0D);
            Kline kline = new Kline();
            kline.setSymbol(currency);
            kline.setPeriod(Kline.PERIOD_YEAR);
            //              ?            kline.setTs(UTCDateUtils.toDate(list.get(list.size() - 1).getDate()).getTime());
            kline.setOpen(list.get(0).getOpen());
            kline.setClose(list.get(list.size() - 1).getClose());
            kline.setHigh(high);
            kline.setLow(low);
            kline.setVolume(0);
            resList.add(kline);
        }
        return resList;
    }

    /**
     * Hourly
     * 4hourly 3 ?     */
    @Override
    public List<Kline> getTimeseriesForFourHourly(String currency) {
        if (isXinlang(currency)) {
            return xinLangDataService.getTimeseriesForFourHourly(currency);

        }
        List<Kline> resList = new ArrayList<>();
        //                                                                                                                          ?               ?
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(UTCDateUtils.GMT_TIME_ZONE));
        Date now = new Date();
        for (int i = 0; i < TraderMadeOptions.fourHourlyPeriod; i++) {
            String startDate = UTCDateUtils.addDay(now, -30);
            String endDate = f.format(now);
            now = UTCDateUtils.toDate(startDate, "yyyy-MM-dd-HH:mm");

            Map<String, String> param = new HashMap<>();
            param.put("currency", currency);
            param.put("start_date", startDate);
            param.put("end_date", endDate);
            param.put("interval", "hourly");
            param.put("period", "4");
            param.put("format", "records");
            param.put("api_key", TraderMadeOptions.apiKey);
            String json = HttpHelper.getJSONFromHttpNew(TraderMadeOptions.timeseries, param, HttpMethodType.GET);
            JSONObject resultJson = JSON.parseObject(json);
            JSONArray dataArray = resultJson.getJSONArray("quotes");
            if (dataArray.size() > 0) {
                List<TimeseriesResult> list = JSONObject.parseArray(JSONObject.toJSONString(dataArray), TimeseriesResult.class);
                for (TimeseriesResult result : list) {
                    Kline kline = new Kline();
                    kline.setSymbol(currency);
                    kline.setPeriod(Kline.PERIOD_4HOUR);
                    //              ?                    kline.setTs(UTCDateUtils.toDate(result.getDate(), "yyyy-MM-dd HH:mm").getTime());
                    kline.setOpen(result.getOpen());
                    kline.setClose(result.getClose());
                    kline.setHigh(result.getHigh());
                    kline.setLow(result.getLow());
                    kline.setVolume(0);
                    resList.add(kline);
                }
            }
        }
        return resList;
    }

    /**
     * Hourly
     * 1hourly 1 ?     */
    @Override
    public List<Kline> getTimeseriesForOneHourly(String currency) {
        if (isXinlang(currency)) {
            return xinLangDataService.getTimeseriesForOneHourly(currency);
        }
        List<Kline> resList = new ArrayList<>();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(UTCDateUtils.GMT_TIME_ZONE));
        Date now = new Date();
        String startDate = UTCDateUtils.addDay(now, -30);
        String endDate = f.format(now);

        Map<String, String> param = new HashMap<>();
        param.put("currency", currency);
        param.put("start_date", startDate);
        param.put("end_date", endDate);
        param.put("interval", "hourly");
        param.put("period", "1");
        param.put("format", "records");
        param.put("api_key", TraderMadeOptions.apiKey);
        String json = HttpHelper.getJSONFromHttpNew(TraderMadeOptions.timeseries, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(json);
        JSONArray dataArray = resultJson.getJSONArray("quotes");
        if (dataArray.size() > 0) {
            List<TimeseriesResult> list = JSONObject.parseArray(JSONObject.toJSONString(dataArray), TimeseriesResult.class);
            for (TimeseriesResult result : list) {
                Kline kline = new Kline();
                kline.setSymbol(currency);
                kline.setPeriod(Kline.PERIOD_60MIN);
                //              ?                kline.setTs(UTCDateUtils.toDate(result.getDate(), "yyyy-MM-dd HH:mm").getTime());
                kline.setOpen(result.getOpen());
                kline.setClose(result.getClose());
                kline.setHigh(result.getHigh());
                kline.setLow(result.getLow());
                kline.setVolume(0);
                resList.add(kline);
            }
        }
        return resList;
    }

    @Override
    public List<Kline> getTimeseriesForTwoHourly(String currency) {
        if (isXinlang(currency)) {
            return xinLangDataService.getTimeseriesForTwoHourly(currency);
        }
        return Lists.newArrayList();
    }

    /**
     * Minute
     * 30minute 10 ?     * 15minute 5 ?     * 5minute  2 ?     * 1minute  1 ?     */
    @Override
    public List<Kline> getTimeseriesOneMinute(String currency) {
        if (isXinlang(currency)) {
            return xinLangDataService.getTimeseriesOneMinute(currency);
        }
        List<Kline> resList = new ArrayList<>();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(UTCDateUtils.GMT_TIME_ZONE));
        Date now = new Date();
        String startDate = UTCDateUtils.addDay(now, -1);
        String endDate = f.format(now);

        Map<String, String> param = new HashMap<>();
        param.put("currency", currency);
        param.put("start_date", startDate);
        param.put("end_date", endDate);
        param.put("interval", "minute");
        param.put("period", "1");
        param.put("format", "records");
        param.put("api_key", TraderMadeOptions.apiKey);
        String json = HttpHelper.getJSONFromHttpNew(TraderMadeOptions.timeseries, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(json);
        JSONArray dataArray = resultJson.getJSONArray("quotes");
        if (dataArray.size() > 0) {
            List<TimeseriesResult> list = JSONObject.parseArray(JSONObject.toJSONString(dataArray), TimeseriesResult.class);
            for (TimeseriesResult result : list) {
                Kline kline = new Kline();
                kline.setSymbol(currency);
                kline.setPeriod(Kline.PERIOD_1MIN);
                //              ?                kline.setTs(UTCDateUtils.toDate(result.getDate(), "yyyy-MM-dd HH:mm").getTime());
                kline.setOpen(result.getOpen());
                kline.setClose(result.getClose());
                kline.setHigh(result.getHigh());
                kline.setLow(result.getLow());
                kline.setVolume(0);
                resList.add(kline);
            }
        }
        return resList;
    }

    /**
     * Minute
     * 30minute 10 ?     * 15minute 5 ?     * 5minute  2 ?     * 1minute  1 ?     */
    @Override
    public List<Kline> getTimeseriesFiveMinute(String currency) {
        if (isXinlang(currency)) {
            return xinLangDataService.getTimeseriesFiveMinute(currency);
        }
        List<Kline> resList = new ArrayList<>();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(UTCDateUtils.GMT_TIME_ZONE));
        Date now = new Date();
        String startDate = UTCDateUtils.addDay(now, -2);
        String endDate = f.format(now);

        Map<String, String> param = new HashMap<>();
        param.put("currency", currency);
        param.put("start_date", startDate);
        param.put("end_date", endDate);
        param.put("interval", "minute");
        param.put("period", "5");
        param.put("format", "records");
        param.put("api_key", TraderMadeOptions.apiKey);
        String json = HttpHelper.getJSONFromHttpNew(TraderMadeOptions.timeseries, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(json);
        JSONArray dataArray = resultJson.getJSONArray("quotes");
        if (dataArray.size() > 0) {
            List<TimeseriesResult> list = JSONObject.parseArray(JSONObject.toJSONString(dataArray), TimeseriesResult.class);
            for (TimeseriesResult result : list) {
                Kline kline = new Kline();
                kline.setSymbol(currency);
                kline.setPeriod(Kline.PERIOD_5MIN);
                //              ?                kline.setTs(UTCDateUtils.toDate(result.getDate(), "yyyy-MM-dd HH:mm").getTime());
                kline.setOpen(result.getOpen());
                kline.setClose(result.getClose());
                kline.setHigh(result.getHigh());
                kline.setLow(result.getLow());
                kline.setVolume(0);
                resList.add(kline);
            }
        }
        return resList;
    }

    /**
     * Minute
     * 30minute 10 ?     * 15minute 5 ?     * 5minute  2 ?     * 1minute  1 ?     */
    @Override
    public List<Kline> getTimeseriesFifteenMinute(String currency) {
        if (isXinlang(currency)) {
            return xinLangDataService.getTimeseriesFifteenMinute(currency);
        }
        List<Kline> resList = new ArrayList<>();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(UTCDateUtils.GMT_TIME_ZONE));
        Date now = new Date();
        for (int i = 0; i < TraderMadeOptions.fifteenMinutePeriod; i++) {
            String startDate = UTCDateUtils.addDay(now, -2);
            String endDate = f.format(now);
            now = UTCDateUtils.toDate(startDate, "yyyy-MM-dd-HH:mm");
            Map<String, String> param = new HashMap<>();
            param.put("currency", currency);
            param.put("start_date", startDate);
            param.put("end_date", endDate);
            param.put("interval", "minute");
            param.put("period", "15");
            param.put("format", "records");
            param.put("api_key", TraderMadeOptions.apiKey);
            String json = HttpHelper.getJSONFromHttpNew(TraderMadeOptions.timeseries, param, HttpMethodType.GET);
            JSONObject resultJson = JSON.parseObject(json);
            JSONArray dataArray = resultJson.getJSONArray("quotes");
            if (dataArray.size() > 0) {
                List<TimeseriesResult> list = JSONObject.parseArray(JSONObject.toJSONString(dataArray), TimeseriesResult.class);
                for (TimeseriesResult result : list) {
                    Kline kline = new Kline();
                    kline.setSymbol(currency);
                    kline.setPeriod(Kline.PERIOD_15MIN);
                    //              ?                    kline.setTs(UTCDateUtils.toDate(result.getDate(), "yyyy-MM-dd HH:mm").getTime());
                    kline.setOpen(result.getOpen());
                    kline.setClose(result.getClose());
                    kline.setHigh(result.getHigh());
                    kline.setLow(result.getLow());
                    kline.setVolume(0);
                    resList.add(kline);
                }
            }
        }
        return resList;
    }

    /**
     * Minute
     * 30minute 10 ?     * 15minute 5 ?     * 5minute  2 ?     * 1minute  1 ?     */
    @Override
    public List<Kline> getTimeseriesThirtyMinute(String currency) {
        if (isXinlang(currency)) {
            return xinLangDataService.getTimeseriesThirtyMinute(currency);
        }
        List<Kline> resList = new ArrayList<>();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(UTCDateUtils.GMT_TIME_ZONE));
        Date now = new Date();
        for (int i = 0; i < TraderMadeOptions.thirtyMinutePeriod; i++) {
            String startDate = UTCDateUtils.addDay(now, -2);
            String endDate = f.format(now);
            now = UTCDateUtils.toDate(startDate, "yyyy-MM-dd-HH:mm");
            Map<String, String> param = new HashMap<>();
            param.put("currency", currency);
            param.put("start_date", startDate);
            param.put("end_date", endDate);
            param.put("interval", "minute");
            param.put("period", "15");
            param.put("format", "records");
            param.put("api_key", TraderMadeOptions.apiKey);
            String json = HttpHelper.getJSONFromHttpNew(TraderMadeOptions.timeseries, param, HttpMethodType.GET);
            JSONObject resultJson = JSON.parseObject(json);
            JSONArray dataArray = resultJson.getJSONArray("quotes");
            if (dataArray.size() > 0) {
                List<TimeseriesResult> list = JSONObject.parseArray(JSONObject.toJSONString(dataArray), TimeseriesResult.class);
                for (TimeseriesResult result : list) {
                    Kline kline = new Kline();
                    kline.setSymbol(currency);
                    kline.setPeriod(Kline.PERIOD_30MIN);
                    //              ?                    kline.setTs(UTCDateUtils.toDate(result.getDate(), "yyyy-MM-dd HH:mm").getTime());
                    kline.setOpen(result.getOpen());
                    kline.setClose(result.getClose());
                    kline.setHigh(result.getHigh());
                    kline.setLow(result.getLow());
                    kline.setVolume(0);
                    resList.add(kline);
                }
            }
        }
        return resList;
    }
}

