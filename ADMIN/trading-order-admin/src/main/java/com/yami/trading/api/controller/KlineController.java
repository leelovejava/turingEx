package com.yami.trading.api.controller;

import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.huobi.constants.KlinePeriodEnum;
import com.yami.trading.huobi.data.TimeZoneConverterService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * K线图
 */
@RestController
@CrossOrigin
@Api(tags = "K线图实时行情")
@Slf4j
public class KlineController {
    @Autowired
    private ItemService itemService;
    @Autowired
    @Qualifier("dataService")
    private DataService dataService;

    @Autowired
    private TimeZoneConverterService timeZoneConverterService;

    @ApiOperation(value = "行情")
    @GetMapping("/api/hobi!getKline.action")
    public Result<List<Map<String, Object>>> getKline(@RequestParam String symbol, @RequestParam String line) {
        ResultObject resultObject = new ResultObject();
        // 1min, 5min, 15min, 30min, 60min, 4hour, 1day, 1mon, 1week
        Item bySymbol = itemService.findBySymbol(symbol);
        if (bySymbol == null) {
            throw new YamiShopBindException("symbol not exist");
        }
        String timeZone = timeZoneConverterService.getTimeZoneByItemCloseType(bySymbol.getOpenCloseType());
        try {
            if ("1quarter".equalsIgnoreCase(line)) {
                line = Kline.PERIOD_QUARTER;
            }
            if ("1year".equalsIgnoreCase(line)) {
                line = Kline.PERIOD_YEAR;
            }
            // 数据处理，取多少记录合适？ TODO
            int size = 300;
            KlinePeriodEnum klinePeriodType = KlinePeriodEnum.lineOf(line);
            if (klinePeriodType != null) {
                size = klinePeriodType.getMaxSize(); // 先返回 maxSize
            }
            List<Kline> data = this.dataService.kline(symbol, line);
            //log.info("------> KlineController.getKline 获取到的 kline 集合长度为:{}", data.size());
            int len = data.size();
            if ("1day".equals(line)
                    || "5day".equals(line)
                    || "1mon".equals(line)
                    || "1week".equals(line)
                    || "quarter".equalsIgnoreCase(line)
                    || "year".equalsIgnoreCase(line)) {
                for (int i = 0; i < len; i++) {
                    Kline datum = data.get(i);
//                    // caster 2023-11-09 调整，基于盘口配置的时区，修改展示时间
//                    Date showTime = DateTimeTools.transferToShowTime(datum.getTs());
//                    datum.setCurrentTime(DateUtil.format(showTime, "yyyy-MM-dd"));

                    String dateStr = timeZoneConverterService.convertTimeZone(datum.getTs(), timeZone);
                    datum.setCurrentTime(dateStr.split(" ")[0]);
                }
            } else if ("1min".equals(line)) {
                for (int i = 0; i < len; i++) {
                    Kline datum = data.get(i);

//                    // caster 2023-11-09 调整，基于盘口配置的时区，修改展示时间
//                    Date showTime = DateTimeTools.transferToShowTime(datum.getTs());
//                    datum.setCurrentTime(DateUtil.format(showTime, "HH:mm"));
                    String  dateStr = timeZoneConverterService.convertTimeZone(datum.getTs(), timeZone);
                    datum.setCurrentTime(dateStr.split(" ")[1]);
                }
            } else {
                for (int i = 0; i < len; i++) {
                    Kline datum = data.get(i);

//                    // caster 2023-11-09 调整，基于盘口配置的时区，修改展示时间
//                    Date showTime = DateTimeTools.transferToShowTime(datum.getTs());
//                    datum.setCurrentTime(DateUtil.format(showTime, "MM-dd HH:mm"));
                    String  dateStr = timeZoneConverterService.convertTimeZone(datum.getTs(), timeZone);
                    String substring = dateStr.substring(dateStr.indexOf("-")+1);
                    datum.setCurrentTime(substring);
                }
            }

            return Result.succeed(this.build(data, line, symbol));
        } catch (Exception e) {
            log.error("getKline error", e);
// k线图获取失败
            throw new YamiShopBindException("Failed to get k-line chart");
        }
    }

    private List<Map<String, Object>> build(List<Kline> data, String line, String symbol) {
        //log.info("------> KlineContrller.build comein ...");
        Set<Long> tsSet = new HashSet<>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Item bySymbol = itemService.findBySymbol(symbol);
        for (int i = 0; i < data.size(); i++) {
            Kline kline = data.get(i);
            Long ts = kline.getTs();
            if (tsSet.contains(ts)) {
                continue;
            } else {
                tsSet.add(ts);
            }

            int decimal = bySymbol.getDecimals();
            double low = kline.getLow();
            double high = kline.getHigh();
            double open = kline.getOpen();
            double close = kline.getClose();
            if ( low == 0) {
                continue;
            }
            if (high == 0) {
                continue;
            }
            if (close == 0) {
                continue;
            }
            if (open == 0) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("line", line);
            map.put("symbol", kline.getSymbol());
            map.put("timestamp", ts);
            map.put("decimals", decimal);
            map.put("ts", ts);
            map.put("current_time", kline.getCurrentTime());
            map.put("open", BigDecimal.valueOf(open).setScale(decimal, RoundingMode.HALF_UP));
            map.put("close", BigDecimal.valueOf(close).setScale(decimal, RoundingMode.HALF_UP));
            map.put("high", BigDecimal.valueOf(high).setScale(decimal, RoundingMode.HALF_UP));
            map.put("low", BigDecimal.valueOf(low).setScale(decimal, RoundingMode.HALF_UP));
            map.put("volume", kline.getVolume());
            list.add(map);
        }
        return list;
    }

}
