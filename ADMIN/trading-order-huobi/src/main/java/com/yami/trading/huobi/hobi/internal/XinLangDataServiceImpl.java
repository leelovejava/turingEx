package com.yami.trading.huobi.hobi.internal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.huobi.data.internal.KlineService;
import com.yami.trading.huobi.hobi.http.HttpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class XinLangDataServiceImpl {

    public static final String KLINE_URL = "https://vip.stock.finance.sina.com.cn/forex/api/jsonp.php/var%20_fx_s{1}_1_{3}=/NewForexService.getMinKline?symbol=fx_s{1}&scale={2}&datalen=2000";
    public static final String KLINE_DAY_URL = "https://vip.stock.finance.sina.com.cn/forex/api/jsonp.php/var%20_fx_s{1}{2}=/NewForexService.getDayKLine?symbol=fx_s{1}&_={2}";

    @Autowired
    private KlineService klineService;

    @Autowired
    private FreeForexRateService freeForexRateService;

    public List<Realtime> realtimeSingle(String symbols) {
        return freeForexRateService.fetchRealtime(symbols);
    }

    public Map<String, List<Kline>> getDailyWeekMonthHistory(String symbol) {
        Map<String, List<Kline>> map = new HashMap<>();
        List<Kline> oneDayPeriod = buildOneDayPeriod(symbol);
        map.put(Kline.PERIOD_1WEEK, klineService.calculateKline(symbol, 7, Kline.PERIOD_1WEEK, oneDayPeriod));
        map.put(Kline.PERIOD_1MON, klineService.calculateKline(symbol, 30, Kline.PERIOD_1MON, oneDayPeriod));
        map.put(Kline.PERIOD_1DAY, oneDayPeriod);
        map.put(Kline.PERIOD_5DAY, klineService.calculateKline(symbol, 5, Kline.PERIOD_5DAY, oneDayPeriod));
        map.put(Kline.PERIOD_QUARTER, klineService.calculateKline(symbol, 90, Kline.PERIOD_QUARTER, oneDayPeriod));
        map.put(Kline.PERIOD_YEAR, klineService.calculateKline(symbol, 365, Kline.PERIOD_YEAR, oneDayPeriod));
        return map;
    }

    public List<Kline> buildOneDayPeriod(String currency) {
        return getTimeseriesByPeriodOneDay(currency);
    }

    public List<Kline> getTimeseriesForFourHourly(String currency) {
        return getTimeseriesByPeriodMinute(currency, 240, Kline.PERIOD_4HOUR);
    }

    public List<Kline> getTimeseriesForTwoHourly(String currency) {
        return getTimeseriesByPeriodMinute(currency, 120, Kline.PERIOD_2HOUR);
    }

    public List<Kline> getTimeseriesForOneHourly(String currency) {
        return getTimeseriesByPeriodMinute(currency, 60, Kline.PERIOD_60MIN);
    }

    public List<Kline> getTimeseriesOneMinute(String currency) {
        return getTimeseriesByPeriodMinute(currency, 1, Kline.PERIOD_1MIN);
    }

    public List<Kline> getTimeseriesFiveMinute(String currency) {
        return getTimeseriesByPeriodMinute(currency, 5, Kline.PERIOD_5MIN);
    }

    public List<Kline> getTimeseriesFifteenMinute(String currency) {
        return getTimeseriesByPeriodMinute(currency, 15, Kline.PERIOD_15MIN);
    }

    public List<Kline> getTimeseriesThirtyMinute(String currency) {
        return getTimeseriesByPeriodMinute(currency, 30, Kline.PERIOD_30MIN);
    }

    @Retryable(value = RuntimeException.class, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public List<Kline> getTimeseriesByPeriodMinute(String currency, int scale, String period) {
        long nowTs = System.currentTimeMillis();
        String url = KLINE_URL.replace("{1}", currency.toLowerCase())
                .replace("{3}", String.valueOf(nowTs))
                .replace("{2}", String.valueOf(scale));

        String json = HttpHelper.sendGetHttp(url, "");
        if (!json.contains("(")) {
            throw new IllegalArgumentException("forex minute kline parse error");
        }
        json = json.split("\\(")[1].replaceAll("\\);$", "").replaceAll("]);$", "").replaceAll("]\\);$", "");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JSONArray dataArray = JSONObject.parseArray(json);

        List<Kline> resList = new ArrayList<>();
        for (Object result : dataArray) {
            JSONObject data = (JSONObject) result;
            Kline kline = new Kline();
            kline.setSymbol(currency);
            kline.setPeriod(period);

            String dateTimeStr = data.getString("d");
            ZonedDateTime dateTime = ZonedDateTime.parse(dateTimeStr, formatter.withZone(ZoneId.of("Asia/Shanghai")));
            long ts = dateTime.toInstant().toEpochMilli();

            kline.setTs(ts);
            kline.setOpen(data.getDouble("o"));
            kline.setClose(data.getDouble("c"));
            kline.setHigh(data.getDouble("h"));
            kline.setLow(data.getDouble("l"));
            kline.setVolume(0);
            kline.setAmount(0);
            resList.add(kline);
        }
        return resList;
    }

    @Retryable(value = RuntimeException.class, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public List<Kline> getTimeseriesByPeriodOneDay(String currency) {
        LocalDate today = LocalDate.now();
        String dateStr = today.getYear() + "_" + today.getMonthValue() + "_" + today.getDayOfMonth();
        String url = KLINE_DAY_URL.replace("{1}", currency.toLowerCase()).replace("{2}", dateStr);

        String json = HttpHelper.sendGetHttp(url, "");
        if (!json.contains("(")) {
            throw new IllegalArgumentException("forex day kline parse error");
        }
        json = json.split("\\(")[1].replace(");", "").replace("\"", "");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[] datas = json.split("\\|");
        List<Kline> resList = new ArrayList<>();

        for (String result : datas) {
            List<String> ds = Splitter.on(",").omitEmptyStrings().splitToList(result);
            if (ds.size() < 5) {
                continue;
            }
            Kline kline = new Kline();
            kline.setSymbol(currency);
            kline.setPeriod(Kline.PERIOD_1DAY);

            LocalDate date = LocalDate.parse(ds.get(0), dateFormatter);
            long ts = date.atStartOfDay(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();

            kline.setTs(ts);
            kline.setOpen(Double.parseDouble(ds.get(1)));
            kline.setClose(Double.parseDouble(ds.get(4)));
            kline.setHigh(Double.parseDouble(ds.get(3)));
            kline.setLow(Double.parseDouble(ds.get(2)));
            kline.setVolume(0);
            kline.setAmount(0);
            resList.add(kline);
        }
        return resList;
    }
}
