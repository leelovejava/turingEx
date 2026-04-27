package com.yami.trading.service.impl;

import com.yami.trading.service.IdentifyingCodeTimeWindowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IdentifyingCodeTimeWindowServiceImpl implements IdentifyingCodeTimeWindowService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String getAuthCode(String key) {
        Object authCode = redisTemplate.opsForValue().get(key);
        if (authCode != null) {
            return String.valueOf(authCode);
        }
        return null;
    }

    @Override
    public void putAuthCode(String key, String authCode) {
        redisTemplate.opsForValue().set(key, authCode, 10, TimeUnit.MINUTES);
    }

    @Override
    public void delAuthCode(String key) {
        redisTemplate.delete(key);
    }
}
