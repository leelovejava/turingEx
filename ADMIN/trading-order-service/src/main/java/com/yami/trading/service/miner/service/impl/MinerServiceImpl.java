package com.yami.trading.service.miner.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.dao.miner.MinerMapper;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.syspara.SysparaService;
import org.apache.commons.lang3.StringUtils;

import com.yami.trading.service.miner.service.MinerRedisKeys;
import com.yami.trading.service.miner.service.MinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class MinerServiceImpl extends ServiceImpl<MinerMapper, Miner> implements MinerService {

//	protected RedisHandler redisHandler;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	protected SysparaService sysparaService;
	@Autowired
	protected DataService dataService;

	public Miner cacheById(String id) {
		return (Miner) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ID + id);
	}

	public boolean save(Miner entity) {

		this.save(entity);

		redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ID + entity.getUuid().toString(), entity);

		Map<String, Miner> map = (Map<String, Miner>) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_MAP);
		if (map == null) {
			map = new ConcurrentHashMap<String, Miner>();
		}
		map.put(entity.getUuid().toString(), entity);
		redisTemplate.opsForValue().set(MinerRedisKeys.MINER_MAP, map);
		return true;
	}

	public void updateA(Miner entity) {
		if(entity == null){
			System.out.println("entity is null");
		}
		this.updateById(entity);

		redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ID + entity.getUuid().toString(), entity);

		Map<String, Miner> map = (Map<String, Miner>) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_MAP);
		if (map == null) {
			map = new ConcurrentHashMap<String, Miner>();
		}
		map.put(entity.getUuid().toString(), entity);
		redisTemplate.opsForValue().set(MinerRedisKeys.MINER_MAP, map);
	}

	public void delete(String id) {
		Miner entity = findById(id);
		this.delete(entity.getUuid());


		redisTemplate.delete(MinerRedisKeys.MINER_ID + entity.getUuid().toString());
		Map<String, Miner> map = (Map<String, Miner>) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_MAP);
		if (map != null && !map.isEmpty()) {
			map.remove(entity.getUuid().toString());
			redisTemplate.opsForValue().set(MinerRedisKeys.MINER_MAP, map);
		}
	}

	public Miner findById(String id) {
		return (Miner) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ID + id);
	}

	public List<Miner> findAll() {
		Map<String, Miner> map = (Map<String, Miner>) redisTemplate.opsForValue().get(MinerRedisKeys.MINER_MAP);
		if (map != null) {
			List<Miner> list = new ArrayList<>(map.values());
			list.sort(new Miner());
			return list;
		}
		return new ArrayList<>();
	}

	public List<Miner> findAllState_1() {
		List<Miner> list = new ArrayList<Miner>();
		for (Miner miner : findAll()) {
			if ("1".equals(miner.getState())) {
				list.add(miner);
			}
		}
		return list;
	}

	public Map<String, Object> getBindOne(Miner miner) {
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("id", miner.getUuid());
		result.put("name", miner.getName());
		result.put("name_en", miner.getName_en());
		result.put("name_cn", miner.getName_cn());
		result.put("daily_rate", miner.getDaily_rate());
		result.put("investment_min", miner.getInvestment_min());
		result.put("investment_max", miner.getInvestment_max());
		result.put("state", miner.getState());
		result.put("on_sale", miner.getOn_sale());
		result.put("test", miner.getTest());
		Double miner_test_profit = sysparaService.find("miner_test_profit").getDouble();
		if (miner.getTest().equals("Y")) {
			result.put("all_rate", Arith.mul(miner_test_profit, miner.getCycle()));
			result.put("cycle", miner.getCycle());
			result.put("daily_rate", miner_test_profit);
		} else {
			result.put("all_rate", Arith.mul(miner.getDaily_rate(), 30));
			result.put("cycle", miner.getCycle_close());
		}

		// 根据产生的收益转化成指定的币种
		String miner_profit_symbol = sysparaService.find("miner_profit_symbol").getSvalue();
		// 矿机购买时使用的币种，则产生
		String miner_buy_symbol = sysparaService.find("miner_buy_symbol").getSvalue();
		double symbol_profit = miner.getTest().equals("Y") ? miner_test_profit
				: Arith.div(Arith.mul(100, miner.getDaily_rate()), 100);// 100为单位的币种收益
		// 收益转化成U
		if (StringUtils.isNotEmpty(miner_buy_symbol) && !"usdt".equalsIgnoreCase(miner_buy_symbol)) {
			List<Realtime> realtime_list = this.dataService.realtime(miner_buy_symbol);
			Realtime realtime = null;
			if (realtime_list.size() > 0) {
				realtime = realtime_list.get(0);
			} else {
				throw new BusinessException("行情获取异常，稍后再试");
			}
			symbol_profit = Arith.mul(symbol_profit, realtime.getClose());
		}

		if (StringUtils.isNotEmpty(miner_profit_symbol) && !"usdt".equalsIgnoreCase(miner_profit_symbol)) {
			List<Realtime> realtime_list = this.dataService.realtime(miner_profit_symbol);
			Realtime realtime = null;
			if (realtime_list.size() > 0) {
				realtime = realtime_list.get(0);
			} else {
				throw new BusinessException("行情获取异常，稍后再试");
			}
			symbol_profit = Arith.div(symbol_profit, realtime.getClose());
			result.put("symbol_profit", symbol_profit);
		} else {
			result.put("symbol_profit", symbol_profit);
		}
		result.put("miner_profit_symbol",
				StringUtils.isEmpty(miner_profit_symbol) ? "USDT" : miner_profit_symbol.toUpperCase());
		result.put("img", "https://trading-order-test.s3.amazonaws.com/common/2023-09-16/783a1a14-f6ad-48e3-adb4-6623cca57480IMG_1558.PNG");
		// 基础信息
		result.put("algorithm", miner.getAlgorithm());
		result.put("computing_power", miner.getComputing_power());
		result.put("computing_power_unit", miner.getComputing_power_unit());
		result.put("power", miner.getPower());
		result.put("product_factory", miner.getProduct_factory());
		result.put("product_size", miner.getProduct_size());
		result.put("weight", miner.getWeight());
		result.put("work_temperature_min", miner.getWork_temperature_min());
		result.put("work_temperature_max", miner.getWork_temperature_max());
		result.put("work_humidity_min", miner.getWork_humidity_min());
		result.put("work_humidity_max", miner.getWork_humidity_max());
		result.put("internet", miner.getInternet());

		result.put("buy_currency", miner.getBuy_currency());
		result.put("output_currency", miner.getOutput_currency());

		return result;
	}

//	public void setRedisHandler(RedisHandler redisHandler) {
//		this.redisHandler = redisHandler;
//	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

//	public List<Finance> findAll_2() {
//
//		LambdaQueryWrapper<Miner> queryWrapper = new LambdaQueryWrapper<Miner>()
//				.eq(Miner::getState,state).eq(FinanceOrder::getPartyId, partyId);
//		list = this.getBaseMapper().selectList(queryWrapper);
//
//		Map<String, Finance> cacheMap = (Map<String, Finance>) redisTemplate.opsForValue().get(FinanceRedisKeys.FINANCE_MAP);
//		if (cacheMap != null && !cacheMap.isEmpty()) {
//			return new ArrayList<Finance>(cacheMap.values());
//		}
//		return new ArrayList<Finance>();
//	}
}
