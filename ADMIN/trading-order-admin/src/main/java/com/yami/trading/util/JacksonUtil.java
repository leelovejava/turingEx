package com.yami.trading.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yami.trading.common.util.CustomeClassUtils;
import com.yami.trading.util.json.DateJsonDeserializer;
import com.yami.trading.util.json.DateJsonSerializer;
import com.yami.trading.util.json.JsonConvertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 *
 * 参考：
 *   https://blog.csdn.net/Remember_Z/article/details/119736770
 *   https://www.cnblogs.com/zincredible/p/15596403.html
 *
 */
public class JacksonUtil {
	private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

	private JacksonUtil() {
	}

	public static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		init(objectMapper);
	}

	public static void init(ObjectMapper objectMapper) {
//		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
//		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
//		objectMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setDateFormat(new STDDateformat());
		objectMapper.setTimeZone(TimeZone.getDefault());//.getTimeZone("GMT+8")
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// objectMapper.configure(MapperFeature.USE_STD_BEAN_NAMING, true);// 导致第一个字母大写
		objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy() {
			private static final long serialVersionUID = 1L;
			private final Logger logger = LoggerFactory.getLogger(this.getClass());

			// 反序列化时调用
			@Override
			public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
				//System.out.println("========> method:" + method.getName() + ", defaultName:" + defaultName);
				if (StrUtil.isBlank(defaultName)) {
					return defaultName;
				}
				if (defaultName.length() <= 3) {
					return defaultName;
				}
				if (method.getDeclaringClass() == Long.class
				        || method.getDeclaringClass() == Integer.class
						|| method.getDeclaringClass() == Float.class
				        || method.getDeclaringClass() == Double.class
						|| method.getDeclaringClass() == Byte.class) {
					return defaultName;
				}

				String fieldNameForMethod = method.getName().substring(3);
				fieldNameForMethod = fieldNameForMethod.substring(0, 1).toLowerCase() + fieldNameForMethod.substring(1);
				Field field = CustomeClassUtils.getFieldByName(method.getDeclaringClass(), fieldNameForMethod);
				if (field == null) {
					//logger.error("未能在类:{} 中找到method:{} 对应的字段属性", method.getDeclaringClass().getName(), method.getName());
					return defaultName;
				}

				JsonProperty jsonPropertyAnn = field.getAnnotation(JsonProperty.class);
				if (jsonPropertyAnn == null) {
					return fieldNameForMethod;
				}
				return jsonPropertyAnn.value();
			}

			// 序列化时调用
			@Override
			public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
				//System.out.println("========> method:" + method.getName() + ", defaultName:" + defaultName);
				if (StrUtil.isBlank(defaultName)) {
					return defaultName;
				}
				if (defaultName.length() <= 3) {
					return defaultName;
				}
				if (method.getName().startsWith("is")) {
					return defaultName;
				}
				if (method.getDeclaringClass() == Long.class
						|| method.getDeclaringClass() == Integer.class
						|| method.getDeclaringClass() == Float.class
						|| method.getDeclaringClass() == Double.class
						|| method.getDeclaringClass() == Byte.class) {
					return defaultName;
				}

				String fieldNameForMethod = method.getName().substring(3);
				fieldNameForMethod = fieldNameForMethod.substring(0, 1).toLowerCase() + fieldNameForMethod.substring(1);
				Field field = CustomeClassUtils.getFieldByName(method.getDeclaringClass(), fieldNameForMethod);
				if (field == null) {
					//logger.error("未能在类:{} 中找到method:{} 对应的字段属性", method.getDeclaringClass().getName(), method.getName());
					return defaultName;
				}

				JsonProperty jsonPropertyAnn = field.getAnnotation(JsonProperty.class);
				if (jsonPropertyAnn == null) {
					return fieldNameForMethod;
				}
				return jsonPropertyAnn.value();
			}
		});

//		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

		SimpleModule module = new SimpleModule("DateTimeModule", Version.unknownVersion());
		module.addDeserializer(Date.class, new DateJsonDeserializer());
		module.addSerializer(Date.class, new DateJsonSerializer());

