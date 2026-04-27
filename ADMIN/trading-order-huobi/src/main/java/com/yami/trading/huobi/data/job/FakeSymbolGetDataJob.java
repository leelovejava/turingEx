package com.yami.trading.huobi.data.job;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.base.Splitter;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.etf.MarketService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FakeSymbolGetDataJob extends AbstractGetDataJob {
    public static volatile boolean first = true;
    private static Logger logger = LoggerFactory.getLogger(FakeSymbolGetDataJob.class);
    @Autowired
    private MarketService marketService;
    @Autowired
    private ItemService itemService;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void start() {
        new Thread(this, "自定义币对实时数据采集").start();
    }

    @Override
    public void run() {

        if (first) {
            // data数据保存间隔时长(毫秒)
            this.interval = 3000;
            first = false;
        }
        while (true) {
            //todo 临时房卡
            try {
                String symbols = new ArrayList<>(itemService.list()).stream().filter(i -> "1".equalsIgnoreCase(i.getFake())).map(Item::getSymbol).collect(Collectors.joining(","));
                if (MarketOpenChecker.isMarketOpen(Item.US_STOCKS)) {
                    this.realtimeHandle(symbols);
                } else {
                    Splitter.on(",").trimResults().splitToList(symbols).forEach(symbol -> {
                        redisTemplate.opsForHash().delete(RedisKeys.SYMBOL_AMOUNT_VOLUME + symbol, "amount");
                        redisTemplate.opsForHash().delete(RedisKeys.SYMBOL_AMOUNT_VOLUME + symbol, "volume");
                    });
                }
            } catch (Exception e) {
                logger.error("run fail", e);
            } finally {
                ThreadUtils.sleep(this.interval);
            }
        }

    }

    @Override
    public String getName() {
        return "机器人实时数据采集";
    }


    @Override
    public void realtimeHandle(String symbols) {
        List<String> symbolList = Splitter.on(",").trimResults().splitToList(symbols);
        if (CollectionUtil.isEmpty(symbolList)) {
            log.error("当前没有行情数据可以采集");
            return;
        }
        List<Realtime> realtimeList = symbolList.stream().map(s -> marketService.queryRealtime(s)).filter(s -> s.getTs() != 0).collect(Collectors.toList());
        super.handleRealTimeList(realtimeList);
    }
}
