package com.yami.trading.huobi.task;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class KlineContext {
    private ConcurrentHashMap<String, Boolean> klineInitState = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Boolean> manalInit = new ConcurrentHashMap<>();

    /***
     * 将symbol k线图初始化设置为成功
     * @param symbol
     */
    public void initKlineSuccess(String symbol){
        klineInitState.put(symbol, true);
    }
    /**
     * 判断当前k线图是否已经初始化成功
     * @param symbol
     * @return
     */
    public boolean isInitSuccess(String symbol){
        return klineInitState.getOrDefault(symbol, false);
    }

    public void setManualInit(String symbol){
        manalInit.put(symbol, true);
    }
    public void setManualFinish(String symbol){
        manalInit.put(symbol, false);
    }
    public boolean isManualIniting(String symbol){
        return manalInit.getOrDefault(symbol, false);
    }
}
