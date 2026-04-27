package com.yami.trading.service;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.domain.OpenCloseTime;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.common.util.UTCDateUtils;
import com.yami.trading.service.syspara.SysparaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static com.yami.trading.bean.item.domain.Item.*;


public class MarketOpenChecker {
    private static final Logger logger = LoggerFactory.getLogger(MarketOpenChecker.class);

    /**
     * 美股
     */
    public final static String US_STOCKS = "US-stocks";

    /**
     * 港股
     */
    public final static String HK_STOCKS = "HK-stocks";
    public final static String TW_STOCKS = "TW-stocks";

    /**
     * 港股
     */
    public final static String A_STOCKS = "A-stocks";
    public final static String JP_STOCKS = "JP-stocks";
    public final static String INDIA_STOCKS = "INDIA-stocks";
    /**
     * 英国股票
     */
    public final static String UK_STOCKS = "UK-stocks";

    public final static String STOP_DAY_A_STOCK = "2024-02-09,2024-02-10,2024-02-11,2024-02-12,2024-02-13,2024-02-14,2024-02-15,2024-02-16,2024-02-17,2024-04-04,2024-04-05,2024-04-06,2024-05-01,2024-05-02,2024-05-03,2024-05-04,2024-05-05,2024-06-10,2024-09-15,2024-09-16,2024-09-17,2024-10-01,2024-10-02,2024-10-03,2024-10-04,2024-10-05,2024-10-06,2024-10-07";
    public final static String STOP_DAY_HK_STOCK = "2024-01-01,2024-02-10,2024-02-11,2024-02-12,2024-04-05,2024-05-01,2024-05-12,2024-06-10,2024-07-01,2024-09-21,2024-10-01,2024-10-14,2024-12-25,2024-12-26";
    public final static String STOP_DAY_US_STOCK = "2024-01-01,2024-01-15,2024-02-19,2024-05-27,2024-07-04,2024-09-02,2024-10-14,2024-11-11,2024-11-28,2024-12-25";

    private static Set<String> aStockStopSet = new HashSet<>();
    private static Set<String> hkStockStopSet = new HashSet<>();
    private static Set<String> usStockStopSet = new HashSet<>();
    public final static String DE_STOCKS = "DE-stocks";

    /**
     * 巴西
     */
    public final static String BZ_STOCKS = "BZ-stocks";

    static {
        aStockStopSet.addAll(Splitter.on(",").trimResults().splitToList(STOP_DAY_A_STOCK));
        hkStockStopSet.addAll(Splitter.on(",").trimResults().splitToList(STOP_DAY_HK_STOCK));
        usStockStopSet.addAll(Splitter.on(",").trimResults().splitToList(STOP_DAY_US_STOCK));

    }

    public static void main(String[] args) {
        //       listUsOpenCloseDateTime();
//        System.out.println("A股是否开放：" + isMarketOpen(A_STOCKS));
//        System.out.println("港股是否开放：" + isMarketOpen(HK_STOCKS));
//       System.out.println("美股是否开放：" + isMarketOpen(A_STOCKS));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime nowNewYork = nowUtc.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
        String formattedDate = nowNewYork.format(formatter);

        System.out.println("---> formattedDate11111:" + formattedDate);
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        isMarketOpen(A_STOCKS);

        //        if (nowNewYork.getDayOfWeek().getValue() < 6 &&
//                ((nowNewYork.getHour() == 9 && nowNewYork.getMinute() >= 30) ||
//                        (nowNewYork.getHour() > 9 && nowNewYork.getHour() < 16))) {
//            return true;
//        }
    }

    /**
     * 是否支持盘前盘后交易
     *
     * @return
     */
    public static boolean isUsSupportPreAndAfter() {
        SysparaService bean = ApplicationUtil.getBean(SysparaService.class);
        Syspara usSupprotPreAfter = bean.find("us_supprot_pre_after");
        if (usSupprotPreAfter == null) {
            return false;
        }
        return "1".equals(usSupprotPreAfter.getSvalue());
    }

