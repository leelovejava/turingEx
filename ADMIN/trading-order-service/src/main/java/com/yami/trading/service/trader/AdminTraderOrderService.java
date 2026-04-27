package com.yami.trading.service.trader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.TraderOrder;

public interface AdminTraderOrderService {

	/**
	 * 分页查询 name_para交易员名称 username 用户名
	 * 
	 */
	public Page pagedQuery(Page page, String trader_name_para, String username, String rolename);
	
	public void delete(String id);

	public void update(TraderOrder entity);

	public void save(TraderOrder entity);


	public TraderOrder findById(String id);
	
}
