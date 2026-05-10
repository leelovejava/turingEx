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
import org.springframework.beans.factory.annotation.Value;
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
 * 雪球数据服务实现类
 *
 * 本类负责从雪球(XueQiu)平台采集以下数据：
 * 1. 股票/ETF的K线数据（日线、周线、月线、季度、年线以及分钟级K线）
 * 2. 实时行情数据（价格、成交量、涨跌幅等）
 * 3. 盘口数据（买卖盘深度）
 * 4. 逐笔交易数据
 * 5. 财经新闻资讯
 *
 * 主要功能：
 * - fetchRealtime: 获取多个标的后实时行情
 * - realtimeSingle: 获取单个标的后实时行情
 * - getHourlyAndMinuteHistory: 获取小时和分钟级别的K线历史数据
 * - getDailyWeekMonthHistory: 获取日、周、月级别的K线历史数据
 * - getMarkets: 获取市场数据
 * - pankous: 获取盘口数据（买卖盘深度）
 * - tradeList: 获取逐笔成交数据
 * - getInformation: 获取雪球财经新闻资讯
 *
 * 数据来源：
 * - K线数据：雪球官方API (https://stock.xueqiu.com)
 * - 实时行情：onjdo.com平台提供的美股实时数据接口
 * - 注意：onjdo.com接口目前可能已失效(404)，需要寻找替代方案
 */
@Component
public class XueQiuDataServiceImpl {

    /**
     * 雪球K线API地址
     *
     * 参数说明：
     * - symbol: 股票代码（如AAPL、TSLA）
     * - begin: 开始时间戳（毫秒）
     * - period: K线周期（1m=1分钟、120m=2小时、1d=1天、1w=1周、1mon=1月）
     * - type: before表示获取历史数据
     * - count: 负数表示向前获取，-500表示获取500条
     * - indicator: kline表示K线指标
     *
     * 示例：https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=TSLA&begin=1682695800000&period=120m&type=before&count=-500&indicator=kline
     */
    public final static String klineUrl = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol={}&begin={}&period={}&type=before&count=-500&indicator=kline";

    /**
     * 线程池，用于并发获取不同周期的K线数据
     * 线程池大小为12，可同时处理12个并发任务
     */
    ExecutorService executor = Executors.newFixedThreadPool(12);

    @Autowired
    private InfomationService infomationService;

    @Qualifier("redissonClientSpider")
    @Autowired
    private RedissonClient redissonClientSpider;

    /**
     * ==========================================
     * 以下是onjdo.com平台的API接口定义
     * 注意：这些接口目前返回404错误，需要替换
     * ==========================================
     */

    /**
     * 获取单个或多个标的后实时行情数据
     *
     * 请求方式：GET
     * 请求参数：currency - 股票代码，多个用逗号分隔（如 "AAPL,TSLA"）
     *
     * 响应数据格式：
     * {
     *   "code": "ok",
     *   "data": [
     *     {
     *       "currency": "AAPL",
     *       "timestamp": 1234567890000,
     *       "open": 150.00,
     *       "close": 151.00,
     *       "mid": 150.50,           // 中间价
     *       "high": 152.00,
     *       "low": 149.00,
     *       "ask": 151.00,            // 卖一价
     *       "bid": 150.50,            // 买一价
     *       "volume": 1000000,        // 成交量
     *       "amount": 150000000,      // 成交额
     *       "percent": 0.67,          // 涨跌幅(%)
     *       "chg": 1.00,              // 涨跌额
     *       "marketCapital": 2500000000000,  // 总市值
     *       "floatMarketCapital": 2400000000000,  // 流通市值
     *       "peForecast": 25.5,       // 预测市盈率
     *       "volumeRatio": 1.2,       // 量比
     *       "turnoverRate": 0.55,     // 换手率(%)
     *       "navps": 4.5,             // 每股净资产
     *       "pb": 3.2,                // 市净率
     *       "amplitude": 2.5,         // 振幅(%)
     *       "eps": 5.8                // 每股收益
     *     }
     *   ]
     * }
     *
     * 问题：此接口返回404，需要替换为其他实时行情数据源
     */
    @Value("${xueqiu.live-url:}")
    private String liveUrl;

    /**
     * 获取市场概要数据
     *
     * 请求方式：GET
     * 请求参数：currency - 股票代码，多个用逗号分隔
     *
     * 响应数据格式：
     * {
     *   "code": "ok",
     *   "data": [StockMarket对象列表]
     * }
     *
     * 问题：此接口返回404，需要替换为其他市场数据源
     */
    @Value("${xueqiu.markets-url:}")
    private String marketsUrl;

    /**
     * 获取盘口数据（买卖盘深度）
     *
     * 请求方式：GET
     * 请求参数：currency - 股票代码
     *
     * 响应数据格式：
     * {
     *   "code": "ok",
     *   "data": [Depth对象列表，每个Depth包含 asks(卖盘)和bids(买盘)]
     * }
     *
     * 问题：此接口返回404，需要替换为其他盘口数据源
     */
    @Value("${xueqiu.pankou-url:}")
    private String pankouUrl;

    /**
     * 获取逐笔成交数据
     *
     * 请求方式：GET
     * 请求参数：currency - 股票代码
     *
     * 响应数据格式：
     * {
     *   "code": "ok",
     *   "data": [[TradeDetails对象列表]]
     * }
     *
     * 问题：此接口返回404，需要替换为其他成交数据源
     */
    @Value("${xueqiu.trade-list-url:}")
    private String tradeListUrl;

    /**
     * 续约接口，用于保持数据采集权限
     *
     * 请求方式：GET
     * 请求参数：
     * - currency: 股票代码
     * - type: 开通/关闭类型（来自Item.openCloseType）
     *
     * 问题：此接口返回404，需要替换
     */
    @Value("${xueqiu.lease-url:}")
    private String leaseUrl;

    private static Logger logger = LoggerFactory.getLogger(XueQiuDataServiceImpl.class);

    @Autowired
    private KlineService klineService;
    @Autowired
    private ItemService itemService;
    @Value("${twelvedata.base-url:https://api.twelvedata.com}")
    private String twelveDataBaseUrl;
    @Value("${twelvedata.api-key:}")
    private String twelveDataApiKey;

    /**
     * 获取盘口数据（买卖盘深度）
     *
     * @param remarks 股票代码（如 "AAPL"）
     * @return 盘口深度数据列表，包含各档位的买卖盘价格和数量
     *
     * 接口调用流程：
     * 1. 构建请求参数 {currency: remarks}
     * 2. 调用 onjdo.com 的 getPanKou 接口
     * 3. 解析返回的JSON，提取code字段
     * 4. 如果code为"ok"，则解析data数组并转换为Depth对象列表
     * 5. 返回空列表表示获取失败
     *
     * 问题：onjdo.com接口已失效(404)，需要替换为其他盘口数据源
     */
    public List<Depth> pankous(String remarks) {
        if (StrUtil.isBlank(pankouUrl)) {
            return Lists.newArrayList();
        }
        try {
            Map<String, String> param = new HashMap<>();
            param.put("currency", remarks);

            String result = HttpHelper.getJSONFromHttpNew(pankouUrl, param, HttpMethodType.GET);
            JSONObject resultJson = JSON.parseObject(result);
            String code = resultJson.getString("code");
            if ("ok".equals(code)) {
                JSONArray dataArray = resultJson.getJSONArray("data");
                return dataArray.toJavaList(Depth.class);
            }
        } catch (Exception e) {
            logger.warn("xueqiu pankou fetch failed, remarks={}, msg={}", remarks, e.getMessage());
        }
        return Lists.newArrayList();
    }

    /**
     * 获取市场概要数据
     *
     * @param symbols 股票代码列表，多个用逗号分隔（如 "AAPL,OIL"）
     * @return 市场数据列表，包含各股票的市场概况信息
     *
     * 接口调用流程：
     * 1. 构建请求参数 {currency: symbols}
     * 2. 调用 onjdo.com 的 getMarkets 接口
     * 3. 解析返回的JSON，提取code字段
     * 4. 如果code为"ok"，则解析data数组并转换为StockMarket对象列表
     * 5. 返回空列表表示获取失败
     *
     * 问题：onjdo.com接口已失效(404)，需要替换为其他市场数据源
     */
    public List<StockMarket> getMarkets(String symbols) {
        if (StrUtil.isBlank(marketsUrl)) {
            return Lists.newArrayList();
        }
        try {
            Map<String, String> param = new HashMap<>();
            param.put("currency", symbols);

            String result = HttpHelper.getJSONFromHttpNew(marketsUrl, param, HttpMethodType.GET);
            JSONObject resultJson = JSON.parseObject(result);
            String code = resultJson.getString("code");
            if ("ok".equals(code)) {
                JSONArray dataArray = resultJson.getJSONArray("data");
                return dataArray.toJavaList(StockMarket.class);
            }
        } catch (Exception e) {
            logger.warn("xueqiu markets fetch failed, symbols={}, msg={}", symbols, e.getMessage());
        }
        return Lists.newArrayList();
    }

    /**
     * 主方法，用于测试getMarkets功能
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        XueQiuDataServiceImpl service = new XueQiuDataServiceImpl();
//        List<Kline> sz300750 = service.buildOneYearPeriod("AAPL");
//        System.out.println(sz300750.size());
        List<StockMarket> markets1 = service.getMarkets("AAPL,OIL");
        System.out.println(markets1);
    }

    /**
     * 续约接口调用
     *
     * 功能说明：
     * - 用于保持数据采集权限的续约
     * - 如果标的的crawlStatus为"DEFAULT"（非激活状态），则跳过续约
     *
     * @param symbol 股票代码
     *
     * 处理流程：
     * 1. 根据股票代码查询标的详细信息
     * 2. 检查crawlStatus，如果为"DEFAULT"则直接返回（不续约）
     * 3. 构建请求参数：currency(股票代码)和type(开通关闭类型)
     * 4. 调用onjdo.com的lease接口
     * 5. 如果返回code为"ok"，记录续约成功日志
     *
     * 问题：onjdo.com接口已失效(404)，需要替换为其他续约接口
     */
    public void lease(String symbol) {
        Item bySymbol = itemService.findBySymbol(symbol);
        if(Item.DEFAULT_ACTIVE.equalsIgnoreCase(bySymbol.getCrawlStatus())){
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("currency", symbol);
        param.put("type", bySymbol.getOpenCloseType());
        if (StrUtil.isBlank(leaseUrl)) {
            return;
        }
        String result = HttpHelper.getJSONFromHttpNew(leaseUrl, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        if ("ok".equals(code)) {
            logger.info("{} 采集续约成功", symbol);
        }
    }

    /**
     * 获取雪球财经新闻资讯
     *
     * 功能说明：
     * - 从雪球平台采集最新的财经新闻和资讯
     * - 解析新闻内容，提取来源信息
     * - 使用Redis缓存已采集的新闻URL，避免重复采集
     * - 缓存有效期为1周（60*60*24*7秒）
     *
     * 处理流程：
     * 1. 获取雪球首页Cookie（用于认证）
     * 2. 调用雪球新闻列表API获取最新15条新闻
     * 3. 遍历每条新闻，解析以下字段：
     *    - dataId: 新闻ID
     *    - text: 新闻内容/描述
     *    - created_at: 创建时间
     *    - target: 原始文章URL
     * 4. 从新闻内容中提取来源（匹配最后一个括号内的文本）
     * 5. 检查Redis是否已缓存该URL
     *    - 如果未缓存，则添加到待保存列表，并缓存URL（有效期1周）
     * 6. 批量保存到数据库
     *
     * 数据保存：
     * - Infomation对象包含：dataId、description、createdAt、type、originUrl、source、lang
     * - lang固定为"zh-CN"（中文）
     * - type固定为"1"（新闻类型）
     */
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
            infom.setLang("en");
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

    /**
     * 获取小时和分钟级别的K线历史数据
     *
     * 功能说明：
     * - 并发获取多个时间周期的K线数据
     * - 支持的周期：4小时、2小时、1小时、30分钟、15分钟、5分钟、1分钟
     *
     * @param symbol 股票代码（如 "AAPL"）
     * @param cookie 雪球认证Cookie（可为空，内部会自动获取）
     * @return Map，key为周期常量（如KlineConstant.PERIOD_4HOUR），value为该周期的K线列表
     *
     * 并发处理：
     * - 使用CompletableFuture并发调用7个不同周期的K线获取方法
     * - 使用allOf等待所有并发任务完成
     * - 每个周期独立获取，互不影响
     *
     * 特殊处理：
     * - 2小时周期：如果雪球API直接返回数据则使用，否则根据1小时数据计算生成
     * - 计算方式：将连续两根1小时K线合并为一根2小时K线
     *
     * 周期与数据范围：
     * - 4小时(4H): 约3个月历史数据
     * - 2小时(2H): 约2个月历史数据
     * - 1小时(1H): 约1个月历史数据
     * - 30分钟(30m): 约10天历史数据
     * - 15分钟(15m): 约5天历史数据
     * - 5分钟(5m): 约2天历史数据
     * - 1分钟(1m): 约1天历史数据
     */
    public Map<String, List<Kline>> getHourlyAndMinuteHistory(String symbol, String cookie)  {
        Map<String, List<Kline>> map = new HashMap<>();
        try {
            // 启动7个并发任务获取不同周期的K线数据
            CompletableFuture<List<Kline>> fourHourlyFuture = CompletableFuture.supplyAsync(() -> getTimeseriesForFourHourly(symbol, cookie), executor);
            CompletableFuture<List<Kline>> oneHourlyFuture = CompletableFuture.supplyAsync(() -> getTimeseriesForOneHourly(symbol, cookie), executor);
            CompletableFuture<List<Kline>> twoHourlyFuture = CompletableFuture.supplyAsync(() -> getTimeseriesForTwoHourly(symbol, cookie), executor);
            CompletableFuture<List<Kline>> thirtyMinuteFuture = CompletableFuture.supplyAsync(() -> getTimeseriesThirtyMinute(symbol, cookie), executor);
            CompletableFuture<List<Kline>> fifteenMinuteFuture = CompletableFuture.supplyAsync(() -> getTimeseriesFifteenMinute(symbol, cookie), executor);
            CompletableFuture<List<Kline>> fiveMinuteFuture = CompletableFuture.supplyAsync(() -> getTimeseriesFiveMinute(symbol, cookie), executor);
            CompletableFuture<List<Kline>> oneMinuteFuture = CompletableFuture.supplyAsync(() -> getTimeseriesOneMinute(symbol, cookie), executor);

            // 等待所有任务完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    fourHourlyFuture, oneHourlyFuture, twoHourlyFuture, thirtyMinuteFuture, fifteenMinuteFuture, fiveMinuteFuture, oneMinuteFuture);

            // 等待所有任务完成
            allFutures.join();

            map.put(KlineConstant.PERIOD_4HOUR, fourHourlyFuture.get());
            map.put(KlineConstant.PERIOD_60MIN, oneHourlyFuture.get());

            // 特殊处理两小时数据：如果雪球API没有直接返回，则根据1小时数据计算
            List<Kline> twoHourlyList = twoHourlyFuture.get();
            if (twoHourlyList == null || twoHourlyList.isEmpty()) {
                try {
                    List<Kline> hourList = oneHourlyFuture.get();
                    if (hourList != null && !hourList.isEmpty()) {
                        // 使用K线服务将1小时K线合并为2小时K线
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

    /**
     * 从新闻内容中提取来源信息
     *
     * @param text 新闻文本内容
     * @return 来源字符串（匹配最后一个括号内的文本），如果没有则返回空字符串
     *
     * 示例：
     * - 输入: "这是一条新闻内容（新浪财经）"
     * - 输出: "新浪财经"
     * - 输入: "这是新闻（来源1）（来源2）"
     * - 输出: "来源2"
     */
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
     * 获取逐笔成交数据
     *
     * @param remarks 股票代码（如 "AAPL"）
     * @param isUSStock 是否为美股（美股会设置深度数据）
     *
     * 功能说明：
     * 1. 调用onjdo.com的getTrade接口获取逐笔成交数据
     * 2. 解析返回数据，获取每笔成交的详情
     * 3. 将成交数据转换为TradeDetails对象列表
     * 4. 将数据存入DataCache（内存缓存）
     * 5. 转换并保存交易数据
     * 6. 如果是美股，还会设置盘口深度数据
     *
     * 问题：onjdo.com接口已失效(404)，需要替换为其他成交数据源
     */
    public void tradeList(String remarks, boolean isUSStock) {
        Map<String, String> param = new HashMap<>();
        param.put("currency", remarks);

        if (StrUtil.isBlank(tradeListUrl)) {
            return;
        }
        String result = HttpHelper.getJSONFromHttpNew(tradeListUrl, param, HttpMethodType.GET);
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

    /**
     * 将逐笔成交数据转换为盘口深度数据并缓存
     *
     * @param symbol 股票代码
     * @param tradeDetails 逐笔成交详情列表
     *
     * 功能说明：
     * 1. 从逐笔成交数据中筛选出所有卖盘(side=1)和买盘(side=-1)
     * 2. 卖盘数据转换为Asks列表，买盘数据转换为Bids列表
     * 3. 每个深度条目包含：价格(price)和数量(amount)
     * 4. 使用成交时间戳作为深度数据的TS
     * 5. 将深度数据封装并存入DataCache
     *
     * 盘口数据结构：
     * - asks: 卖盘列表（按价格升序排列）
     * - bids: 买盘列表（按价格降序排列）
     */
    public static void setTradeListToDepth(String symbol, List<TradeDetails> tradeDetails) {
        Depth depth = new Depth();
        depth.setTs(tradeDetails.get(0).getTimestamp() / 1000);
        depth.setSymbol(symbol);

        // 筛选卖盘（side=1）并转换为深度条目
        List<DepthEntry> asks = tradeDetails.stream().filter(t -> t.getSide() == -1).map(t -> {
            DepthEntry depthEntry = new DepthEntry();
            depthEntry.setAmount((double) t.getTrade_volume());
            depthEntry.setPrice(t.getCurrent());
            return depthEntry;
        }).collect(Collectors.toList());

        // 筛选买盘（side=1）并转换为深度条目
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
     * 将逐笔成交数据转换为交易数据并缓存
     *
     * @param symbol 股票代码
     * @param tradeDetails 逐笔成交详情列表
     *
     * 功能说明：
     * 1. 从DataCache获取现有的TradeTimeObject
     * 2. 将逐笔成交数据转换为TradeEntry列表
     * 3. 转换规则：
     *    - direction: side=1转为"sell"（卖出），side=-1转为"buy"（买入）
     *    - amount: 成交数量
     *    - price: 成交价格
     *    - ts: 时间戳（毫秒转秒）
     * 4. 将转换后的数据存入TradeTimeObject并更新到DataCache
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
     * 获取原始的K线图数据（按分钟）
     *
     * @param symbol 股票代码
     * @param cookie 雪球认证Cookie
     * @return JSON字符串，直接返回雪球API的原始响应
     *
     * 注意：此方法返回的是未经解析的JSON字符串
     * 主要用于调试或需要原始数据的场景
     */
    public String getRawTimeseriesByMinute(String symbol, String cookie) {
        long nowTs = System.currentTimeMillis();
        long begin = nowTs;
        String url = StrUtil.format(klineUrl, symbol, begin, "1m");
        return HttpHelper.sendGetHttp(url, null, cookie);
    }

    /**
     * 获取单个标的后实时行情数据
     *
     * @param symbol 股票代码（如 "AAPL"）
     * @return Realtime对象列表，包含该标的的实时行情信息
     *
     * 功能说明：
     * 1. 调用onjdo.com的getLiveRates接口获取实时数据
     * 2. 解析返回数据，提取各字段信息
     * 3. 将数据转换为Realtime对象并设置精度
     *
     * 返回的Realtime对象包含以下字段：
     * - symbol: 股票代码（通过remarks转换）
     * - name: 名称（同symbol）
     * - ts: 时间戳
     * - open: 开盘价
     * - close: 收盘价（中间价mid或收盘价close）
     * - high: 最高价
     * - low: 最低价
     * - ask: 卖一价
     * - bid: 买一价
     * - volume: 成交量
     * - amount: 成交额
     * - percent: 涨跌幅(%)
     * - chg: 涨跌额
     * - marketCapital: 总市值
     * - floatMarketCapital: 流通市值
     * - peForecast: 预测市盈率
     * - volumeRatio: 量比
     * - turnoverRate: 换手率
     * - navps: 每股净资产
     * - pb: 市净率
     * - amplitude: 振幅
     * - eps: 每股收益
     *
     * 问题：onjdo.com接口已失效(404)，需要替换为其他实时行情数据源
     */
    public List<Realtime> realtimeSingle(String symbol) {
        if (StrUtil.isBlank(twelveDataApiKey)) {
            logger.warn("twelvedata api-key is empty, skip realtimeSingle. symbol={}", symbol);
            return Lists.newArrayList();
        }
        return realtimeFromTwelveData(symbol);
    }

    private List<Realtime> realtimeFromTwelveData(String symbols) {
        List<Realtime> list = new ArrayList<>();
        if (StrUtil.isBlank(symbols) || StrUtil.isBlank(twelveDataApiKey)) {
            return list;
        }
        String[] symbolArr = symbols.split(",");
        for (String raw : symbolArr) {
            String one = raw == null ? "" : raw.trim();
            if (one.isEmpty()) {
                continue;
            }
            try {
                String url = twelveDataBaseUrl + "/quote";
                Map<String, String> param = new HashMap<>();
                param.put("symbol", one);
                param.put("apikey", twelveDataApiKey);
                String result = HttpHelper.getJSONFromHttpNew(url, param, HttpMethodType.GET);
                if (StrUtil.isBlank(result)) {
                    continue;
                }
                JSONObject obj = JSON.parseObject(result);
                if (obj == null || (obj.containsKey("code") && obj.getInteger("code") >= 400)) {
                    continue;
                }
                String code = obj.getString("code");
                if ("400".equals(code) || "401".equals(code) || "404".equals(code) || "429".equals(code)) {
                    continue;
                }

                String symbolRaw = obj.getString("symbol");
                if (StrUtil.isBlank(symbolRaw)) {
                    continue;
                }
                String symbolUpper = symbolRaw.toUpperCase(Locale.ROOT);
                String symbolByRemarks = itemService.getSymbolByRemarks(symbolUpper);
                if (StrUtil.isBlank(symbolByRemarks)) {
                    symbolByRemarks = symbolUpper;
                }
                int decimal = itemService.getDecimal(symbolByRemarks);
                if (decimal <= 0) {
                    decimal = 4;
                }

                BigDecimal close = obj.getBigDecimal("close");
                BigDecimal open = obj.getBigDecimal("open");
                BigDecimal high = obj.getBigDecimal("high");
                BigDecimal low = obj.getBigDecimal("low");
                BigDecimal volume = obj.getBigDecimal("volume");
                BigDecimal change = obj.getBigDecimal("change");
                BigDecimal percentChange = obj.getBigDecimal("percent_change");
                if (close == null) {
                    continue;
                }

                Realtime realtime = new Realtime();
                realtime.setSymbol(symbolByRemarks);
                realtime.setName(symbolByRemarks);
                realtime.setTs(System.currentTimeMillis());
                realtime.setOpen((open == null ? close : open).setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                realtime.setClose(close.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                realtime.setHigh((high == null ? close : high).setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                realtime.setLow((low == null ? close : low).setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                realtime.setAsk(close.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                realtime.setBid(close.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                realtime.setVolume((volume == null ? BigDecimal.ZERO : volume).setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                realtime.setAmount(0D);
                if (change != null) {
                    realtime.setChg(change.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                }
                if (percentChange != null) {
                    realtime.setPercent(percentChange.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                }
                list.add(realtime);
            } catch (Exception e) {
                logger.warn("twelvedata quote error, symbol={}, msg={}", one, e.getMessage());
            }
        }
        return list;
    }

    /**
     * ==========================================
     * 以下是日、周、月级别K线数据获取方法
     * ==========================================
     */

    /**
     * 获取日、周、月级别的K线历史数据
     *
     * @param symbol 股票代码（如 "AAPL"）
     * @param cookie 雪球认证Cookie（可为空，内部会自动获取）
     * @return Map，key为周期常量，value为该周期的K线列表
     *
     * 支持的周期及数据范围：
     * - 1day(日线): 最近1年历史数据
     * - 1week(周线): 最近5年历史数据
     * - 1mon(月线): 最近5年历史数据
     * - 5day(5日线): 约1000天历史数据
     * - quarter(季度线): 约100年历史数据
     * - year(年线): 约100年历史数据
     *
     * 并发处理：
     * - 使用CompletableFuture并发调用6个不同周期的K线获取方法
     * - 大幅减少数据获取时间
     */
    public Map<String, List<Kline>> getDailyWeekMonthHistory(String symbol,String cookie) {
        Map<String, List<Kline>> map = new HashMap<>();
        try {
            // 启动6个并发任务获取不同周期的K线数据
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

    /**
     * 获取日线K线数据
     *
     * @param currency 股票代码（如 "AAPL"）
     * @param cookie 雪球认证Cookie
     * @return 日线K线列表
     *
     * 内部调用：
     * - getTimeseriesByPeriod(currency, "day", Kline.PERIOD_1DAY, 365, cookie)
     * - 表示获取日线周期，最近365天数据
     */
    public List<Kline> buildOneDayPeriod(String currency, String cookie) {
        return getTimeseriesByPeriod(currency, "day", Kline.PERIOD_1DAY, 365, cookie);

    }

    /**
     * 获取周线K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 周线K线列表
     *
     * 内部调用：
     * - getTimeseriesByPeriod(currency, "week", Kline.PERIOD_1WEEK, 365*5, cookie)
     * - 表示获取周线周期，最近5年数据
     */
    public List<Kline> buildOneWeekPeriod(String currency, String cookie) {
        return getTimeseriesByPeriod(currency, "week", Kline.PERIOD_1WEEK, 365 * 5, cookie);

    }

    /**
     * 获取月线K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 月线K线列表
     *
     * 内部调用：
     * - getTimeseriesByPeriod(currency, "month", Kline.PERIOD_1MON, 365*5, cookie)
     * - 表示获取月线周期，最近5年数据
     */
    public List<Kline> buildOneMonthPeriod(String currency, String cookie) {
        return getTimeseriesByPeriod(currency, "month", Kline.PERIOD_1MON, 365 * 5, cookie);
    }

    /**
     * 获取季度K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 季度K线列表
     *
     * 内部调用：
     * - getTimeseriesByPeriod(currency, "quarter", Kline.PERIOD_QUARTER, 365*100, cookie)
     * - 表示获取季度周期，最多100年数据
     */
    public List<Kline> buildOneQuarterPeriod(String currency , String cookie) {
        return getTimeseriesByPeriod(currency, "quarter", Kline.PERIOD_QUARTER, 365 * 100, cookie);
    }

    /**
     * 获取年线K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 年线K线列表
     *
     * 内部调用：
     * - getTimeseriesByPeriod(currency, "year", Kline.PERIOD_YEAR, 365*100, cookie)
     * - 表示获取年线周期，最多100年数据
     */
    public List<Kline> buildOneYearPeriod(String currency , String cookie) {
        return getTimeseriesByPeriod(currency, "year", Kline.PERIOD_YEAR, 365 * 100, cookie);
    }

    /**
     * 生成5日K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 5日K线列表
     *
     * 算法说明：
     * 1. 获取1000天的日线数据
     * 2. 从最新一根日线开始，每隔5根K线合并为一根5日K线
     * 3. 合并规则：
     *    - open: 第一根K线的开盘价
     *    - close: 最后一根K线的收盘价
     *    - high: 5根K线中的最高价
     *    - low: 5根K线中的最低价
     *    - volume: 5根K线的成交量之和
     *    - amount: 5根K线的成交额之和
     *    - ts: 最后一根K线的时间戳
     *
     * 重试机制：
     * - 使用@Retryable注解，失败时最多重试5次
     * - 每次重试间隔2秒（@Backoff(delay = 2000)）
     */
    @Retryable(
            value = {RuntimeException.class},  // 需要重试的异常类型
            maxAttempts = 5,  // 最大重试次数
            backoff = @Backoff(delay = 2000)  // 退避策略：每次重试之间的延迟
    )
    public List<Kline> buildFiveDayPeriod(String currency, String cookie) {
        List<Kline> result = Lists.newArrayList();
        List<Kline> timeseriesByOneDay = getTimeseriesByPeriod(currency, "day", Kline.PERIOD_1DAY, 1000 , cookie);

        // 按时间戳升序排列
        Collections.sort(timeseriesByOneDay, Kline::compareTo);
        int lastIndex = timeseriesByOneDay.size() - 1;

        // 从最新一根开始，每隔5根K线生成一根5日K线
        for (int i = lastIndex; i >= 5; i = i - 5) {
            // 获取连续的5根K线
            List<Kline> klineOneTop5 = new ArrayList<>();
            klineOneTop5.add(timeseriesByOneDay.get(i));
            klineOneTop5.add(timeseriesByOneDay.get(i - 1));
            klineOneTop5.add(timeseriesByOneDay.get(i - 2));
            klineOneTop5.add(timeseriesByOneDay.get(i - 3));
            klineOneTop5.add(timeseriesByOneDay.get(i - 4));

            // 计算最高价和最低价
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

            // 创建新的5日K线
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

            // 计算总成交量和总成交额
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
     * 生成4小时K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 4小时K线列表
     *
     * 算法说明：
     * 1. 获取120分钟的K线数据（120条，约3个月）
     * 2. 每隔2根120分钟K线合并为一根4小时K线
     * 3. 合并规则：
     *    - open: 第一根K线的开盘价
     *    - close: 最后一根K线的收盘价
     *    - high: 两根K线中的最高价
     *    - low: 两根K线中的最低价
     *    - volume: 两根K线的成交量之和
     *    - amount: 两根K线的成交额之和
     *    - ts: 第一根K线的时间戳
     *
     * 重试机制：
     * - 使用@Retryable注解，失败时最多重试5次
     * - 每次重试间隔2秒
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
     * 生成2小时K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 2小时K线列表
     *
     * 内部调用：
     * - getTimeseriesByMinute(currency, 120, 300, cookie)
     * - 表示获取120分钟周期，约300条数据
     */
    public List<Kline> getTimeseriesForTwoHourly(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 120, 300, cookie);
    }

    /**
     * 生成1小时K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 1小时K线列表
     *
     * 内部调用：
     * - getTimeseriesByMinute(currency, 60, 300, cookie)
     * - 表示获取60分钟周期，约300条数据
     */
    public List<Kline> getTimeseriesForOneHourly(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 60, 300, cookie);
    }

    /**
     * 生成30分钟K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 30分钟K线列表
     *
     * 内部调用：
     * - getTimeseriesByMinute(currency, 30, 10, cookie)
     * - 表示获取30分钟周期，约10条数据
     */
    public List<Kline> getTimeseriesThirtyMinute(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 30, 10, cookie);
    }

    /**
     * 生成15分钟K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 15分钟K线列表
     *
     * 内部调用：
     * - getTimeseriesByMinute(currency, 15, 15, cookie)
     * - 表示获取15分钟周期，约15条数据
     */
    public List<Kline> getTimeseriesFifteenMinute(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 15, 15, cookie);
    }

    /**
     * 生成5分钟K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 5分钟K线列表
     *
     * 内部调用：
     * - getTimeseriesByMinute(currency, 5, 15, cookie)
     * - 表示获取5分钟周期，约15条数据
     */
    public List<Kline> getTimeseriesFiveMinute(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 5, 15, cookie);

    }

    /**
     * 生成1分钟K线数据
     *
     * @param currency 股票代码
     * @param cookie 雪球认证Cookie
     * @return 1分钟K线列表
     *
     * 内部调用：
     * - getTimeseriesByMinute(currency, 1, 15, cookie)
     * - 表示获取1分钟周期，约15条数据
     */
    public List<Kline> getTimeseriesOneMinute(String currency, String cookie) {
        return getTimeseriesByMinute(currency, 1, 15,cookie);

    }

    /**
     * 从雪球API获取K线历史数据（通用方法）
     *
     * @param currency 股票代码（如 "AAPL"）
     * @param periodXieQiu 雪球API的周期参数（如 "1m", "120m", "1d", "1w", "1mon"）
     * @param sysPeriod 系统内部周期常量（如 Kline.PERIOD_1DAY）
     * @param limitDays 需要获取的历史数据天数
     * @param cookie 雪球认证Cookie（可为空，内部会自动获取）
     * @return K线列表，按时间升序排列
     *
     * 算法说明：
     * 1. 计算开始和结束时间戳
     * 2. 分页获取数据：每次最多获取500条
     * 3. 使用时间戳去重，避免重复数据
     * 4. 数据按时间升序排列
     * 5. 修复可能的K线数据问题
     *
     * 雪球API返回数据格式：
     * {
     *   "data": {
     *     "item": [
     *       [时间戳, 成交量, 开盘价, 最高价, 最低价, 收盘价, ... , 成交额],
     *       ...
     *     ]
     *   }
     * }
     *
     * 重试机制：
     * - 使用@Retryable注解，失败时最多重试5次
     * - 每次重试间隔2秒
     */
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

        // 如果没有提供Cookie，则自动获取雪球Cookie
        if(cookie == null){
            cookie = HttpHelper.getCookie("https://xueqiu.com/");
        }

        // 将股票代码转换为雪球格式（如 AAPL -> US_AAPL）
        if(itemService != null){
            currency = itemService.findBySymbol(currency).getRemarks();
        }

        Set<Long> tsSet = new HashSet<>();

        // 分页获取数据，直到获取完所有历史数据
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

                // 解析时间戳（可能是毫秒或秒）
                long ts = Long.parseLong(result.get(0).toString());
                if (Long.toString(ts).length() > 13) {
                    ts = ts / 1000;
                }

                // 去重
                if (tsSet.contains(ts)) {
                    continue;
                } else {
                    tsSet.add(ts);
                }
                kline.setTs(ts);

                // 解析K线数据
                // result数组格式：[时间戳, 成交量, 开盘价, 最高价, 最低价, 收盘价, ..., 成交额]
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

            // 如果没有获取到新数据，退出循环
            if (minTS == begin) {
                break;
            }

            begin = minTS;
            if (begin < startTs) {
                break;
            }
        }

        // 按时间升序排列
        Collections.sort(resList);

        // 修复开盘价：如果相邻两根K线的开盘价不连续，用前一根的收盘价填充
        int len = resList.size();
        for (int i = 1; i < len; i++) {
            resList.get(i).setOpen(resList.get(i - 1).getClose());
        }

        return resList;
    }

    /**
     * 获取分钟级K线数据的便捷方法
     *
     * @param currency 股票代码
     * @param minute 分钟数（如 1, 5, 15, 30, 60, 120）
     * @param limitDays 限制天数
     * @param cookie 雪球认证Cookie
     * @return 相应周期的K线列表
     *
     * 内部调用：
     * - getTimeseriesByPeriod(currency, minute+"m", minute+"min", limitDays, cookie)
     * - 将分钟数转换为雪球API的周期参数
     */
    public List<Kline> getTimeseriesByMinute(String currency, int minute, long limitDays, String cookie) {
        return getTimeseriesByPeriod(currency, minute + "m", minute + "min", limitDays, cookie );
    }

    /**
     * 从 Twelve Data 获取所有周期的K线数据（用于股票/外汇初始化）
     * 周期映射：1min/5min/15min/30min/60min/4hour/1day/1week/1mon
     */
    public Map<String, List<Kline>> getKlineFromTwelveData(String symbol) {
        Map<String, List<Kline>> result = new HashMap<>();
        if (StrUtil.isBlank(twelveDataApiKey)) {
            logger.warn("twelvedata api-key is empty, skip kline. symbol={}", symbol);
            return result;
        }
        // Twelve Data interval -> sysPeriod, outputsize
        String[][] intervals = {
            {"1min",  KlineConstant.PERIOD_1MIN,  "500"},
            {"5min",  KlineConstant.PERIOD_5MIN,  "500"},
            {"15min", KlineConstant.PERIOD_15MIN, "500"},
            {"30min", KlineConstant.PERIOD_30MIN, "500"},
            {"1h",    KlineConstant.PERIOD_60MIN, "500"},
            {"4h",    KlineConstant.PERIOD_4HOUR, "500"},
            {"1day",  KlineConstant.PERIOD_1DAY,  "500"},
            {"1week", KlineConstant.PERIOD_1WEEK, "500"},
            {"1month",KlineConstant.PERIOD_1MON,  "500"},
        };
        String remarks = itemService.findBySymbol(symbol).getRemarks();
        int decimal = itemService.getDecimal(symbol);
        if (decimal <= 0) decimal = 4;
        for (String[] row : intervals) {
            try {
                Map<String, String> param = new HashMap<>();
                param.put("symbol", remarks);
                param.put("interval", row[0]);
                param.put("outputsize", row[2]);
                param.put("apikey", twelveDataApiKey);
                String json = HttpHelper.getJSONFromHttpNew(twelveDataBaseUrl + "/time_series", param, HttpMethodType.GET);
                if (StrUtil.isBlank(json)) continue;
                JSONObject obj = JSON.parseObject(json);
                if (obj == null || !"ok".equals(obj.getString("status"))) continue;
                JSONArray values = obj.getJSONArray("values");
                if (values == null || values.isEmpty()) continue;
                List<Kline> klines = new ArrayList<>();
                for (int i = 0; i < values.size(); i++) {
                    JSONObject v = values.getJSONObject(i);
                    Kline k = new Kline();
                    k.setSymbol(symbol);
                    k.setPeriod(row[1]);
                    // datetime: "2024-01-02 09:30:00" or "2024-01-02"
                    String dt = v.getString("datetime");
                    try {
                        java.text.SimpleDateFormat sdf = dt.length() > 10
                            ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            : new java.text.SimpleDateFormat("yyyy-MM-dd");
                        k.setTs(sdf.parse(dt).getTime());
                    } catch (Exception e) {
                        continue;
                    }
                    BigDecimal open  = v.getBigDecimal("open");
                    BigDecimal high  = v.getBigDecimal("high");
                    BigDecimal low   = v.getBigDecimal("low");
                    BigDecimal close = v.getBigDecimal("close");
                    BigDecimal vol   = v.getBigDecimal("volume");
                    if (open == null || high == null || low == null || close == null) continue;
                    k.setOpen(open.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    k.setHigh(high.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    k.setLow(low.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    k.setClose(close.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    k.setVolume(vol == null ? 0 : vol.setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    klines.add(k);
                }
                Collections.sort(klines);
                result.put(row[1], klines);
            } catch (Exception e) {
                logger.warn("twelvedata kline error, symbol={}, interval={}, msg={}", symbol, row[0], e.getMessage());
            }
        }
        return result;
    }
}
