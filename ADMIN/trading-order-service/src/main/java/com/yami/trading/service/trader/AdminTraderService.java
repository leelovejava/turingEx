package com.yami.trading.service.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.Trader;

public interface AdminTraderService {

	/**
	 * 分页查询
	 */
	public Page pagedQuery(Page page, String name_para, String username);

	public void save(Trader entity);

	public void delete(String id);

	public void update(Trader entity);

	public Trader findByPartyId(String partyId);
	public Trader findById(String id);
}
