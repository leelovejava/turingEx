package com.yami.trading.admin.facade;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.config.ThreadPool;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.huobi.hobi.internal.SpiderService;
import com.yami.trading.huobi.hobi.internal.XueQiuDataServiceImpl;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ItemVisitFacade {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ItemService itemService;
    @Autowired
    private XueQiuDataServiceImpl xueQiuDataService;
    @Autowired
    private KlineService klineService;
    @Autowired
    private SpiderService spiderService;

    /**
     * 次逻辑不会涉及高并发
     *
     * @param symbol
     */
    @Transactional
    public void updateOrInsertVisit(String symbol) {
        log.info("ItemVisitFacade 正在访问 {}", symbol);
        Item item = itemService.findBySymbol(symbol);
        // 如果当前不是默认激活和激活，激活先.并且初始化k线图
        if (!StrUtil.equalsAny(item.getCrawlStatus(), Item.ACTIVE, Item.DEFAULT_ACTIVE)) {
            log.info("ItemVisitFacade 正在激活 {}", symbol);
            itemService.crawlActive(symbol);
            log.info("ItemVisitFacade 数据库激活 {} 完成", symbol);

            ThreadPool.getApplicationThreadPool().execute(() -> {
                        xueQiuDataService.lease(symbol);
                        log.info("ItemVisitFacade 开始初始化k线图 {}", symbol);
                        boolean selfDataInitSuccess = saveInitSelfData(symbol);
                        log.info("ItemVisitFacade 从自己数据源初始化k线图获取 {} 状态为 {}", symbol, selfDataInitSuccess);
                        String cookie = HttpHelper.getCookie("https://xueqiu.com/");
                        Map<String, List<Kline>> dailyWeekMonthHistoryMap = xueQiuDataService.getDailyWeekMonthHistory(symbol, cookie);
                        Map<String, List<Kline>> hourlyAndMinuteHistoryMap = xueQiuDataService.getHourlyAndMinuteHistory(symbol, cookie);
                        if (CollectionUtil.isNotEmpty(dailyWeekMonthHistoryMap) && CollectionUtil.isNotEmpty(hourlyAndMinuteHistoryMap)){
                            klineService.saveInit(symbol, dailyWeekMonthHistoryMap, hourlyAndMinuteHistoryMap);
                        }
                        log.info("ItemVisitFacade 初始化k线图 {} 完成", symbol);
                    }
            );
        }

        String defaultActive = Item.DEFAULT_ACTIVE.equalsIgnoreCase(item.getCrawlStatus()) ? "1" : "0";
        // 将币对的访问次数+1
        String sql = "INSERT INTO t_item_visit (symbol, visit_count, last_visit_time, default_active) " +
                "VALUES (?, 1, NOW(), ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "visit_count = visit_count + 1, " +
                "last_visit_time = NOW()";

        jdbcTemplate.update(sql, symbol, defaultActive);
        log.info("ItemVisitFacade 访问次数+1 {} 完成", symbol);

    }

    private boolean saveInitSelfData(String symbols) {
        log.info("正在通过自己数据源 初始化k綫圖:{}", symbols);
        List<Map<String, List<Kline>>> klines = spiderService.getKlines(symbols);
        if (CollectionUtil.isEmpty(klines)) {
            return false;
        }
        for (Map<String, List<Kline>> map : klines) {
            List<Kline> klines5P = map.get(Kline.PERIOD_5MIN);
            if (CollectionUtil.isNotEmpty(klines5P)) {
                String symbol = klines5P.get(0).getSymbol();
                klineService.saveInit(symbol, map);
                DataCache.clearLatestRealTime60s(symbol);
                //  dataDBService.delete(symbols);
                log.info("初始化k线图{} 数量为{}", symbol, map.size());
                return true;
            }
        }
        log.info("完成自己数据源 初始化k綫圖:{}", symbols);
        return false;

    }

    public List<String> getSymbolsOlderThanNDay(int days) {
        String query = "SELECT symbol FROM t_item_visit WHERE default_active = 0 and last_visit_time <= DATE_SUB(NOW(), INTERVAL ? DAY)";
        return jdbcTemplate.queryForList(query, new Object[]{days}, String.class);
    }


    public static void main(String[] args) {
        XueQiuDataServiceImpl xueQiuDataService = new XueQiuDataServiceImpl();
        String cookie = HttpHelper.getCookie("https://xueqiu.com/");
        String symbol = "SZ300066";
        Map<String, List<Kline>> dailyWeekMonthHistoryMap = xueQiuDataService.getDailyWeekMonthHistory(symbol, cookie);
        Map<String, List<Kline>> hourlyAndMinuteHistoryMap = xueQiuDataService.getHourlyAndMinuteHistory(symbol, cookie);
        System.out.println(dailyWeekMonthHistoryMap);
    }
}
