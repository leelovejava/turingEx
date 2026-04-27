package com.yami.trading.huobi.data;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.service.data.ItemTypeTimezoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class TimeZoneConverterService {

    @Autowired
    private ItemTypeTimezoneService itemTypeTimezoneService;

    /**
     * Converts a given timestamp to a specified time zone.
     *
     * @param timestamp The timestamp to be converted.
     * @param timeZone  The target time zone (e.g., "Asia/Tokyo", "Asia/Shanghai").
     * @return The formatted date-time string in the target time zone.
     */
    public String convertTimeZone(long timestamp, String timeZone) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(timeZone));
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(zonedDateTime);
    }

    public String convertTimeZone(String timeZone) {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ZonedDateTime zonedTime = now.withZoneSameInstant(ZoneId.of(timeZone));
        return formatter.format(zonedTime);
    }

    /**
     * 币对开盘类型算字符串
     * @param itemCloseType
     * @return
     */
    public String converNowTimeStrByType(String itemCloseType) {
        String timeZomne = getTimeZoneByItemCloseType(itemCloseType);
        return convertTimeZone(timeZomne);
    }

    /**
     * 毫秒和币对开盘类型算字符串
     * @param timestamp
     * @param itemCloseType
     * @return
     */
    public String convertTsStrByType(long timestamp, String itemCloseType) {
        String timeZomne = getTimeZoneByItemCloseType(itemCloseType);
        return convertTimeZone(timestamp, timeZomne);
    }

    public String getTimeZoneByItemCloseType(String itemCloseType){
        Map<String, String> itemTypeTimezoneMap = itemTypeTimezoneService.getItemTypeTimezoneMap();
        return itemTypeTimezoneMap.getOrDefault(itemCloseType, "Asia/Singapore");
    }

    // Example usage
}