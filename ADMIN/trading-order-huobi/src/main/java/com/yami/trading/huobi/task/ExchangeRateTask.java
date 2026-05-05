package com.yami.trading.huobi.task;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yami.trading.bean.rate.domain.ExchangeRate;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.huobi.hobi.constant.TraderMadeOptions;
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

/**
 * ExchangeRateTask - 外汇汇率定时同步任务
 *
 * 功能说明：
 * 该任务负责从外部汇率API接口定时获取最新汇率数据，并更新到系统数据库中
 * 汇率数据用于外汇交易品种的价格换算和结算
 *
 * 数据来源：使用 Frankfurter API (https://api.frankfurter.dev)
 * - 完全免费，无需API密钥
 * - 覆盖200+货币
 * - 数据来源55+中央银行
 * 更新策略：每6小时执行一次
 */
@Component
@Slf4j
public class ExchangeRateTask {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private SysparaService sysparaService;

    @Value("${exchange.rate:https://api.frankfurter.dev/v2/rates?base=USD}")
    private String exchangeRateUrl;

    /**
     * 外汇汇率定时同步方法
     *
     * 执行周期：每6小时执行一次（cron: 0 0 0/6 * * ?）
     *
     * 执行条件：系统参数 auto_exchange_rate 配置为 true 时才执行同步
     *
     * 处理流程：
     * 1. 检查系统参数 auto_exchange_rate，判断是否启用自动汇率同步
     * 2. 查询数据库中所有需要同步的汇率配置（ExchangeRate表）
     * 3. 调用 Frankfurter API 获取最新汇率数据
     * 4. 将API返回的数组转换为汇率Map
     * 5. 遍历系统中的汇率配置，匹配并更新对应的汇率值
     * 6. 汇率精度保留4位小数，向下取整
     */
    @Scheduled(cron = "0 0 0/6 * * ?")
    public void getExchangeRate() {
        Syspara syspara = sysparaService.find("auto_exchange_rate");
        if (Objects.nonNull(syspara) && syspara.getBoolean()) {
            List<ExchangeRate> rates = exchangeRateService.list();
            if (CollectionUtil.isNotEmpty(rates)) {
                try {
                    String json = HttpHelper.getJSONFromHttpNew(exchangeRateUrl, new HashMap<>(), HttpMethodType.GET);
                    JSONArray resultArray = JSON.parseArray(json);
                    
                    // 将Frankfurter API的数组转换为汇率Map (quote -> rate)
                    Map<String, BigDecimal> ratesMap = new HashMap<>();
                    for (int i = 0; i < resultArray.size(); i++) {
                        JSONObject rateObj = resultArray.getJSONObject(i);
                        String quote = rateObj.getString("quote");
                        BigDecimal rate = rateObj.getBigDecimal("rate");
                        ratesMap.put(quote, rate);
                    }

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

    /**
     * 测试Frankfurter API
     */
    public static void main(String[] args) {
        String json = HttpHelper.getJSONFromHttpNew("https://api.frankfurter.dev/v2/rates?base=USD", new HashMap<>(), HttpMethodType.GET);
        JSONArray resultArray = JSON.parseArray(json);
        
        // 将Frankfurter API的数组转换为汇率Map (quote -> rate)
        Map<String, BigDecimal> ratesMap = new HashMap<>();
        for (int i = 0; i < resultArray.size(); i++) {
            JSONObject rateObj = resultArray.getJSONObject(i);
            String quote = rateObj.getString("quote");
            BigDecimal rate = rateObj.getBigDecimal("rate");
            ratesMap.put(quote, rate);
        }

        if (ratesMap.containsKey("AUD")) {
            System.out.println("result: AUD = " + ratesMap.get("AUD").setScale(4, RoundingMode.DOWN));
        }
    }
}