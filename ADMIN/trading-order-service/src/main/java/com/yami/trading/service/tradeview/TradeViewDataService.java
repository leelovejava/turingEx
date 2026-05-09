package com.yami.trading.service.tradeview;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.http.HttpHelper;
import com.yami.trading.common.http.HttpMethodType;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * TradeView数据服务
 * <p>
 * 该服务负责从数据源获取股票和行业数据，并提供相关的数据处理功能。
 * 主要功能包括：获取股票数据、获取行业数据、计算行业涨跌幅等。
 * <p>
 * 当前状况：原接口 https://onjdo.com/stock/api/live/getTradeViewData 已失效(404)
 * 解决方案：使用 Twelve Data API 作为替代数据源
 * 
 * 配置方式：
 * 在 application.yml 中配置：
 *   twelvedata:
 *     base-url: https://api.twelvedata.com
 *     api-key: YOUR_API_KEY  # 需要在 Twelve Data 官网注册获取
 * 
 * Twelve Data官网：https://twelvedata.com/
 * 免费额度：每月8000次API调用，延迟1分钟
 */
@Component
@Slf4j
public class TradeViewDataService {

    /**
     * TradeView数据接口URL（已失效）
     * 默认值为：https://onjdo.com/stock/api/live/getTradeViewData
     */
    @Value("${tradeview.data-url:https://onjdo.com/stock/api/live/getTradeViewData}")
    private String tradeViewDataUrl;

    /**
     * Twelve Data API 基础URL
     * Twelve Data是一个可靠的股票数据API服务，提供实时和历史股票数据
     */
    @Value("${twelvedata.base-url:https://api.twelvedata.com}")
    private String twelveDataBaseUrl;

    /**
     * Twelve Data API Key（需要在配置文件中设置）
     * 获取地址：https://twelvedata.com/pricing（免费额度可用）
     */
    @Value("${twelvedata.api-key:}")
    private String twelveDataApiKey;

    /**
     * 股票服务，用于从数据库获取股票列表
     */
    @Autowired
    private ItemService itemService;

    /**
     * 根据类型获取TradeView股票数据
     *
     * @param type 数据类型参数，用于区分不同类型的股票数据
     * @return 返回符合条件的股票数据列表
     * @throws RuntimeException 当无法获取数据时抛出异常
     */
    public List<SymbolTradeViewData> getTradeViewDataStocks(String type) {
        List<SymbolTradeViewData> lists = Lists.newArrayList();
        
        // 优先从Twelve Data API获取数据（原接口已失效）
        if (StrUtil.isNotBlank(twelveDataApiKey)) {
            try {
                lists = fetchFromTwelveData(type);
                if (!lists.isEmpty()) {
                    log.info("Successfully fetched data from Twelve Data API, type={}, count={}", type, lists.size());
                    return lists;
                }
            } catch (Exception e) {
                log.warn("Failed to fetch data from Twelve Data API, type={}, msg={}", type, e.getMessage());
            }
        }

        // 原接口已失效，直接抛出异常
        String errorMsg = String.format("Failed to fetch TradeView stock data. Original TradeView API (%s) is no longer available. Please configure Twelve Data API Key in application.yml.", tradeViewDataUrl);
        log.error(errorMsg);
        throw new RuntimeException(errorMsg);
    }

