package com.yami.trading.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * 日期工具类
 *
 * 参考资料：
 *     https://blog.csdn.net/shuair/article/details/126694562
 *     https://www.cnblogs.com/convict/p/16180509.html
 *
 *  时区对照表：
 *     http://t.zoukankan.com/kakaisgood-p-12523507.html
 *     https://blog.csdn.net/LordForce/article/details/132101076
 */
public class DateTimeTools {
	private final static Logger logger = LoggerFactory.getLogger(DateTimeTools.class);

	public static final String DEFAULT_DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// 处理类似格式：2022-07-08T04:02:43.85
	public static final String DATA_TIME_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	// 带时区格式
	public static final String DATA_TIME_FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ssZ";

	public static final String DAY_FORMAT = "yyyy-MM-dd";

	// 提前缓存各个时区的标记，以及各个时区和 UTC 时区的时差
	public static TimeZone utcZone = TimeZone.getTimeZone("UTC");

	/**
	 * 基于系统时区，将指定 date 类型对象转换为 LocalDateTime 对象
	 *
	 * @param date
	 * @return
	 */
	public static LocalDateTime date2LocalDateTime(Date date) {
		if (date == null) {
			return null;
		}

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();

		// 方式1
		ZonedDateTime zonedDateTime = date.toInstant().atZone(zoneId);
		return zonedDateTime.toLocalDateTime();

//		// 方式2
//		return LocalDateTime.ofInstant(date.toInstant(), zoneId);
	}

	public static LocalDate date2LocalDate(Date date) {
		if (date == null) {
			return null;
		}

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();

		// 方式1
		ZonedDateTime zonedDateTime = date.toInstant().atZone(zoneId);
		return zonedDateTime.toLocalDate();

//		// 方式2
//		LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date.toInstant(), zoneId);
//		return localDateTime2.toLocalDate();
	}

	public static LocalTime date2LocalTime(Date date) {
		if (date == null) {
			return null;
		}

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();

		// 方式1
		ZonedDateTime zonedDateTime = date.toInstant().atZone(zoneId);
		return zonedDateTime.toLocalTime();

		// 方式2
//		LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date.toInstant(), zoneId);
//		return localDateTime2.toLocalTime();
	}

	/**
	 * 将当前系统时区的时间转换成 Date 类型对象；
	 * 注意：参数 time 关联的是当前系统时区的时间
	 *
	 * @param time
	 * @return
	 */
	public static Date localDateTime2Date(LocalDateTime time) {
		if (time == null) {
			return null;
		}

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();

		Instant instant = time.atZone(zoneId).toInstant();
		return Date.from(instant);
	}

	public static Date localDate2Date(LocalDate time) {
		if (time == null) {
			return null;
		}
		// 由于`LocalDate`不带有时间信息，所以必须设置时间，才能转 Date

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();
		Instant instant;
		Date date;

		// 方式1：atStartOfDay，自动赋予午夜时间，返回 LocalDateTime，设置时区返回 ZonedDateTime，进而得到 Instant
		instant = time.atStartOfDay().atZone(zoneId).toInstant();
		date = Date.from(instant);
		System.out.println(date);

		return date;

//		// 方式2
//		instant = time.atStartOfDay(zoneId).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式3：atStartOfDay 的底层实现
//		instant = time.atTime(LocalTime.MIDNIGHT).atZone(zoneId).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式4
//		instant = LocalDateTime.of(time, LocalTime.MIDNIGHT).atZone(zoneId).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式5：通过 Timestamp 得到 Instant
//		instant = Timestamp.valueOf(time.atTime(LocalTime.MIDNIGHT)).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式6
//		instant = Timestamp.valueOf(LocalDateTime.of(time, LocalTime.MIDNIGHT)).toInstant();
//		date = Date.from(instant);
//		System.out.println(date);
//
//		// 方式7：通过毫秒数初始化 Date 对象
//		Timestamp timestamp = Timestamp.valueOf(time.atTime(LocalTime.MIDNIGHT));
//		date = new Date(timestamp.getTime());
//		System.out.println(date);
	}

	public static Date localTime2Date(LocalTime time) {
		if (time == null) {
			return null;
		}

		// 由于 LocalTime 不带有日期信息，所以必须设置日期，才能转 Date
		LocalDate nowDate = LocalDate.now();

		// 时区
		ZoneId zoneId = ZoneId.systemDefault();
		Instant instant;
		Date date;

		instant = LocalDateTime.of(nowDate, time).atZone(zoneId).toInstant();
		date = Date.from(instant);

		instant = Timestamp.valueOf(LocalDateTime.of(nowDate, time)).toInstant();
		date = Date.from(instant);

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(nowDate, time));
		date = new Date(timestamp.getTime());