//		/**
//		 * https://segmentfault.com/a/1190000038538882?utm_source=sf-related
//		 */
//		module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
//		module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
//		module.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
//		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
//		module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
//		module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));

//		// sql
//		module.addSerializer(java.sql.Date.class, new SqlDateJsonSerializer());
//		module.addDeserializer(java.sql.Date.class, new SqlDateJsonDeserializer());
//		module.addSerializer(Timestamp.class, new TimestampJsonSerializer());
//		module.addDeserializer(Timestamp.class, new TimestampJsonDeserializer());
//		module.addSerializer(java.sql.Time.class, new SqlTimeJsonSerializer());
//		module.addDeserializer(java.sql.Time.class, new SqlTimeJsonDeserializer());
//
//		GuavaModule guavaModule = new GuavaModule();
//		objectMapper.registerModule(guavaModule);
//
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		objectMapper.registerModule(javaTimeModule);

//		// 兼容 jackson 2.5 以下的版本, 对 Map.Entry 序列化做特殊处理
//		module.addSerializer(Map.Entry.class, new JsonSerializer<Map.Entry>() {
//			@Override
//			public void serialize(Map.Entry entry, JsonGenerator gen, SerializerProvider serializers)
//					throws IOException {
//				gen.writeObject(new KeyValue(entry.getKey(), entry.getValue()));
//			}
//		});

		objectMapper.registerModule(module);
	}

	public static ObjectMapper getMapper() {
		return objectMapper;
	}

	public static String toJson(Object obj) {
		if (obj == null) {
			return null;
		}

		Object tmpObj = obj;
		if (AopUtils.isAopProxy(obj)) {
			// 代理模式下，可能 object 的字段值都是空，导致打印不出预期值
			Class realCls = AopUtils.getTargetClass(obj);
			try {
				tmpObj = realCls.newInstance();
				BeanUtil.copyProperties(obj, tmpObj);
			} catch (Exception e) {
				logger.error("基于class类构造对象报错", e);
			}
		}

		StringWriter sw = new StringWriter();
		JsonGenerator gen = null;
		boolean closed = false;
		try {
			gen = new JsonFactory().createJsonGenerator(sw);
			objectMapper.writeValue(gen, tmpObj);
			gen.close();
			closed = true;
			return sw.toString();
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	public static String toJson(Object obj, boolean isTrimBlank) {
		return toJson(obj);
	}

	public static <T> T fromJson(String jsonStr, Class<T> objClass) { // throws JsonParseException, JsonMappingException, IOException
		if (StrUtil.isBlank(jsonStr)) {
			return null;
		}
		try {
			return objectMapper.readValue(jsonStr, objClass);
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	public static <T> T fromJson(String jsonStr, TypeReference typeReference) {// throws JsonParseException, JsonMappingException, IOException
		if (StrUtil.isBlank(jsonStr)) {
			return null;
		}
		try {
			// TypeReference newType = new TypeReference<List<NationalCityCode>>() {};
			return (T)(objectMapper.readValue(jsonStr, typeReference));
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	public static <T> T fromJson(String jsonStr, Type type) {// throws JsonParseException, JsonMappingException, IOException
//		Type[] types = new Type[1];
//		final ParameterizedTypeImpl type4 = ParameterizedTypeImpl.make(ResponseData.class, types, ResponseData.class.getDeclaringClass());
//		TypeReference<T> typeReference = new TypeReference<T>() {
//			@Override
//			public Type getType() {
//				return type;
//			}
//		};
		// Type type = new TypeToken<List<Student>>() {}.getType()

		if (StrUtil.isBlank(jsonStr)) {
			return null;
		}
		try {
			JavaType javaType = objectMapper.constructType(type);
			return (T)(objectMapper.readValue(jsonStr, javaType));
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	public static <T> List<T> ofList(Object object, Class<T> tClass) {// throws IOException
		if (null == object) {
			return null;
		}
		String json = toJson(object);
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, tClass);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	/**
	 * 将json字符串转换为map
	 *
	 * @param json json字符串
	 * @return Map
	 * @throws JsonProcessingException JsonProcessingException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> json2map(String json) {// throws JsonProcessingException
		if (StrUtil.isBlank(json)) {
			return new HashMap<>();
		}
		try {
			return (Map<String, Object>) objectMapper.readValue(json, Map.class);
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	public static <K, V> Map<K, V> json2map(String json, Class<K> keyClass, Class<V> valueClass) {
		if (StrUtil.isBlank(json)) {
			return new HashMap<>();
		}
		MapType mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);
		try {
			return objectMapper.readValue(json, mapType);
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	public static <K, V> Map<K, V> bean2map(Object object, Class<K> keyClass, Class<V> valueClass) {
		if (null == object) {
			return null;
		}
		String json = toJson(object);
		MapType mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);
		try {
			return objectMapper.readValue(json, mapType);
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	public static Map<String, Object> bean2map(Object object) {
		if (null == object) {
			return null;
		}
		String json = toJson(object);
		return json2map(json);
	}

	/**
	 * 泛化转换方式，此方式最为强大、灵活
	 * <p>
	 * example：
	 * <p>
	 * {@code Map<String, List<UserInfo>> listMap = genericConvert(jsonStr, new TypeReference<Map<String, List<UserInfo>>>() {});}
	 *
	 * @param json json字符串
	 * @param type type
	 * @param <T>  泛化
	 * @return T
	 * @throws JsonProcessingException JsonProcessingException
	 */
	public static <T> T genericConvert(String json, TypeReference<T> type) {// throws JsonProcessingException
		if (StrUtil.isBlank(json)) {
			return null;
		}
		try {
			return objectMapper.readValue(json, type);
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	/**
	 * 将map转换为实体类对象
	 *
	 * @param map  map
	 * @param type 实体对象类型
	 * @param <T>  泛型
	 * @return 实体对象
	 */
	public static <T> T map2bean(Map map, Class<T> type) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		return objectMapper.convertValue(map, type);
	}

	/**
	 * 将json字符串转换为实体类集合
	 *
	 * @param json json字符串
	 * @param type 实体对象类型
	 * @param <T>  泛型
	 * @return list集合
	 * @throws JsonProcessingException JsonProcessingException
	 */
	public static <T> List<T> json2list(String json, Class<T> type) {// throws JsonProcessingException
		if (StrUtil.isBlank(json)) {
			return null;
		}

		CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
		try {
			return objectMapper.readValue(json, collectionType);
		} catch (IOException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	/**
	 * 检查字符串是否是json格式
	 *
	 * @param jsonStr 待检查字符串
	 * @return 是：true，否：false
	 */
	public static boolean isJsonString(String jsonStr) {
		if (StrUtil.isBlank(jsonStr)) {
			return false;
		}
		try {
			objectMapper.readTree(jsonStr);
			return true;
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("检查字符串是否是json格式...{}", e.getMessage());
			}
			return false;
		}
	}

//	public static JsonObject fromJson(String jsonStr) {
//		try {
//			objectMapper.par
//			return (T)(objectMapper.readValue(jsonStr, typeReference));
//		} catch (IOException e) {
//			JsonConvertException err = new JsonConvertException(e);
//			throw err;
//		}
//	}

	/**
	 * 打印json到控制台
	 *
	 * @param obj    需要打印的对象
	 * @param pretty 是否打印美观格式
	 * @param <T>    泛型
	 */
	public static <T> void printJson(T obj, boolean pretty) {
		if (obj == null) {
			return;
		}
		try {
			if (pretty) {
				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
			} else {
				System.out.println(objectMapper.writeValueAsString(obj));
			}
		} catch (JsonProcessingException e) {
			JsonConvertException err = new JsonConvertException(e);
			throw err;
		}
	}

	public static void main(String[] args) {
		// GMT+8
		TimeZone tz = TimeZone.getTimeZone("Etc/GMT-8");
		System.out.println("----> tz:" + tz.getID());
	}

}
