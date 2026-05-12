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

/**
 * 火币数据服务实现类
 * <p>
 * 负责从火币(Huobi)和抹茶(MEXC)交易所获取加密货币实时数据、K线数据、深度数据和交易数据
 * 同时支持股票(雪球)、外汇(新浪)、台股等其他数据源的适配调用
 * 
 * <p>核心功能：
 * <ul>
 *   <li>获取加密货币K线数据 (kline)</li>
 *   <li>获取加密货币深度数据 (depth)</li>
 *   <li>获取加密货币交易数据 (trade)</li>
 *   <li>获取实时行情数据 (realtime)</li>
 *   <li>获取历史数据 (getDailyWeekMonthHistory, getHourlyAndMinuteHistory)</li>
 * </ul>
 */
@Component
public class HobiDataServiceImpl implements HobiDataService {
    private static Logger logger = LoggerFactory.getLogger(HobiDataServiceImpl.class);

    /** 雪球数据服务 - 用于获取股票数据 */
    @Autowired
    private XueQiuDataServiceImpl xueQiuDataService;
    /** 新浪数据服务 - 用于获取外汇数据 */
    @Autowired
    private XinLangDataServiceImpl xinLangDataService;
    /** 商品服务 - 用于查询商品配置信息 */
    @Autowired
    private ItemService itemService;
    /** K线服务 - 用于K线数据计算和处理 */
    @Autowired
    private KlineService klineService;
    /** 台股数据服务 - 用于获取台股数据 */
    @Autowired
    private TWDataServiceImpl twDataService;
    /** 免费外汇汇率服务 - 用于获取外汇实时数据 */
    @Autowired
    private FreeForexRateService freeForexRateService;
    /** 爬虫服务 - 用于从Redis获取实时数据 */
    @Autowired
    private SpiderService spiderService;

    /**
     * API调用间隔时间(毫秒) - 用于限流，防止频繁调用火币API被封禁
     */
    private int interval = 100;

    /**
     * 重试等待时间(毫秒) - 当API调用被限流时的等待时间
     */
    private int sleep = 100;

    /**
     * 上次API调用时间 - 用于控制调用频率
     */
    private volatile Date last_time = new Date();

    /**
     * API调用锁 - 用于防止并发调用，保证API调用的串行执行
     */
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

