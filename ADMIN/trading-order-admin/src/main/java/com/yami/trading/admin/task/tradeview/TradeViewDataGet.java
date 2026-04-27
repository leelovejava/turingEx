package com.yami.trading.admin.task.tradeview;

import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.service.tradeview.SymbolTradeViewData;
import com.yami.trading.service.tradeview.SymbolTradeViewDataHolder;
import com.yami.trading.service.tradeview.TradeViewDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TradeViewDataGet {

    @Autowired
    private TradeViewDataService tradeViewDataService;

    @Scheduled(cron = "* */5 * ? * *")
    public void fetchIndustry(){
        List<SymbolTradeViewData> tradeViewData = tradeViewDataService.getTradeViewDataIndustry();
        SymbolTradeViewDataHolder.industryMap = tradeViewDataService.industryRate(tradeViewData);
        SymbolTradeViewDataHolder.typeList.put(Item.A_STOCKS, tradeViewDataService.getTradeViewDataStocks(Item.A_STOCKS));
        SymbolTradeViewDataHolder.typeList.put(Item.HK_STOCKS, tradeViewDataService.getTradeViewDataStocks(Item.HK_STOCKS));
        SymbolTradeViewDataHolder.typeList.put(Item.TW_STOCKS, tradeViewDataService.getTradeViewDataStocks(Item.TW_STOCKS));



    }
}
