package com.yami.trading.huobi.task;

import com.alibaba.fastjson.JSONObject;
import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.data.domain.DepthEntry;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.Arith;
import com.yami.trading.huobi.data.TimeZoneConverterService;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DepthTimeObject;
import com.yami.trading.huobi.websocket.WebSocketServer;
import com.yami.trading.huobi.websocket.WebSocketSession;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * DepthPushJob - 市场深度数据推送任务
 *
 * 功能说明：
 * 该任务负责从缓存获取市场深度数据（买卖盘口），并通过WebSocket推送给客户端
 * 市场深度展示了各个价位的挂单量，是技术分析的重要数据
 *
 * 数据来源：DataCache缓存的市场深度数据
 * 推送方式：通过WebSocket Server向订阅的客户端推送数据
 * 执行频率：每500毫秒执行一次（高频推送）
 *
 * 核心特性：
 * 1. 支持多个客户端同时订阅不同币种的深度数据
 * 2. 根据市场开市/休市状态决定数据推送策略
 * 3. 对深度数据进行随机化处理，增加盘口活跃度
 * 4. 休市时使用缓存数据，确保客户端显示正常
 */
@Component
@Slf4j
public class DepthPushJob implements Runnable {

    // 用于存储休市时的深度数据缓存，key为币种symbol，value为JSON字符串
    private ConcurrentHashMap<String, String> lastData = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(DepthPushJob.class);

    @Autowired
    private ItemService itemService;

    @Autowired
    private TimeZoneConverterService timeZoneConverterService;

    @Autowired
    private DataService dataService;

    /**
     * 启动深度数据推送任务
     *
     * 创建一个独立线程来执行深度数据推送
     * 线程名称为 "depthPushJob"
     */
    public void start() {
        new Thread(this, "depthPushJob").start();
        if (logger.isInfoEnabled())
            logger.info("启动depthPushJob！");
    }

    /**
     * 核心运行方法 - 定时执行深度数据推送
     *
     * 执行频率：每500毫秒执行一次
     * 异常处理：单个周期异常不会中断服务，仅记录日志
     */
    public void run() {
        while (true) {
            try {
                this.depthHandle();
            } catch (Exception e) {
                logger.error("run fail", e);
            } finally {
                ThreadUtils.sleep(500);
            }
        }
    }

    /**
     * 深度数据处理核心方法
     *
     * 处理流程：
     * 1. 从WebSocketServer获取所有订阅了深度数据的客户端会话
     * 2. 提取所有订阅的币种去重集合
     * 3. 遍历每个币种，获取深度数据并处理
     * 4. 根据市场开市/休市状态决定数据来源（实时或缓存）
     * 5. 向订阅该币种的客户端推送深度数据
     */
    private void depthHandle() {
        try {
            ResultObject depthResult = new ResultObject();
            Map<String, String> depthResultMap = new HashMap<>();

            // 检查是否有客户端订阅了深度数据
            if (!WebSocketServer.depthMap.isEmpty()) {
                // 获取所有客户端请求的币种，去重集合
                Set<String> symbolSet = new HashSet<String>();
                for (String socketKey : WebSocketServer.depthMap.keySet()) {
                    WebSocketSession webSocketSession = WebSocketServer.depthMap.get(socketKey);
                    String symbolKey = webSocketSession.getParam();
                    symbolSet.add(symbolKey);
                }

                // 遍历每个订阅的币种
                for (String symbol : symbolSet) {
                    // 从缓存获取深度数据
                    DepthTimeObject depth = DataCache.getDepth(symbol);
                    Item bySymbol = itemService.findBySymbol(symbol);

                    // 如果没有对应的数据库记录，记录警告日志
                    if (bySymbol == null) {
                        logger.warn("---> DepthPushJob.depthHandle 当前 symbol:{} 没有对应的数据库记录", symbol);
                    }

                    // 如果缓存中没有深度数据，则调用服务获取
                    if (depth == null) {
                        dataService.depth(symbol);
                    }

                    // 如果缓存中有深度数据，则处理并设置结果
                    if (null != depth && null != depth.getDepth()) {
                        Depth depthData = depth.getDepth();
                        Realtime realtime = DataCache.getRealtime(symbol);
                        depthResult.setData(this.depthRevise(depthData, symbol, realtime.getClose(), true));
                    }

                    // 判断市场是否开市
                    if (MarketOpenChecker.isMarketOpenByItemCloseType(bySymbol.getOpenCloseType())) {
                        // 开市：使用实时数据
                        String jsonString = JSONObject.toJSONString(depthResult);
                        depthResultMap.put(symbol, jsonString);
                        lastData.put(symbol, jsonString);
                    } else {
                        // 休市：优先使用缓存数据
                        if (lastData.containsKey(symbol)) {
                            depthResultMap.put(symbol, lastData.get(symbol));
                        } else {
                            // 如果缓存也没有，则初始化一次
                            String jsonString = JSONObject.toJSONString(depthResult);
                            depthResultMap.put(symbol, jsonString);
                            lastData.put(symbol, jsonString);
                        }
                    }
                }

                // 如果没有有效数据，直接返回
                if (depthResultMap.isEmpty()) {
                    return;
                }

                // 向订阅的客户端推送数据
                for (String socketKey : WebSocketServer.depthMap.keySet()) {
                    WebSocketSession webSocketSession = WebSocketServer.depthMap.get(socketKey);
                    String type = webSocketSession.getType();
                    String symbolKey = webSocketSession.getParam();
                    WebSocketServer.sendToMessageById(socketKey, depthResultMap.get(symbolKey), type);
                }
            }
        } catch (Throwable e) {
            log.error("depthHandle error", e);
        }
    }

