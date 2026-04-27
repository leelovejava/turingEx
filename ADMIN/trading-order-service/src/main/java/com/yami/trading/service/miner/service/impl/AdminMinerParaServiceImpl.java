package com.yami.trading.service.miner.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.service.miner.service.AdminMinerParaService;
import com.yami.trading.bean.miner.MinerPara;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.miner.MinerParaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminMinerParaServiceImpl extends ServiceImpl<MinerParaMapper, MinerPara> implements AdminMinerParaService {

//	private PagedQueryDao pagedQueryDao;

	public Page pagedQuery(int pageNo, int pageSize, String miner_id) {
		StringBuffer queryString = new StringBuffer(
				" SELECT minerPara.UUID id,minerPara.MINER_ID miner_id,minerPara.CYCLE cycle,minerPara.AMOUNT amount ");
		queryString.append(" FROM T_MINER_PARA minerPara WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(miner_id)) {
			queryString.append(" and  minerPara.MINER_ID =:miner_id ");
			parameters.put("miner_id", miner_id);
		}
		queryString.append(" ORDER BY minerPara.CYCLE+0 ASC ");
//		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		return page;
		return null;
	}

//	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
//		this.pagedQueryDao = pagedQueryDao;
//	}
}