		return date;
	}

	public static Date getDateFromMs(long ms) {
		return new Date(ms);
	}

	public static LocalDate getLocalDateFromMs(long ms) {
		Date date = getDateFromMs(ms);
		return date2LocalDate(date);
	}

	public static LocalTime getLocalTimeFromMs(long ms) {
		Date date = getDateFromMs(ms);
		return date2LocalTime(date);
	}

	public static LocalDateTime getLocalDateTimeFromMs(long ms) {
		Date date = getDateFromMs(ms);
		return date2LocalDateTime(date);
	}

	/**
	 * 获取当前天
	 *
	 * @return String
	 */
	public static String getCurDayStr() {
		return new SimpleDateFormat(DAY_FORMAT).format(new Date());
	}

	public static String getDayStr(Date time) {
		if (time == null) {
			return null;
		}

		return new SimpleDateFormat(DAY_FORMAT).format(time);
	}

	/**
	 * 获取前一天
	 * 注意时区的影响！！！
	 *
	 * @return Date
	 */
	public static Date getPreviousDate() {
		Calendar begin = Calendar.getInstance();
		begin.set(Calendar.DAY_OF_MONTH, begin.get(Calendar.DAY_OF_MONTH) - 1);
		begin.set(Calendar.HOUR_OF_DAY, 0);
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		return begin.getTime();
	}

	/**
	 * 获取下N天
	 * 注意时区的影响！！！
	 *
	 * @param date
	 *            当前时间
	 * @param n
	 *            下N天
	 * @return Date 下N天日期
	 */
	public static Date getNextDate(Date date, int n) {
		if (date == null) {
			return null;
		}

		Calendar begin = Calendar.getInstance();
		begin.setTime(date);
		begin.set(Calendar.DAY_OF_MONTH, begin.get(Calendar.DAY_OF_MONTH) + n);
		begin.set(Calendar.HOUR_OF_DAY, 0);
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		begin.set(Calendar.MILLISECOND, 0);
		return begin.getTime();
	}

	/**
	 * 添加秒数
	 * 注意时区的影响！！！
	 *
	 * @return Date
	 */
	public static Date addSecond(Long date, int second) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		cal.add(Calendar.SECOND, second);
		return cal.getTime();
	}

	/**
	 * 添加小时
	 * 注意时区的影响！！！
	 *
	 * @param date
	 * @param hour
	 * @return Date
	 */
	public static Date addHour(Long date, int hour) {
		if (date == null || date <= 0) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		return cal.getTime();
	}

	/**
	 * 添加小时
	 * 注意时区的影响！！！
	 *
	 * @param date
	 * @param hour
	 * @return Date
	 */
	public static Date addHour(Date date, int hour) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);;
		cal.add(Calendar.HOUR_OF_DAY, hour);
		return cal.getTime();
	}

	/**
	 * 注意时区的影响！！！
	 *
	 * @param date
	 * @param minutes
	 * @return
	 */
	public static Date addMinutes(Date date, int minutes) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	/**
	 * 添加秒数
	 * 注意时区的影响！！！
	 *
	 * @return Date
	 */
	public static Date addSecond(Date date, int second) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, second);
		return cal.getTime();
	}

	/**
	 * 添加天数
	 * 注意时区的影响！！！
	 *
	 * @param date
	 * @param day
	 * @return Date
	 */
	public static Date addDay(Date date, int day) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, day);
		return cal.getTime();
	}

	/**
	 * 两个时间之间相差距离多少天
	 *
	 * @param starttime 时间参数 1：
	 * @param endtime 时间参数 2：
	 * @return 相差天数
	 */
	public static long getDistanceDays(String starttime, String endtime) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date one;
		Date two;
		long days = 0;
		try {
			one = df.parse(starttime);
			two = df.parse(endtime);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff ;
			if (time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			days = diff / (1000 * 60 * 60 * 24);
		} catch (ParseException e) {
			logger.error("计算两个时间:{} - {} 的天数差距报错", starttime, endtime, e);
		}

		return days;//返回相差多少天
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * @param starttime 时间参数 1 格式：1990-01-01 12:00:00
	 * @param endtime 时间参数 2 格式：2009-01-01 12:00:00
	 * @return long[] 返回值为：{天, 时, 分, 秒}
	 */
	public static long[] getDistanceTimes(String starttime, String endtime) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(starttime);
			two = df.parse(endtime);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff ;
			if (time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff/1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		} catch (ParseException e) {
			logger.error("计算两个时间:{} - {} 的天数差距报错", starttime, endtime, e);
			return null;
		}

		long[] times = {day, hour, min, sec};
		return times;
	}

	/**
	 * 注意时区的影响！！！
	 *
	 * @param ms
	 * @return
	 */
	public static Date fromMilliseconds(long ms) {
		if (ms <= 0L) {
			throw new RuntimeException("错误的参数");
		}

		return new Date(ms);
	}

	public static String formatDateTime(Date date) {
		return DateUtil.formatDateTime(date);
	}

	public static String formatDateTime(LocalTime localTime) {
		if (localTime == null) {
			return null;
		}

		Date date = localTime2Date(localTime);
		return DateUtil.formatDateTime(date);
	}

	public static String formatDateTime(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		Date date = localDateTime2Date(localDateTime);
		return DateUtil.formatDateTime(date);
	}

	public static String formatDateTime(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}

		Date date = localDate2Date(localDate);
		return DateUtil.formatDateTime(date);
	}

	public static TimeZone getLocalTimeZone() {
		// 香港是上海时区，该时区和北京时区没有时差
		// utc 标准时区比北京时区晚 8 个小时，就是说北京上午 8 点，UTC 0点
		Calendar ca = Calendar.getInstance();
		TimeZone tz = ca.getTimeZone();// 等效：TimeZone.getDefault();

		logger.info("当前时区:{}", tz.getID());

		return tz;
	}

	public static ZoneId getLocalZoneId() {
		return TimeZone.getDefault().toZoneId();
	}

	/**
	 * 返回美东时区 zone 对象
	 *
	 * @return
	 */
	public static ZoneId getMDZone() {
		return ZoneId.of("Etc/GMT+4");
		//return ZoneId.of("America/New_York");
	}

	/**
	 * 将参数 time 对应的时区时间认为是 fromZone 参数对应的时区时间，转成 toZone 时区对应的时间。
	 * 注意：如果原始时间 time 使用的时区和参数 fromZone 的不一致，则换算结果是不合预期的！！！！
	 *      例如：如果 time 是基于中欧标准时间（比北京时间晚 6 个小时）得到的值， fromZone 填写的是
	 *           北京时区， toZone 是美东时区，则得到的结果将比北京时间晚 18 个小时的时间，而非预期的
	 *           比中欧时间晚 6 个小时的时间（美东时区比北京时区晚 12 个小时）
	 *
	 * 常识：北京时区时间比 UTC 时间早 8 个小时，北京时区时间比中欧时区时间早 6 个小时，北京时区时间比美东时区时间早 12 个小时
	 *
	 * @param time
	 * @param fromZone
	 * @param toZone
	 * @return
	 */
	public static LocalDateTime convertZoneTime(final LocalDateTime time, final ZoneId fromZone, final ZoneId toZone) {
		final ZonedDateTime zonedtime = time.atZone(fromZone);
		final ZonedDateTime converted = zonedtime.withZoneSameInstant(toZone);
		return converted.toLocalDateTime();
	}

	/**
	 * 将当前时区的时间转换为指定时区的时间
	 *
	 * @param time
	 * @param toZone
	 * @return
	 */
	public static LocalDateTime convertZoneTime(final LocalDateTime time, final ZoneId toZone) {
		return convertZoneTime(time, ZoneId.systemDefault(), toZone);
	}

	/**
	 * 将指定时区的时间转换为 UTC 时间；
	 * 注意： time 对象对应的时区需要和参数 fromZone 的保持一致，否则结果会不如预期！！！！
	 *
	 * @param time
	 * @param fromZone
	 * @return
	 */
	public static LocalDateTime toUtc(final LocalDateTime time, final ZoneId fromZone) {
		return convertZoneTime(time, fromZone, ZoneOffset.UTC);
	}

	/**
	 * 将当前时区产生的时间转换为 UTC 时间
	 *
	 * @param time
	 * @return
	 */
	public static LocalDateTime toUtc(final LocalDateTime time) {
		return toUtc(time, ZoneId.systemDefault());
	}

	public static String standardTimeFormat(Date time) {
		return standardTimeFormat(time, null);
	}

	/**
	 * 提取当前系统时区所在的时间 time，对应于指定时区 fromZone 的时间字符串
	 *
	 * @param time
	 * @param fromZone
	 * @return
	 */
	public static String standardTimeFormat(Date time, ZoneId fromZone) {
		if (time == null) {
			return null;
		}
		if (fromZone == null) {
			fromZone = ZoneId.systemDefault();
		}

		SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATA_TIME_FORMAT);
		TimeZone formatTimeZone = TimeZone.getTimeZone(fromZone);
		formatter.setTimeZone(formatTimeZone);
		return formatter.format(time);
	}

	/**
	 * 基于指定时区，来解析传入的时间，返回的是当前系统时区的时间
	 *
	 * 注意：
	 *   本方法返回的是 --- 基于 fromZone 时区来解析字符串时间，返回基于同样的时间戳下，对应的当前系统时区时间；
	 *   务必清楚，此处返回的不是系统时区下减去时间偏移量后计算的 fromZone 时间值。
	 *
	 *   例如：如果传入的 timeStr = 2023-11-08 12:00:00，当前系统时区是 Etc/GMT-8，传入的 fromZone = Etc/GMT+4;
	 *   那么调用本方法返回的 date 对象使用当前系统时区的时间处理工具打印出来的值是： 2023-11-09 00:00:00
	 *
	 * @param timeStr
	 * @param fromZone
	 * @return
	 */
	public static Date parseWithTimeZone(String timeStr, ZoneId fromZone) {
		if (StrUtil.isBlank(timeStr)) {
			return null;
		}
		if (fromZone == null) {
			fromZone = ZoneId.systemDefault();
		}

		timeStr = normalize(timeStr);

		SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATA_TIME_FORMAT);
		TimeZone formatTimeZone = TimeZone.getTimeZone(fromZone);
		formatter.setTimeZone(formatTimeZone);
		try {
			return formatter.parse(timeStr);
		} catch (Exception e) {
			throw new RuntimeException("日期转换报错");
		}
	}

	/**
	 * 基于指定时区，来解析传入的时间，返回的是当前系统时区的时间
	 *
	 * @param time
	 * @param fromZone
	 * @return
	 */
	public static Date parseWithTimeZone(Date time, ZoneId fromZone) {
		LocalDateTime fromLocalTime = DateTimeTools.date2LocalDateTime(time);

		Instant instant = fromLocalTime.atZone(fromZone).toInstant();
		Date targetZoneDate = Date.from(instant);

		return targetZoneDate;
	}

	public static String normalize(String dateStr) {
		if (StrUtil.isBlank(dateStr)) {
			return StrUtil.str(dateStr);
		}

		// 日期时间分开处理
		final List<String> dateAndTime = StrUtil.splitTrim(dateStr, ' ');
		final int size = dateAndTime.size();
		if (size < 1 || size > 2) {
			// 非可被标准处理的格式
			return StrUtil.str(dateStr);
		}

		dateStr = dateStr.replace("T", " ");
		final StringBuilder builder = StrUtil.builder();

		// 日期部分（"\"、"/"、"."、"年"、"月"都替换为"-"）
		String datePart = dateAndTime.get(0).replaceAll("[/.年月]", "-");
		datePart = StrUtil.removeSuffix(datePart, "日");
		builder.append(datePart);

		// 时间部分
		if (size == 2) {
			builder.append(' ');
			String timePart = dateAndTime.get(1).replaceAll("[时分秒]", ":");
			timePart = StrUtil.removeSuffix(timePart, ":");
			//将ISO8601中的逗号替换为.
			timePart = timePart.replace(',', '.');
			builder.append(timePart);
		}

		return builder.toString();
	}