    /**
     * 市场深度数据解析与处理
     *
     * 功能：
     * 1. 将深度数据格式化为前端需要的格式
     * 2. 补充不足20条的盘口数据（买方和卖方各补足20条）
     * 3. 对价格和数量添加随机波动，使盘口看起来更活跃
     * 4. 处理精度格式化和时区时间
     *
     * @param data 深度数据
     * @param symbol 币种
     * @param close 当前收盘价/最新价
     * @param random 是否添加随机波动
     * @return 处理后的深度数据Map
     */
    public Map<String, Object> depthRevise(Depth data, String symbol, Double close, boolean random) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("symbol", symbol);

        Item item = this.itemService.findBySymbol(data.getSymbol());
        List<Map<String, Object>> asks_list = new ArrayList<Map<String, Object>>();

        // 卖盘（asks）补足到20条
        int asksSize = data.getAsks().size();
        for (int i = 0; i < 20 - asksSize; i++) {
            DepthEntry e = new DepthEntry();
            e.setAmount(RandomUtil.randomFloat(10, 100, 0));
            e.setPrice(close);
            data.getAsks().add(e);
        }

        // 买盘（bids）补足到20条
        int bidsSize = data.getBids().size();
        for (int i = 0; i < 20 - bidsSize; i++) {
            DepthEntry e = new DepthEntry();
            e.setAmount(RandomUtil.randomFloat(10, 100, 0));
            e.setPrice(close);
            data.getBids().add(e);
        }

        // 处理卖盘数据（asks）
        Set<String> asksPrices = new HashSet<>();
        asksSize = data.getAsks().size();
        bidsSize = data.getBids().size();

