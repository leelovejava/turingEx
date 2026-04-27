package com.yami.trading.service.miner.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface AdminMinerService {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para);
}
