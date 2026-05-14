package com.yami.trading.huobi.data.internal;

import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.hobi.internal.SpiderService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.huobi.hobi.internal.FakeKlineInitService;
import com.yami.trading.huobi.task.KlineContext;
import com.yami.trading.service.item.ItemService;

@Component
@Slf4j
public class KlineInitServiceImpl implements KlineInitService {
	
    @Autowired
    private ItemService itemService;
    
    @Autowired
    private KlineContext klineContext;

    @Autowired
    private KlineService klineService;
    
    @Autowired
    private HobiDataService hobiDataService;
    
    @Autowired
    private CryptosKlineService cryptosKlineService;
    
    @Autowired
    private FakeKlineInitService fakeKlineInitService;

    @Autowired
    private DataDBService dataDBService;

    @Autowired
    private SpiderService spiderService;

    @Autowired
    private com.yami.trading.huobi.hobi.internal.XueQiuDataServiceImpl xueQiuDataService;

    @Override
    public void klineInit(String symbols) {
        if (!symbols.contains(",")) {
            Item bySymbol = itemService.findBySymbol(symbols);
            if(!bySymbol.isActive()){
                return;
            }
            log.debug("当前开始初始化币对k线图: {}", symbols);
            if (Item.cryptos.equalsIgnoreCase(bySymbol.getType())) {
                cryptosKlineService.saveInit(symbols);
                // 清理下价格历史数据，避免计算时候影响
               // DataCache.getCryptosRealtimeHistory(symbols).clear();
                DataCache.clearCryptosRealtimeHistory(symbols);
                dataDBService.delete(symbols);
            } else if ("1".equalsIgnoreCase(bySymbol.getFake())) {
                fakeKlineInitService.saveInit(symbols);
                dataDBService.delete(symbols);

            } else {
                saveInitSelfData(symbols);
            }
            log.debug("当前完成初始化币对k线图: {}", symbols);
        } else {
            String[] symbolsArrays = symbols.split(",");
            boolean selfData = true;
            for (String symbol : symbolsArrays) {
                Item bySymbol = itemService.findBySymbol(symbol);
                if(!bySymbol.isActive()){
                    return;
                }
                log.debug("当前开始初始化币对k线图: {}", symbol);
                if (Item.cryptos.equalsIgnoreCase(bySymbol.getType())) {
                    cryptosKlineService.saveInit(symbol);
                    // 清理下价格历史数据，避免计算时候影响
                    DataCache.clearCryptosRealtimeHistory(symbols);
                    dataDBService.delete(symbols);
                    selfData = false;
                } else if ("1".equalsIgnoreCase(bySymbol.getFake())) {
                    fakeKlineInitService.saveInit(symbols);
                    dataDBService.delete(symbols);

                    selfData = false;
                } else {
                    break;
                }
                log.debug("当前完成初始化币对k线图: {}", symbol);
            }
            if (selfData){
                saveInitSelfData(symbols);
            }
        }
    }
    private void saveInitSelfData(String symbols) {
        log.debug("正在通过Twelve Data初始化k线图:{}", symbols);
        String[] arr = symbols.contains(",") ? symbols.split(",") : new String[]{symbols};
        for (String symbol : arr) {
            try {
                Map<String, List<Kline>> map = xueQiuDataService.getKlineFromTwelveData(symbol.trim());
                if (map.isEmpty()) {
                    continue;
                }
                List<Kline> klines5P = map.get(Kline.PERIOD_5MIN);
                if (CollectionUtil.isNotEmpty(klines5P)) {
                    klineService.saveInit(symbol.trim(), map);
                    DataCache.clearLatestRealTime60s(symbol.trim());
                    log.debug("初始化k线图{} 数量为{}", symbol, map.size());
                }
            } catch (Exception e) {
                log.error("Twelve Data初始化k线图失败, symbol={}", symbol, e);
            }
        }
        log.debug("完成Twelve Data初始化k线图:{}", symbols);
    }
}
