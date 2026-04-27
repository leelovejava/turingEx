package com.yami.trading.service.miner.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.service.miner.service.MinerParaService;
import com.yami.trading.bean.miner.MinerPara;
import com.yami.trading.dao.miner.MinerParaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MinerParaServiceImpl extends ServiceImpl<MinerParaMapper, MinerPara> implements MinerParaService {

	public boolean save1(MinerPara entity) {
		return this.save(entity);
	}

	public void update(MinerPara entity) {
		this.update(entity);
	}

	public void delete(String id) {
		MinerPara entity = findById(id);
		this.delete(id);
	}

	public MinerPara findById(String id) {

//		return (MinerPara) getHibernateTemplate().get(MinerPara.class, id);
		return null;
	}

	public List<MinerPara> findByMinerId(String minerId) {
//		return (List<MinerPara>) this.find("FROM MinerPara WHERE miner_id=?0 ", new Object[] { minerId });
		return null;
	}
}
