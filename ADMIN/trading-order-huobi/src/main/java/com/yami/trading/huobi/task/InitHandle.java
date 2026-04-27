package com.yami.trading.huobi.task;

import com.yami.trading.huobi.task.contract.ContractApplyOrderHandleJob;
import com.yami.trading.huobi.task.contract.ContractOrderCalculationJob;
import com.yami.trading.huobi.task.future.FuturesOrderCalculationJob;
import com.yami.trading.huobi.task.future.consumer.FuturesRecomConsumeServer;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.data.AdjustmentValueCache;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DataDBService;
import com.yami.trading.huobi.data.internal.KlineInitService;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.huobi.data.job.*;
import com.yami.trading.huobi.task.summary.SummaryCrawl;
import com.yami.trading.service.contract.ContractOrderCalculationService;
import com.yami.trading.service.exchange.ExchangeLeverApplyOrderService;
import com.yami.trading.service.exchange.job.ExchangeApplyOrderHandleJob;
import com.yami.trading.service.exchange.job.ExchangeLeverApplyOrderHandleJob;
import com.yami.trading.service.finance.loadcache.FinanceLoadCacheService;
import com.yami.trading.service.future.FuturesLoadCacheService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.TipService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Component
public class InitHandle implements CommandLineRunner {
    @Autowired
    private KlineInitService klineInitService;
    @Autowired
    protected ItemService itemService;
    @Autowired
    protected SummaryCrawl summaryCrawl;
    @Autowired
    protected DataDBService dataDBService;
    @Autowired
    protected KlineService klineService;
    @Autowired
    protected HighLowHandleJob highLowHandleJob;
    @Autowired
    private KlineCacheJob klineCacheJob;
    @Autowired
    protected StockGetDataJob stockGetDataJob;
    @Autowired
    protected ForexGetDataJob forexGetDataJob;
    @Autowired
    protected CryptosGetDataJob cryptosGetDataJob;
    @Autowired
    protected FakeSymbolGetDataJob fakeSymbolGetDataJob;
    @Autowired
    protected SaveRealtimeServer saveRealtimeServer;
    @Autowired
    private FuturesLoadCacheService futuresLoadCacheService;
    @Autowired
    private FuturesRecomConsumeServer futuresRecomConsumeServer;
    @Autowired
    protected ContractOrderCalculationService contractOrderCalculationService;
    @Autowired
    protected ContractApplyOrderHandleJob contractApplyOrderHandleJob;
    @Autowired
    protected ContractOrderCalculationJob contractOrderCalculationJob;
    @Autowired
    protected SysparaService sysparaService;
    @Autowired
    private RealtimePushJob realtimePushJob;
    @Autowired
    private CleanDataJob cleanDataJob;
    @Autowired
    private StockGetMarketJob stockGetMarketJob;
    /**
     * 交割合约持仓单盈亏计算线程启动
     */
    @Autowired
    private FuturesOrderCalculationJob futuresOrderCalculationJob;
    @Autowired
    protected DepthPushJob depthPushJob;
    @Autowired
    protected TradePushJob tradePushJob;
    @Autowired
    protected DataFrequencyServer dataFrequencyServer;
    @Autowired
    private KlineLoadCache klineLoadCache;

    @Autowired
    ExchangeApplyOrderHandleJob exchangeApplyOrderHandleJob;

    @Autowired
    ExchangeLeverApplyOrderHandleJob exchangeLeverApplyOrderHandleJob;
    @Autowired
    TipService tipService;

    @Autowired
    FinanceLoadCacheService financeLoadCacheService;
    @Override
    public void run(String... args) {
        // 初始化缓存
        //loadCacheService.loadcache();
        tipService.init();
        futuresLoadCacheService.loadcache();
        financeLoadCacheService.loadcache();
        // 已经加了注解
//        minerLoadCacheService.loadcache();
        // todo 先注释观察报错
//		futuresRecomConsumeServer.start();
        log.info("开始Data初始化........");
        List<Item> items = new ArrayList<>(itemService.listWithOutCache());
        for (Item item : items) {
            AdjustmentValueCache.getCurrentValue().put(item.getSymbol(), item.getAdjustmentValue());
        }

        for (Item item : items) {
            Realtime realtime = dataDBService.get(item.getSymbol());
            if (realtime != null) {
                DataCache.putRealtime(item.getSymbol(), realtime);
            }
        }

        for (Item item : items) {
            if(Item.cryptos.equalsIgnoreCase(item.getType())){
                List<Realtime> list = this.dataDBService.findRealtimeOneDay(item.getSymbol());
                DataCache.putCryptosRealtimeHistory(item.getSymbol(), list);
            }

        }

        klineLoadCache.loadCache();
        // 高低修正
        highLowHandleJob.start();
        highLowHandleJob.bulidHighLow();

        stockGetMarketJob.start();
        // 获取realtime实时数据
        stockGetDataJob.start();
        forexGetDataJob.start();
        cryptosGetDataJob.start();
        //fakeSymbolGetDataJob.start();
        // 实时数据批量保存线程
        saveRealtimeServer.start();
        klineCacheJob.start();

        /**
         * 委托单处理线程启动
         */
        contractApplyOrderHandleJob.start();
        /**
         * 持仓单盈亏计算线程启动
         */
        contractOrderCalculationService.setOrder_close_line(this.sysparaService.find("order_close_line").getBigDecimal());
        contractOrderCalculationService.setOrder_close_line_type(this.sysparaService.find("order_close_line_type").getInteger());
        contractOrderCalculationJob.start();

        // todo 做模块判断,后续打开
        futuresOrderCalculationJob.start();

        realtimePushJob.start();
        depthPushJob.start();
        tradePushJob.start();
        dataFrequencyServer.start();
        cleanDataJob.taskJob();
        log.info("完成Data初始化。");

        /**
         * 币币委托单处理线程启动
         */
        exchangeApplyOrderHandleJob.start();
        exchangeLeverApplyOrderHandleJob.start();
        //klineService.clean();
    }
}