    /**
     * 判断是否开市
     *
     * @param closeType
     * @return
     */
    public static boolean isMarketOpenByItemCloseType(String closeType) {
        // 放开美股测试
//        if(closeType.equalsIgnoreCase(US_STOCKS)){
//            return true;
//        }
        List<String> stocksType = Lists.newArrayList(A_STOCKS, HK_STOCKS, US_STOCKS, TW_STOCKS, JP_STOCKS, INDIA_STOCKS, UK_STOCKS, DE_STOCKS, BZ_STOCKS);
        if (StrUtil.isBlank(closeType)) {
            return true;
        }
        if (stocksType.contains(closeType)) {
            return isMarketOpen(closeType);
        } else if ("forex".equalsIgnoreCase(closeType)) {
            return UTCDateUtils.isOpen();
        } else {
            return true;
        }
    }

    public static boolean isMarketOpen(String market, int minutes) {
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime nowUtcClose = nowUtc.minusMinutes(minutes);
        ZonedDateTime nowUtcOpen = nowUtc.plusMinutes(minutes);
        if (Item.forex.equalsIgnoreCase(market)) {
            return UTCDateUtils.isOpen(minutes);
        } else if (Item.cryptos.equalsIgnoreCase(market)) {
            return true;
        }
        return isOpen(market, nowUtcClose) || isOpen(market, nowUtcOpen);
    }

    public static boolean isMarketOpen(String market) {
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        return isOpen(market, nowUtc);
    }

    private static boolean isOpen(String market, ZonedDateTime nowUtc) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        switch (market) {
            case A_STOCKS: {
                ZonedDateTime nowShanghai = nowUtc.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
                String formattedDate = nowShanghai.format(formatter);
                //logger.info("---> MarketOpenChecker.isOpen market:{}, dayOfWeek:{}, hour:{}", market, nowShanghai.getDayOfWeek().getValue(), nowShanghai.getHour());
                if (aStockStopSet.contains(formattedDate)) {
                    return false;
                }
                if (nowShanghai.getDayOfWeek().getValue() < 6 &&
                        ((nowShanghai.getHour() == 9 && nowShanghai.getMinute() >= 30) ||
                                (nowShanghai.getHour() == 10) ||
                                (nowShanghai.getHour() == 11 && nowShanghai.getMinute() <= 30) ||
                                (nowShanghai.getHour() >= 13 && nowShanghai.getHour() < 15))) {
                    return true;
                }
                break;
            }
            case HK_STOCKS: {

                ZonedDateTime nowHongKong = nowUtc.withZoneSameInstant(ZoneId.of("Asia/Hong_Kong"));
                String formattedDate = nowHongKong.format(formatter);
                if (hkStockStopSet.contains(formattedDate)) {
                    return false;
                }
                if (nowHongKong.getDayOfWeek().getValue() < 6 &&
                        ((nowHongKong.getHour() == 9 && nowHongKong.getMinute() >= 30) ||
                                (nowHongKong.getHour() > 9 && nowHongKong.getHour() < 12) ||
                                (nowHongKong.getHour() >= 13 && nowHongKong.getHour() < 16))) {
                    return true;
                }
                break;
            }
            case US_STOCKS: {
                ZonedDateTime nowNewYork = nowUtc.withZoneSameInstant(ZoneId.of("America/New_York"));
                String formattedDate = nowNewYork.format(formatter);
                if (usStockStopSet.contains(formattedDate)) {
                    return false;
                }
                if (!isUsSupportPreAndAfter()) {
                    if (nowNewYork.getDayOfWeek().getValue() < 6 &&
                            ((nowNewYork.getHour() == 9 && nowNewYork.getMinute() >= 30) ||
                                    (nowNewYork.getHour() > 9 && nowNewYork.getHour() < 16))) {
                        return true;
                    }
                } else {
                    // 设置美股的交易时间
                    LocalTime preMarketOpen = LocalTime.of(4, 0); // 上午 4:00
                    LocalTime marketClose = LocalTime.of(20, 0); // 下午 8:00

                    // 获取当前时间
                    LocalTime currentTime = nowNewYork.toLocalTime();
                    // 判断当前时间是否在交易时间内
                    return !currentTime.isBefore(preMarketOpen) && currentTime.isBefore(marketClose);
                }

                break;
            }
            case TW_STOCKS: {

                ZonedDateTime nowInShanghai = nowUtc.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
                LocalTime marketOpen = LocalTime.of(9, 0);
                LocalTime marketClose = LocalTime.of(13, 30);
                if (nowInShanghai.getDayOfWeek().getValue() >= 6) {
                    return false;
                }
                return !nowInShanghai.toLocalTime().isBefore(marketOpen) && !nowInShanghai.toLocalTime().isAfter(marketClose);

            }
            case JP_STOCKS: {
                // 将当前时间转换为日本时区
                ZonedDateTime nowInJapan = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));

