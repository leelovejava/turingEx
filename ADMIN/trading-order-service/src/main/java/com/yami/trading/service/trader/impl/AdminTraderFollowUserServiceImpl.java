package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.TraderFollowUser;
import com.yami.trading.dao.trader.TraderFollowUserMapper;
import com.yami.trading.service.trader.AdminTraderFollowUserService;
import com.yami.trading.service.trader.TraderFollowUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class AdminTraderFollowUserServiceImpl implements AdminTraderFollowUserService {

	@Resource
	TraderFollowUserMapper traderFollowUserMapper;
	@Resource
	private TraderFollowUserService traderFollowUserService;

	@Override
	public void save(TraderFollowUser entity, String trader_id) {
		traderFollowUserService.save(entity, trader_id);

	}

	public TraderFollowUser findById(String id) {
		TraderFollowUser traderFollowUser = traderFollowUserMapper.selectById(id);
//		return ApplicationUtil.executeGet(id, TraderFollowUser.class);
		return traderFollowUser;
	}

	@Override
	public void update(TraderFollowUser entity) {
//		this.traderFollowUserService.update(entity);
		int result = traderFollowUserMapper.updateById(entity);
	}

	@Override
	public void delete(String id) {
//		TraderFollowUser entity = findById(id);
//		if (entity != null) {
//			this.traderFollowUserService.deleteCancel(id);
//		}
		traderFollowUserMapper.deleteById(id);
	}

	public Page pagedQuery(Page page, String name, String username) {
		List<Map<String, Object>> datas = traderFollowUserMapper.listDatas((page.getCurrent() - 1) * page.getSize(), page.getSize(), name, username);
		page.setRecords(datas);
		return page;
	}

}
