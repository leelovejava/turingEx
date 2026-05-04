package com.yami.trading.huobi.task;

import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.data.domain.Trade;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DepthTimeObject;
import com.yami.trading.huobi.data.internal.TradeTimeObject;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * CryptoCurrencyDataTask - 加密货币（虚拟货币）数据定时采集任务
 *
 * 功能说明：
 * 该任务负责从火币（Huobi）交易所定时采集虚拟货币的市场数据，包括：
 * 1. 市场深度数据（买卖盘口数据）
 * 2. 近期交易记录（成交记录）
 *
 * 数据采集后存储到DataCache缓存中，供其他任务（如DepthPushJob、TradePushJob）使用
 *
 * 执行周期：每3秒执行一次（高频采集）
 * 处理范围：仅处理虚拟货币（Item.cryptos）类型的数据
 *
 * @author Jerry
 * @date 2023/11/12
 */
@Component
@Lazy(value = false)
@Slf4j
public class CryptoCurrencyDataTask {

    @Resource
    ItemService itemService;

    @Resource
    HobiDataService hobiDataService;

    // 标记火币数据是否已完成首次初始化
    // 首次初始化前会采集所有品种数据，初始化后可根据市场状态过滤
    private volatile boolean isHuobiInit = false;

    /**
     * 深度数据、近期交易记录 火币采集任务
     *
     * 执行周期：每3秒执行一次
     *
     * 执行流程：
     * 1. 获取所有虚拟货币品种列表
     * 2. 遍历每个品种，调用collection()采集深度和交易数据
     * 3. 采集完成后标记初始化完成
     *
     * 注意：
     * - 暂不使用市场开盘状态过滤，初始化期间会采集所有品种数据
     * - 首次采集后isHuobiInit设为true，后续可扩展为只采集开盘品种
     */
    @Async
    @Scheduled(cron = "*/3 * * * * ?")
    public void DepthTradeCollectionTask() {
        try {
            // 获取所有虚拟货币品种
            List<Item> items = itemService.findByType(Item.cryptos);

            // 遍历每个品种进行数据采集
            for (Item item : items) {
                // 暂不使用的市场开盘状态检查逻辑（保留以便后续扩展）
                // boolean isOpen = MarketOpenChecker.isMarketOpenByItemCloseType(item.getOpenCloseType());
                // if (isHuobiInit && !isOpen) {
                //     log.warn("---> CryptoCurrencyDataTask.DepthTradeCollectionTask 当前 symbol:{} 对应的股票处于未开盘状态，" +
                //             "忽略采集的深度数据", item.getSymbol());
                //     continue;
                // }

                // 调用collection方法采集该品种的数据
                collection(item);
            }

            // 标记首次初始化完成
            isHuobiInit = true;

        } catch (Throwable e) {
            log.error("深度数据、近期交易记录 火币采集任务，异常信息：", e);
        }
    }

    /**
     * 采集单个品种的深度数据和交易数据
     *
     * 处理逻辑：
     * 1. 调用hobiDataService.depthDecorator()获取火币深度数据
     * 2. 将深度数据封装到DepthTimeObject并放入DataCache缓存
     * 3. 调用hobiDataService.tradeDecorator()获取火币交易数据
     * 4. 将交易数据封装到TradeTimeObject并放入DataCache缓存
     *
     * @param item 交易品种对象
     */
    private void collection(Item item) {
        // 采集深度数据（买卖盘口）
        Depth depth = hobiDataService.depthDecorator(item.getRemarks(), 0);
        if (depth != null) {
            DepthTimeObject timeObject = new DepthTimeObject();
            timeObject.setDepth(depth);
            // 放入缓存，key为币种symbol
            DataCache.putDepth(item.getSymbol(), timeObject);
        }

        // 采集交易数据（近期成交记录）
        Trade trade = hobiDataService.tradeDecorator(item.getRemarks(), 0);
        if (trade != null) {
            TradeTimeObject timeObject = new TradeTimeObject();
            timeObject.put(item.getSymbol(), trade.getData());
            // 放入缓存，key为币种symbol
            DataCache.putTrade(item.getSymbol(), timeObject);
        }
    }
}