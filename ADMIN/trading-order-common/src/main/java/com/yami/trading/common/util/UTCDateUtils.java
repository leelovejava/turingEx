package com.yami.trading.common.util;

import cn.hutool.core.lang.Tuple;
import com.yami.trading.common.util.DateUtil;
import org.springframework.util.Assert;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UTCDateUtils {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    // 协调世界时
    public static final String GMT_TIME_ZONE = "GMT";

    public static final String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // 判断是否休市用 东三区
    public static final String GMT3 = "GMT+3:00";

    public static long calcTimeBetweenInMinute(Date startDate, Date endDate) {
        return calcTimeBetween("m", startDate, endDate);
    }

    public static boolean isOpen() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(GMT3));
        long currentTime = calendar.getTime().getTime();
        if (currentTime < UTCDateUtils.getClosedTime() && currentTime >= UTCDateUtils.getOpenTime()) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isOpen(int minutes) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(GMT3));
        long currentTime = calendar.getTime().getTime();
        int milliseconds = 1000 * 60 * minutes;
        if (currentTime - milliseconds < UTCDateUtils.getClosedTime() && currentTime + milliseconds >= UTCDateUtils.getOpenTime()) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        boolean open = isOpen(30);
    }


    public static long getOpenTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(GMT3));
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime().getTime();
    }

    public static long getClosedTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(GMT3));
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        return calendar.getTime().getTime();
    }

    /**
     * 获取当前时间所在的周的第一天（周一为第一天）
     */
    public static String getFirstDateOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int index = calendar.get(Calendar.DAY_OF_WEEK);
        index = (index == 1) ? (index + 7) : index;
        date = DateUtil.dateBeforeNDate(date, index - 2, "yyyy-MM-dd");
        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return f.format(date);
    }

    public static String addYear(Date date, int addYears) {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, addYears);
        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        f.setTimeZone(TimeZone.getTimeZone(GMT_TIME_ZONE));
        return f.format(calendar.getTime());
    }

    public static String addMonth(Date oldDate, int addMonths) {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.setTime(oldDate);
        calendar.add(Calendar.MONTH, addMonths);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(GMT_TIME_ZONE));
        return f.format(calendar.getTime());
    }

    public static String addDay(Date oldDate, int addDays) {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.setTime(oldDate);
        calendar.add(Calendar.DATE, addDays);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(GMT_TIME_ZONE));
        return f.format(calendar.getTime());
    }

    public static String addHour(Date oldDate, int addHours) {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.setTime(oldDate);
        calendar.add(Calendar.HOUR, addHours);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(GMT_TIME_ZONE));
        return f.format(calendar.getTime());
    }

    public static String addMinute(Date oldDate, int addMinutes) {
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.setTime(oldDate);
        calendar.add(Calendar.MINUTE, addMinutes);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        f.setTimeZone(TimeZone.getTimeZone(GMT_TIME_ZONE));
        return f.format(calendar.getTime());
    }

    public static Date toDate(String s) {
        return toDate(s, DEFAULT_DATE_FORMAT);
    }

    // 时区问题 CASTER TODO
    public static Date toDate(String string, String pattern) {
        return toDate(string, pattern, TimeZone.getTimeZone(GMT_TIME_ZONE));
    }

    public static Date toDate(String string, String pattern, TimeZone timeZone) {
        try {
            SimpleDateFormat sdf = (SimpleDateFormat) createDateFormat(pattern, timeZone);
            return sdf.parse(string);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static DateFormat createDateFormat(String pattern, TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        TimeZone gmt = timeZone;
        sdf.setTimeZone(gmt);
        sdf.setLenient(true);
        return sdf;
    }

    public static long calcTimeBetween(String unitType, Date startDate, Date endDate) {
        Assert.hasText(unitType);
        Assert.notNull(startDate);
        Assert.notNull(endDate);
        long between = endDate.getTime() - startDate.getTime();
        if (unitType.equals("ms")) {
            return between;
        } else if (unitType.equals("s")) {
            return between / 1000;// 返回秒
        } else if (unitType.equals("m")) {
            return between / 60000;// 返回分钟
        } else if (unitType.equals("h")) {
            return between / 3600000;// 返回小时
        } else if (unitType.equals("d")) {
            return between / 86400000;// 返回天数
        } else {
            throw new IllegalArgumentException("the unitType is unknown");
        }
    }


    public static Tuple getStartAndEnd(Integer period) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = LocalDateTime.now();
        switch (period) {
            case 1:
                startDate = endDate.with(LocalTime.MIN);
                break;
            case 2:
                endDate = endDate.with(LocalTime.MIN).minusSeconds(1);
                startDate = endDate.with(LocalTime.MIN);
                break;
            case 3:
                startDate = endDate.minusDays(6).with(LocalTime.MIN);
                break;
            case 4:
                startDate = endDate.minusDays(14).with(LocalTime.MIN);
                break;
            case 5:
                startDate = endDate.minusDays(29).with(LocalTime.MIN);
                break;
            default:
                System.out.println("Invalid period input");
        }
        long startTimestamp = startDate.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long endTimestamp = endDate.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return new Tuple(startTimestamp, endTimestamp);
    }

    public static Date toDate(LocalDateTime dateTime) {
        //将LocalDateTime对象转换为Date对象
        // LocalDateTime dateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        Instant instant2 = zonedDateTime.toInstant();
        Date date2 = Date.from(instant2);
        return date2;
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        //将Date对象转换为LocalDateTime
        // Date date = new Date();
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime;
    }

    public static boolean isTimestampFromToday(long timestamp, ZoneId zoneId) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDate timestampDate = instant.atZone(zoneId).toLocalDate();
        LocalDate today = LocalDate.now();

        return timestampDate.isEqual(today);
    }

}
