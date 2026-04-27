package com.yami.trading.service.finance.loadcache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yami.trading.bean.finance.Finance;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.service.finance.service.FinanceRedisKeys;
import com.yami.trading.service.finance.service.FinanceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FinanceLoadCacheService {
    private static final Log logger = LogFactory.getLog(FinanceLoadCacheService.class);
    @Autowired
    private FinanceService financeService;
    @Autowired
    private RedisTemplate redisTemplate;

    public void loadcache() {
        load();
        logger.info("完成Finance数据加载redis");
    }

    public void load() {
        List<Finance> all = financeService.selectAllNoCache();
        Map<String, Finance> cacheMap = new ConcurrentHashMap<String, Finance>();
        for (Finance finance : all) {
            cacheMap.put(finance.getUuid(), finance);
            redisTemplate.opsForValue().set(FinanceRedisKeys.FINANCE_ID + finance.getUuid(), finance);
        }
        redisTemplate.opsForValue().set(FinanceRedisKeys.FINANCE_MAP, cacheMap);
    }

//	public void setRedisHandler(RedisHandler redisHandler) {
//		this.redisHandler = redisHandler;
//	}

}
