package com.yami.trading.admin.task;

import com.yami.trading.init.cache.LoadRiskClientCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RefreshRiskClientJob {
    @Autowired
    private LoadRiskClientCache loadRiskClientCache;

    @Scheduled(cron = "0 */5 * * * ?")
    public void crawl() {
        try {
//            LocalDateTime now = LocalDateTime.now();
//            String timeStr = now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd+HH:mm:ss"));
//            long t = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
//            String url = "https://flash-api.jin10.com/get_flash_list?channel=-8200&vip=1&max_time=%s&t=%s";
//            url = String.format(url, timeStr, t);

            loadRiskClientCache.loadData();
        } catch (Exception e) {
            log.error("金十资讯采集异常", e);
        }
    }

//    public static void main(String[] args) throws Exception {
//        Jin10InformationGet get = new Jin10InformationGet();
//        get.crawl();
//    }
}
