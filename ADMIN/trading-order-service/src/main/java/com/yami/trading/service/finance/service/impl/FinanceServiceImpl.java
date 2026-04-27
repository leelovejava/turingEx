package com.yami.trading.service.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.finance.Finance;
import com.yami.trading.dao.finance.FinanceMapper;
import com.yami.trading.service.finance.service.FinanceRedisKeys;
import com.yami.trading.service.finance.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class FinanceServiceImpl extends ServiceImpl<FinanceMapper, Finance> implements FinanceService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void save(Finance entity, String login_safeword, String operaterUsername) {


        //todo
//		User sec =  this.secUserService.findByUserName(operaterUsername);
//		String sysSafeword = sec.getSafePassword();
//		String safeword_md5 = passwordEncoder.encode(login_safeword);
//		if (!safeword_md5.equals(sysSafeword)) {
//			throw new BusinessException("资金密码错误");
//		}
        this.save(entity);
//		redisHandler.setSync(FinanceRedisKeys.FINANCE_ID + entity.getId().toString(), entity);
        redisTemplate.opsForValue().set(FinanceRedisKeys.FINANCE_ID + entity.getUuid(), entity);

        Map<String, Finance> cacheMap = (Map<String, Finance>) redisTemplate.opsForValue().get(FinanceRedisKeys.FINANCE_MAP);
        if (cacheMap == null) {
            cacheMap = new ConcurrentHashMap<String, Finance>();
        }
        cacheMap.put(entity.getUuid(), entity);
//		redisHandler.setSync(FinanceRedisKeys.FINANCE_MAP, cacheMap);
        redisTemplate.opsForValue().set(FinanceRedisKeys.FINANCE_MAP, cacheMap);

    }

    public void update(Finance entity, String login_safeword, String operaterUsername) {

        System.out.println("operaterUsername => " + operaterUsername);
        //todo
//		User sec =  this.secUserService.findByUserName(operaterUsername);

//		String sysSafeword = sec.getSafePassword();
//		String safeword_md5 = passwordEncoder.encode(login_safeword);
//		if (!safeword_md5.equals(sysSafeword)) {
//			throw new BusinessException("资金密码错误");
//		}

        this.updateById(entity);
//		redisHandler.setSync(FinanceRedisKeys.FINANCE_ID + entity.getId().toString(), entity);
        redisTemplate.opsForValue().set(FinanceRedisKeys.FINANCE_ID + entity.getUuid(), entity);

        Map<String, Finance> cacheMap = (Map<String, Finance>) redisTemplate.opsForValue().get(FinanceRedisKeys.FINANCE_MAP);
        if (cacheMap == null) {
            cacheMap = new ConcurrentHashMap<String, Finance>();
        }
        cacheMap.put(entity.getUuid(), entity);
        redisTemplate.opsForValue().set(FinanceRedisKeys.FINANCE_MAP, cacheMap);

    }

    public void delete(String id, String login_safeword, String operaterUsername) {

        //todo
//		User sec =  this.secUserService.findByUserName(operaterUsername);
//		String sysSafeword = sec.getSafePassword();
//		String safeword_md5 = passwordEncoder.encode(login_safeword);
//		if (!safeword_md5.equals(sysSafeword)) {
//			throw new BusinessException("资金密码错误");
//		}


        Finance entity = findById(id);
        this.removeById(entity);
//		redisHandler.remove(FinanceRedisKeys.FINANCE_ID + entity.getUuid().toString());
        redisTemplate.delete(FinanceRedisKeys.FINANCE_ID + entity.getUuid());

        Map<String, Finance> cacheMap = (Map<String, Finance>) redisTemplate.opsForValue().get(FinanceRedisKeys.FINANCE_MAP);
        if (cacheMap != null && !cacheMap.isEmpty()) {
            cacheMap.remove(entity.getUuid());
            redisTemplate.opsForValue().set(FinanceRedisKeys.FINANCE_MAP, cacheMap);
        }


    }

    public Finance findById(String id) {
        return (Finance) redisTemplate.opsForValue().get(FinanceRedisKeys.FINANCE_ID + id);
    }

    public List<Finance> findAll() {
        Map<String, Finance> cacheMap = (Map<String, Finance>) redisTemplate.opsForValue().get(FinanceRedisKeys.FINANCE_MAP);
        if (cacheMap != null && !cacheMap.isEmpty()) {
            return new ArrayList<>(cacheMap.values());
        }
        return new ArrayList<>();
    }

    public List<Finance> findAllState_1() {
        LambdaQueryWrapper<Finance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Finance::getState, 1);
        queryWrapper.orderByAsc(Finance::getInvestmentMin);
        return list(queryWrapper);
    }

    @Override
    public List<Finance> selectAllNoCache() {
        return list();
    }
}
