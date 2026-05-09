package com.yami.trading.admin.task.tradeview;

import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.service.tradeview.SymbolTradeViewData;
import com.yami.trading.service.tradeview.SymbolTradeViewDataHolder;
import com.yami.trading.service.tradeview.TradeViewDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * TradeView 数据获取定时任务
 * 
 * 该类负责定时从数据源获取股票和行业数据，并将数据存入全局缓存中供前端使用。
 * 
 * 定时任务说明：
 * - fetchIndustry(): 每5分钟执行一次，获取行业数据和各市场股票数据
 * 
 * 数据来源：
 * - 主要使用 Twelve Data API（原接口 https://onjdo.com/stock/api/live/getTradeViewData 已失效）
 * - 需要在 application.yml 中配置 Twelve Data API Key
 * 
 * 数据缓存：
 * - SymbolTradeViewDataHolder.industryMap: 存储行业涨跌幅映射 (Map<String, String>)
 * - SymbolTradeViewDataHolder.typeList: 存储各市场股票数据 (Map<String, List<SymbolTradeViewData>>)
 * 
 * 支持的市场类型：
 * - Item.A_STOCKS: A股
 * - Item.HK_STOCKS: 港股
 * - Item.TW_STOCKS: 台股
 * - Item.US_STOCKS: 美股（用于行业数据计算）
 */
@Component
public class TradeViewDataGet {

    /**
     * 日志记录器
     */
    private final Logger log = LoggerFactory.getLogger(TradeViewDataGet.class);

    /**
     * TradeView数据服务
     * 负责从 Twelve Data API 获取股票和行业数据
     */
    @Autowired
    private TradeViewDataService tradeViewDataService;

    /**
     * 获取行业数据和各市场股票数据
     * 
     * 执行周期：每5分钟
     * 
     * 执行流程：
     * 1. 获取行业数据（美股数据用于计算行业涨跌幅）
     * 2. 计算行业平均涨跌幅，存入 industryMap
     * 3. 获取A股股票数据，存入 typeList
     * 4. 获取港股股票数据，存入 typeList
     * 5. 获取台股股票数据，存入 typeList
     * 
     * 数据缓存结构：
     * - industryMap: { "科技": "2.5", "金融": "-1.3", ... }
     * - typeList: { "A_STOCKS": [SymbolTradeViewData, ...], "HK_STOCKS": [...], ... }
     * 
     * 异常处理：
     * - 捕获所有异常并记录警告日志，不中断定时任务
     * - 异常信息包含具体错误原因
     */
    @Scheduled(cron = "* */5 * ? * *")
    public void fetchIndustry(){
        try {
            // 步骤1: 获取行业数据（基于美股）
            List<SymbolTradeViewData> tradeViewData = tradeViewDataService.getTradeViewDataIndustry();
            
            // 步骤2: 计算行业涨跌幅并存入缓存
            SymbolTradeViewDataHolder.industryMap = tradeViewDataService.industryRate(tradeViewData);
            
            // 步骤3: 获取各市场股票数据并存入缓存
            SymbolTradeViewDataHolder.typeList.put(Item.A_STOCKS, tradeViewDataService.getTradeViewDataStocks(Item.A_STOCKS));
            SymbolTradeViewDataHolder.typeList.put(Item.HK_STOCKS, tradeViewDataService.getTradeViewDataStocks(Item.HK_STOCKS));
            SymbolTradeViewDataHolder.typeList.put(Item.TW_STOCKS, tradeViewDataService.getTradeViewDataStocks(Item.TW_STOCKS));
            
            log.debug("TradeView数据获取完成，行业数量={}", SymbolTradeViewDataHolder.industryMap.size());
        } catch (Exception e) {
            // 记录异常但不中断任务，下次定时执行时会重试
            log.warn("获取TradeView数据失败: {}", e.getMessage());
        }
    }
}