package com.yami.trading.api.controller.exchange.job;

import com.yami.trading.service.ExchangeLeverProfitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExchangeLeverProfitJob {
    @Autowired
    ExchangeLeverProfitService exchangeLeverProfitService;

    @Scheduled(cron ="*/5 * * * * ?" )
    public void taskJob() {
        try {
            exchangeLeverProfitService.handleOrders();
        } catch (Exception e) {
            log.error("ExchangeLeverProfitJob run fail", e);
        }
    }
}
