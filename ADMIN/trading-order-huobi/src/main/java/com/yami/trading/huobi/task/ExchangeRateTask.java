package com.yami.trading.huobi.task;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yami.trading.bean.rate.domain.ExchangeRate;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.huobi.hobi.http.HttpMethodType;
import com.yami.trading.service.rate.ExchangeRateService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class ExchangeRateTask {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private SysparaService sysparaService;

    @Value("${exchange.rate:https://api.frankfurter.app/latest?from=USD}")
    private String exchangeRateUrl;

    @Scheduled(cron = "0 0 0/6 * * ?")
    public void getExchangeRate() {
        Syspara syspara = sysparaService.find("auto_exchange_rate");
        if (Objects.nonNull(syspara) && syspara.getBoolean()) {
            List<ExchangeRate> rates = exchangeRateService.list();
            if (CollectionUtil.isNotEmpty(rates)) {
                try {
                    String json = HttpHelper.getJSONFromHttpNew(exchangeRateUrl, new HashMap<>(), HttpMethodType.GET);
                    Map<String, BigDecimal> ratesMap = new HashMap<>();
                    fillRatesMap(ratesMap, json);

                    for (ExchangeRate rate : rates) {
                        BigDecimal rateValue = ratesMap.get(rate.getCurrency());
                        if (Objects.nonNull(rateValue)) {
                            rate.setRata(rateValue.setScale(4, RoundingMode.DOWN));
                            exchangeRateService.updateById(rate);
                        }
                    }
                } catch (Exception e) {
                    log.error("采集外汇汇率失败:{} ", e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        String json = HttpHelper.getJSONFromHttpNew("https://api.frankfurter.app/latest?from=USD", new HashMap<>(), HttpMethodType.GET);
        Map<String, BigDecimal> ratesMap = new HashMap<>();
        fillRatesMap(ratesMap, json);

        if (ratesMap.containsKey("AUD")) {
            System.out.println("result: AUD = " + ratesMap.get("AUD").setScale(4, RoundingMode.DOWN));
        }
    }

    private static void fillRatesMap(Map<String, BigDecimal> ratesMap, String json) {
        if (json == null || json.trim().isEmpty()) {
            return;
        }
        String trimmed = json.trim();
        if (trimmed.startsWith("[")) {
            JSONArray resultArray = JSON.parseArray(trimmed);
            for (int i = 0; i < resultArray.size(); i++) {
                JSONObject rateObj = resultArray.getJSONObject(i);
                String quote = rateObj.getString("quote");
                BigDecimal rate = rateObj.getBigDecimal("rate");
                if (quote != null && rate != null) {
                    ratesMap.put(quote, rate);
                }
            }
            return;
        }
        JSONObject root = JSON.parseObject(trimmed);
        JSONObject ratesObj = root.getJSONObject("rates");
        if (ratesObj == null) {
            return;
        }
        for (String quote : ratesObj.keySet()) {
            BigDecimal rate = ratesObj.getBigDecimal(quote);
            if (rate != null) {
                ratesMap.put(quote, rate);
            }
        }
    }
}