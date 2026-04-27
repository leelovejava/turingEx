package com.yami.trading.service.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface AdminTraderFollowUserOrderService {

	/**
	 * 分页查询 name_para交易员名称 username 用户名
	 * 
	 */
	public Page pagedQuery(Page page, String trader_name_para, String username, String rolename);

	
}
