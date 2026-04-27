package com.yami.trading.service.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.finance.Finance;
import com.yami.trading.bean.finance.FinanceOrder;

import java.util.List;

public interface AdminFinanceOrderService extends IService<FinanceOrder> {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String username_para, String finance_para, String status_para,
						   List<String> children, String orderNo, String rolename_para , String createTime , String closeTime);
}
