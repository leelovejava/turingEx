package com.yami.trading.admin.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yami.trading.admin.facade.MachineTranslationService;
import com.yami.trading.bean.ipo.XueQiuNewStocks;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.query.Query;
import com.yami.trading.huobi.hobi.internal.SpiderService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 定时将采集到的新股入库
 */
@Component
@Slf4j
public class NewSharesConfigTask {

    public static final String NEW_STOCKS = "new_stocks";
    @Autowired
    private ItemService itemService;

    @Autowired
    private SpiderService spiderService;
    @Autowired
    private MachineTranslationService translationService;
    @Scheduled(cron = "0 */5 * * * ?")
    public void insertNewStocks() {
        log.info("NewSharesConfigTask 开始同步新股插入");
        List<XueQiuNewStocks> xueQiuNewStocks = spiderService.fetchNewStocks();
        List<XueQiuNewStocks> xueQiuNewStocksNotList = spiderService.fetchNewStocksNotList();
        xueQiuNewStocks.addAll(xueQiuNewStocksNotList);

        if (CollectionUtil.isNotEmpty(xueQiuNewStocks)) {
            Map<String, String> map = xueQiuNewStocks.stream().collect(Collectors.toMap(XueQiuNewStocks::getSymbol, XueQiuNewStocks::getName));
            Set<String> allSymbols = map.keySet();
            if(CollectionUtil.isEmpty(allSymbols)){
                return;
            }
            QueryWrapper<Item> queryWrapper = new QueryWrapper<Item>();
            queryWrapper.in("symbol", allSymbols);
            Set<String> exist = itemService.list(queryWrapper).stream().map(Item::getSymbol).collect(Collectors.toSet());
            List<String> symbolsNeed = allSymbols.stream().filter(s -> !exist.contains(s)).collect(Collectors.toList());

            Item aStockTemplate = itemService.findByType(Item.A_STOCKS).stream().filter(i -> !Item.indices.equalsIgnoreCase(i.getType())).findFirst().orElse(null);
            Item hkstockTemplate = itemService.findByType(Item.HK_STOCKS).stream().filter(i -> !Item.indices.equalsIgnoreCase(i.getType())).findFirst().orElse(null);
            List<Item> itemList = new ArrayList<>();
            for (String symbol : symbolsNeed) {
                Item template = hkstockTemplate;
                if (StrUtil.startWithIgnoreCase(symbol, "s")) {
                    template = aStockTemplate;
                }
                if(template == null){
                    continue;
                }
                Item item = BeanUtil.copyProperties(template, Item.class);
                item.setUuid(null);
                item.setSymbol(symbol);
                item.setRemarks(symbol);
                item.setSymbolData(symbol);
                item.setName(map.get(symbol));
                item.setAdjustmentValue(0);
                item.setSymbolFullName(map.get(symbol));
                item.setCategory(NEW_STOCKS);
                item.setEnName(translationService.translate(item.getName()));
                item.setCrawlStatus(null);
                itemList.add(item);
            }
            // 去掉已经存在的

            if (CollectionUtil.isNotEmpty(itemList)) {
                itemService.saveBatch(itemList);
            }
            log.info("NewSharesConfigTask 插入新股数据 {}", symbolsNeed);

        }
    }

}