package com.yami.trading.service.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.TraderUser;

import java.util.List;

/**
 * 用户跟随交易员记录
 */
public interface AdminTraderUserService {

	/**
	 * 分页查询
	 */
	public Page pagedQuery(Page page, String name_para, String username);

	public void save(TraderUser entity);

	public void delete(String id);

	public void update(TraderUser entity);

	public List<TraderUser> findByPartyId(String partyId);

//	public List<TraderUser> findByTraderPartyId(String traderPartyId);

	public TraderUser findById(String id);
}
