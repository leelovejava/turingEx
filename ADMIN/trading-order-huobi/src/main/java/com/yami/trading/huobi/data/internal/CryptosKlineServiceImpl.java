package com.yami.trading.huobi.data.internal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.common.config.RequestDataHelper;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.hobi.HobiDataService;
import com.yami.trading.huobi.task.KlineContext;
import com.yami.trading.service.data.KlineDBService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CryptosKlineServiceImpl implements CryptosKlineService{
    private Logger logger = LoggerFactory.getLogger(CryptosKlineServiceImpl.class);

    @Autowired
    private KlineDBService klineDBService;
    @Autowired
    private HobiDataService hobiDataService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private NamedParameterJdbcOperations namedParameterJdbcTemplate;
    @Autowired
    private KlineContext klineContext;
    @Override
    public void saveInit(String symbol) {
        try {
            klineContext.setManualInit(symbol);
            Map<String, Object> parameters = new HashMap();
            parameters.put("symbol", symbol);
            for (int i = 0; i <= Constants.TABLE_PARTITIONS - 1; i++) {
                namedParameterJdbcTemplate.update("DELETE FROM t_kline_" + i + " WHERE SYMBOL = :symbol", parameters);
            }
            this.bulidInit(symbol, Kline.PERIOD_1MIN);
            this.bulidInit(symbol, Kline.PERIOD_5MIN);
            this.bulidInit(symbol, Kline.PERIOD_15MIN);
            this.bulidInit(symbol, Kline.PERIOD_30MIN);
            this.bulidInit(symbol, Kline.PERIOD_60MIN);
            this.bulidInit(symbol, Kline.PERIOD_4HOUR);
            this.bulidInit(symbol, Kline.PERIOD_1DAY);
            this.bulidInit(symbol, Kline.PERIOD_1MON);
            this.bulidInit(symbol, Kline.PERIOD_1WEEK);
        }catch (Exception e){
            logger.error("k线图初始化失败 {}" ,symbol ,e);
        }finally {
            klineContext.setManualFinish(symbol);
        }


    }

    public void bulidInit(String symbol, String line) {
        RequestDataHelper.set("symbol", symbol);
        List<Kline> list = hobiDataService.kline(itemService.findBySymbol(symbol).getRemarks(), line, null,
                0);
        klineDBService.saveOrUpdateBatch(list);
        KlineTimeObject model = new KlineTimeObject();
        Collections.sort(list); // 按时间升序
        model.setKline(list);
        DataCache.putKline(symbol, line, model);
        RequestDataHelper.clear();

    }

    @Override
    public void saveOne(String symbol, String line) {
        RequestDataHelper.set("symbol", symbol);
        Realtime realtime = DataCache.getRealtime(symbol);
        if (realtime == null) {
            return;
        }

        Kline lastOne = null;

        List<Kline> list = this.find(symbol, line, 1);
        if (list.size() > 0) {
            lastOne = list.get(0);
        }

        String key = symbol + "_" + line;
        Kline hobiOne = DataCache.getKlineHoBi(key);
        if (hobiOne == null || lastOne == null) {
            // 取不到远程数据，直接退出
            return;
        }
        RequestDataHelper.set("symbol", symbol);
        Kline kline = this.bulidKline(realtime, lastOne, hobiOne, line);
        kline.setPeriod(line);
        klineDBService.save(kline);

        RequestDataHelper.clear();

        KlineTimeObject timeObject = DataCache.getKline(symbol, line);
        if (timeObject == null) {
            timeObject = new KlineTimeObject();
        }
        timeObject.getKline().add(kline);
        DataCache.putKline(symbol, line, timeObject);
        RequestDataHelper.clear();

    }

    @Override
    public Kline bulidKline(Realtime realtime, Kline lastOne, Kline hobiOne, String line) {
        Kline kline = new Kline();
        kline.setSymbol(realtime.getSymbol());
        kline.setTs(realtime.getTs());
        kline.setOpen(realtime.getOpen());
        kline.setClose(realtime.getClose());
        if(realtime.getOpen() >= realtime.getClose()){
            kline.setHigh(realtime.getOpen());
            kline.setLow(realtime.getClose());
        }else{
            kline.setHigh(realtime.getClose());
            kline.setLow(realtime.getOpen());
        }
        /**
         * 新传回来的volume是固定的 需要除以Arith.div(realtime.getVolume(), 倍数)
         */
        kline.setVolume(realtime.getVolume());

        if (lastOne != null) {
            kline.setOpen(lastOne.getClose());
        }
        int interval = 3;

        HighLow highLow = null;
        switch (line) {
            case "1min":
                highLow = HighLowHandle.get(realtime.getSymbol(), (60) / interval, interval);
                break;

            case "5min":
                highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 5) / interval, interval);
                break;
            case "15min":
                highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 15) / interval, interval);
                break;
            case "30min":
                highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 30) / interval, interval);
                break;

            case "60min":
                highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 60) / interval, interval);
                break;

            case "4hour":
                highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 60 * 4) / interval, interval);
                break;
            case "1day":
                highLow = HighLowHandle.get(realtime.getSymbol(), (60 * 60 * 24) / interval, interval);
                break;

            case Kline.PERIOD_1WEEK:
                highLow = HighLowHandle.getByDay(realtime.getSymbol(), 7);
                break;

            case Kline.PERIOD_1MON:
                highLow = HighLowHandle.getByDay(realtime.getSymbol(), 30);
                break;

        }

        if (highLow != null && highLow.getHigh() != 0) {
            kline.setHigh(highLow.getHigh());
        }
        if (highLow != null && highLow.getLow() != 0) {
            kline.setLow(highLow.getLow());
        }

        kline.setVolume(hobiOne.getVolume());

        return kline;
    }

    /**
     * 查询所有的K线数据
     * 时间升序排列 [1,2,3]
     * 最后一条数据是最新的
     */
    @Override
    public List<Kline> find(String symbol, String line, int pageSize) {
        RequestDataHelper.set("symbol", symbol);
        LambdaQueryWrapper<Kline> queryWrapper = new LambdaQueryWrapper<Kline>()
                .eq(Kline::getSymbol, symbol)
                .eq(Kline::getPeriod, line)
                .orderByDesc(Kline::getTs)
                .last("LIMIT " + pageSize);
        List<Kline> klines = klineDBService.getBaseMapper().selectList(queryWrapper);
        RequestDataHelper.clear();
        return klines;
    }

    @Override
    public void delete(String line, int days) {
        Map<String, Object> parameters = new HashMap();
        Long ts = DateUtils.addDate(new Date(), days).getTime();
        parameters.put("line", line);
        parameters.put("ts", ts);
        for (int i = 0; i <= Constants.TABLE_PARTITIONS - 1; i++) {
            this.namedParameterJdbcTemplate.update("DELETE FROM t_kline_" + i + " WHERE TS < :ts  AND PERIOD=:line ", parameters);
        }
    }
}
