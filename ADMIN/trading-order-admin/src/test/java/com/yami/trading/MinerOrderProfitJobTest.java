package com.yami.trading;

import com.yami.trading.service.miner.job.MinerOrderProfitJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MinerOrderProfitJobTest {

    @Autowired
    private MinerOrderProfitJob job;

    @Test
    void taskJob() {
        job.taskJob();
    }
}
