package com.yami.trading.service.tradeview;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class SymbolTradeViewDataHolder {

    public static Map<String, String> industryMap = Maps.newConcurrentMap();

    public static Map<String, List<SymbolTradeViewData>> typeList  = Maps.newConcurrentMap();
}
