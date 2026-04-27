package com.yami.trading.common.util;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.time.ZoneId;

public class TimeZoneContext {
    /**
     * 对外展示时间使用的该时区对应的时间，主要用于处理 Date 类型，或者 String 类型的时间展示
     */
    public static ThreadLocal<String> showTimeZoneId = new TransmittableThreadLocal<String>() {
        protected synchronized String initialValue() {
            return ZoneId.systemDefault().getId();
        }
    };

    /**
     * 客户端当前的系统时区，主要用于处理时间戳类型的时间转换，以迎合/方便客户端解析时间戳
     */
    public static ThreadLocal<String> clientTimeZoneId = new TransmittableThreadLocal<String>() {
        protected synchronized String initialValue() {
            return ZoneId.systemDefault().getId();
        }
    };

}
