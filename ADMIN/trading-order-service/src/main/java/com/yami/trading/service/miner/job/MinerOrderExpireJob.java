package com.yami.trading.service.miner.job;

import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.dao.miner.MinerOrderMapper;
import com.yami.trading.service.miner.service.MinerOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MinerOrderExpireJob {

    @Autowired
    private MinerOrderMapper minerOrderMapper;

    @Autowired
    private MinerOrderService minerOrderService;

    @Scheduled(cron = "56 0/5 * * * ?")
    public void closeExpiredOrders() {
        try {
            log.info("========== 开始执行到期订单关闭任务 ==========");
            List<MinerOrder> expiredOrders = minerOrderMapper.selectExpiredOrders();

            if (expiredOrders.isEmpty()) {
                log.info("没有需要关闭的到期订单");
                return;
            }

            log.info("查询到 {} 条到期订单，准备关闭", expiredOrders.size());
            int successCount = 0;
            int failCount = 0;

            for (MinerOrder order : expiredOrders) {
                try {
                    log.info("处理到期订单，orderNo:{}, stopTime:{}", order.getOrder_no(), order.getStop_time());
                    order.setState("0");
                    order.setCompute_day(new Date());
                    minerOrderService.saveClose(order);
                    successCount++;
                    log.info("订单关闭成功，orderNo:", order.getOrder_no());
                } catch (Exception e) {
                    failCount++;
                    log.error("订单关闭失败，orderNo:{}, error:{}", order.getOrder_no(), e.getMessage(), e);
                }
            }

            log.info("========== 到期订单关闭任务完成，成功:{}, 失败:{} ==========", successCount, failCount);
        } catch (Exception e) {
            log.error("到期订单关闭任务执行异常", e);
        }
    }
}
