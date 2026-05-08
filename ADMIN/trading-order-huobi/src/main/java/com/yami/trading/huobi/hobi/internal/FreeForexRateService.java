package com.yami.trading.huobi.hobi.internal;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.service.item.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 免费外汇汇率服务
 *
 * 本类提供免费的外汇汇率数据获取功能，用于交易系统中的货币兑换计算。
 *
 * 数据来源：Frankfurter API (https://api.frankfurter.dev)
 * - Frankfurter 是一个开源的外汇汇率API，数据来源于50多个央行和官方机构
 * - 支持160+种货币的汇率查询
 * - 完全免费，无需API密钥
 * - 每日更新一次汇率数据
 *
 * 主要功能：
 * - fetchRealtime: 获取指定货币对的实时汇率
 *
 * 支持的货币对格式：
 * - 必须是6位字母，如 "EURUSD" 表示欧元/美元汇率
 * - 前3位是基础货币（base），后3位是报价货币（quote）
 * - 例如：EURUSD = 1欧元 = ?美元
 *
 * 汇率计算逻辑：
 * - Frankfurter API 以USD为基准返回所有货币兑美元的汇率
 * - 例如：EURUSD = USD汇率(USD/EUR) / USD汇率(USD/USD)
 * - 由于USD/USD = 1，所以 EURUSD = 1 / rates.get("EUR") 不对
 * - 正确计算：EURUSD = rates.get("USD") / rates.get("EUR") = 1 / rates.get("EUR")
 * - Frankfurter返回的rates是以USD为base的，所以：
 *   - rates.get("EUR") 表示 1 USD = ? EUR
 *   - 要计算 EURUSD = 1 EUR = ? USD，需要用 quoteRate / baseRate
 *   - 其中 baseRate = rates.get("EUR")，quoteRate = rates.get("USD") = 1
 *
 * 使用示例：
 * - fetchRealtime("EURUSD") 返回欧元/美元的实时汇率
 * - fetchRealtime("EURUSD,GBPUSD,JPYUSD") 返回多个货币对的汇率
 *
 * 注意事项：
 * - 这是免费接口，有频率限制，请勿频繁调用
 * - 汇率数据每日更新，非实时
 * - 返回的Realtime对象中，open/high/low/close/bid/ask都是相同的价格
 */
@Component
public class FreeForexRateService {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(FreeForexRateService.class);

    /**
     * Frankfurter API 地址
     *
     * 接口说明：
     * - base=USD 表示以美元为基准货币
     * - API会返回所有货币兑美元的汇率
     * - 例如：{"EUR": 0.92, "GBP": 0.79, ...} 表示 1 USD = 0.92 EUR
     *
     * API响应格式：
     * {
     *   "amount": 1,
     *   "base": "USD",
     *   "date": "2026-05-04",
     *   "rates": {
     *     "EUR": 0.8542,
     *     "GBP": 0.7301,
     *     "JPY": 110.15,
     *     ...
     *   }
     * }
     */
    private static final String FREE_LIVE_URL = "https://api.frankfurter.app/latest?from=USD";

    /**
     * Item服务，用于获取货币对的小数位数配置
     */
    @Autowired
    private ItemService itemService;

    /**
     * 获取指定货币对的实时汇率
     *
     * @param symbols 货币对字符串，多个用逗号分隔
     *                  格式：6位大写字母，如 "EURUSD" 表示欧元/美元
     *                  示例："EURUSD" 或 "EURUSD,GBPUSD,JPYUSD"
     * @return Realtime对象列表，每个对象包含货币对的汇率信息
     *         返回的Realtime对象主要字段说明：
     *         - symbol: 货币对代码（如 "EURUSD"）
     *         - name: 货币对名称（同symbol）
     *         - ts: 时间戳（毫秒）
     *         - open/high/low/close: 汇率价格（对于外汇，这些值相同）
     *         - bid/ask: 买一价/卖一价（对于外汇，这些值相同）
     *         - volume: 成交量（此处为0）
     *         - amount: 成交额（此处为0）
     *
     * 处理流程：
     * 1. 参数校验：如果symbols为空，直接返回空列表
     * 2. 调用Frankfurter API获取美元汇率数据
     * 3. 解析响应，提取rates对象和date字段
     * 4. 将时间戳转换为毫秒
     * 5. 解析symbols（支持多个货币对，用逗号分隔）
     * 6. 对每个货币对：
     *    - 验证格式（必须是6位大写字母）
     *    - 提取基础货币和报价货币
     *    - 计算汇率：quote货币/USD汇率 ÷ base货币/USD汇率
     *    - 获取小数位数配置
     *    - 创建Realtime对象并添加到返回列表
     * 7. 捕获所有异常并记录日志
     * 8. 返回结果列表（即使部分货币对获取失败，也会返回成功的部分）
     *
     * 计算示例：
     * - 要计算 EURUSD（1欧元=?美元）
     * - Frankfurter返回 rates = {"EUR": 0.92} 表示 1 USD = 0.92 EUR
     * - 所以 1 EUR = 1/0.92 USD = 1.087 USD
     * - 即 EURUSD = 1 / rates.get("EUR") = 1 / 0.92 = 1.087
     *
     * 错误处理：
     * - 如果API返回空结果或解析失败，返回空列表
     * - 如果某个货币对解析失败，跳过该货币对，继续处理其他的
     * - 所有异常都会被捕获并记录，不会抛出
     */
    public List<Realtime> fetchRealtime(String symbols) {
        // 初始化返回列表
        List<Realtime> list = new ArrayList<>();

        // 参数校验：如果symbols为空，直接返回空列表
        if (StrUtil.isBlank(symbols)) {
            return list;
        }

        try {
            // 第1步：调用Frankfurter API获取汇率数据
            // 使用HttpHelper发送GET请求
            String result = HttpHelper.sendGetHttp(FREE_LIVE_URL, "");

            // 检查返回结果是否为空
            if (StrUtil.isBlank(result)) {
                return list;
            }

            // 第2步：解析JSON响应
            JSONObject body = JSON.parseObject(result);

            // 第3步：验证响应状态
            // Frankfurter API 成功时返回的JSON中不包含"result"字段
            // 失败时可能包含result字段且值不为success
            // 也可直接检查rates是否存在
            if (!"success".equalsIgnoreCase(body.getString("result")) && body.get("rates") == null) {
                logger.warn("免费外汇接口返回失败: {}", body.toJSONString());
                return list;
            }

            // 第4步：提取汇率数据
            // rates对象包含所有货币兑美元的汇率
            JSONObject rates = body.getJSONObject("rates");
            if (rates == null || rates.isEmpty()) {
                return list;
            }

            // 第5步：处理时间戳
            // Frankfurter返回date字段（格式如"2026-05-04"），不是Unix时间戳
            Long ts = body.getLong("time_last_update_unix");
            if (ts == null) {
                // API没有返回Unix时间戳，尝试从date字段解析
                String dateStr = body.getString("date");
                if (dateStr != null) {
                    try {
                        // 解析日期字符串为时间戳
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        Date date = sdf.parse(dateStr);
                        ts = date.getTime() / 1000;
                    } catch (Exception e) {
                        // 日期解析失败，使用当前时间
                        ts = System.currentTimeMillis() / 1000;
                    }
                } else {
                    // 也没有date字段，使用当前时间
                    ts = System.currentTimeMillis() / 1000;
                }
            }
            // 转换为毫秒时间戳
            long tsMs = ts * 1000L;

            // 第6步：解析货币对列表
            // 支持多个货币对，用逗号分隔
            // 格式：如 "EURUSD,GBPUSD,JPYUSD"
            String[] pairs = symbols.toUpperCase().split(",");

            // 第7步：遍历每个货币对，计算汇率
            for (String pair : pairs) {
                // 去除空格，防止 " EURUSD " 这样的输入导致问题
                String symbol = pair == null ? "" : pair.trim();

                // 验证格式：必须是6位大写字母
                // 正则 ^[A-Z]{6}$ 确保：
                // - 只有字母
                // - 正好6个字符
                // - 全部大写
                if (!symbol.matches("^[A-Z]{6}$")) {
                    continue;
                }

                // 提取基础货币和报价货币
                // 例如 EURUSD：base = "EUR", quote = "USD"
                String base = symbol.substring(0, 3);   // 前3位是基础货币
                String quote = symbol.substring(3, 6);   // 后3位是报价货币

                // 获取两种货币兑美元的汇率
                // rateOf方法：如果货币是USD，返回BigDecimal.ONE
                // 否则从API返回的rates中获取
                BigDecimal baseRate = rateOf(rates, base);   // 1 USD = ? base货币
                BigDecimal quoteRate = rateOf(rates, quote); // 1 USD = ? quote货币

                // 校验汇率数据有效性
                // 必须非空、非零才能计算
                if (baseRate == null || quoteRate == null || baseRate.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                // 第8步：计算汇率
                // 公式：base货币/quote货币 = quoteRate / baseRate
                // 例如 EURUSD = (1 USD = ? USD) / (1 USD = ? EUR) = 1 / rates.get("EUR")
                // 即 EURUSD = quoteRate / baseRate
                // 因为 quoteRate 对于 USD 来说是 1
                BigDecimal price = quoteRate.divide(baseRate, 12, RoundingMode.HALF_UP);

                // 获取该货币对的小数位数
                // 用于格式化输出价格
                int decimal = decimalOf(symbol);

                // 四舍五入到指定小数位
                double p = price.setScale(decimal, RoundingMode.HALF_UP).doubleValue();

                // 第9步：创建Realtime对象
                Realtime realtime = new Realtime();
                realtime.setSymbol(symbol);
                realtime.setName(symbol);
                realtime.setTs(tsMs);

                // 对于外汇汇率，open/high/low/close/bid/ask都使用相同的价格
                // 因为Frankfurter只提供日度数据，不提供实时报价
                realtime.setOpen(p);
                realtime.setClose(p);
                realtime.setHigh(p);
                realtime.setLow(p);
                realtime.setBid(p);
                realtime.setAsk(p);

                // 外汇交易量通常不使用，这里设为0
                realtime.setAmount(0D);
                realtime.setVolume(0D);

                // 添加到结果列表
                list.add(realtime);
            }
        } catch (Exception e) {
            // 捕获所有异常，记录日志
            // 不抛出异常，保证服务的可用性
            logger.warn("免费外汇接口请求异常, symbols={}, msg={}", symbols, e.getMessage());
        }

        // 返回结果列表
        return list;
    }

    /**
     * 获取指定货币兑美元的汇率
     *
     * @param rates API返回的汇率对象
     *              结构：{"EUR": 0.92, "GBP": 0.79, ...}
     *              表示 1 USD = ? 该货币
     * @param currency 货币代码（3位大写字母，如 "EUR"）
     * @return 该货币兑美元的汇率
     *         - 如果是USD，返回 BigDecimal.ONE（1.0）
     *         - 其他货币返回 rates.get(currency)
     *
     * 说明：
     * - Frankfurter API 以USD为基准
     * - rates中的值表示 1 USD = ? 该货币
     * - 所以 USD 自己的汇率是 1
     *
     * 示例：
     * - rateOf(rates, "USD") 返回 1.0
     * - rateOf(rates, "EUR") 返回 0.92（表示 1 USD = 0.92 EUR）
     * - rateOf(rates, "JPY") 返回 110.15（表示 1 USD = 110.15 JPY）
     */
    private BigDecimal rateOf(JSONObject rates, String currency) {
        // 如果是美元，直接返回1
        // 因为 1 USD = 1 USD
        if ("USD".equals(currency)) {
            return BigDecimal.ONE;
        }
        // 其他货币从rates中获取
        return rates.getBigDecimal(currency);
    }

    /**
     * 获取货币对的小数位数
     *
     * @param symbol 货币对代码（6位字母，如 "EURUSD"）
     * @return 小数位数
     *         - 如果获取失败，默认返回6位
     *
     * 说明：
     * - 不同货币对可能有不同的小数位数要求
     * - 例如：JPY/USD 可能只需要2位小数
     *         而 BTC/USD 可能需要8位小数
     * - 这个配置通常存储在ItemService中
     *
     * 常见货币对的小数位：
     * - 主要货币对（EURUSD, GBPUSD等）：4-5位
     * - 日元货币对（USDJPY等）：2-3位
     * - 加密货币（BTCUSD等）：8位
     */
    private int decimalOf(String symbol) {
        try {
            // 尝试从ItemService获取配置
            return itemService.getDecimal(symbol);
        } catch (Exception ignore) {
            // 获取失败，使用默认值6位
            return 6;
        }
    }
}
