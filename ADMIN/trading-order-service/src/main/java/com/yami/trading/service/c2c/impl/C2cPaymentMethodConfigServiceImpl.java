package com.yami.trading.service.c2c.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.Log;
import com.yami.trading.common.constants.PayTemplateParamEnum;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.dao.c2c.C2cPaymentMethodConfigMapper;
import com.yami.trading.service.c2c.C2cPaymentMethodConfigService;
import com.yami.trading.service.system.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class C2cPaymentMethodConfigServiceImpl extends ServiceImpl<C2cPaymentMethodConfigMapper, C2cPaymentMethodConfig> implements C2cPaymentMethodConfigService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    LogService logService;

    @Override
    public Map<String, String> getMethodConfigMap() {
        List<C2cPaymentMethodConfig> methodConfigList = list();
        Map<String, String> methodConfigMap = new HashMap<String, String>();
        for (int i = 0; i < methodConfigList.size(); i++) {
            C2cPaymentMethodConfig config = methodConfigList.get(i);
            if (null != config) {
                methodConfigMap.put(config.getUuid(), config.getMethodName());
            }
        }
        return methodConfigMap;
    }

    @Override
    public Map<String, C2cPaymentMethodConfig> getC2cPMethodConfigMap() {
        return getC2cPMethodConfigMapByType("2");
    }

    public Map<String, C2cPaymentMethodConfig> getC2cPMethodConfigMapByType(String type) {
        List<C2cPaymentMethodConfig> methodConfigList = list(Wrappers.<C2cPaymentMethodConfig>query().lambda()
                .eq(C2cPaymentMethodConfig::getType, type));
        Map<String, C2cPaymentMethodConfig> methodConfigMap = new HashMap<>();
        for (int i = 0; i < methodConfigList.size(); i++) {
            C2cPaymentMethodConfig config = methodConfigList.get(i);
            if (null != config) {
                methodConfigMap.put(config.getUuid(), config);
            }
        }

        return methodConfigMap;
    }


    @Override
    public Map<String, C2cPaymentMethodConfig> getBankCardPMethodConfigMap() {
        return getC2cPMethodConfigMapByType("1");
    }

    @Override
    @Transactional
    public C2cPaymentMethodConfig add(C2cPaymentMethodConfig cfg) {
        save(cfg);
        redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID + cfg.getUuid(), cfg);
        Map<String, String> map1 = (Map<String, String>) redisTemplate.opsForValue().get(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID_TYPE);
        if (null == map1) {
            map1 = new ConcurrentHashMap<String, String>();
        }
        map1.put(cfg.getUuid(), String.valueOf(cfg.getMethodType()));
        redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID_TYPE, map1);
        return cfg;
    }

    @Override
    public void delete(String id) {
        C2cPaymentMethodConfig entity = getById(id);
        if (entity != null) {
            removeById(id);
            redisTemplate.delete(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID + entity.getUuid().toString());

            Map<String, String> map1 = (Map<String, String>)redisTemplate.opsForValue().get(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID_TYPE);
            if (map1 != null && !map1.isEmpty()) {
                map1.remove(id);
            } else {
                map1 = new ConcurrentHashMap<String, String>();
            }
            redisTemplate.opsForValue().set(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID_TYPE, map1);
        }
    }

    @Override
    public Map<String, String> getMethodConfigIdTypeMap() {
        Map<String, String> map = (Map<String, String>)redisTemplate.opsForValue().get(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID_TYPE);
        if (null == map || map.isEmpty()) {
            return new HashMap<String, String>();
        }

        return map;
    }

    @Override
    public C2cPaymentMethodConfig get(String id) {
        return (C2cPaymentMethodConfig) redisTemplate.opsForValue().get(RedisKeys.C2C_PAYMENT_METHOD_CONFIG_ID + id);
    }
}