    /**
     * 获取加密货币K线数据
     * <p>
     * 根据指定的交易对、周期和数量从火币或抹茶交易所获取K线数据
     * 
     * @param symbolData 交易对标识(remarks字段值)
     * @param period 周期类型: 1min, 5min, 15min, 30min, 60min, 4hour, 1day, 1week, 1mon
     * @param num 获取数量(可选，为null时使用默认数量)
     * @param maximum 重试次数(内部使用，用于递归重试)
     * @return K线数据列表
     */
    @Override
    public List<Kline> kline(String symbolData, String period, Integer num, int maximum) {
        // 如果配置为抹茶数据源，调用抹茶K线接口
        if(!spiderService.isHuobi()){
            return mexcKline(symbolData, period);
        }
        
        List<Kline> list = new ArrayList<Kline>();
        Item item = itemService.findByRemarks(symbolData);
        if (item == null) {
            logger.warn("[kline] 未找到商品配置, symbolData={}", symbolData);
            return list;
        }
        
        boolean current_lock = false;
        // 限流控制：检查是否正在调用或调用间隔过短
        if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
            ThreadUtils.sleep(sleep);
            if (maximum >= 100) {
                logger.warn("[kline] 重试次数已达上限, 放弃获取K线数据, symbolData={}", symbolData);
                return list;
            } else {
                return this.kline(symbolData, period, num, ++maximum);
            }
        } else {
            try {
                current_lock = true;
                lock = true;
                
                // 构建请求参数
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("symbol", symbolData);
                param.put("period", period);
                
                // 根据周期设置默认获取数量
                if (num == null) {
                    if (Kline.PERIOD_1MIN.equals(period)) {
                        param.put("size", 1440);  // 1分钟K线，获取1440根(一天)
                    } else if (Kline.PERIOD_5MIN.equals(period)) {
                        param.put("size", 576);   // 5分钟K线，获取576根(约2天)
                    } else if (Kline.PERIOD_15MIN.equals(period)) {
                        param.put("size", 576);   // 15分钟K线，获取576根(约6天)
                    } else if (Kline.PERIOD_30MIN.equals(period)) {
                        param.put("size", 576);   // 30分钟K线，获取576根(约12天)
                    } else if (Kline.PERIOD_60MIN.equals(period)) {
                        param.put("size", 576);   // 1小时K线，获取576根(约24天)
                    } else if (Kline.PERIOD_4HOUR.equals(period)) {
                        param.put("size", 576);   // 4小时K线，获取576根(约96天)
                    } else if (Kline.PERIOD_1DAY.equals(period)) {
                        param.put("size", 500);   // 日线，获取500根(约1年半)
                    } else if (Kline.PERIOD_1MON.equals(period)) {
                        param.put("size", 500);   // 月线，获取500根(约40年)
                    } else if (Kline.PERIOD_1WEEK.equals(period)) {
                        param.put("size", 500);   // 周线，获取500根(约10年)
                    }
                } else {
                    param.put("size", num);
                }

                // 调用火币K线API
                String result = HttpHelper.getJSONFromHttp(Config.url + Config.kline, param, HttpMethodType.GET);
                JSONObject resultJson = JSON.parseObject(result);
                String status = resultJson.getString("status");
                
                if ("ok".equals(status)) {
                    JSONArray dataArray = resultJson.getJSONArray("data");
                    // 当只获取1根K线时从索引0开始，否则从索引1开始(跳过最新的不完整K线)
                    int start = (num != null && num == 1) ? 0 : 1;
                    
                    for (int i = start; i < dataArray.size(); i++) {
                        JSONObject realtimeJson = dataArray.getJSONObject(i);
                        Kline kline = new Kline();
                        kline.setSymbol(item.getSymbol());
                        kline.setPeriod(period);
                        kline.setTs(Long.valueOf(realtimeJson.getString("id") + "000"));  // 时间戳转换(秒转毫秒)
                        kline.setOpen(realtimeJson.getDouble("open"));
                        kline.setClose(realtimeJson.getDouble("close"));
                        kline.setHigh(realtimeJson.getDouble("high"));
                        kline.setLow(realtimeJson.getDouble("low"));
                        kline.setVolume(realtimeJson.getDouble("vol"));
                        list.add(kline);
                    }
                }
            } catch (Exception e) {
                logger.error("[kline] 获取K线数据失败, symbolData={}, period={}", symbolData, period, e);
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
     * 获取深度数据并应用调整因子
     * <p>
     * 对从交易所获取的深度数据应用调整值(adjustmentValue)和倍数(multiple)因子
     * 主要用于模拟盘或杠杆交易场景的数据调整
     * 
     * @param symbol 交易对标识(remarks字段值)
     * @param maximum 重试次数
     * @return 调整后的深度数据
     */
    @Override
    public Depth depthDecorator(String symbol, int maximum) {
        Depth depth = this.depth(symbol, maximum);
        Item item = itemService.findByRemarks(symbol);
        
        // 如果没有调整需求，直接返回原始深度数据
        if ((depth == null || item.getAdjustmentValue() == 0) && (item.getMultiple() == 0 || item.getMultiple() == 1)) {
            return depth;
        }

        // 调整卖盘(asks)数据
        List<DepthEntry> asks = depth.getAsks();
        for (int i = 0; i < asks.size(); i++) {
            DepthEntry depthEntry = asks.get(i);
            // 应用倍数因子
            if (item.getMultiple() > 0) {
                depthEntry.setAmount(Arith.mul(depthEntry.getAmount(), item.getMultiple()));
            }
            // 应用价格调整值
            depthEntry.setPrice(Arith.add(depthEntry.getPrice(), item.getAdjustmentValue()));
        }

        // 调整买盘(bids)数据
        List<DepthEntry> bids = depth.getBids();
        for (int i = 0; i < bids.size(); i++) {
            DepthEntry depthEntry = bids.get(i);
            // 应用倍数因子
            if (item.getMultiple() > 0) {
                depthEntry.setAmount(Arith.mul(depthEntry.getAmount(), item.getMultiple()));
            }
            // 应用价格调整值
            depthEntry.setPrice(Arith.add(depthEntry.getPrice(), item.getAdjustmentValue()));
        }
        
        return depth;
    }

    /**
     * 获取加密货币深度数据
     * <p>
     * 从火币或抹茶交易所获取指定交易对的盘口深度数据，包含买盘和卖盘信息
     * 
     * @param symbol 交易对标识(remarks字段值)
     * @param maximum 重试次数(内部使用)
     * @return 深度数据对象
     */
    @Override
    public Depth depth(String symbol, int maximum) {
        // 如果配置为抹茶数据源，调用抹茶深度接口
        if(!spiderService.isHuobi()){
            return mexcdepth(symbol);
        }
        
        boolean current_lock = false;
        if (StringUtils.isNullOrEmpty(symbol)) {
            logger.warn("[depth] 交易对为空");
            return null;
        }
        
        // 限流控制
        if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
            ThreadUtils.sleep(sleep);
            if (maximum >= 100) {
                logger.warn("[depth] 重试次数已达上限, 放弃获取深度数据, symbol={}", symbol);
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
                param.put("type", "step2");  // step2表示精度较低的深度数据

                // 调用火币深度API
                String result = HttpHelper.getJSONFromHttp(Config.url + Config.depth, param, HttpMethodType.GET);
                JSONObject resultJson = JSON.parseObject(result);
                String status = resultJson.getString("status");
                
                if ("ok".equals(status)) {
                    JSONObject dataJson = resultJson.getJSONObject("tick");
                    Long ts = resultJson.getLongValue("ts");
                    Depth depth = new Depth();

                    Item item = itemService.findByRemarks(symbol);
                    if (item == null) {
                        logger.warn("[depth] 未找到商品配置, symbol={}", symbol);
                        return null;
                    }
                    depth.setSymbol(item.getSymbol());
                    depth.setTs(ts);

                    // 解析买盘数据(bids)
                    JSONArray bidsArray = dataJson.getJSONArray("bids");
                    for (int i = 0; i < bidsArray.size(); i++) {
                        JSONArray object = (JSONArray) bidsArray.get(i);
                        DepthEntry depthEntry = new DepthEntry();
                        depthEntry.setPrice(object.getDouble(0));   // 价格
                        depthEntry.setAmount(object.getDouble(1));  // 数量
                        depth.getBids().add(depthEntry);
                    }

                    // 解析卖盘数据(asks)
                    JSONArray asksArray = dataJson.getJSONArray("asks");
                    for (int i = 0; i < asksArray.size(); i++) {
                        JSONArray object = (JSONArray) asksArray.get(i);
                        DepthEntry depthEntry = new DepthEntry();
                        depthEntry.setPrice(object.getDouble(0));   // 价格
                        depthEntry.setAmount(object.getDouble(1));  // 数量
                        depth.getAsks().add(depthEntry);
                    }

                    return depth;
                }

            } catch (Exception e) {
                logger.error("[depth] 获取深度数据失败, symbol={}", symbol, e);
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
     * 获取交易数据并应用调整因子
     * <p>
     * 对从交易所获取的成交数据应用调整值(adjustmentValue)和倍数(multiple)因子
     * 
     * @param symbol 交易对标识(remarks字段值)
     * @param maximum 重试次数
     * @return 调整后的交易数据
     */
    @Override
    public Trade tradeDecorator(String symbol, int maximum) {
        Trade trade = this.trade(symbol, maximum);
        Item item = itemService.findByRemarks(symbol);
        
        // 如果没有调整需求，直接返回原始交易数据
        if ((trade == null || item.getAdjustmentValue() == 0) && (item.getMultiple() == 0 || item.getMultiple() == 1)) {
            return trade;
        }
        
        List<TradeEntry> data = trade.getData();
        for (int i = 0; i < data.size(); i++) {
            TradeEntry tradeEntry = data.get(i);
            // 应用倍数因子
            if (item.getMultiple() > 0) {
                tradeEntry.setAmount(Arith.mul(tradeEntry.getAmount(), item.getMultiple()));
            }
            // 应用价格调整值
            tradeEntry.setPrice(Arith.add(tradeEntry.getPrice(), item.getAdjustmentValue()));
        }
        return trade;
    }

    /**
     * 获取加密货币交易数据(成交记录)
     * <p>
     * 从火币或抹茶交易所获取指定交易对的最新成交记录
     * 
     * @param symbol 交易对标识(remarks字段值)
     * @param maximum 重试次数(内部使用)
     * @return 交易数据对象
     */
    @Override
    public Trade trade(String symbol, int maximum) {
        // 如果配置为抹茶数据源，调用抹茶交易接口
        if(!spiderService.isHuobi()){
            return mexcTrade(symbol);
        }
        
        boolean current_lock = false;
        if (StringUtils.isNullOrEmpty(symbol)) {
            logger.warn("[trade] 交易对为空");
            return null;
        }
        
        // 限流控制
        if (lock || (System.currentTimeMillis() - last_time.getTime()) < interval) {
            ThreadUtils.sleep(sleep);
            if (maximum >= 100) {
                logger.warn("[trade] 重试次数已达上限, 放弃获取交易数据, symbol={}", symbol);
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

                // 调用火币交易API
                String result = HttpHelper.getJSONFromHttp(Config.url + Config.trade, param, HttpMethodType.GET);
                JSONObject resultJson = JSON.parseObject(result);
                String status = resultJson.getString("status");
                
                if ("ok".equals(status)) {
                    JSONObject dataJson = resultJson.getJSONObject("tick");
                    Long ts = resultJson.getLongValue("ts");

                    Trade trade = new Trade();

                    Item item = itemService.findByRemarks(symbol);
                    if (item == null) {
                        logger.warn("[trade] 未找到商品配置, symbol={}", symbol);
                        return null;
                    }
                    trade.setSymbol(item.getSymbol());
                    trade.setTs(ts);

                    // 解析成交记录
                    JSONArray dataArray = dataJson.getJSONArray("data");
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject object = dataArray.getJSONObject(i);
                        TradeEntry tradeEntry = new TradeEntry();
                        tradeEntry.setTs(object.getLong("ts"));      // 成交时间
                        tradeEntry.setPrice(object.getDouble("price"));  // 成交价格
                        tradeEntry.setAmount(object.getDouble("amount")); // 成交数量
                        tradeEntry.setDirection(object.getString("direction")); // 成交方向(buy/sell)
                        trade.getData().add(tradeEntry);
                    }
                    return trade;
                }

            } catch (Exception e) {
                logger.error("[trade] 获取交易数据失败, symbol={}", symbol, e);
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

    /**
     * 判断是否为股票数据源(雪球)
     * <p>
     * 根据商品类型判断是否应该从雪球获取数据
     * 
     * @param symbol 商品标识
     * @return true-股票数据，应从雪球获取；false-非股票数据
     */
    public boolean isXueQiu(String symbol) {
        // 特殊处理：IYW(科技ETF)、UUP(美元指数ETF)也使用雪球数据源
        if("IYW,UUP".contains(symbol)){
            return true;
        }
        String type = itemService.findBySymbol(symbol).getType();
        return type.contains("stock") || (type.equalsIgnoreCase(Item.indices));
    }

    /**
     * 判断是否为台股数据源
     * 
     * @param symbol 商品标识
     * @return true-台股数据；false-非台股数据
     */
    public boolean isTW(String symbol) {
        String type = itemService.findBySymbol(symbol).getType();
        return Item.TW_STOCKS.equalsIgnoreCase(type);
    }

    /**
     * 判断是否为新浪外汇数据源
     * <p>
     * 根据商品类型和分类判断是否应该从新浪获取外汇数据
     * 
     * @param symbol 商品标识
     * @return true-新浪外汇数据；false-非新浪外汇数据
     */
    public boolean isXinlang(String symbol) {
        return false;
    }

    /**
     * 获取单个商品的实时行情数据
     * <p>
     * 根据商品类型自动选择数据源：
     * <ul>
     *   <li>股票 -> 雪球数据源</li>
     *   <li>外汇 -> 新浪数据源</li>
     *   <li>其他 -> 免费外汇汇率服务</li>
     * </ul>
     * 
     * @param symbol 商品标识
     * @return 实时行情数据列表
     */
    public List<Realtime> realtimeSingle(String symbol) {
        if (isXueQiu(symbol)) {
            return xueQiuDataService.realtimeSingle(symbol);
        }
        if (isXinlang(symbol)) {
            return xinLangDataService.realtimeSingle(symbol);
        }
        return freeForexRateService.fetchRealtime(symbol);
    }

    /**
     * 获取加密货币实时行情数据
     * <p>
     * 从Redis中获取爬虫服务预存的加密货币实时数据
     * 根据配置选择火币或抹茶数据源
     * 
     * @param symbols 交易对列表(逗号分隔)
     * @return 实时行情数据列表
     */
    @Override
    public List<Realtime> realtimeCryptos(String symbols) {
        logger.debug("[Crypto] 开始获取加密货币实时数据, symbols={}", symbols);
        
        boolean isHuobi = spiderService.isHuobi();
        logger.debug("[Crypto] 数据源类型: {}", isHuobi ? "火币(Huobi)" : "抹茶(MEXC)");
        
        List<Realtime> realtimeList;
        // 根据数据源配置选择不同的Redis Key
        if(isHuobi){
            realtimeList = this.spiderService.fetchRealtimeList(symbols);  // 火币数据源使用主Key
        }else{
             realtimeList = spiderService.fetchRealtimeList(symbols, REALTIME_HASH_BAK);  // 抹茶数据源使用备用Key
        }

        logger.debug("[Crypto] 获取加密货币实时数据完成, 返回数据量: {}, symbols={}",
                 realtimeList != null ? realtimeList.size() : 0, symbols);
        
        // 数据为空时输出警告日志，帮助排查问题
        if (realtimeList == null || realtimeList.isEmpty()) {
            logger.warn("[Crypto] WARNING: 获取加密货币数据为空! 请检查: 1.Redis中是否有数据 2.爬虫服务是否正常运行 3.数据源配置是否正确");
        }
        
        return realtimeList;
    }


    /**
     * 获取通用实时行情数据
     * <p>
     * 优先从免费外汇汇率服务获取，如果失败则逐个调用各数据源获取
     * 
     * @param symbols 商品标识列表(逗号分隔)
     * @return 实时行情数据列表
     */
    @Override
    public List<Realtime> realtime(String symbols) {
        // 优先从免费外汇服务获取
        List<Realtime> realtimeList = freeForexRateService.fetchRealtime(symbols);
        if (!realtimeList.isEmpty()) {
            return realtimeList;
        }
        
        // 如果免费服务返回空，则逐个调用各数据源
        List<Realtime> fallback = Collections.synchronizedList(new ArrayList<>());
        Splitter.on(",").omitEmptyStrings().splitToList(symbols)
            .parallelStream()
            .map(this::realtimeSingle)
            .forEach(fallback::addAll);
        return fallback;
    }

    /**
     * 获取雪球股票实时行情数据
     * 
     * @param remarks 股票标识列表(逗号分隔)
     * @return 实时行情数据列表
     */
    @Override
    public List<Realtime> realtimeXueQiu(String remarks) {
        return xueQiuDataService.realtimeSingle(remarks);
    }

    /**
     * 获取新浪外汇实时行情数据
     * 
     * @param symbols 外汇标识列表(逗号分隔)
     * @return 实时行情数据列表
     */
    @Override
    public List<Realtime> realtimeXinLang(String symbols) {
        return xinLangDataService.realtimeSingle(symbols);
    }

    /**
     * 获取台股实时行情数据
     * 
     * @param remarks 台股标识列表(逗号分隔)
     * @return 实时行情数据列表
     */
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

    @Override
    public Map<String, List<Kline>> getDailyWeekMonthHistory(String symbol) {
        if (isTW(symbol)) {
            return twDataService.getDailyWeekMonthHistory(symbol);
        }
        return xinLangDataService.getDailyWeekMonthHistory(symbol);
    }


    @Override
    public Map<String, List<Kline>> getHourlyAndMinuteHistory(String symbol) {
        if (isTW(symbol)) {
            return twDataService.getHourlyAndMinuteHistory(symbol);
        }
        Map<String, List<Kline>> map = new HashMap<>();
        map.put(KlineConstant.PERIOD_4HOUR, xinLangDataService.getTimeseriesForFourHourly(symbol));
        map.put(KlineConstant.PERIOD_2HOUR, xinLangDataService.getTimeseriesForTwoHourly(symbol));
        map.put(KlineConstant.PERIOD_60MIN, xinLangDataService.getTimeseriesForOneHourly(symbol));
        map.put(KlineConstant.PERIOD_30MIN, xinLangDataService.getTimeseriesThirtyMinute(symbol));
        map.put(KlineConstant.PERIOD_15MIN, xinLangDataService.getTimeseriesFifteenMinute(symbol));
        map.put(KlineConstant.PERIOD_5MIN, xinLangDataService.getTimeseriesFiveMinute(symbol));
        map.put(KlineConstant.PERIOD_1MIN, xinLangDataService.getTimeseriesOneMinute(symbol));
        return map;
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

