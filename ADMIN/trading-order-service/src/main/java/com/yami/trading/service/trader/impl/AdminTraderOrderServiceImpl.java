package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.TraderOrder;
import com.yami.trading.dao.trader.TraderOrderMapper;
import com.yami.trading.service.trader.AdminTraderOrderService;
import com.yami.trading.service.trader.TraderOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class AdminTraderOrderServiceImpl implements AdminTraderOrderService {

	@Resource
	private TraderOrderMapper traderOrderMapper;
	@Resource
	private TraderOrderService traderOrderService;

	public Page pagedQuery(Page page, String name, String username, String rolename) {
		List<Map<String, Object>> datas = traderOrderMapper.listMDatas((page.getCurrent() - 1) * page.getSize(), page.getSize(), name, rolename, username);
		page.setRecords(datas);
		return page;
	}

	@Override
	public void delete(String id) {
		traderOrderService.delete(id);
		
	}

	@Override
	public void update(TraderOrder entity) {
		traderOrderService.update(entity);
		
	}

	@Override
	public void save(TraderOrder entity) {
		traderOrderService.save(entity);
		
	}

	@Override
	public TraderOrder findById(String id) {
		return traderOrderService.findById(id);
	}

	
}
