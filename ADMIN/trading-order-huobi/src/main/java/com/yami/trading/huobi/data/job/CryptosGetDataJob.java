package com.yami.trading.huobi.data.job;

import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CryptosGetDataJob extends AbstractGetDataJob {

    public static volatile boolean first = true;
    private static Logger logger = LoggerFactory.getLogger(CryptosGetDataJob.class);

    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private HobiDataService hobiDataService;
    @Autowired
    private ItemService itemService;

    @Override
    public void start() {
        log.info("[CryptoJob] ========== CryptosGetDataJob 启动 ==========");
        new Thread(this, "CryptosGetDataJob").start();
        log.info("[CryptoJob] ========== CryptosGetDataJob 线程已创建 ==========");
    }

    @Override
    public void run() {
        log.info("[CryptoJob] ========== CryptosGetDataJob.run() 开始执行 ==========");

        if (first) {
            /**
             * data数据保存间隔时长(毫秒)
             */
            this.interval = 3000;
            log.info("[CryptoJob] 首次执行, 设置采集间隔: {}ms", this.interval);
            first = false;
        }
        
        log.info("[CryptoJob] 进入数据采集循环...");
        while (true) {
            log.info("[CryptoJob] >>> 循环执行中, 当前时间: {}", System.currentTimeMillis());
            try {
                log.info("[CryptoJob] --- 查询数据库获取加密货币列表...");
                List<Item> byType = itemService.findByType(Item.cryptos);
                log.info("[CryptoJob] --- 查询到 {} 个加密货币", byType.size());
                
                String symbols = byType.stream().map(i -> i.getRemarks().replace("usdt", "")).collect(Collectors.joining(","));
                log.info("[CryptoJob] 开始采集加密货币数据, 币种数量: {}, symbols: {}", byType.size(), symbols);
                
                this.realtimeHandle(symbols);
            } catch (Exception e) {
                logger.error("[CryptoJob] 采集加密货币数据失败", e);
            } finally {
                log.info("[CryptoJob] <<< 等待 {}ms 后继续采集...", this.interval);
                ThreadUtils.sleep(this.interval);
            }
        }

    }

    @Override
    public String getName() {
        return "虚拟货币数据采集";
    }


    @Override
    public void realtimeHandle(String symbols) {
        List<Realtime> realtimeList = this.hobiDataService.realtimeCryptos(symbols);
        super.handleRealTimeList(realtimeList);
    }


}
