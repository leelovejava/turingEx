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
import java.util.ArrayList;
import java.util.List;

@Component
public class FreeForexRateService {

    private static final Logger logger = LoggerFactory.getLogger(FreeForexRateService.class);
    private static final String FREE_LIVE_URL = "https://open.er-api.com/v6/latest/USD";

    @Autowired
    private ItemService itemService;

    public List<Realtime> fetchRealtime(String symbols) {
        List<Realtime> list = new ArrayList<>();
        if (StrUtil.isBlank(symbols)) {
            return list;
        }
        try {
            String result = HttpHelper.sendGetHttp(FREE_LIVE_URL, "");
            if (StrUtil.isBlank(result)) {
                return list;
            }
            JSONObject body = JSON.parseObject(result);
            if (!"success".equalsIgnoreCase(body.getString("result"))) {
                logger.warn("免费外汇接口返回失败: {}", body.toJSONString());
                return list;
            }
            JSONObject rates = body.getJSONObject("rates");
            if (rates == null || rates.isEmpty()) {
                return list;
            }
            Long ts = body.getLong("time_last_update_unix");
            long tsMs = ts == null ? System.currentTimeMillis() : ts * 1000L;

            String[] pairs = symbols.toUpperCase().split(",");
            for (String pair : pairs) {
                String symbol = pair == null ? "" : pair.trim();
                if (!symbol.matches("^[A-Z]{6}$")) {
                    continue;
                }
                String base = symbol.substring(0, 3);
                String quote = symbol.substring(3, 6);
                BigDecimal baseRate = rateOf(rates, base);
                BigDecimal quoteRate = rateOf(rates, quote);
                if (baseRate == null || quoteRate == null || baseRate.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                BigDecimal price = quoteRate.divide(baseRate, 12, RoundingMode.HALF_UP);
                int decimal = decimalOf(symbol);
                double p = price.setScale(decimal, RoundingMode.HALF_UP).doubleValue();

                Realtime realtime = new Realtime();
                realtime.setSymbol(symbol);
                realtime.setName(symbol);
                realtime.setTs(tsMs);
                realtime.setOpen(p);
                realtime.setClose(p);
                realtime.setHigh(p);
                realtime.setLow(p);
                realtime.setBid(p);
                realtime.setAsk(p);
                realtime.setAmount(0D);
                realtime.setVolume(0D);
                list.add(realtime);
            }
        } catch (Exception e) {
            logger.warn("免费外汇接口请求异常, symbols={}, msg={}", symbols, e.getMessage());
        }
        return list;
    }

    private BigDecimal rateOf(JSONObject rates, String currency) {
        if ("USD".equals(currency)) {
            return BigDecimal.ONE;
        }
        return rates.getBigDecimal(currency);
    }

    private int decimalOf(String symbol) {
        try {
            return itemService.getDecimal(symbol);
        } catch (Exception ignore) {
            return 6;
        }
    }
}

