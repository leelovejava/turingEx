package com.yami.trading.huobi.task;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.data.AdjustmentValueCache;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DataDBService;
import com.yami.trading.huobi.data.internal.KlineConstant;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.huobi.data.internal.KlineTimeObject;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kline组件初始化加载缓存服务
 *
 * @author Jerry
 * @date 2023/5/8
 */
@Component
@Slf4j
public class KlineLoadCache {

    @Autowired
    private KlineContext klineContext;
    @Autowired
    private DataDBService dataDBService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private KlineService klineService;

    public void loadCache() {
        List<Item> items = new ArrayList<>(itemService.listWithOutCache());
        log.info("--------------> KlineLoadCache.loadCache, item 集合长度为1:" + items.size());
        // 加载调整值到内存
        for (Item item : items) {
            AdjustmentValueCache.getCurrentValue().put(item.getSymbol(), item.getAdjustmentValue());
        }

        // 加载最新实时价格数据到内存
        log.info("--------------> KlineLoadCache.loadCache, item 集合长度为2:" + items.size());
        for (Item item : items) {
            if(!item.isActive()){
                continue;
            }
            String symbol = item.getSymbol();
            Realtime realtime = klineService.findLatestRealtime(symbol);
            if (null != realtime) {
                DataCache.putLatestRealTime(symbol, realtime);
            }
        }

        try {
            // 加载最新实时价格数据到内存
            log.info("--------------> KlineLoadCache.loadCache, item 集合长度为3:" + items.size());
            for (Item item : items) {
                if(!item.isActive()){
                    continue;
                }
                String symbol = item.getSymbol();
                List<Realtime> list = dataDBService.listRealTime60s(symbol);
                if (null == list || list.size() <= 0) {
                    list = new ArrayList<>();
                }
                DataCache.putLatestRealTime60s(symbol, list);
                if (CollectionUtil.isNotEmpty(list)) {
                    DataCache.putLatestRealTime(symbol, list.get(0));
                }
            }

            // 加载K线数据到内存
            log.info("--------------> KlineLoadCache.loadCache, item 集合长度为4:" + items.size());
            long beginTime = System.currentTimeMillis();
            for (Item item : items) {
                if(!item.isActive()){
                    continue;
                }
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_1MIN);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_5MIN);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_15MIN);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_30MIN);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_60MIN);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_2HOUR);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_4HOUR);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_1DAY);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_5DAY);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_1WEEK);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_1MON);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_QUARTER);
                bulidInit(item.getSymbol(), KlineConstant.PERIOD_YEAR);
                log.info("系统启动时候，{} k线图初始化成功", item.getSymbol());
                klineContext.initKlineSuccess(item.getSymbol());
            }
            long endTime = System.currentTimeMillis();
            log.info("------> 加载: {} 项 K 线图耗时:{} ", items.size(), (endTime - beginTime));
        } catch (Exception e) {
            log.error("--------------> KlineLoadCache.loadCache, item 集合长度为5:" + items.size() + ", 异常:", e);
            throw e;
        }
    }

    public void bulidInit(String symbol, String line) {
        List<Kline> list = klineService.find(itemService.findBySymbol(symbol).getSymbol(), line, Integer.MAX_VALUE);
        KlineTimeObject model = new KlineTimeObject();
        Collections.sort(list);
        model.setKline(list);
        DataCache.putKline(symbol, line, model);
    }

    public void setDataDBService(DataDBService dataDBService) {
        this.dataDBService = dataDBService;
    }

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    public void setKlineService(KlineService klineService) {
        this.klineService = klineService;
    }

}