//	/**
//	 * 将当前时区时间转换成 UTC 时区时间
//	 * 注意：此处 Date 对象对应的时区都是系统默认时区
//	 *
//	 * @param time
//	 * @return
//	 */
//	public static Date toUtc(Date time) {
//		if (time == null) {
//			return null;
//		}
//
//		String localTimeZoneId = TimeZone.getDefault().getID();
//		long timeDiff = timeZoneDiffMap.get(localTimeZoneId);
//		return (new Date(time.getTime() - timeDiff * 1000L));
//	}
//
//	public static Date toUtc(Date time, ZoneId fromZone) {
//		if (time == null) {
//			return null;
//		}
//		if (fromZone == null) {
//			fromZone = ZoneId.systemDefault();
//		}
//
//		// 提取指定时区和 UTC 的时差
//		long timeDiff = timeZoneDiffMap.get(fromZone.getId());
//		// 对应时区的时间毫秒值减去时差，就是 UTC 时间
//		return (new Date(time.getTime() - timeDiff * 1000L));
//	}
//
//	/**
//	 *
//	 * @Description: 基于零时区位置的展示时间，计算本地时间；
//	 *               注意：基于返回值算出的时间毫秒值，算是标准时间毫秒值
//	 *
//	 * @time 2020年7月31日 下午9:38:08
//	 * @author daydayup
//	 * @param utcTime
//	 * @return
//	 * Date
//	 * @throws
//	 */
//	public static Date fromUtc(Date utcTime) {
//		if (utcTime == null) {
//			return null;
//		}
//
//		String localTimeZoneId = TimeZone.getDefault().getID();
//		long timeDiff = timeZoneDiffMap.get(localTimeZoneId);
//		return (new Date(utcTime.getTime() + timeDiff * 1000L));
//	}
//
//	public static Date fromUtc(Date utcTime, ZoneId toZone) {
//		if (utcTime == null) {
//			return null;
//		}
//		if (toZone == null) {
//			toZone = ZoneId.systemDefault();
//		}
//
//		long timeDiff = timeZoneDiffMap.get(toZone.getId());
//		return (new Date(utcTime.getTime() + timeDiff * 1000L));
//	}

	/**
	 * 基于当前系统时区指定的时间，计算目标时区的时间
	 *
	 * @param fromTime : 系统时区的时间
	 * @param targetZone : 转换成目标时区
	 * @return
	 */
	public static Date convertZoneTime(Date fromTime, ZoneId targetZone) {
		return convertZoneTime(fromTime, ZoneId.systemDefault(), targetZone);
	}

	public static Date convertZoneTime(long fromTime, ZoneId targetZone) {
		if (fromTime <= 0) {
			return null;
		}
		String checkTimeStr = String.valueOf(fromTime);
		if (checkTimeStr.startsWith("1")) {
			if (checkTimeStr.length() == 10) {
				// 1000000000（秒） 对应 2001-09-09 09:46:40，识别为基于秒的时间戳
				checkTimeStr = checkTimeStr + "000";
			}
		} else {
			// 首位数大于 1，要么是 9几年的秒时间，要么是 9 几年的毫秒时间，基于秒的有 9 位数
			if (checkTimeStr.length() <= 9) {
				// 基于秒的时间戳
				checkTimeStr = checkTimeStr + "000";
			}
		}

		fromTime = Long.parseLong(checkTimeStr);
		Date dateTime = new Date(fromTime);

		return convertZoneTime(dateTime, targetZone);
	}

	/**
	 * 基于时区 fromZone 的时间 timeStr 解析成时区 toZone 的时间
	 * 注意：返回的是基于 toZone 时区的字面时间，不是系统时区的时间
	 *
	 * @param timeStr
	 * @param fromZone
	 * @param toZone
	 * @return
	 */
	public static Date convertZoneTime(String timeStr, ZoneId fromZone, ZoneId toZone) {
		// 注意：返回的 oriTime 是系统时区的时间值
		Date oriTime = parseWithTimeZone(timeStr, fromZone);

		// 注意写法！
		return convertZoneTime(oriTime, ZoneId.systemDefault(), toZone);
	}

	/**
	 * 基于时区 fromZone 的时间 time 解析成时区 toZone 的时间
	 *
	 * @param time
	 * @param fromZone
	 * @param toZone
	 * @return
	 */
	public static Date convertZoneTime(Date time, ZoneId fromZone, ZoneId toZone) {
		if (time == null) {
			return null;
		}

		// toZone 时区相对于 fromZone 时区的时间偏移
		long timeDiff = getTimeOffset(fromZone, toZone);
		return (new Date(time.getTime() + timeDiff));
	}

	/**
	 * 将系统时区时间转换为对外展示配置的时区时间
	 *
	 * @param localZoneTime
	 * @return
	 */
	public static Date transferToShowTime(Date localZoneTime) {
		if (localZoneTime == null) {
			return null;
		}

		String outPutTimeZoneId = TimeZoneContext.showTimeZoneId.get();
		if (StrUtil.isBlank(outPutTimeZoneId)) {
			throw new RuntimeException("错误的使用方式");
		}

		Date retDate = convertZoneTime(localZoneTime, ZoneId.of(outPutTimeZoneId));
		return retDate;
	}

	/**
	 * 具体业务中使用的方法
	 *
	 * @param fromTime
	 * @return
	 */
	public static Date transferToShowTime(Long fromTime) {
		if (fromTime == null || fromTime <= 0) {
			return null;
		}
		String checkTimeStr = String.valueOf(fromTime);
		if (checkTimeStr.startsWith("1")) {
			if (checkTimeStr.length() == 10) {
				// 1000000000（秒） 对应 2001-09-09 09:46:40，识别为基于秒的时间戳
				checkTimeStr = checkTimeStr + "000";
			}
		} else {
			// 首位数大于 1，要么是 9几年的秒时间，要么是 9 几年的毫秒时间，基于秒的有 9 位数
			if (checkTimeStr.length() <= 9) {
				// 基于秒的时间戳
				checkTimeStr = checkTimeStr + "000";
			}
		}

		fromTime = Long.parseLong(checkTimeStr);
		Date dateTime = new Date(fromTime);

		return transferToShowTime(dateTime);
	}

	/**
	 * 注意：本方法用于将当前系统时区时间戳，参考预期的展示时间，转换为客户端时区能正确解析的时间戳。
	 * 此处涉及多个时区偏移换算问题，其他业务在不清楚本方法功能时不要随意使用！！！
	 * 本方法目前专用于将业务 API 返回给前端的时间戳值做加工，返回给前端解析时间。
	 *
	 * @param localTimestamp
	 * @return
	 */
	public static long transferShowTimeToClientTime(Long localTimestamp) {
		if (localTimestamp == null || localTimestamp <= 0) {
			return 0L;
		}

		// 时间戳类型： 1-毫秒时间戳， 2-秒时间戳
		int timestampType = 1;
		String checkTimeStr = String.valueOf(localTimestamp);
		if (checkTimeStr.startsWith("1")) {
			if (checkTimeStr.length() == 10) {
				// 1000000000（秒） 对应 2001-09-09 09:46:40，识别为基于秒的时间戳
				checkTimeStr = checkTimeStr + "000";
				timestampType = 2;
			}
		} else {
			// 首位数大于 1，要么是 9几年的秒时间，要么是 9 几年的毫秒时间，基于秒的有 9 位数
			if (checkTimeStr.length() <= 9) {
				// 基于秒的时间戳
				checkTimeStr = checkTimeStr + "000";
				timestampType = 2;
			}
		}

		localTimestamp = Long.parseLong(checkTimeStr);

		String showTimeZoneId = TimeZoneContext.showTimeZoneId.get();
		if (StrUtil.isBlank(showTimeZoneId)) {
			throw new RuntimeException("错误的使用方式");
		}
		String clientTimeZoneId = TimeZoneContext.clientTimeZoneId.get();
		if (StrUtil.isBlank(clientTimeZoneId)) {
			throw new RuntimeException("错误的使用方式");
		}

		long timeOffset = getTimeOffset(ZoneId.of(clientTimeZoneId), ZoneId.of(showTimeZoneId));
		//logger.info("----------------> DateTimeTools.transferShowTimeToClientTime 传入时间戳:{}, showTimeZoneId:{}, clientTimeZoneId:{}, timeOffset:{}",
		//		localTimestamp, showTimeZoneId, clientTimeZoneId, timeOffset);

		long retTimestamp = localTimestamp + timeOffset;
		if (timestampType == 1) {
			// 以毫秒入，以毫秒出
			return retTimestamp;
		} else if (timestampType == 2) {
			// 以秒入，以秒出
			return retTimestamp / 1000L;
		}

		// TODO
		return retTimestamp;
	}

	/**
	 * 将基于 fromZone 时区的时间值 inputTime，转换为对应的系统时间
	 *
	 * @param inputTime : 请求参数传入的字符串时间值
	 * @param timeFormat : 时间格式
	 * @param inputTimeZone : 参数时间使用的时区，如果未指定时区，则从当前线程上下文中自动提取时区
	 * @return
	 */
	public static Date readQueryTime(String inputTime, String timeFormat, ZoneId inputTimeZone) {
		if (StrUtil.isBlank(inputTime)) {
			return null;
		}

		if (inputTimeZone == null) {
			String inPutTimeZoneId = TimeZoneContext.showTimeZoneId.get();
			if (StrUtil.isBlank(inPutTimeZoneId)) {
				throw new RuntimeException("不能识别当前时间所属的时区，无法解析");
			}

			inputTimeZone = ZoneId.of(inPutTimeZoneId);
		}

		return convertZoneTime(inputTime, inputTimeZone, ZoneId.systemDefault());
	}

	/**
	 * 计算指定时区当前时间到 UTC 时间的时差，返回单位为毫秒
	 * 当值大于 0，代表当前时区更早，例如当前时区已经 12 点了，utc 时区才 11 点。
	 *
	 * 参考资料：
	 *     https://qa.1r1g.com/sf/ask/1105223941/
	 *
	 * @return
	 */
	public static long getTimeOffset(ZoneId zoneId) {
		LocalDateTime refNow = LocalDateTime.now();
		ZoneId refZoneId = ZoneId.of("Z");
		ZonedDateTime refTime = refNow.atZone(refZoneId);

		ZonedDateTime curZoneTime = refNow.atZone(zoneId);
		Duration diff = Duration.between(curZoneTime, refTime);
		return diff.toMillis();
	}

	/**
	 * 计算 targetZone 时区时间偏移量比 sourceZone 时区时间偏移量大的值
	 *
	 * @param sourceZone
	 * @param targetZone
	 * @return
	 */
	public static long getTimeOffset(ZoneId sourceZone, ZoneId targetZone) {
		LocalDateTime refNow = LocalDateTime.now();
		ZonedDateTime refTime = refNow.atZone(sourceZone);

		ZonedDateTime targetZoneTime = refNow.atZone(targetZone);
		Duration diff = Duration.between(targetZoneTime, refTime);
		return diff.toMillis();
	}

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("GMT-4")));
		long now = System.currentTimeMillis();
		TimeZoneContext.showTimeZoneId.set("GMT+4");
		TimeZoneContext.clientTimeZoneId.set("GMT+3");

		long retNow = transferShowTimeToClientTime(now / 1000L);
		System.out.println("----> now:" + now + ", retNow:" + retNow);
	}

	/**
	 * 时区资料：
	 * https://zhuanlan.zhihu.com/p/538934548
	 * https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
	 *
	 * @param args
	 */
	public static void main2(String[] args) {
//		for (String oneZone : TimeZone.getAvailableIDs()) {
//			System.out.println("-----> oneZone:" + oneZone);
//		}
		//TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("GMT+4")));
		System.out.println("-----> defaultTimeZone:" + ZoneId.systemDefault());

		Date now = new Date();
		// 展示 GMT-4 时区当前时间
		TimeZoneContext.showTimeZoneId.set("GMT-4");
		Date showTime = transferToShowTime(now);

		Date innerTime = readQueryTime("2023-11-08 12:00:00", "yyyy-MM-dd HH:mm:ss", ZoneId.of("GMT-3"));
		System.out.println("-----> now:" + DateUtil.formatDateTime(now) + ", showTime:" + DateUtil.formatDateTime(showTime) + ", innerTime:" + DateUtil.formatDateTime(innerTime));

		// 注意： atZone 的用途是，将系统时区所在的时刻，应用到目标时区来解释，同时返回的时间是系统时区的时间。
		// 而不是误以为的：将系统时区对应的时间，换算成目标时区的时间，并将这个目标时区的时间换算成系统时区的时间
		LocalDateTime localNow = LocalDateTime.now();
		Instant convertInstant = localNow.atZone(ZoneId.of("GMT-4")).toInstant();
		Date convertDateTime = Date.from(convertInstant);
		System.out.println("-----> convertDateTime:" + DateUtil.formatDateTime(convertDateTime));

		String timeStr = "2023-11-08 12:00:00";
		Date strTime1 = parseWithTimeZone(timeStr, ZoneId.of("Etc/GMT+4")); // 返回： 2023-11-09 00:00:00
		Date strTime2 = parseWithTimeZone(timeStr, ZoneId.of("Etc/GMT-8")); // 返回： 2023-11-08 12:00:00
		System.out.println("-----> strTime1:" + DateUtil.formatDateTime(strTime1) + ", strTime2:" + DateUtil.formatDateTime(strTime2));

		// 符合预期
		Date convertTime1 = convertZoneTime(timeStr, ZoneId.of("Etc/GMT+4"), ZoneId.of("Etc/GMT+5"));
		System.out.println("-------> convertTime1: " + DateUtil.formatDateTime(convertTime1));

		// 符合预期
		long offset1 = getTimeOffset(ZoneId.of("GMT+0800"));
		long offset2 = getTimeOffset(ZoneId.of("Z"));
		long offset3 = getTimeOffset(ZoneId.of("GMT+0800"), ZoneId.of("GMT+7"));
		System.out.println("------> offset1:" + offset1 + ", offset2:" + offset2 + ", offset3:" + offset3);

		Date localNowTime = new Date();
		TimeZone tz1 = TimeZone.getTimeZone("GMT+0500");// Etc/GMT+4  America/New_York （北京时间比纽约时间早 13 个小时）
		SimpleDateFormat timeZoneDateFormat1 = new SimpleDateFormat(DEFAULT_DATA_TIME_FORMAT);
		timeZoneDateFormat1.setTimeZone(tz1);

		TimeZone tz2 = TimeZone.getTimeZone("GMT+4:00");// Etc/GMT+4  America/New_York （北京时间比纽约时间早 13 个小时）
		SimpleDateFormat timeZoneDateFormat2 = new SimpleDateFormat(DEFAULT_DATA_TIME_FORMAT);
		timeZoneDateFormat2.setTimeZone(tz2);
        // 符合预期
		System.out.println("------> tz1.time:" + timeZoneDateFormat1.format(localNowTime) + ", tz2.time:" + timeZoneDateFormat2.format(localNowTime));

//		try {
//			Date time = timeZoneDateFormat1.parse(normalize(timeStr));
//			String targetTime = timeZoneDateFormat1.format(localNowTime);
//			System.out.println("-----> localTime1:" + DateUtil.formatDateTime(time) +
//					", localTime2:" + timeZoneDateFormat1.format(time) + ", targetTime:" + targetTime);
//		} catch (Exception e) {
//			throw new RuntimeException("日期转换报错");
//		}

		// now1 是北京时区的时间值
		LocalDateTime now1 = LocalDateTime.now();
		long now1time = System.currentTimeMillis();
		// 现在是北京时间 2023-11-06 22:00:00， 已经是 Etc/GMT 时区上偏离 0 时区的 -8 得到的时间，下面将获取此时 +4 时区的时间（相对于当前时区的时间），
		// 这代表 +4 时区上此时已经是当前北京时间 12 个小时后的时间了，这既是目标时区的当时时间（例如北京地方 12点，目标地方已经夜里 24点），也可理解为当前
		// 时区的一个未来时间
		ZoneId zoneId = ZoneId.of("Etc/GMT+4"); // 北京时区是： Etc/GMT-8

		// 计算当前在 ETC/GMT+4 时区的时间
		Instant instant = now1.atZone(zoneId).toInstant();
		Date date1 = Date.from(instant);
		long now2time = date1.getTime();
		// 以下显示：无论是从时间字符串打印来看，还是时间戳差值来看，都是 12 个小时后的时间
		System.out.println("-----> now1 at Etc/GMT+4 : " + DateUtil.formatDateTime(date1) + ", offset: " + (now2time - now1time));

		// 符合预期
		Date now2 = new Date();
		Date date2 = convertZoneTime(now2, ZoneId.of("Etc/GMT-8"), ZoneId.of("Etc/GMT+4"));
		System.out.println("-----> now2 at Etc/GMT+4 : " + DateUtil.formatDateTime(date2) + ", offset: " + (date2.getTime() - now2.getTime()));

		// 符合预期
		Date now22 = new Date();
		Date date22 = convertZoneTime(now22, ZoneId.of("GMT+3"), ZoneId.of("GMT+4"));
		System.out.println("-----> now22 at GMT+4 : " + DateUtil.formatDateTime(date22));

		// 符合预期
		long timeoffset1 = getTimeOffset(ZoneId.of("Etc/GMT-8"));
		long timeoffset2 = getTimeOffset(ZoneId.of("Etc/GMT+4"));
		System.out.println("------> timeoffset1:" + timeoffset1 + ", timeoffset2:" + timeoffset2);

		// 符合预期
		Date now3 = new Date();
		Date date3 = convertZoneTime(now3, ZoneId.of("Etc/GMT+4"));
		System.out.println("-----> now3 at Etc/GMT+4 : " + DateUtil.formatDateTime(date3) + ", offset: " + (date3.getTime() - now3.getTime()));

		// Etc/GMT+4 地方所在的 date3 时刻，也是当前时区 Etc/GMT-8 的一个未来时间(但是是一个虚拟的 Etc/GMT+4 地方的时间)
		// Etc/GMT+4 时区的当前时间，转换为 Etc/GMT-8 时区的时间，是要在此基础上加 12 个小时，符合预期
		Date now4 = new Date();
		Date date4 = convertZoneTime(now3, ZoneId.of("Etc/GMT+4"), ZoneId.of("Etc/GMT-8"));
		System.out.println("-----> date4 at Etc/GMT-8 : " + DateUtil.formatDateTime(date4) + ", offset: " + (now4.getTime() - date4.getTime()));

		// 符合预期
		Date date5 = convertZoneTime("2023-11-08 12:00:00", ZoneId.of("GMT+8"), ZoneId.of("GMT-4"));
		System.out.println("-----> date5 at GMT-4 : " + DateUtil.formatDateTime(date5));

		// 符合预期
		Date localNowTime2 = new Date();
		String date61 = standardTimeFormat(localNowTime2, ZoneId.of("GMT+8"));
		String date62 = standardTimeFormat(localNowTime2, ZoneId.of("GMT-4"));
		System.out.println("-----> date61 : " + date61 + ", date62:" + date62);


//		for (int i = 0; i < 200; i++) {
//			Date now = new Date();
//			LocalDateTime locatTime = date2LocalDateTime (now);
//			if (now.getTime() != DateTimeTools.localDateTime2Date(locatTime).getTime()) {
//				System.out.println("----------------> 时间转换有差距, now:" + DateUtil.formatDateTime(now));
//			}
//
//			try {
//				Thread.sleep(500L);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}

	private static void testTimeZoneOffset() {
		// 问题：随着时间的不同，通过以下方式计算时差不是一个固定值，目前看来有时是 4 个小时，有时是 5 个小时！！！！
//		String refTimeStr = "2022-09-15 01:38:01"; // 字面时间
		String refTimeStr = "2000-01-02 01:00:00"; // 字面时间
		SimpleDateFormat refSDF1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat refSDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		refSDF1.setTimeZone(utcZone);
		refSDF2.setTimeZone(TimeZone.getTimeZone("America/New_York"));

		try {
			Date utcTime = refSDF1.parse(refTimeStr);
			Date mdTime = refSDF2.parse(refTimeStr);
			logger.info("utcTime时分秒:{}-{}-{} {}:{}, mdTime时分秒:{}-{}-{} {}:{}", utcTime.getYear(), utcTime.getMonth(), utcTime.getDay(), utcTime.getHours(), utcTime.getMinutes(), mdTime.getYear(), mdTime.getMonth(), mdTime.getDay(), mdTime.getHours(), mdTime.getMinutes());
			logger.info("utcTime:{}, mdTime:{}, 时差:{} 个小时", DateUtil.formatDateTime(utcTime), DateUtil.formatDateTime(mdTime), (mdTime.getTime() - utcTime.getTime())/3600000L);
		} catch (ParseException e) {
			logger.error("[DateTimeUtils static] 初始化时区信息报错:", e);
		}
	}
}
