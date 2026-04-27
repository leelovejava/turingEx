package com.yami.trading.service.tradeview;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.http.HttpHelper;
import com.yami.trading.common.http.HttpMethodType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class TradeViewDataService {
    public final static String getTradeViewData = "https://onjdo.com/stock/api/live/getTradeViewData";
    public List<SymbolTradeViewData> getTradeViewDataStocks(String type) {
        List<SymbolTradeViewData> lists = Lists.newArrayList();
        HashMap<String, String> param = Maps.newHashMap();
        param.put("type", type);
        String result = HttpHelper.getJSONFromHttpNew(getTradeViewData, param, HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        if ("ok".equals(code)) {
            return resultJson.getJSONArray("data").toJavaList(SymbolTradeViewData.class);
        }
        return lists;
    }

    public List<SymbolTradeViewData> getTradeViewDataIndustry() {
        List<SymbolTradeViewData> lists = Lists.newArrayList();
        String result = HttpHelper.getJSONFromHttpNew(getTradeViewData, Maps.newHashMap(), HttpMethodType.GET);
        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        if ("ok".equals(code)) {
            List<SymbolTradeViewData> data = resultJson.getJSONArray("data").toJavaList(SymbolTradeViewData.class);
            SymbolTradeViewDataHolder.typeList.put(Item.US_STOCKS, data);
            return data;
        }
        return lists;
    }

    public Map<String, String> industryRate(List<SymbolTradeViewData> symbolTradeViewData){
        Map<String, String> industryRate = new HashMap<>();
        Map<String, List<SymbolTradeViewData>> listMap = symbolTradeViewData.stream().collect(Collectors.groupingBy(SymbolTradeViewData::getIndustry));
        for(String industry : listMap.keySet()){
            industryRate.put(industry, calculateRate(listMap.get(industry)));
        }
        return industryRate;
    }

    public String calculateRate(List<SymbolTradeViewData> dataList){
        BigDecimal rateAmount = new BigDecimal(0);
        BigDecimal sumAmount = new BigDecimal(0);
        for(SymbolTradeViewData symbolTradeViewData : dataList){
            rateAmount = rateAmount.add(symbolTradeViewData.getRate().multiply(symbolTradeViewData.getMarketCap()));
            sumAmount = sumAmount.add(symbolTradeViewData.getMarketCap());
        }
        return rateAmount.divide(sumAmount,2, RoundingMode.HALF_UP).toPlainString();
    }

    public static void main(String[] args) {
        String content = FileUtil.readUtf8String("F:\\data.txt");
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray data = jsonObject.getJSONArray("data");
        List<SymbolTradeViewData> lists = Lists.newArrayList();
        for(Object o : data){
            JSONObject jo = (JSONObject) o;
            JSONArray d = jo.getJSONArray("d");
            String rate = d.get(2).toString();
            int len = d.size();
            String symbol = d.get(len-3).toString();
            String industry = d.get(len-7).toString();
            BigDecimal marketCap = new BigDecimal(d.get(15).toString());
            SymbolTradeViewData symbolTradeViewData = new SymbolTradeViewData();
            symbolTradeViewData.setRate(new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP));
            symbolTradeViewData.setSymbol(symbol);
            symbolTradeViewData.setMarketCap(marketCap);
            symbolTradeViewData.setIndustry(industry);
            lists.add(symbolTradeViewData);
        }
        Map<String, String> stringStringMap = new  TradeViewDataService().industryRate(lists);
        System.out.println(stringStringMap);


    }

}
