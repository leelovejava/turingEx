package com.yami.trading.huobi.task;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.huobi.hobi.http.HttpMethodType;
import com.yami.trading.huobi.hobi.internal.SpiderService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HuobiRealtimeSyncTask {

    @Qualifier("redissonClientSpider")
    @Autowired
    private RedissonClient redissonClientSpider;

    @Autowired
    private ItemService itemService;

    @Value("${huobi.realtime-sync.enabled:true}")
    private boolean enabled;

    @Value("${huobi.realtime-sync.url:https://api.huobi.pro/market/tickers}")
    private String huobiTickersUrl;

    @Value("${huobi.realtime-sync.detail-url:https://api.huobi.pro/market/detail/merged}")
    private String huobiDetailUrl;

    @Scheduled(cron = "${huobi.realtime-sync.cron:*/3 * * * * ?}")
    public void syncToSpiderRealtimeHash() {
        if (!enabled) {
            return;
        }
        log.info("[HuobiSyncV2] start sync realtime_hash");
        try {
            String result = HttpHelper.getJSONFromHttpNew(huobiTickersUrl, Collections.emptyMap(), HttpMethodType.GET);
            JSONObject json = JSON.parseObject(result);
            if (json == null || !"ok".equalsIgnoreCase(json.getString("status"))) {
                log.warn("[HuobiSync] invalid response, status={}", json == null ? null : json.getString("status"));
                return;
            }

            JSONArray data = json.getJSONArray("data");
            if (data == null || data.isEmpty()) {
                log.warn("[HuobiSync] empty ticker data from huobi");
                return;
            }

            Map<String, JSONObject> byPair = data.stream()
                    .map(o -> (JSONObject) o)
                    .filter(Objects::nonNull)
                    .filter(o -> StrUtil.isNotBlank(o.getString("symbol")))
                    .collect(Collectors.toMap(o -> o.getString("symbol").toLowerCase(), Function.identity(), (a, b) -> a));
            if (log.isInfoEnabled()) {
                List<String> samples = byPair.keySet().stream().limit(10).collect(Collectors.toList());
                log.info("[HuobiSync] fetched tickers={}, sampleSymbols={}", byPair.size(), samples);
            }

            Map<String, JSONObject> byBase = new HashMap<>();
            for (Map.Entry<String, JSONObject> entry : byPair.entrySet()) {
                String pair = entry.getKey();
                String base = extractBase(pair);
                if (StrUtil.isBlank(base)) {
                    continue;
                }
                byBase.putIfAbsent(base, entry.getValue());
            }

            List<Item> cryptos = itemService.list().stream()
                    .filter(Objects::nonNull)
                    .filter(i -> Item.cryptos.equalsIgnoreCase(i.getType()))
                    .collect(Collectors.toList());

            if (cryptos.isEmpty()) {
                return;
            }

            long ts = System.currentTimeMillis();
            RMap<String, String> realtimeMap = redissonClientSpider.getMap(SpiderService.REALTIME_HASH);
            int success = 0;
            int miss = 0;
            List<String> missSamples = new ArrayList<>();

            for (Item item : cryptos) {
                String remark = item.getRemarks();
                if (StrUtil.isBlank(remark)) {
                    continue;
                }
                String raw = remark.trim().toLowerCase();
                String base = normalizeBase(raw);
                JSONObject ticker = byPair.get(base + "usdt");
                if (ticker == null) {
                    ticker = byPair.get(base + "usdc");
                }
                if (ticker == null) {
                    ticker = byPair.get(base + "usd");
                }
                if (ticker == null) {
                    ticker = byPair.get(base + "husd");
                }
                if (ticker == null) {
                    // remark 可能本身就是交易对，例如 dogeusdt
                    ticker = byPair.get(raw);
                }
                if (ticker == null) {
                    ticker = byBase.get(base);
                }
                if (ticker == null) {
                    ticker = fetchSingleTicker(base);
                    if (ticker == null) {
                        miss++;
                        if (missSamples.size() < 8) {
                            missSamples.add(base);
                        }
                        continue;
                    }
                }

                BigDecimal open = safeNum(ticker.getBigDecimal("open"));
                BigDecimal close = safeNum(ticker.getBigDecimal("close"));
                BigDecimal high = safeNum(ticker.getBigDecimal("high"));
                BigDecimal low = safeNum(ticker.getBigDecimal("low"));
                BigDecimal amount = safeNum(ticker.getBigDecimal("amount"));
                BigDecimal vol = safeNum(ticker.getBigDecimal("vol"));

                BigDecimal chg = close.subtract(open);
                BigDecimal percent = BigDecimal.ZERO;
                if (open.compareTo(BigDecimal.ZERO) > 0) {
                    percent = chg.divide(open, 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                }

                JSONObject one = new JSONObject();
                one.put("symbol", base);
                one.put("time", ts);
                one.put("current", close);
                one.put("open", open);
                one.put("close", close);
                one.put("high", high);
                one.put("low", low);
                one.put("amount", amount);
                one.put("volume", vol);
                one.put("chg", chg);
                one.put("percent", percent);
                one.put("ask", close);
                one.put("bid", close);

                realtimeMap.put(base, one.toJSONString());
                success++;
            }

            log.info("[HuobiSync] write realtime_hash done, success={}, miss={}, total={}, missSamples={}",
                    success, miss, cryptos.size(), missSamples);
            try {
                log.info("[HuobiSync] realtime_hash size now={}", realtimeMap.size());
            } catch (Exception ignore) {
            }
        } catch (Exception e) {
            log.error("[HuobiSync] sync failed", e);
        }
    }

    private static String extractBase(String pair) {
        if (StrUtil.isBlank(pair)) {
            return null;
        }
        String p = pair.toLowerCase();
        String[] quotes = {"usdt", "usdc", "husd", "usd", "btc", "eth"};
        for (String q : quotes) {
            if (p.endsWith(q) && p.length() > q.length()) {
                return p.substring(0, p.length() - q.length());
            }
        }
        return null;
    }

    private static String normalizeBase(String remark) {
        String fromPair = extractBase(remark);
        return StrUtil.isBlank(fromPair) ? remark : fromPair;
    }

    private JSONObject fetchSingleTicker(String base) {
        try {
            String[] quotes = {"usdt", "usdc", "usd", "husd"};
            for (String q : quotes) {
                Map<String, String> param = new HashMap<>();
                param.put("symbol", base + q);
                String result = HttpHelper.getJSONFromHttpNew(huobiDetailUrl, param, HttpMethodType.GET);
                JSONObject json = JSON.parseObject(result);
                if (json == null || !"ok".equalsIgnoreCase(json.getString("status"))) {
                    continue;
                }
                JSONObject tick = json.getJSONObject("tick");
                if (tick == null) {
                    continue;
                }
                log.info("[HuobiSync] fallback hit base={}, pair={}", base, base + q);
                JSONObject one = new JSONObject();
                one.put("symbol", base);
                one.put("open", safeNum(tick.getBigDecimal("open")));
                one.put("close", safeNum(tick.getBigDecimal("close")));
                one.put("high", safeNum(tick.getBigDecimal("high")));
                one.put("low", safeNum(tick.getBigDecimal("low")));
                one.put("amount", safeNum(tick.getBigDecimal("amount")));
                one.put("vol", safeNum(tick.getBigDecimal("vol")));
                return one;
            }
        } catch (Exception ignore) {
            // ignore single symbol fallback errors
        }
        return null;
    }

    private static BigDecimal safeNum(BigDecimal n) {
        return n == null ? BigDecimal.ZERO : n;
    }
}