        for (int i = 0; i < asksSize; i++) {
            DepthEntry depthEntry = data.getAsks().get(i);
            Map<String, Object> asks_map = new HashMap<String, Object>();
            double price;
            double amount;

            if (random) {
                // 添加随机价格波动
                double addPriceValue = getRandomValue(String.valueOf(depthEntry.getPrice()), item.getDecimals());
                double addAmountValue = getRandomValue((int) depthEntry.getAmount().doubleValue());

                price = Arith.add(depthEntry.getPrice(), addPriceValue);
                // 确保卖盘价格不低于最新价
                if (price < close) {
                    price = Arith.add(close, addPriceValue);
                } else {
                    price = Arith.add(close, addPriceValue / 10);
                }
                amount = Arith.add(depthEntry.getAmount(), addAmountValue);
            } else {
                price = depthEntry.getPrice();
                amount = depthEntry.getAmount();
            }

            // 跳过无效价格
            if (price <= 0) {
                continue;
            }

            // 格式化价格和数量
            if (item.getDecimals() < 0) {
                asks_map.put("price", price);
                asks_map.put("amount", amount);
            } else {
                String format = "";
                if (item.getDecimals() == 0) {
                    format = "#";
                } else {
                    format = "#.";
                    for (int j = 0; j < item.getDecimals(); j++) {
                        format = format + "#";
                    }
                }

                DecimalFormat df = new DecimalFormat(format);
                df.setRoundingMode(RoundingMode.FLOOR);

                asks_map.put("price", df.format(price));
                // 去重：同一价格只保留一条
                if (asksPrices.contains(df.format(price))) {
                    continue;
                } else {
                    asksPrices.add(df.format(price));
                }
                asks_map.put("amount", df.format(amount));

                // 设置当前时区时间
                try {
                    String timeZone = timeZoneConverterService.getTimeZoneByItemCloseType(item.getOpenCloseType());
                    String dateStr = timeZoneConverterService.convertTimeZone(timeZone);
                    asks_map.put("current_time", dateStr.split(" ")[1]);
                } catch (Exception e) {
                    log.error("深度图设置 current_time失败");
                }
            }
            asks_list.add(asks_map);
        }

        // 卖盘数据按价格升序排序
        map.put("asks", asks_list.stream().sorted((a, b) -> {
            String priceA = (String) a.get("price");
            String priceB = (String) b.get("price");
            return Double.compare(Double.parseDouble(priceA), Double.parseDouble(priceB));
        }).collect(Collectors.toList()));

