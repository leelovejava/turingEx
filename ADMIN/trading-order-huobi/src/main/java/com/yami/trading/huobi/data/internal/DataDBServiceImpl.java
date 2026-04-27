package com.yami.trading.huobi.data.internal;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.config.RequestDataHelper;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.huobi.data.job.RealtimeQueue;
import com.yami.trading.service.data.RealtimeService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class DataDBServiceImpl implements DataDBService {
    @Autowired
    private NamedParameterJdbcOperations namedParameterJdbcTemplate;
    @Autowired
    private ItemService itemService;
    @Autowired
    private RealtimeService realtimeService;

    @Override
    public void saveAsyn(Realtime realtime) {
        Realtime current = DataCache.getRealtime(realtime.getSymbol());
        if (current == null || !current.getTs().equals(realtime.getTs())) {
            String symbol = realtime.getSymbol();
            Item item = itemService.findBySymbol(realtime.getSymbol());
            if (item.getMultiple() > 0) {
                realtime.setVolume(BigDecimal.valueOf(realtime.getVolume() * item.getMultiple()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue());
                realtime.setAmount(BigDecimal.valueOf(realtime.getAmount() * item.getMultiple()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue());
            }

            Realtime latestRealtime = DataCache.getLatestRealTime(symbol);
            // 超过1分钟丢弃
            if (null != latestRealtime && latestRealtime.getTs() >= (realtime.getTs() + 60 * 1000)) {
                return;
            }
            /**
             * 时间有变化，才保存
             */
            DataCache.putRealtime(realtime.getSymbol(), realtime);
            // 虚拟货币才需要的逻辑
            if (Item.cryptos.equalsIgnoreCase(item.getType())) {
                // 虚拟货币才需要维护最搞，最低。24小时信息
                Double high = DataCache.getRealtimeHigh(realtime.getSymbol());
                if (high != null && high >= realtime.getClose()) {
                    realtime.setHigh(new BigDecimal(high).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue());
                }
                Double low = DataCache.getRealtimeLow(realtime.getSymbol());
                if (low != null && low <= realtime.getClose()) {
                    realtime.setLow(new BigDecimal(low).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue());
                }

                Double h24Before = DataCache.getRealtime24HBeforeOpen(realtime.getSymbol());
                if (h24Before != null) {
                    realtime.setOpen(new BigDecimal(h24Before).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue());
                }

                List<Realtime> listHistory = DataCache.getCryptosRealtimeHistory(realtime.getSymbol());
                if (listHistory == null) {
                    listHistory = new LinkedList<>();
                }
                if (realtime.getLow() > 0) {
                    /**
                     * 修正最低为0的BUG，直接丢弃
                     */
                    listHistory.add(realtime);
                    DataCache.putCryptosRealtimeHistory(realtime.getSymbol(), listHistory);
                }
            }

            DataCache.putLatestRealTime(symbol, realtime);

            // 最近60s内实时价格集合
            // 此处要做优化，如果最近一次ts和采集的不一样，需要更新的
            List<Realtime> list = DataCache.getLatestRealTime60s(symbol);
            if (list == null) {
                log.info("---> DataDBServiceImpl.saveAsyn 当前 symbol:{} 下的 60s 实时数据为空", symbol);
            }
            // 当前时间和ts最后一个时间不一样，才更新.一样的话不更新，避免k线图计算
            if (CollectionUtil.isEmpty(list)) {
                list = new ArrayList<>();
                list.add(realtime);
                DataCache.putLatestRealTime60s(symbol, list);
                RealtimeQueue.add(realtime);
            } else if (!Objects.equals(CollectionUtil.getLast(list).getTs(), realtime.getTs())) {
                if (list.size() >= KlineConstant.LATEST_REALTIME_LIST_MAX) {
                    list.remove(0);
                }
                list.add(realtime);
                DataCache.putLatestRealTime60s(symbol, list);
                RealtimeQueue.add(realtime);
            }
        } else {
            DataCache.putLatestRealTime(current.getSymbol(), current);
        }
    }

    @Override
    public void saveBatch(List<Realtime> entities) {
        RequestDataHelper.set("symbol", entities.get(0).getSymbol());
        realtimeService.saveBatch(entities);
        RequestDataHelper.clear();

    }

    @Override
    public Realtime get(String symbol) {
        RequestDataHelper.set("symbol", symbol);
        LambdaQueryWrapper<Realtime> queryWrapper = new LambdaQueryWrapper<Realtime>()
                .eq(Realtime::getSymbol, symbol)
                .orderByDesc(Realtime::getTs)
                .last("LIMIT 1");
        Realtime realtime = realtimeService.getBaseMapper().selectOne(queryWrapper);
        RequestDataHelper.clear();
        return realtime;
    }

    @Override
    public void deleteRealtime(int days) {
        for (int i = 0; i <= Constants.TABLE_PARTITIONS - 1; i++) {
            Map<String, Object> parameters = new HashMap();
            Long ts = DateUtils.addDate(new Date(), days).getTime();
            parameters.put("ts", ts);
            this.namedParameterJdbcTemplate.update("DELETE FROM t_realtime_" + i + " WHERE ts < :ts", parameters);
        }
    }

    @Override
    public List<Realtime> findRealtimeOneDay(String symbol) {
        int interval = 3;
        int num = (24 * 60 * 60) / interval;
        RequestDataHelper.set("symbol", symbol);

        LambdaQueryWrapper<Realtime> queryWrapper = new LambdaQueryWrapper<Realtime>()
                .eq(Realtime::getSymbol, symbol)
                .orderByDesc(Realtime::getTs)
                .last("LIMIT " + num);
        List<Realtime> realtimes = realtimeService.getBaseMapper().selectList(queryWrapper);
        Collections.sort(realtimes);
        RequestDataHelper.clear();
        return realtimes;

    }

    @Override
    public void delete(String symbol) {
        RequestDataHelper.set("symbol", symbol);
        QueryWrapper<Realtime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("symbol", symbol);
        realtimeService.remove(queryWrapper);
        RequestDataHelper.clear();
    }

    /**
     * 获取最新60s实时价格数据
     */
    @Override
    public List<Realtime> listRealTime60s(String symbol) {
        RequestDataHelper.set("symbol", symbol);
        int data_interval = 3000;
        // 取数据条数为
        int limit = 60 * 1000 / data_interval;
        LambdaQueryWrapper<Realtime> queryWrapper = new LambdaQueryWrapper<Realtime>()
                .eq(Realtime::getSymbol, symbol)
                .orderByDesc(Realtime::getTs)
                .last("LIMIT " + limit);
        List<Realtime> realtimes = realtimeService.getBaseMapper().selectList(queryWrapper);
        RequestDataHelper.clear();
        return realtimes;
    }


}
