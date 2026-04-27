package com.yami.trading.util;

public class DatePatternConstant {
	/**
	 * 反序列化支持的日志格式类型转换
	 */
	public final static String[] DESERIALIZE_DATE_PATTERN = {
			"yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd HH:mm:ss.SSS",
			"yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
			"yyyy年MM月dd日",
			"yyyyMMdd",
			"HH:mm:ss"
	};

	/**
	 * 序列化默认的日志输出格式
	 */
	public final static String DEFALUT_SERIALIZE_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

}