        // 处理买盘数据（bids）
        Set<String> bidPriceSet = new HashSet<>();
        List<Map<String, Object>> bids_list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < bidsSize; i++) {
            DepthEntry depthEntry = data.getBids().get(i);
            String priceTemp = new BigDecimal(String.valueOf(depthEntry.getPrice())).toPlainString();

            double addPriceValue = getRandomValue(priceTemp, item.getDecimals());
            double addAmountValue = getRandomValue((int) depthEntry.getAmount().doubleValue());
            double price;
            double amount;

            if (random) {
                // 买盘价格随机波动（向下）
                price = Arith.add(depthEntry.getPrice(), -addPriceValue);
                // 确保买盘价格不高于最新价
                if (price >= close) {
                    price = Arith.add(close, -addPriceValue);
                } else {
                    price = Arith.add(close, -addPriceValue / 10);
                }
                amount = Arith.add(depthEntry.getAmount(), addAmountValue);
            } else {
                price = depthEntry.getPrice();
                amount = depthEntry.getAmount();
            }

            if (price <= 0) {
                continue;
            }

            Map<String, Object> bids_map = new HashMap<>();
            if (item.getDecimals() < 0) {
                bids_map.put("price", price);
                bids_map.put("amount", amount);
            } else {
                String format = "";
                if (item.getDecimals() == 0) {
                    format = "#";
                } else {
                    format = "#.";
                    for (int j = 0; j < item.getDecimals(); j++) {
                        format = format + "#";
                    }
                }

                DecimalFormat df = new DecimalFormat(format);
                bids_map.put("price", df.format(price));
                // 去重
                if (bidPriceSet.contains(df.format(price))) {
                    continue;
                } else {
                    bidPriceSet.add(df.format(price));
                }
                bids_map.put("amount", df.format(amount));

                // 设置当前时区时间
                try {
                    String timeZone = timeZoneConverterService.getTimeZoneByItemCloseType(item.getOpenCloseType());
                    String dateStr = timeZoneConverterService.convertTimeZone(timeZone);
                    bids_map.put("current_time", dateStr.split(" ")[1]);
                } catch (Exception e) {
                    log.error("深度图设置 current_time失败");
                }
            }
            bids_list.add(bids_map);
        }

        // 买盘数据按价格降序排序（价格高的在前）
        map.put("bids", bids_list.stream().sorted((a, b) -> {
            String priceA = (String) a.get("price");
            String priceB = (String) b.get("price");
            return Double.compare(Double.parseDouble(priceB), Double.parseDouble(priceA));
        }).collect(Collectors.toList()));

        return map;
    }

    /**
     * 生成随机波动值（用于数量）
     *
     * 根据数值的数量级决定随机范围：
     * - 个位数：0.01 ~ 0.1999
     * - 十位数：0.1 ~ 0.5999
     * - 百位数：0.1 ~ 2.9999
     * - 千位数：1 ~ 3.9999
     * - 万位数及以上：1 ~ 5.9999
     *
     * @param value 基准值
     * @return 随机波动值
     */
    private double getRandomValue(int value) {
        double addValue;
        if (value > 0) {
            int count = 0;
            while (value > 0) {
                value = value / 10;
                count++;
            }
            // 个位
            if (count == 1) {
                addValue = RandomUtil.randomFloat(0.01, 0.1999, 4);
                return addValue;
            }
            // 十位
            if (count == 2) {
                addValue = RandomUtil.randomFloat(0.1, 0.5999, 4);
                return addValue;
            }
            // 百位
            if (count == 3) {
                addValue = RandomUtil.randomFloat(0.1, 2.9999, 4);
                return addValue;
            }
            // 千位
            if (count == 4) {
                addValue = RandomUtil.randomFloat(1, 3.9999, 4);
                return addValue;
            }
            // 万位
            if (count == 5) {
                addValue = RandomUtil.randomFloat(1, 5.9999, 4);
                return addValue;
            }
            // 十万位及以上
            else {
                addValue = RandomUtil.randomFloat(1, 5.9999, 4);
                return addValue;
            }
        } else {
            addValue = RandomUtil.randomFloat(0.01, 0.2999, 4);
            return addValue;
        }
    }

    /**
     * 生成随机波动值（用于价格）
     *
     * 根据数值的数量级和小数位数决定随机范围
     * 支持更高精度的随机值生成
     *
     * @param value 基准值字符串
     * @param decimal 小数位数
     * @return 随机波动值
     */
    private double getRandomValue(String value, int decimal) {
        double addValue;
        double d = Double.valueOf(value);
        int val = (int) d;

        // 个位数>0
        if (val > 0) {
            int count = 0;
            while (val > 0) {
                val = val / 10;
                count++;
            }
            // 个位
            if (count == 1) {
                addValue = RandomUtil.randomFloat(0.01, 0.1999, 4);
                return addValue;
            }
            // 十位
            if (count == 2) {
                addValue = RandomUtil.randomFloat(0.1, 0.5999, 4);
                return addValue;
            }
            // 百位
            if (count == 3) {
                addValue = RandomUtil.randomFloat(0.1, 2.9999, 4);
                return addValue;
            }
            // 千位
            if (count == 4) {
                addValue = RandomUtil.randomFloat(1, 3.9999, 4);
                return addValue;
            }
            // 万位
            if (count == 5) {
                addValue = RandomUtil.randomFloat(1, 5.9999, 4);
                return addValue;
            }
            // 十万位及以上
            else {
                addValue = RandomUtil.randomFloat(1, 5.9999, 4);
                return addValue;
            }
        }
        // 个位=0的情况，根据小数位数决定随机范围
        else {
            int valueLength = decimal;
            if (valueLength <= 4) {
                addValue = RandomUtil.randomFloat(0.0001, 0.0299, 4);
                return addValue;
            }
            if (4 < valueLength && valueLength <= 6) {
                addValue = RandomUtil.randomFloat(0.000001, 0.00299, 6);
                return addValue;
            }
            if (6 < valueLength && valueLength <= 8) {
                addValue = RandomUtil.randomFloat(0.00000001, 0.0000299, 8);
                return addValue;
            }
            if (8 < valueLength && valueLength <= 10) {
                addValue = RandomUtil.randomFloat(0.00000001, 0.0000299, 10);
                return addValue;
            } else {
                addValue = RandomUtil.randomFloat(0.00000000001, 0.0000000299, 11);
                return addValue;
            }
        }
    }

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }
}