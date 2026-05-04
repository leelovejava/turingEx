package com.yami.trading.huobi.task;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.google.common.collect.Lists;
import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DepthTimeObject;
import com.yami.trading.huobi.data.internal.KlineInitService;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.huobi.hobi.internal.XueQiuDataServiceImpl;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * KlineBlankRepairTask.java - K线空白修复任务
 */
@Component
@Lazy(value = false)
@Slf4j
public class KlineBlankRepairTask {
    @Autowired
    private KlineService klineService;

    @Autowired
    private ItemService itemService;
    @Autowired
    @Qualifier("dataService")
    private DataService dataService;
    @Autowired
    private KlineInitService klineInitService;
    @Autowired
    private KlineContext klineContext;


    // 虚拟货币10分钟检查下虚拟货币，外汇，股票的k线图是否为空
    /**
     * 虚拟货币K线修复任务
     * 周期：每7分钟执行一次
     * 处理逻辑：检查虚拟货币K线图是否为空，若为空则进行修复
     */
    @Scheduled(cron = "0 0/7 * ? * *")
    @Async
    public void doTask() throws InterruptedException {
        List<String> cryptosPeriod = Lists.newArrayList(Kline.PERIOD_1MIN, Kline.PERIOD_5MIN, Kline.PERIOD_15MIN, Kline.PERIOD_30MIN, Kline.PERIOD_60MIN,
                Kline.PERIOD_4HOUR, Kline.PERIOD_1DAY, Kline.PERIOD_1WEEK, Kline.PERIOD_1MON
        );
        List<Item> cryptos = itemService.findByType(Item.cryptos);
        checkTask(cryptosPeriod, cryptos);

    }

    /**
     * 外汇K线修复任务
     * 周期：每6分钟执行一次
     * 处理逻辑：检查外汇K线图是否为空，若为空则进行修复
     */
    @Scheduled(cron = "0 0/6 * ? * *")
    @Async
    public void doForexTask() throws InterruptedException {
        List<String> forexPeriod = Lists.newArrayList(Kline.PERIOD_1MIN, Kline.PERIOD_5MIN, Kline.PERIOD_15MIN, Kline.PERIOD_30MIN, Kline.PERIOD_60MIN,
                Kline.PERIOD_2HOUR, Kline.PERIOD_1DAY, Kline.PERIOD_5DAY, Kline.PERIOD_1WEEK, Kline.PERIOD_1MON
        );
        List<Item> forexs = itemService.findByType(Item.forex);
        checkTask(forexPeriod, forexs);

    }

    /**
     * 香港股票K线修复任务
     * 周期：每5分钟执行一次
     * 处理逻辑：检查香港股票K线图是否为空，若为空则进行修复
     */
    @Async
    @Scheduled(cron = "0 0/5 * ? * *")
    public void doHkTask() throws InterruptedException {
        List<String> stockPeriod = Lists.newArrayList(Kline.PERIOD_1MIN, Kline.PERIOD_5MIN, Kline.PERIOD_15MIN, Kline.PERIOD_30MIN, Kline.PERIOD_60MIN,
                Kline.PERIOD_2HOUR, Kline.PERIOD_1DAY, Kline.PERIOD_5DAY, Kline.PERIOD_1WEEK, Kline.PERIOD_1MON, Kline.PERIOD_QUARTER, Kline.PERIOD_YEAR
        );
        List<Item> stocks = itemService.findByType(Item.HK_STOCKS);
        checkTask(stockPeriod, stocks);

    }

    /**
     * 台湾股票K线修复任务
     * 周期：每5分钟执行一次
     * 处理逻辑：检查台湾股票K线图是否为空，若为空则进行修复
     */
    @Async
    @Scheduled(cron = "0 0/5 * ? * *")
    public void doTWTask() throws InterruptedException {
        List<String> stockPeriod = Lists.newArrayList(Kline.PERIOD_1MIN, Kline.PERIOD_5MIN, Kline.PERIOD_15MIN, Kline.PERIOD_30MIN, Kline.PERIOD_60MIN,
                Kline.PERIOD_2HOUR, Kline.PERIOD_1DAY, Kline.PERIOD_5DAY, Kline.PERIOD_1WEEK, Kline.PERIOD_1MON, Kline.PERIOD_QUARTER, Kline.PERIOD_YEAR
        );
        List<Item> stocks = itemService.findByType(Item.TW_STOCKS);
        checkTask(stockPeriod, stocks);
    }

    /**
     * 香港股票K线修复任务
     * 周期：每5分钟执行一次
     * 处理逻辑：检查香港股票K线图是否为空，若为空则进行修复
     */
    @Async
    @Scheduled(cron = "0 0/5 * ? * *")
    public void doATask() throws InterruptedException {
        List<String> stockPeriod = Lists.newArrayList(Kline.PERIOD_1MIN, Kline.PERIOD_5MIN, Kline.PERIOD_15MIN, Kline.PERIOD_30MIN, Kline.PERIOD_60MIN,
                Kline.PERIOD_2HOUR, Kline.PERIOD_1DAY, Kline.PERIOD_5DAY, Kline.PERIOD_1WEEK, Kline.PERIOD_1MON, Kline.PERIOD_QUARTER, Kline.PERIOD_YEAR
        );
        List<Item> stocks = itemService.findByType(Item.A_STOCKS);
        checkTask(stockPeriod, stocks);
    }