    /**
     * 获取TradeView行业数据
     * <p>
     * 该方法会将获取到的数据同时存入SymbolTradeViewDataHolder中进行缓存。
     *
     * @return 返回行业数据列表
     * @throws RuntimeException 当无法获取数据时抛出异常
     */
    public List<SymbolTradeViewData> getTradeViewDataIndustry() {
        List<SymbolTradeViewData> lists = Lists.newArrayList();
        
        // 优先从Twelve Data API获取数据（原接口已失效）
        if (StrUtil.isNotBlank(twelveDataApiKey)) {
            try {
                lists = fetchFromTwelveData(Item.US_STOCKS);
                if (!lists.isEmpty()) {
                    SymbolTradeViewDataHolder.typeList.put(Item.US_STOCKS, lists);
                    log.info("Successfully fetched industry data from Twelve Data API, count={}", lists.size());
                    return lists;
                }
            } catch (Exception e) {
                log.warn("Failed to fetch industry data from Twelve Data API, msg={}", e.getMessage());
            }
        }

        // 原接口已失效，直接抛出异常
        String errorMsg = String.format("Failed to fetch TradeView industry data. Original TradeView API (%s) is no longer available. Please configure Twelve Data API Key in application.yml.", tradeViewDataUrl);
        log.error(errorMsg);
        throw new RuntimeException(errorMsg);
    }

    /**
     * 从Twelve Data API获取股票数据
     * 
     * Twelve Data API提供可靠的股票实时和历史数据。
     * API文档：https://twelvedata.com/docs
     * 
     * @param type 股票类型（如 US_STOCKS, A_STOCKS, HK_STOCKS）
     * @return 股票数据列表
     */
    private List<SymbolTradeViewData> fetchFromTwelveData(String type) {
        List<SymbolTradeViewData> lists = Lists.newArrayList();
        
        // 根据类型获取对应的股票代码列表
        List<String> symbols = getStockSymbolsByType(type);
        
        if (symbols.isEmpty()) {
            log.warn("No symbols found for type: {}", type);
            return lists;
        }
        
        log.debug("Fetching {} symbols from Twelve Data API for type: {}", symbols.size(), type);
        
        for (String symbol : symbols) {
            try {
                String url = twelveDataBaseUrl + "/quote";
                Map<String, String> param = Maps.newHashMap();
                param.put("symbol", symbol);
                param.put("apikey", twelveDataApiKey);
                
                String result = HttpHelper.getJSONFromHttpNew(url, param, HttpMethodType.GET);
                if (StrUtil.isBlank(result)) {
                    log.debug("Empty response for symbol: {}", symbol);
                    continue;
                }
                
                JSONObject obj = JSON.parseObject(result);
                if (obj == null) {
                    log.debug("Null JSON response for symbol: {}", symbol);
                    continue;
                }
                
                // 检查API错误响应
                if (obj.containsKey("code")) {
                    int code = obj.getInteger("code");
                    if (code >= 400) {
                        String message = obj.getString("message");
                        log.warn("Twelve Data API error for symbol {}: {} - {}", symbol, code, message);
                        continue;
                    }
                }
                
                String symbolRaw = obj.getString("symbol");
                if (StrUtil.isBlank(symbolRaw)) {
                    log.debug("Empty symbol in response for: {}", symbol);
                    continue;
                }
                
                // 构建SymbolTradeViewData对象
                SymbolTradeViewData data = new SymbolTradeViewData();
                data.setSymbol(symbolRaw.toUpperCase(Locale.ROOT));
                
                // 获取涨跌幅
                BigDecimal percentChange = obj.getBigDecimal("percent_change");
                if (percentChange != null) {
                    data.setRate(percentChange.setScale(2, RoundingMode.HALF_UP));
                } else {
                    data.setRate(BigDecimal.ZERO);
                }
                
                // 获取市值（Twelve Data提供的市值字段）
                BigDecimal marketCap = obj.getBigDecimal("market_cap");
                if (marketCap != null) {
                    data.setMarketCap(marketCap);
                } else {
                    data.setMarketCap(BigDecimal.ZERO);
                }
                
                // 设置行业（Twelve Data不直接提供行业信息）
                data.setIndustry(getIndustryForSymbol(data.getSymbol()));
                
                lists.add(data);
                
            } catch (Exception e) {
                log.warn("Failed to fetch symbol {} from Twelve Data API, msg={}", symbol, e.getMessage());
            }
        }
        
        log.debug("Successfully fetched {} items from Twelve Data API for type: {}", lists.size(), type);
        return lists;
    }

