package com.yami.trading.service.miner.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface AdminMinerOrderService {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String username_para, String miner_para, String status_para,
						   List<String> children, String orderNo, String rolename_para);
	
	public void addOrder(String uid,double amount,String minerId,String operator_username);
}
