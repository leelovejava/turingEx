package com.yami.trading.huobi.hobi.internal;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yami.trading.bean.data.domain.*;
import com.yami.trading.bean.ipo.XueQiuNewStocks;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DepthTimeObject;
import com.yami.trading.huobi.data.internal.TradeTimeObject;
import com.yami.trading.huobi.hobi.constant.TraderMadeOptions;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.huobi.hobi.http.HttpMethodType;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 爬虫数据服务类
 * 
 * 该类负责从 Redis 中获取爬虫抓取的各种行情数据,主要功能包括:
 * 1. 获取实时行情数据(Realtime)
 * 2. 获取市场状态信息(StockMarket)
 * 3. 获取盘口深度数据(Depth)
 * 4. 获取交易明细数据(TradeDetails)
 * 5. 获取K线数据(Kline)
 * 6. 获取新股数据(XueQiuNewStocks)
 * 7. 数据源切换(火币/抹茶)
 * 
 * 数据来源:
 * - 爬虫服务抓取外部数据源(火币/抹茶)
 * - 数据存储在 Redis 中
 * - 本服务从 Redis 读取数据并转换为 Java 对象
 * 
 * @author System
 */
@Slf4j
@Component
public class SpiderService {
    /**
     * 实时行情数据Redis Key
     */
    public static final String REALTIME_HASH = "realtime_hash";
    public static final String REALTIME_HASH_BAK = "realtime_hash_bak";
    /**
     * 市场状态数据Redis Key
     */
    public static final String MARKET_HASH = "market_hash";
    /**
     * 交易明细数据Redis Key(A股和美股有)
     */
    public static final String TRADER_HASH = "trader_hash";

    public static final String PANKOU_HASH = "pankou_hash";
    public static final String KLINE_SYMBOL = "kline_";
    public static final String XUEQIU_NEW_STOCKS = "xueqiu_new_stocks";
    public static final String XUEQIU_NEW_STOCKS_NOT_LISTED = "xueqiu_new_stocks_not_listed";
    public static final String SPIDER_SWITCH_CRYPTOS_KEY  = "spider_switch_cryptos_key";
    public static final String HUOBI = "huobi";
    public static final String MEXC = "mexc";

    @Qualifier("redissonClientSpider")
    @Autowired
    private RedissonClient redissonClientSpider;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private ItemService itemService;

    /**
     * 判断是否使用火币数据源
     * 
     * 判断逻辑:
     * 1. 如果Redis中全局配置为抹茶,直接返回false
     * 2. 如果本地系统参数配置为抹茶,返回false
     * 3. 其他情况返回true
     * 
     * 优先级: Redis全局配置 > 本地系统参数配置
     * 
     * @return true-使用火币数据源, false-使用抹茶数据源
     */
    public boolean isHuobi(){
        Object o = redissonClientSpider.getBucket(SPIDER_SWITCH_CRYPTOS_KEY).get();
        if(o != null){
            if(ObjectUtil.equals(MEXC, o.toString())){
                return false;
            }
        }
        Syspara superSecret = sysparaService.find("cryptos_use_huobi");
        // 配置了是抹茶数据源
        if(superSecret != null && "0".equalsIgnoreCase(superSecret.getSvalue())){
            return false;
        }
        return true;

    }

    /**
     * 从Redis获取雪球新股列表(已上市)
     * 
     * @return 新股列表,如果Redis中没有数据返回空列表
     */
    public List<XueQiuNewStocks> fetchNewStocks() {
        Object o = redissonClientSpider.getBucket(XUEQIU_NEW_STOCKS).get();
        if(o == null){
            return Lists.newArrayList();
        }
        return JSONArray.parseArray(o.toString(), XueQiuNewStocks.class);
    }

    /**
     * 从Redis获取雪球新股列表(未上市)
     * 
     * @return 新股列表,如果Redis中没有数据返回空列表
     */
    public List<XueQiuNewStocks> fetchNewStocksNotList() {
        Object o = redissonClientSpider.getBucket(XUEQIU_NEW_STOCKS_NOT_LISTED).get();
        if(o == null){
            return Lists.newArrayList();
        }
        return JSONArray.parseArray(o.toString(), XueQiuNewStocks.class);
    }

