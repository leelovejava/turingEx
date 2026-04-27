package com.yami.trading.huobi.task;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
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

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class ExchangeRateTask {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private SysparaService sysparaService;

    @Value("${exchange.rate}")
    private String exchangeRateUrl;

    /**
     * 每小时执行一次，现在的apikey,一个月只能调用1000次
     */
    @Scheduled(cron = "0 0 */6 * * ?")
    public void getExchangeRate() {

        Syspara syspara = sysparaService.find("auto_exchange_rate");

        //配置了汇率同步才同步数据，否则不同步数据
        if (Objects.nonNull(syspara) && syspara.getBoolean()) {

            List<ExchangeRate> rates = exchangeRateService.list();

            if (CollectionUtil.isNotEmpty(rates)) {

                try {

                    String json = HttpHelper.getJSONFromHttpNew(exchangeRateUrl, new HashMap<>(), HttpMethodType.GET);
                    JSONObject resultJson = JSON.parseObject(json);
                    JSONObject dataArray = resultJson.getJSONObject("rates");

                    for (ExchangeRate rate : rates) {
                        if (Objects.nonNull(dataArray) && Objects.nonNull(dataArray.getBigDecimal(rate.getCurrency()))) {
                            rate.setRata(dataArray.getBigDecimal(rate.getCurrency()).setScale(4, RoundingMode.DOWN));
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

        String json = HttpHelper.getJSONFromHttpNew(TraderMadeOptions.openExchangeRatesRates, new HashMap<>(), HttpMethodType.GET);

        JSONObject resultJson = JSON.parseObject(json);
        JSONObject dataArray = resultJson.getJSONObject("rates");

        if (Objects.nonNull(dataArray) && Objects.nonNull(dataArray.getBigDecimal("AUD"))) {
            System.out.println("result" + dataArray.getBigDecimal("AUD").setScale(4, RoundingMode.DOWN));
        }
    }
}
