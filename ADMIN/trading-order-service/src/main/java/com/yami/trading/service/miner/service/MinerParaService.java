package com.yami.trading.service.miner.service;

import com.yami.trading.bean.miner.MinerPara;

import java.util.List;

public interface MinerParaService {

	public boolean save1(MinerPara entity);

	public void update(MinerPara entity);

	public void delete(String id);

	public MinerPara findById(String id);

	public List<MinerPara> findByMinerId(String minerId);
}