    /**
     * 从Redis获取实时行情数据(指定Redis Key)
     * 
     * 执行流程:
     * 1. 从Redis获取指定Key的Map数据
     * 2. 解析币种代码字符串
     * 3. 批量获取币种对应的行情数据
     * 4. 将JSON数据转换为Realtime对象
     * 5. 标准化价格精度(根据币种小数位数)
     * 6. 处理时间戳格式(16位/13位/10位转换)
     * 
     * @param remarks 币种代码字符串,多个币种用逗号分隔
     * @param key Redis中的数据Key
     * @return 实时行情数据列表
     */
    public List<Realtime> fetchRealtimeList(String remarks, String key) {
        log.info("[Spider] 开始从Redis获取实时数据, key={}, remarks={}", key, remarks);
        
        List<Realtime> list = new ArrayList<>();
        RMap<String, String> map = redissonClientSpider.getMap(key);
        
        if (map == null) {
            log.error("[Spider] ERROR: Redis Map为空, key={}", key);
            return list;
        }
        
        Set<String> keys = Splitter.on(",").trimResults().splitToStream(remarks).collect(Collectors.toSet());
        log.info("[Spider] 需要查询的币种数量: {}, key={}", keys.size(), key);
        
        Map<String, String> values = map.getAll(keys);
        
        log.info("[Spider] Redis返回的数据量: {}, key={}", values != null ? values.size() : 0, key);
        
        if (values == null || values.isEmpty()) {
            log.warn("[Spider] WARNING: Redis中没有找到对应数据! key={}, remarks={}", key, remarks);
            log.warn("[Spider] 可能的原因: 1.爬虫服务未运行 2.爬虫写入失败 3.Redis连接问题");
        }
        for (String symbol : values.keySet()) {
            JSONObject realtimeJson = JSONObject.parseObject(values.get(symbol).trim());
            if ("eth".equalsIgnoreCase(symbol)) {
                log.info("[CryptoDebug] Redis raw eth, key={}, current={}, open={}, close={}, percent={}, chg={}, ts={}",
                        key,
                        realtimeJson.get("current"),
                        realtimeJson.get("open"),
                        realtimeJson.get("close"),
                        realtimeJson.get("percent"),
                        realtimeJson.get("chg"),
                        realtimeJson.get("time"));
            }
            realtimeJson.put("mid", realtimeJson.get("current"));
            realtimeJson.put("currency", realtimeJson.get("symbol"));
            realtimeJson.put("timestamp", realtimeJson.get("time"));
            realtimeJson.put("marketCapital", realtimeJson.get("market_capital"));
            realtimeJson.put("floatMarketCapital", realtimeJson.get("float_market_capital"));
            realtimeJson.put("peForecast", realtimeJson.get("pe_forecast"));
            realtimeJson.put("volumeRatio", realtimeJson.get("volume_ratio"));
            realtimeJson.put("turnoverRate", realtimeJson.get("turnover_rate"));
            Realtime realtime = new Realtime();
            // realtime.setUuid(ApplicationUtil.getCurrentTimeUUID());
            String currency = realtimeJson.getString("currency");
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
            BigDecimal mid = realtimeJson.getBigDecimal("mid");
            BigDecimal close = realtimeJson.getBigDecimal("close");
            if (mid != null && mid.compareTo(BigDecimal.ZERO) > 0) {
                realtime.setClose(mid.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
            } else if (close != null) {
                realtime.setClose(close.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
            } else {
                realtime.setClose(0D);
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
            if ("eth".equalsIgnoreCase(symbolByRemarks)) {
                log.info("[CryptoDebug] Parsed eth, key={}, symbol={}, open={}, close={}, chg={}, percent={}",
                        key,
                        symbolByRemarks,
                        realtime.getOpen(),
                        realtime.getClose(),
                        realtime.getChg(),
                        realtime.getPercent());
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
        return list;
    }
    
    /**
     * 从Redis获取实时行情数据(使用默认Key)
     * 
     * @param remarks 币种代码字符串,多个币种用逗号分隔
     * @return 实时行情数据列表
     */
    public List<Realtime> fetchRealtimeList(String remarks) {
       return fetchRealtimeList(remarks, REALTIME_HASH);
    }

    /**
     * 从Redis获取市场状态信息
     * 
     * @param remaks 市场代码字符串,多个市场用逗号分隔
     * @return 市场状态信息列表
     */
    public List<StockMarket> getMarkets(String remaks) {
        List<StockMarket> list = new ArrayList<>();
        RMap<String, String> map = redissonClientSpider.getMap(MARKET_HASH);
        Set<String> keys = Splitter.on(",").trimResults().splitToStream(remaks).collect(Collectors.toSet());
        Map<String, String> values = map.getAll(keys);
        for (String symbol : values.keySet()) {
            list.add(JSONObject.parseObject(values.get(symbol), StockMarket.class));
        }
        return list;
    }


    /**
     * 从Redis获取盘口深度数据
     * 
     * @param remaks 币种代码字符串,多个币种用逗号分隔
     * @return 盘口深度数据列表
     */
    public List<Depth> pankous(String remaks) {
        List<Depth> list = new ArrayList<>();
        RMap<String, String> map = redissonClientSpider.getMap(PANKOU_HASH);
        Set<String> keys = Splitter.on(",").trimResults().splitToStream(remaks).collect(Collectors.toSet());
        Map<String, String> values = map.getAll(keys);
        for (String symbol : values.keySet()) {
            list.add(JSONObject.parseObject(values.get(symbol), Depth.class));
        }
        return list;
    }


    /**
     * 从Redis获取交易明细数据并保存到缓存
     * 
     * 执行流程:
     * 1. 从Redis获取交易明细数据
     * 2. 将数据保存到DataCache缓存
     * 3. 转换为TradeEntry格式
     * 4. 如果是美股,额外转换为深度数据
     * 
     * @param remarks 币种代码字符串,多个币种用逗号分隔
     * @param isUSStock 是否是美股,美股需要额外生成深度数据
     */
    public void tradeList(String remarks, boolean isUSStock) {
        RMap<String, String> map = redissonClientSpider.getMap(TRADER_HASH);
        Set<String> keys = Splitter.on(",").trimResults().splitToStream(remarks).collect(Collectors.toSet());
        Map<String, String> values = map.getAll(keys);
        for (String s : values.keySet()) {
            List<TradeDetails> tradeDetails = JSONObject.parseArray(values.get(s), TradeDetails.class);
            if (!tradeDetails.isEmpty()) {
                String symbol = itemService.getSymbolByRemarks(tradeDetails.get(0).getSymbol());
                DataCache.putStockTradeList(symbol, tradeDetails);
                tradeListToTrade(symbol, tradeDetails);
                if (isUSStock) {
                    setTradeListToDepth(symbol, tradeDetails);
                }
            }
        }
    }


    /**
     * 将交易明细数据转换为深度数据(美股专用)
     * 
     * 转换规则:
     * - side=-1: 卖单,放入asks
     * - side=1: 买单,放入bids
     * 
     * @param symbol 币种代码
     * @param tradeDetails 交易明细列表
     */
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


    /**
     * 将交易明细数据转换为TradeEntry格式
     * 
     * 转换规则:
     * - side=1: 卖单, direction="sell"
     * - side=-1: 买单, direction="buy"
     * 
     * @param symbol 币种代码
     * @param tradeDetails 交易明细列表
     */
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
     * 从Redis获取K线数据
     * 
     * 数据结构:
     * - 外层Map: Key为币种代码, Value为各周期K线
     * - 内层Map: Key为周期(1min/5min/15min/30min/1hour/1day等), Value为K线列表
     * 
     * @param symbols 币种代码字符串,多个币种用逗号分隔
     * @return K线数据列表,每个元素对应一个币种的各周期K线
     */
    public List<Map<String, List<Kline>>> getKlines(String symbols) {
        List<String> remarksList = Splitter.on(",").omitEmptyStrings().splitToList(symbols).stream().map(s -> itemService.findBySymbol(s).getRemarks()).collect(Collectors.toList());
        String remarks = remarksList.stream().collect(Collectors.joining(","));
        List<Map<String, List<Kline>>> list = new ArrayList<>();

        List<JSONObject> dataList = new ArrayList<>();

        for (String symbol : remarksList) {
            RMap<String, String> map = redissonClientSpider.getMap(KLINE_SYMBOL + symbol);
            JSONObject data = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            map.keySet().forEach(k -> {
                jsonObject.put(k, JSONObject.parseArray(map.get(k)));
            });
            data.put(symbol, jsonObject);
            dataList.add(data);
        }

        try {
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, List<Kline>> map = new HashMap<>();
                JSONObject klineJson = dataList.get(i);
                for (String s : remarksList) {
                    if (klineJson.containsKey(s)) {
                        JSONObject jsonObject = klineJson.getJSONObject(s);
                        Set<String> periods = jsonObject.keySet();
                        for (String period : periods) {
                            JSONArray jsonArray = jsonObject.getJSONArray(period);
                            if(jsonArray != null){
                                List<Kline> javaList = jsonArray.toJavaList(Kline.class);
                                javaList.forEach(k -> k.setSymbol(itemService.getSymbolByRemarks(s)));
                                map.put(period, javaList);
                            }
                        }
                        list.add(map);
                    }
                }

            }
        }catch (Exception e) {
            log.error("getKlines error", e);
        }
        return list;
    }
}
