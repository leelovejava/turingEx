package com.yami.trading.common.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lh
 */
@Slf4j
@SuppressWarnings("unchecked")
public class RedisUtil {

    private static RedisTemplate<String, Object> redisTemplate = ApplicationUtil.getBean("redisTemplate", RedisTemplate.class);
    private static RedisTemplate<String, Object> redisTemplate2 = ApplicationUtil.getBean("redisTemplate2", RedisTemplate.class);

    public static final StringRedisTemplate STRING_REDIS_TEMPLATE = ApplicationUtil.getBean("stringRedisTemplate", StringRedisTemplate.class);


    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("设置redis指定key失效时间错误:", e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效 失效时间为负数，说明该主键未设置失效时间（失效时间默认为-1）
     */
    public static Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false 不存在
     */
    public static Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("redis判断key是否存在错误：", e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }


    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static <T> T get(String key) {
        return key == null ? null : (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置redis缓存错误：", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增 此时value值必须为int类型 否则报错
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public static Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return STRING_REDIS_TEMPLATE.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public static Long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return STRING_REDIS_TEMPLATE.opsForValue().increment(key, -delta);
    }

    /**
     * push 同步
     */
    public static void pushSync(String key, Object object) {
        STRING_REDIS_TEMPLATE.opsForList().leftPush(key, JSON.toJSONString(object, SerializerFeature.WriteClassName));
    }

    /**
     * 从队列尾取一个Object
     */
    public static Object poll(String key) {
        String value = STRING_REDIS_TEMPLATE.opsForList().rightPop(key);
        if (value == null || value.equals("nil")) {
            return null;
        }
        return JSON.parse(value);
    }

    public static void hSet(String hash, String key, Object value) {
        redisTemplate2.opsForHash().put(hash, key, value);
    }

    public static void hmSet(String cacheKey, Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        redisTemplate2.opsForHash().putAll(cacheKey, map);
    }

    public static <T> T hGet(String hash, String key) {
        return (T) redisTemplate2.opsForHash().get(hash, key);
    }

    public static <T> List<T> hmGet(String cacheKey, List fields) {
        return (List<T>) redisTemplate2.opsForHash().multiGet(cacheKey, fields);
    }

    public static <T> T hDel(String hash, String key) {
        return (T) redisTemplate2.opsForHash().delete(hash, key);
    }

    public static void sadd(String cacheKey, String element) {
        redisTemplate.opsForSet().add(cacheKey, element);
    }

    public static void sremove(String cacheKey, String element) {
        redisTemplate.opsForSet().remove(cacheKey, element);
    }

    public static boolean sexist(String cacheKey, String element) {
        return redisTemplate.opsForSet().isMember(cacheKey, element);
    }

    public static Long sCount(String cacheKey) {
        return redisTemplate.opsForSet().size(cacheKey);
    }

    public static String spop(String cacheKey) {
        Object element = redisTemplate.opsForSet().pop(cacheKey);
        if (element == null) {
            return null;
        }

        return element.toString();
    }

    public static void zadd(String cacheKey, String element, double score) {
        redisTemplate.opsForZSet().add(cacheKey, element, score);
    }

    public static void zremove(String cacheKey, String element) {
        redisTemplate.opsForZSet().remove(cacheKey, element);
    }

    public static void leftPush(String cacheKey, String element) {
        redisTemplate.opsForList().leftPush(cacheKey, element);
    }

    public static String rightPop(String cacheKey) {
        String element = (String) redisTemplate.opsForList().rightPop(cacheKey);
        return element;
    }

    public static List<String> rightPop(String cacheKey, int count) {
        if (count <= 1) {
            count = 1;
        }

        List<String> retList = new ArrayList<>();
        List<Object> list = redisTemplate.opsForList().rightPop(cacheKey, count);
        if (CollectionUtil.isEmpty(list)) {
            return retList;
        }
        for (Object one : list) {
            retList.add(one.toString());
        }

        return retList;
    }

}
