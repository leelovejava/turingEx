package com.yami.trading.huobi.data.job;


import com.alibaba.fastjson.JSONObject;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.util.Arith;
import com.yami.trading.huobi.data.AdjustmentValueCache;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.internal.DataDBService;
import com.yami.trading.huobi.data.model.AdjustmentValue;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
public abstract class AbstractGetDataJob implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static volatile boolean first = true;
    /**
     * 数据接口调用间隔时长(毫秒)
     */
    protected int interval;
    @Autowired
    protected SysparaService sysparaService;
    @Autowired
    protected DataDBService dataDBService;
    @Autowired
    protected HobiDataService hobiDataService;
    @Autowired
    protected ItemService itemService;

    public void start() {
        new Thread(this, getName()).start();
    }

    @Override
    public abstract void run();

    public abstract String getName();


    public abstract void realtimeHandle(String symbols);

    public void handleRealTimeList(List<Realtime> realtimeList) {
        for (Realtime realtime : realtimeList) {
            try {
                String symbol = realtime.getSymbol();
                symbol =  itemService.getSymbolByRemarks(symbol);
                Integer decimal = itemService.getDecimal(symbol);
                Item item = this.itemService.findBySymbol(symbol);
                Double currentValue = AdjustmentValueCache.getCurrentValue().get(symbol);
                AdjustmentValue delayValue = AdjustmentValueCache.getDelayValue().get(symbol);
                //logger.info("---> AbstractGetDataJob.handleRealTimeList debug 位置1, item:{}", item);
                if (delayValue != null) {
                    // 延时几次
                    int frequency = (int) Arith.div(Arith.mul(delayValue.getSecond(), 1000.0D), this.interval);

                    if (frequency <= 1) {
                        if (currentValue == null) {
                            AdjustmentValueCache.getCurrentValue().put(symbol, delayValue.getValue());
                        } else {
                            AdjustmentValueCache.getCurrentValue().put(symbol, delayValue.getValue() + currentValue);
                        }

                        if (item.getAdjustmentValue() != AdjustmentValueCache.getCurrentValue().get(symbol)) {
                            item.setAdjustmentValue(AdjustmentValueCache.getCurrentValue().get(symbol));
                            itemService.saveOrUpdate(item);
                        }
                        AdjustmentValueCache.getDelayValue().remove(symbol);
                    } else {
                        // 本次调整值
                        //logger.info("---> AbstractGetDataJob.handleRealTimeList debug 位置2, current symbol:{} value:{}", symbol, AdjustmentValueCache.getCurrentValue().get(symbol));
                        double currentValueFrequency = BigDecimal.valueOf(delayValue.getValue() / frequency).setScale(decimal, RoundingMode.HALF_UP).doubleValue();
                        // delayValue.getValue().divide(new BigDecimal(frequency), decimal, RoundingMode.HALF_UP);
                        if (currentValue == null) {
                            AdjustmentValueCache.getCurrentValue().put(symbol, currentValueFrequency);
                        } else {
                            AdjustmentValueCache.getCurrentValue().put(symbol, currentValue + currentValueFrequency);
                        }

                        delayValue.setValue(delayValue.getValue() - currentValueFrequency);
                        delayValue.setSecond(Arith.sub(delayValue.getSecond(), Arith.div(this.interval, 1000.0D)));
                        AdjustmentValueCache.getDelayValue().put(symbol, delayValue);

                        if (item.getAdjustmentValue() != AdjustmentValueCache.getCurrentValue().get(symbol)) {
                            item.setAdjustmentValue(AdjustmentValueCache.getCurrentValue().get(symbol));
                            itemService.saveOrUpdate(item);
                        }
                    }
                }

                currentValue = AdjustmentValueCache.getCurrentValue().get(realtime.getSymbol());
                //logger.info("---> AbstractGetDataJob.handleRealTimeList debug 位置3, currentValue:{}", currentValue);
                if (currentValue != null && currentValue != 0) {
                    realtime.setClose(BigDecimal.valueOf(realtime.getClose() + currentValue).setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setAsk(BigDecimal.valueOf(realtime.getAsk() + currentValue).setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                    realtime.setBid(BigDecimal.valueOf(realtime.getBid() + currentValue).setScale(decimal, RoundingMode.HALF_UP).doubleValue());
                }

                try {
                    // 缓存中最新一条Realtime数据
                    Realtime realtimeLast = DataCache.getRealtime(symbol);
                    // 临时处理：正常10秒超过25%也不合理,丢弃.只有虚拟货币才这样执行
                    boolean checkRate = getName().contains("虚拟货币");
                    double rate = 0;
                    if (!checkRate) {
                        saveData(realtime, symbol, item);
                    } else {
                        if (realtimeLast != null) {
                            rate = Math.abs(Arith.sub(realtime.getClose(), realtimeLast.getClose()));
                        }
                        if (null == realtimeLast) {
                            log.error("缓存里面没有realtimeLast数据, 注意观察");
                            saveData(realtime, symbol, item);
                            return;
                        }
                        if (realtimeLast.getClose() == 0) {
                            saveData(realtime, symbol, item);
                        } else if (Arith.div(rate, realtimeLast.getClose()) < 0.25D) {
                            saveData(realtime, symbol, item);
                        } else {
                            log.error("当前{}价格{},上一次价格为{}过25%也不合理,丢弃Realtime,不入库", realtime.getSymbol(), realtime.getClose(), realtimeLast.getClose());
                        }
                    }
                } catch (Exception e) {
                    logger.error("---> AbstractGetDataJob.handleRealTimeList debug 位置4, realtime -> symbol:{}, error: ", realtime.getSymbol(), e);
                    throw e;
                }
            } catch (Exception e) {
                log.error("数据采集失败 {}:[]",realtime.getSymbol(), JSONObject.toJSON(realtime),  e);
            }
        }
    }

    private void saveData(Realtime realtime, String symbol, Item item) {
        Double high = DataCache.getRealtimeHigh(symbol);
        Double low = DataCache.getRealtimeLow(symbol);
        if (realtime.getTs().toString().length() <= 10) {
            realtime.setTs(Long.valueOf(realtime.getTs() + "000"));
        }
        realtime.setName(item.getName());
        if (high == null || realtime.getHigh() > high) {
            // 刷新内存中的 high
            DataCache.putRealtimeHigh(symbol, realtime.getHigh());
        }
        if ((low == null || realtime.getLow() < low) && realtime.getLow() > 0) {
            // 刷新内存中的 low，并且只使用值大于 0 的 low
            DataCache.putRealtimeLow(symbol, realtime.getLow());
        }
        this.dataDBService.saveAsyn(realtime);
    }

}
