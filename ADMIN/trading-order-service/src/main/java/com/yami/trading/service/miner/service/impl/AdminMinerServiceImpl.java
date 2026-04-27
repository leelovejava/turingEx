package com.yami.trading.service.miner.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.service.miner.service.AdminMinerService;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.dao.miner.MinerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminMinerServiceImpl extends ServiceImpl<MinerMapper, Miner> implements AdminMinerService {
//	protected PagedQueryDao pagedQueryDao;

	public Page pagedQuery(int pageNo, int pageSize, String name_para) {
//		StringBuffer queryString = new StringBuffer(
//				" SELECT miner.UUID id,miner.NAME name,miner.NAME_EN name_en,miner.NAME_CN name_cn,miner.IMG img,miner.CYCLE cycle,miner.CYCLE_CLOSE cycle_close, "
//						+ " miner.SHOW_DAILY_RATE show_daily_rate  ,miner.DAILY_RATE daily_rate  ,miner.STATE state,miner.ON_SALE on_sale,miner.TEST test,"
//						+ " miner.INVESTMENT_MIN investment_min,miner.INVESTMENT_MAX investment_max ");
//		queryString.append(" FROM T_MINER miner WHERE 1 = 1 ");
//		Map<String, Object> parameters = new HashMap<>();
//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append(" and  miner.NAME like:name ");
//			parameters.put("name", "%" + name_para + "%");
//		}
//		queryString.append(" ORDER BY miner.INVESTMENT_MIN+0 ASC ");

//		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		return page;

		Page page = new Page(pageNo,pageSize);
		this.baseMapper.pagedQuery(page,name_para);

		return page;
	}

//	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
//		this.pagedQueryDao = pagedQueryDao;
//	}
}