                // 获取当前是星期几
                DayOfWeek dayOfWeek = nowInJapan.getDayOfWeek();

                // 检查是否为周末（周六或周日）
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    return false;
                }

                // 检查是否为日本公共假日（此处需要更详细的实现）

                // 获取当前时间的小时和分钟
                int hour = nowInJapan.getHour();
                int minute = nowInJapan.getMinute();

                // 检查是否在开盘时间内
                // 上午9:00到上午11:30
                if (hour >= 9 && hour < 11 || (hour == 11 && minute <= 30)) {
                    return true;
                }

                // 下午12:30到下午3:00
                if (hour >= 12 && hour < 15) {
                    return true;
                }

                // 其他时间股市关闭
                return false;
            }
            case INDIA_STOCKS: {
                // 将当前时间转换为日本时区
                ZonedDateTime nowInIndia = nowUtc.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
                LocalTime timeNow = nowInIndia.toLocalTime();

                // 获取当前是星期几
                DayOfWeek dayOfWeek = nowInIndia.getDayOfWeek();

                // 检查是否为周末（周六或周日）
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    return false;
                }

                // 印度股市的开盘和收盘时间
                LocalTime marketOpen = LocalTime.of(9, 15);
                LocalTime marketClose = LocalTime.of(15, 30);

                // 判断当前是否是工作日以及时间是否在开盘时间内
                return timeNow.isAfter(marketOpen) && timeNow.isBefore(marketClose);
            }
            case UK_STOCKS: {
                // 创建伦敦时区的当前时间
                ZonedDateTime nowInLondon = nowUtc.withZoneSameInstant(ZoneId.of("Europe/London"));

                // 英国股市开盘和闭市时间
                LocalTime marketOpen = LocalTime.of(8, 0); // 上午 8:00
                LocalTime marketClose = LocalTime.of(16, 30); // 下午 4:30
                boolean isWeekend = nowInLondon.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        nowInLondon.getDayOfWeek() == DayOfWeek.SUNDAY;
                // 获取当前时间
                LocalTime currentTime = nowInLondon.toLocalTime();

                // 判断当前时间是否在开盘时间内
                return !currentTime.isBefore(marketOpen) && currentTime.isBefore(marketClose) && !isWeekend;
            }
            case DE_STOCKS: {
                // 设置德国（法兰克福）的时区
                // 获取当前的德国时间
                ZonedDateTime nowInFrankfurt = nowUtc.withZoneSameInstant(ZoneId.of("Europe/Berlin"));
                // 德国股市的标准开盘和收盘时间
                LocalTime openingTime = LocalTime.of(9, 0);
                LocalTime closingTime = LocalTime.of(17, 30);

                // 获取当前时间
                LocalTime currentTime = nowInFrankfurt.toLocalTime();
                boolean isWeekend = nowInFrankfurt.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        nowInFrankfurt.getDayOfWeek() == DayOfWeek.SUNDAY;
                // 判断当前时间是否在开盘时间内
                return currentTime.isAfter(openingTime) && currentTime.isBefore(closingTime) && !isWeekend;
            }
            case BZ_STOCKS: {
                // 设置巴西圣保罗的时区
                ZoneId saoPauloZone = ZoneId.of("America/Sao_Paulo");

                // 获取当前的巴西圣保罗时间
                ZonedDateTime nowInSaoPaulo = nowUtc.withZoneSameInstant(saoPauloZone);

                // 巴西股市的标准开盘和收盘时间
                LocalTime openingTime = LocalTime.of(10, 0);
                LocalTime closingTime = LocalTime.of(17, 55);

                // 获取当前时间
                LocalTime currentTime = nowInSaoPaulo.toLocalTime();
                boolean isWeekend = nowInSaoPaulo.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        nowInSaoPaulo.getDayOfWeek() == DayOfWeek.SUNDAY;
                // 判断当前时间是否在开盘时间内
                return currentTime.isAfter(openingTime) && currentTime.isBefore(closingTime) && !isWeekend;
            }
            case UK_METALS: {
                // 设置伦敦金属交易所的开盘和闭市时间
                LocalTime openingTime = LocalTime.of(1, 0); // 01:00 AM London time
                LocalTime closingTime = LocalTime.of(19, 0); // 07:00 PM London time

                // 获取当前伦敦时间
                ZonedDateTime nowInLondon = nowUtc.withZoneSameInstant(ZoneId.of("Europe/London"));
                LocalTime currentTime = nowInLondon.toLocalTime();
                // Check if today is a weekend
                boolean isWeekend = nowInLondon.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        nowInLondon.getDayOfWeek() == DayOfWeek.SUNDAY;
                // 判断当前时间是否在交易时间内
                return currentTime.isAfter(openingTime) && currentTime.isBefore(closingTime) && !isWeekend;
            }
            case CAD_STOCKS: {
                // 设置伦敦金属交易所的开盘和闭市时间
                LocalTime openingTime = LocalTime.of(9, 30); // 09:30 AM Toronto time
                LocalTime closingTime = LocalTime.of(16, 0); // 04:00 PM Toronto time

                // 获取当前多伦多时间
                ZonedDateTime nowInToronto = nowUtc.withZoneSameInstant(ZoneId.of("America/Toronto"));
                LocalTime currentTime = nowInToronto.toLocalTime();
                // Check if today is a weekend
                boolean isWeekend = nowInToronto.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        nowInToronto.getDayOfWeek() == DayOfWeek.SUNDAY;
                // 判断当前时间是否在交易时间内
                return currentTime.isAfter(openingTime) && currentTime.isBefore(closingTime) && !isWeekend;
            }
            case FR_STOCKS: {
                // 设置伦敦金属交易所的开盘和闭市时间
                LocalTime openingTime = LocalTime.of(9, 0); // 09:00 AM Toronto time
                LocalTime closingTime = LocalTime.of(17, 30); // 05:30 PM Toronto time

                // 获取当前巴黎时间
                ZonedDateTime nowInParis = nowUtc.withZoneSameInstant(ZoneId.of("Europe/Paris"));
                LocalTime currentTime = nowInParis.toLocalTime();
                // Check if today is a weekend
                boolean isWeekend = nowInParis.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        nowInParis.getDayOfWeek() == DayOfWeek.SUNDAY;
                // 判断当前时间是否在交易时间内
                return currentTime.isAfter(openingTime) && currentTime.isBefore(closingTime) && !isWeekend;
            }
            case SG_STOCKS: {

                // 获取当前巴黎时间
                ZonedDateTime nowInSg = nowUtc.withZoneSameInstant(ZoneId.of("Asia/Singapore"));
                // Check if today is a weekend
                // 判断是否为周末
                boolean isWeekend = nowInSg.getDayOfWeek() == DayOfWeek.SATURDAY || nowInSg.getDayOfWeek() == DayOfWeek.SUNDAY;
                if (isWeekend) {
                    return false; // 周末股市不开盘
                }
                // 判断当前时间是否在开盘时间范围内
                int hour = nowInSg.getHour();
                boolean isMorningSession = hour >= 9 && hour < 12;
                boolean isAfternoonSession = hour >= 13 && hour < 17;

                return isMorningSession || isAfternoonSession;

            }
            default:
                throw new IllegalArgumentException("无效的市场名称");
        }
        return false;
    }


    public static List<OpenCloseTime> listUsOpenCloseDateTime() {
        int year = 2023;
        List<OpenCloseTime> times = Lists.newArrayList();
        ZoneId nyseZone = ZoneId.of("America/New_York");  // 纽约时区
        ZoneId beijingZone = ZoneId.of("Asia/Shanghai");  // 北京时区

        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 0, 0);

        LocalDateTime currentDateTime = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (!currentDateTime.isAfter(endDate)) {
            OpenCloseTime openCloseTime = new OpenCloseTime();

            ZonedDateTime nowUtc = ZonedDateTime.now(ZoneId.of("UTC"));
            ZonedDateTime nowNewYork = nowUtc.withZoneSameInstant(ZoneId.of("America/New_York"));
            String formattedDate = nowNewYork.format(formatter);
            // 休息日停盘
            if (usStockStopSet.contains(formattedDate)) {
                continue;
            }
            if (currentDateTime.getDayOfWeek() != DayOfWeek.SATURDAY && currentDateTime.getDayOfWeek() != DayOfWeek.SUNDAY) {
                ZonedDateTime nyseOpen = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(9)
                        .withMinute(30)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime nyseClose = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(16)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime beijingOpen = nyseOpen.withZoneSameInstant(beijingZone);
                ZonedDateTime beijingClose = nyseClose.withZoneSameInstant(beijingZone);
                Date openDate = Date.from(beijingOpen.toInstant());
                Date closeDate = Date.from(beijingClose.toInstant());
                openCloseTime.setOpenBjDate(openDate);
                openCloseTime.setCloseBjDate(closeDate);
                openCloseTime.setCloseTs(beijingClose.toEpochSecond() * 1000);
                openCloseTime.setOpenTs(beijingOpen.toEpochSecond() * 1000);
                times.add(openCloseTime);
            }

            currentDateTime = currentDateTime.plusDays(1);
        }
        return times;
    }


    public static List<OpenCloseTime> listHKOpenCloseDateTime() {
        int year = 2023;
        List<OpenCloseTime> times = Lists.newArrayList();
        ZoneId nyseZone = ZoneId.of("Asia/Hong_Kong");  // 纽约时区
        ZoneId beijingZone = ZoneId.of("Asia/Shanghai");  // 北京时区

        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 0, 0);

        LocalDateTime currentDateTime = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (!currentDateTime.isAfter(endDate)) {


            ZonedDateTime nowUtc = ZonedDateTime.now(ZoneId.of("UTC"));
            ZonedDateTime nowNewYork = nowUtc.withZoneSameInstant(ZoneId.of("Asia/Hong_Kong"));
            String formattedDate = nowNewYork.format(formatter);
            // 休息日停盘
            if (hkStockStopSet.contains(formattedDate)) {
                continue;
            }
            if (currentDateTime.getDayOfWeek() != DayOfWeek.SATURDAY && currentDateTime.getDayOfWeek() != DayOfWeek.SUNDAY) {
                OpenCloseTime openCloseTime = new OpenCloseTime();
                ZonedDateTime nyseOpen = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(9)
                        .withMinute(30)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime nyseClose = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(12)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime beijingOpen = nyseOpen.withZoneSameInstant(beijingZone);
                ZonedDateTime beijingClose = nyseClose.withZoneSameInstant(beijingZone);
                Date openDate = Date.from(beijingOpen.toInstant());
                Date closeDate = Date.from(beijingClose.toInstant());
                openCloseTime.setOpenBjDate(openDate);
                openCloseTime.setCloseBjDate(closeDate);
                openCloseTime.setCloseTs(beijingClose.toEpochSecond() * 1000);
                openCloseTime.setOpenTs(beijingOpen.toEpochSecond() * 1000);
                times.add(openCloseTime);

                OpenCloseTime openCloseTime1 = new OpenCloseTime();
                ZonedDateTime nyseOpen1 = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(13)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime nyseClose1 = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(16)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime beijingOpen1 = nyseOpen1.withZoneSameInstant(beijingZone);
                ZonedDateTime beijingClose1 = nyseClose1.withZoneSameInstant(beijingZone);
                Date openDate1 = Date.from(beijingOpen1.toInstant());
                Date closeDate1 = Date.from(beijingClose1.toInstant());
                openCloseTime.setOpenBjDate(openDate1);
                openCloseTime.setCloseBjDate(closeDate1);
                openCloseTime.setCloseTs(beijingClose1.toEpochSecond() * 1000);
                openCloseTime.setOpenTs(beijingOpen1.toEpochSecond() * 1000);
                times.add(openCloseTime1);
            }

            currentDateTime = currentDateTime.plusDays(1);
        }
        return times;
    }


    public static List<OpenCloseTime> listAOpenCloseDateTime() {
        int year = 2023;
        List<OpenCloseTime> times = Lists.newArrayList();
        ZoneId nyseZone = ZoneId.of("Asia/Shanghai");  // 纽约时区
        ZoneId beijingZone = ZoneId.of("Asia/Shanghai");  // 北京时区

        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 0, 0);

        LocalDateTime currentDateTime = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (!currentDateTime.isAfter(endDate)) {


            ZonedDateTime nowUtc = ZonedDateTime.now(ZoneId.of("UTC"));
            ZonedDateTime nowNewYork = nowUtc.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
            String formattedDate = nowNewYork.format(formatter);
            // 休息日停盘
            if (aStockStopSet.contains(formattedDate)) {
                continue;
            }
            if (currentDateTime.getDayOfWeek() != DayOfWeek.SATURDAY && currentDateTime.getDayOfWeek() != DayOfWeek.SUNDAY) {
                OpenCloseTime openCloseTime = new OpenCloseTime();
                ZonedDateTime nyseOpen = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(9)
                        .withMinute(30)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime nyseClose = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(11)
                        .withMinute(30)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime beijingOpen = nyseOpen.withZoneSameInstant(beijingZone);
                ZonedDateTime beijingClose = nyseClose.withZoneSameInstant(beijingZone);
                Date openDate = Date.from(beijingOpen.toInstant());
                Date closeDate = Date.from(beijingClose.toInstant());
                openCloseTime.setOpenBjDate(openDate);
                openCloseTime.setCloseBjDate(closeDate);
                openCloseTime.setCloseTs(beijingClose.toEpochSecond() * 1000);
                openCloseTime.setOpenTs(beijingOpen.toEpochSecond() * 1000);
                times.add(openCloseTime);

                OpenCloseTime openCloseTime1 = new OpenCloseTime();
                ZonedDateTime nyseOpen1 = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(13)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime nyseClose1 = ZonedDateTime.of(currentDateTime, nyseZone)
                        .withHour(15)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

                ZonedDateTime beijingOpen1 = nyseOpen1.withZoneSameInstant(beijingZone);
                ZonedDateTime beijingClose1 = nyseClose1.withZoneSameInstant(beijingZone);
                Date openDate1 = Date.from(beijingOpen1.toInstant());
                Date closeDate1 = Date.from(beijingClose1.toInstant());
                openCloseTime.setOpenBjDate(openDate1);
                openCloseTime.setCloseBjDate(closeDate1);
                openCloseTime.setCloseTs(beijingClose1.toEpochSecond() * 1000);
                openCloseTime.setOpenTs(beijingOpen1.toEpochSecond() * 1000);
                times.add(openCloseTime1);
            }

            currentDateTime = currentDateTime.plusDays(1);
        }
        return times;
    }

}
