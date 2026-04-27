package com.yami.trading.huobi.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import com.yami.trading.huobi.hobi.http.HttpMethodType;
import com.yami.trading.huobi.task.domain.TradeViewData;

public class TradeViewDataService {
    public final static String getTradeViewData = "https://onjdo.com/stock/api/live/getTradeViewData";

    public static TradeViewData getTradeViewData() {
        String result = HttpHelper.getJSONFromHttpNew(getTradeViewData, Maps.newHashMap(), HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        if ("ok".equals(code)) {
            TradeViewData tradeViewData = resultJson.getObject("data", TradeViewData.class);
            return tradeViewData;

        }
        return null;
    }

}
