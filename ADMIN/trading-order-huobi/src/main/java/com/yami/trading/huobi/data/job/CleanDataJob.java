package com.yami.trading.huobi.data.job;

import com.yami.trading.huobi.data.internal.KlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanDataJob {
    @Autowired
    private KlineService klineService;

    @Scheduled(cron = "0 0 4 * * ?")
    public void taskJob() {
        klineService.clean();
    }

}
