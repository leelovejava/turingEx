package com.yami.trading.util.json;

import cn.hutool.core.date.format.FastDateFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.TimeZoneContext;
import com.yami.trading.util.DatePatternConstant;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

public class DateJsonSerializer extends JsonSerializer<Date> {

	private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(DatePatternConstant.DEFALUT_SERIALIZE_DATE_PATTERN);

	@Override
	public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		String outPutTimeZoneId = TimeZoneContext.showTimeZoneId.get();
		Date retDate = DateTimeTools.convertZoneTime(date, ZoneId.of(outPutTimeZoneId));

		jsonGenerator.writeString(date != null ? DATE_FORMAT.format(retDate) : "null");
	}
}
