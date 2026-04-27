package com.yami.trading.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.finance.Finance;


public interface AdminFinanceService extends IService<Finance> {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para);
}
