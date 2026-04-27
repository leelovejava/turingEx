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
        new Thread(this, "CryptosGetDataJob").start();
    }

    @Override
    public void run() {

        if (first) {
            /**
             * data数据保存间隔时长(毫秒)
             */
            this.interval = 3000;

            first = false;
        }
        while (true) {
            try {
                List<Item> byType = itemService.findByType(Item.cryptos);
                String symbols = byType.stream().map(i -> i.getRemarks().replace("usdt", "")).collect(Collectors.joining(","));
                this.realtimeHandle(symbols);
            } catch (Exception e) {
                logger.error("run fail", e);
            } finally {
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
