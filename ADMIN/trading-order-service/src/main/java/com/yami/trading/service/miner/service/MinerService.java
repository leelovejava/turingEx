package com.yami.trading.service.miner.service;

import com.yami.trading.bean.miner.Miner;

import java.util.List;
import java.util.Map;


/**
 * 矿机
 * 
 * @author User
 *
 */
public interface MinerService {

	public boolean save(Miner miner);

	public void updateA(Miner miner);

	public Miner findById(String id);

	public void delete(String id);

	public List<Miner> findAll();

	public List<Miner> findAllState_1();

	public Miner cacheById(String id);
	
	public Map<String,Object> getBindOne(Miner miner);

}
