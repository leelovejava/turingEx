package com.yami.trading.admin.facade;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.dto.MarketQuotations;
import com.yami.trading.bean.item.dto.MarketQuotationsAdjust;
import com.yami.trading.bean.model.Log;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.huobi.data.internal.AdjustmentValueService;
import com.yami.trading.huobi.data.model.AdjustmentValue;
import com.yami.trading.security.common.model.YamiSysUser;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.system.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

@Component
@Slf4j
public class MarketQuotationsFacade {
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    @Autowired
    private AdjustmentValueService adjustmentValueService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private LogService logService;

    /**
     * 行情列表
     *
     * @return
     */
    public List<MarketQuotations> marketQuotationsList(List<String> symbols) {
        if (CollectionUtils.isEmpty(symbols)) {
            return null;
        }
        List<MarketQuotations> resultList = Lists.newLinkedList();
        List<Realtime> realtimes = this.dataService.realtime(StringUtils.join(symbols, ","));
        Set<String> symbolKey = new HashSet<>();
        for (Realtime realtime : realtimes) {
            Integer decimal = itemService.getDecimal(realtime.getSymbol());
            MarketQuotations marketQuotations = new MarketQuotations();
            if (symbolKey.contains(realtime.getSymbol())) {
                continue;
            }
            String name = itemService.findBySymbol(realtime.getSymbol()).getName();
            marketQuotations.setName(name);

            marketQuotations.setSymbol(realtime.getSymbol());
            double currentValue = this.adjustmentValueService.getCurrentValue(realtime.getSymbol());
            if (currentValue == 0) {
                marketQuotations.setAdjustValue("0");
                marketQuotations.setNewPrice(Arith.str(realtime.getClose(), decimal));
            } else {
                marketQuotations.setAdjustValue(Arith.str(currentValue, decimal));
                // 原来价格是调整之后减去调整值
                marketQuotations.setNewPrice(String.valueOf(Arith.sub(realtime.getClose(), currentValue, decimal)));
            }
            marketQuotations.setAfterValue(Arith.str(realtime.getClose(), decimal));
            resultList.add(marketQuotations);
            symbolKey.add(realtime.getSymbol());
        }
        return resultList;
    }

    /**
     * 获取详情
     *
     * @param symbol
     * @return
     */
    public MarketQuotationsAdjust getDetails(String symbol) {
        MarketQuotationsAdjust marketQuotationsAdjust = new MarketQuotationsAdjust();
        Integer decimal = itemService.getDecimal(symbol);
        marketQuotationsAdjust.setSymbol(symbol);
        Realtime realtime = null;
        List<Realtime> realtimes = dataService.realtime(symbol);
        if(CollectionUtil.isNotEmpty(realtimes)){
            realtime = realtimes.get(0);
        }
        if (realtime == null) {
            List<Realtime> realtimeList = this.dataService.realtime(symbol);
            if (CollectionUtil.isEmpty(realtimeList)) {
                log.error("{} 当前没有实时价格", symbol);
                return marketQuotationsAdjust;
            }
            realtime = realtimeList.get(0);
        }
        Item item = this.itemService.findBySymbol(symbol);
        double currentValue = this.adjustmentValueService.getCurrentValue(symbol);
        if (currentValue == 0) {
            marketQuotationsAdjust.setAdjustValue(Arith.str(0, decimal));
        } else {
            marketQuotationsAdjust.setAdjustValue(Arith.str(currentValue, decimal));
        }
        if (currentValue == 0) {
            marketQuotationsAdjust.setNewPrice(Arith.str(realtime.getClose(), decimal));
        } else {
            marketQuotationsAdjust.setNewPrice(String.valueOf(Arith.sub(realtime.getClose(), currentValue, decimal)));
        }
        marketQuotationsAdjust.setPips(Arith.str(item.getPips(), decimal));
        marketQuotationsAdjust.setAfterValue(Arith.str(realtime.getClose(), decimal));

        AdjustmentValue delayValue = this.adjustmentValueService.getDelayValue(symbol);

        if (delayValue != null) {
            marketQuotationsAdjust.setDelayValue(Arith.str(delayValue.getValue(), decimal));
            marketQuotationsAdjust.setDelaySecond("" + delayValue.getSecond());
        }
        return marketQuotationsAdjust;
    }

    /**
     * 行情调整预计算值
     *
     * @param symbol
     * @param type
     * @param value
     * @return
     */
    public Map<String, String> calculateValue(String symbol, String type, double value) {
        Map<String, String> resultMap = new HashMap<>();
        Integer decimal = itemService.getDecimal(symbol);
        Realtime realtime;
        realtime = this.dataService.realtime(symbol).get(0);
        Item item = this.itemService.findBySymbol(symbol);
        double currentValue = this.adjustmentValueService.getCurrentValue(symbol);
        if (currentValue == 0) {
            resultMap.put("newPrice", Arith.str(realtime.getClose(), decimal));
        } else {
            resultMap.put("newPrice", String.valueOf(Arith.sub(realtime.getClose(), currentValue, decimal)));
        }

        double temp;
        if (type.equalsIgnoreCase("0")) {
            temp = Arith.add(value, item.getPips());
            // 调整量
            resultMap.put("adjustCurrentValue", Arith.str(temp, decimal));
            // 调整后的值
            resultMap.put("adjustValueAfter", String.valueOf(Arith.add(realtime.getClose(), temp, decimal)));
        } else if (type.equalsIgnoreCase("1")) {
            temp = Arith.sub(value, item.getPips());

            resultMap.put("adjustCurrentValue", temp + "");
            resultMap.put("adjustValueAfter", String.valueOf(Arith.add(realtime.getClose(), temp, decimal)));
        } else {
            temp = value;
            resultMap.put("adjustCurrentValue", temp + "");
            resultMap.put("adjustValueAfter", String.valueOf(Arith.add(realtime.getClose(), temp, decimal)));
        }

        if (currentValue == 0) {
            resultMap.put("adjustValue", Arith.str(item.getPips(), decimal));
        } else {
            resultMap.put("adjustValue", String.valueOf(Arith.add(temp, currentValue, decimal)));
        }
        AdjustmentValue delayValue = this.adjustmentValueService.getDelayValue(symbol);

        if (delayValue != null) {
            resultMap.put("delayValue", Arith.str(delayValue.getValue(), decimal));
            resultMap.put("delaySecond", delayValue.getSecond() + "");
        }
        return resultMap;

    }

    public void adjust(String symbol, double second, double value) {
        double currentValue = this.adjustmentValueService.getCurrentValue(symbol);
        if (currentValue == 0) {
            Realtime realtime = this.dataService.realtime(symbol).get(0);
            currentValue = realtime.getClose();
        }
        String log = MessageFormat.format("ip:" + IPHelper.getIpAddr() + ",调整行情,币种:{0},原值:{1},调整值:{2},调整时间:{3}",
                symbol, currentValue, value, second);

        this.adjustmentValueService.adjust(symbol, value, second);
        saveLog(log);
        ThreadUtils.sleep(1000);
    }

    public void saveLog(String content) {
        YamiSysUser sysUser = SecurityUtils.getSysUser();
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setOperator(sysUser.getUsername());
        log.setUsername(sysUser.getUsername());
        log.setUserId(sysUser.getUserId().toString());
        log.setLog(content);
        log.setCreateTime(new Date());
        logService.save(log);
    }
}
