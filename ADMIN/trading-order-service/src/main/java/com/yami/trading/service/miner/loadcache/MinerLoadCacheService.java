package com.yami.trading.service.miner.loadcache;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.common.util.Arith;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yami.trading.service.miner.service.MinerRedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class MinerLoadCacheService {
	private static final Log logger = LogFactory.getLog(MinerLoadCacheService.class);

//	protected RedisHandler redisHandler;
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	protected NamedParameterJdbcOperations namedParameterJdbcTemplate;


	@PostConstruct
	public void loadcache() {
		load();
		logger.info("完成Miner数据加载redis");

		loadMinerOrder();
		logger.info("完成MinerOrder数据加载redis");
	}

	public void load() {
		redisTemplate.delete(MinerRedisKeys.MINER_MAP);
		List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM T_MINER ORDER BY INVESTMENT_MIN ASC");

		Map<String, Miner> cacheMap = new ConcurrentHashMap<String, Miner>();

		for (Map<String, Object> map : list) {

			// 创建ObjectMapper对象
			ObjectMapper objectMapper = new ObjectMapper();

			// 将Map转换为对象
			Miner miner = objectMapper.convertValue(map, Miner.class);

			cacheMap.put(miner.getUuid(), miner);
			redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ID + miner.getUuid(), miner);
		}

		redisTemplate.opsForValue().set(MinerRedisKeys.MINER_MAP, cacheMap);
	}

	public void loadMinerOrder() {
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM T_MINER_ORDER ");

		Map<String, Map<String, MinerOrder>> cacheMap = new ConcurrentHashMap<String, Map<String, MinerOrder>>();
		
		// 矿机总资产
		Map<String, Double> minerAssetsMap = new ConcurrentHashMap<String, Double>();

		for (Map<String, Object> map1 : list) {

			// 创建ObjectMapper对象
			ObjectMapper objectMapper = new ObjectMapper();

			MinerOrder minerOrder = JSON.parseObject(JSON.toJSONString(map1), MinerOrder.class);

			// 将Map转换为对象
			//MinerOrder minerOrder = objectMapper.convertValue(map1, MinerOrder.class);
			
			Miner miner = (Miner) this.redisTemplate.opsForValue().get(MinerRedisKeys.MINER_ID + minerOrder.getMiner_id().toString());
			if (miner.getTest().equals("N")) {
				if (cacheMap.containsKey(minerOrder.getPartyId())) {
					Map<String, MinerOrder> map = cacheMap.get(minerOrder.getPartyId().toString());
					map.put(minerOrder.getOrder_no(), minerOrder);
					cacheMap.put(minerOrder.getPartyId().toString(), map);
				} else {
					Map<String, MinerOrder> map = new ConcurrentHashMap<String, MinerOrder>();
					map.put(minerOrder.getOrder_no(), minerOrder);
					cacheMap.put(minerOrder.getPartyId().toString(), map);
				}
			}

			this.redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_ORDERNO + minerOrder.getOrder_no(), minerOrder);
			
			// 获取 单个订单 矿机总资产
			Double minerAssetsOrder = 0.000D;
			
			// 状态：0/正常赎回； 1/ 托管中 ；2/提前赎回 (违约)；3/取消；
			if ("1".equals(minerOrder.getState())) {
				minerAssetsOrder = minerOrder.getAmount();
			}

			if (minerAssetsMap.containsKey(minerOrder.getPartyId())) {
				Double minerAssetsOld = minerAssetsMap.get(minerOrder.getPartyId().toString());
				if (null == minerAssetsOld) {
					minerAssetsOld = 0.000D;
				}
				minerAssetsOld = Arith.add(minerAssetsOld, minerAssetsOrder);
				minerAssetsMap.put(minerOrder.getPartyId().toString(), minerAssetsOld);
			} else {
				minerAssetsMap.put(minerOrder.getPartyId().toString(), minerAssetsOrder);
			}
		}

		for (Entry<String, Map<String, MinerOrder>> entry : cacheMap.entrySet()) {
			this.redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ORDER_PARTY_ID + entry.getKey(), entry.getValue());
		}
		
		for (Entry<String, Double> entry : minerAssetsMap.entrySet()) {
			this.redisTemplate.opsForValue().set(MinerRedisKeys.MINER_ASSETS_PARTY_ID + entry.getKey(), entry.getValue());
		}
	}

//	public void setRedisHandler(RedisHandler redisHandler) {
//		this.redisHandler = redisHandler;
//	}

}
