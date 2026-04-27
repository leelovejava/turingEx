package com.yami.trading.util.json;//package com.cute.cloud.common.core.util.jackson;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.TimeZoneContext;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class DateJsonDeserializer extends JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		String dateTimeStr = jp.getText();
		if (StrUtil.isNotBlank(dateTimeStr)) {
			try {
				String inputPutTimeZoneId = TimeZoneContext.showTimeZoneId.get();

				dateTimeStr = dateTimeStr.replace("T", " ").trim();
				Date inputDateTime = null;
				if (dateTimeStr.length() == "2023-01-01 00:00:00".length()) {
					inputDateTime = DateUtil.parseDateTime(dateTimeStr);
				} else if (dateTimeStr.length() == "2023-01-01".length()) {
					inputDateTime = DateUtil.parseDate(dateTimeStr);
				} else {
					throw new RuntimeException("不支持当前时间格式:" + dateTimeStr);
				}

				Date systemZoneTime = DateTimeTools.convertZoneTime(inputDateTime, ZoneId.of(inputPutTimeZoneId), ZoneId.systemDefault());
				return systemZoneTime;
			} catch (Exception e) {
				throw new JsonParseException(jp, "cannot deserialize date string: " + dateTimeStr, jp.getCurrentLocation(), e);
			}
		}

		return null;
	}

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-5"));
		String timeStr = "2023-11-07 13:00:00";
		Date systemZoneTime = DateTimeTools.convertZoneTime(timeStr, ZoneId.of("GMT+8"), ZoneId.systemDefault());
		System.out.println("----> localTime:" + DateUtil.formatDateTime(systemZoneTime));
	}
}