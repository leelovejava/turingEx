package com.yami.trading.service.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.TraderFollowUser;

public interface AdminTraderFollowUserService {

	/**
	 * 分页查询 name_para交易员名称 username 用户名
	 * 
	 */
	public Page pagedQuery(Page page, String name_para, String username);

	public void delete(String id);

	public void update(TraderFollowUser entity);

	public void save(TraderFollowUser entity, String trader_id);


	public TraderFollowUser findById(String id);
}