    /**
     * 投资股票K线修复任务
     * 周期：每5分钟执行一次
     * 处理逻辑：检查投资股票K线图是否为空，若为空则进行修复
     */
    @Async
    @Scheduled(cron = "0 0/5 * ? * *")
    public void doInvestTask() throws InterruptedException {
        List<String> stockPeriod = Lists.newArrayList(Kline.PERIOD_1MIN, Kline.PERIOD_5MIN, Kline.PERIOD_15MIN, Kline.PERIOD_30MIN, Kline.PERIOD_60MIN,
                Kline.PERIOD_2HOUR, Kline.PERIOD_1DAY, Kline.PERIOD_5DAY, Kline.PERIOD_1WEEK, Kline.PERIOD_1MON, Kline.PERIOD_QUARTER, Kline.PERIOD_YEAR
        );
        List<Item> stocks = itemService.findByType(Item.JP_STOCKS);
        stocks.addAll(itemService.findByType(Item.INDIA_STOCKS));
        stocks.addAll(itemService.findByType(Item.UK_STOCKS));
        stocks.addAll(itemService.findByType(Item.DE_STOCKS));
        stocks.addAll(itemService.findByType(Item.BZ_STOCKS));
        stocks.addAll(itemService.findByType(Item.CAD_STOCKS));
        stocks.addAll(itemService.findByType(Item.FR_STOCKS));
        stocks.addAll(itemService.findByType(Item.SG_STOCKS));

        checkTask(stockPeriod, stocks);
    }

    /**
     * 美国股票K线修复任务
     * 周期：每5分钟执行一次
     * 处理逻辑：检查美国股票K线图是否为空，若为空则进行修复
     */
    @Async
    @Scheduled(cron = "0 0/5 * ? * *")
    public void doUSTask() throws InterruptedException {
        List<String> stockPeriod = Lists.newArrayList(Kline.PERIOD_1MIN, Kline.PERIOD_5MIN, Kline.PERIOD_15MIN, Kline.PERIOD_30MIN, Kline.PERIOD_60MIN,
                Kline.PERIOD_2HOUR, Kline.PERIOD_1DAY, Kline.PERIOD_5DAY, Kline.PERIOD_1WEEK, Kline.PERIOD_1MON, Kline.PERIOD_QUARTER, Kline.PERIOD_YEAR
        );
        List<Item> stocks = itemService.findByType(Item.US_STOCKS);
        checkTask(stockPeriod, stocks);
    }

    /**
     * 美国股票K线修复任务
     * 周期：每5分钟执行一次
     * 处理逻辑：检查美国股票K线图是否为空，若为空则进行修复
     */
    @Async
    @Scheduled(cron = "0 0/5 * ? * *")
    public void doETFTask() throws InterruptedException {
        List<String> stockPeriod = Lists.newArrayList(Kline.PERIOD_1MIN, Kline.PERIOD_5MIN, Kline.PERIOD_15MIN, Kline.PERIOD_30MIN, Kline.PERIOD_60MIN,
                Kline.PERIOD_2HOUR, Kline.PERIOD_1DAY, Kline.PERIOD_5DAY, Kline.PERIOD_1WEEK, Kline.PERIOD_1MON, Kline.PERIOD_QUARTER, Kline.PERIOD_YEAR
        );
        List<Item> stocks = itemService.findByType(Item.indices);
        checkTask(stockPeriod, stocks);
    }

    /**
     * 检查K线图是否为空
     * @param periods K线周期
     * @param items 产品列表
     */
    public void checkTask(List<String> periods, List<Item> items){
        ThreadUtil.execAsync(() ->{
            for(Item item : items){
                if(!item.isActive()){
                    continue;
                }
                if(!klineContext.isInitSuccess(item.getSymbol())){
                    continue;
                }
                if(klineContext.isManualIniting(item.getSymbol())){
                    continue;
                }

                boolean needInit = false;
                for(String period : periods){
                    // 数据处理
                    List<Kline> data = this.dataService.kline(item.getSymbol(), period);
                    if(CollectionUtil.isEmpty(data)){
                        // 规避有问题的
//                        if(item.getSymbol().equalsIgnoreCase("00941") || item.getSymbol().equalsIgnoreCase("IYW")){
//                            break;
//                        }
                        log.info(" {} -> {} k线图为空，正在重新初始化", item.getSymbol(), period);
                        needInit = true;
                        break;
                    }

                }
                if(needInit){
                    klineInitService.klineInit(item.getSymbol());
                }
            }
        });

    }


}
