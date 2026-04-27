package com.yami.trading.admin.task;

import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RefreshItemJob {
    @Autowired
    private ItemService itemService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void refresh() {
        try {
            itemService.init();
        } catch (Exception e) {
            log.error("定时任务初始化item list 失败", e);
        }
    }


}