    /**
     * 根据类型获取股票代码列表
     * 
     * 优先从数据库获取，如果数据库没有则使用默认列表
     * 
     * @param type 股票类型
     * @return 股票代码列表
     */
    private List<String> getStockSymbolsByType(String type) {
        List<String> symbols = Lists.newArrayList();
        
        // 优先从数据库获取股票列表
        try {
            List<Item> items = itemService.findByType(type);
            if (!items.isEmpty()) {
                symbols = items.stream()
                        .map(item -> convertToTwelveDataSymbol(item.getRemarks(), type))
                        .filter(StrUtil::isNotBlank)
                        .collect(Collectors.toList());
                log.debug("Fetched {} symbols from database for type: {}", symbols.size(), type);
                return symbols;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch symbols from database for type: {}, using default list", type);
        }
        
        // 如果数据库获取失败或为空，使用默认列表
        return getDefaultStockSymbols(type);
    }

    /**
     * 获取默认股票代码列表（备用）
     * 
     * @param type 股票类型
     * @return 默认股票代码列表
     */
    private List<String> getDefaultStockSymbols(String type) {
        List<String> symbols = Lists.newArrayList();
        
        switch (type) {
            case Item.US_STOCKS:
                // 美股热门股票
                symbols.add("AAPL");
                symbols.add("GOOGL");
                symbols.add("MSFT");
                symbols.add("AMZN");
                symbols.add("TSLA");
                symbols.add("META");
                symbols.add("NVDA");
                symbols.add("JPM");
                symbols.add("V");
                symbols.add("JNJ");
                symbols.add("WMT");
                symbols.add("BABA");
                symbols.add("TCEHY");
                break;
            case Item.A_STOCKS:
                // A股（需要使用Twelve Data支持的格式：代码+交易所后缀）
                symbols.add("600519.SS");  // 贵州茅台
                symbols.add("000858.SZ");  // 五粮液
                symbols.add("000001.SZ");  // 平安银行
                symbols.add("601318.SS");  // 中国平安
                symbols.add("600036.SS");  // 招商银行
                symbols.add("000333.SZ");  // 美的集团
                break;
            case Item.HK_STOCKS:
                // 港股（需要使用Twelve Data支持的格式）
                symbols.add("0005.HK");    // 汇丰控股
                symbols.add("0700.HK");    // 腾讯控股
                symbols.add("0016.HK");    // 新鸿基地产
                symbols.add("0001.HK");    // 长和
                symbols.add("0388.HK");    // 香港交易所
                break;
            case Item.TW_STOCKS:
                // 台股
                symbols.add("2330.TW");    // 台积电
                symbols.add("2317.TW");    // 鸿海
                symbols.add("2454.TW");    // 联发科
                break;
            default:
                // 默认返回美股
                symbols.add("AAPL");
                symbols.add("GOOGL");
                symbols.add("MSFT");
                break;
        }
        
        return symbols;
    }

    /**
     * 将数据库中的股票代码转换为Twelve Data格式
     * 
     * Twelve Data支持的格式：
     * - 美股：AAPL, GOOGL（直接代码）
     * - A股：600519.SS（上交所）, 000858.SZ（深交所）
     * - 港股：0005.HK
     * - 台股：2330.TW
     * 
     * @param remarks 数据库中的股票代码
     * @param type 股票类型
     * @return Twelve Data格式的股票代码
     */
    private String convertToTwelveDataSymbol(String remarks, String type) {
        if (StrUtil.isBlank(remarks)) {
            return null;
        }
        
        // 移除可能的前缀
        String symbol = remarks.toUpperCase(Locale.ROOT).trim();
        
        switch (type) {
            case Item.A_STOCKS:
                // A股需要添加交易所后缀
                if (symbol.startsWith("6") && symbol.length() == 6) {
                    return symbol + ".SS";  // 上交所
                } else if (symbol.startsWith("0") && symbol.length() == 6) {
                    return symbol + ".SZ";  // 深交所
                } else if (symbol.startsWith("3") && symbol.length() == 6) {
                    return symbol + ".SZ";  // 深交所创业板
                }
                break;
            case Item.HK_STOCKS:
                // 港股需要添加.HK后缀
                if (symbol.matches("\\d{4}")) {
                    return symbol + ".HK";
                }
                break;
            case Item.TW_STOCKS:
                // 台股需要添加.TW后缀
                if (symbol.matches("\\d{4}")) {
                    return symbol + ".TW";
                }
                break;
            case Item.US_STOCKS:
            default:
                // 美股直接返回
                return symbol;
        }
        
        return symbol;
    }

    /**
     * 获取股票所属行业
     * 
     * 由于Twelve Data不直接提供行业信息，这里使用预设的行业映射。
     * 实际应用中可以从数据库或其他数据源获取行业信息。
     * 
     * @param symbol 股票代码
     * @return 行业名称
     */
    private String getIndustryForSymbol(String symbol) {
        Map<String, String> industryMap = Maps.newHashMap();
        
        // 美股行业映射
        industryMap.put("AAPL", "科技");
        industryMap.put("GOOGL", "科技");
        industryMap.put("MSFT", "科技");
        industryMap.put("AMZN", "消费");
        industryMap.put("TSLA", "汽车");
        industryMap.put("META", "科技");
        industryMap.put("NVDA", "科技");
        industryMap.put("JPM", "金融");
        industryMap.put("V", "金融");
        industryMap.put("JNJ", "医药");
        industryMap.put("WMT", "消费");
        industryMap.put("BABA", "科技");
        industryMap.put("TCEHY", "科技");
        
        // A股行业映射
        industryMap.put("600519.SS", "消费");
        industryMap.put("000858.SZ", "消费");
        industryMap.put("000001.SZ", "金融");
        industryMap.put("601318.SS", "金融");
        industryMap.put("600036.SS", "金融");
        industryMap.put("000333.SZ", "家电");
        
        // 港股行业映射
        industryMap.put("0005.HK", "金融");
        industryMap.put("0700.HK", "科技");
        industryMap.put("0016.HK", "地产");
        industryMap.put("0001.HK", "综合");
        industryMap.put("0388.HK", "金融");
        
        // 台股行业映射
        industryMap.put("2330.TW", "科技");
        industryMap.put("2317.TW", "科技");
        industryMap.put("2454.TW", "科技");
        
        return industryMap.getOrDefault(symbol, "其他");
    }

    /**
     * 计算行业涨跌幅
     * 
     * @param tradeViewData 股票数据列表
     * @return 行业涨跌幅映射
     */
    public Map<String, String> industryRate(List<SymbolTradeViewData> tradeViewData) {
        Map<String, String> industryRateMap = Maps.newHashMap();
        
        if (CollectionUtils.isEmpty(tradeViewData)) {
            return industryRateMap;
        }
        
        // 按行业分组
        Map<String, List<SymbolTradeViewData>> industryGroup = tradeViewData.stream()
                .collect(Collectors.groupingBy(SymbolTradeViewData::getIndustry));
        
        // 计算每个行业的平均涨跌幅
        for (Map.Entry<String, List<SymbolTradeViewData>> entry : industryGroup.entrySet()) {
            String industry = entry.getKey();
            List<SymbolTradeViewData> stocks = entry.getValue();
            
            BigDecimal totalRate = stocks.stream()
                    .map(SymbolTradeViewData::getRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal avgRate = totalRate.divide(
                    BigDecimal.valueOf(stocks.size()), 
                    2, 
                    RoundingMode.HALF_UP
            );
            
            industryRateMap.put(industry, avgRate.toPlainString());
        }
        
        return industryRateMap;
    }
}