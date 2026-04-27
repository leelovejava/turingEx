package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.dao.trader.TraderFollowUserOrderMapper;
import com.yami.trading.service.trader.AdminTraderFollowUserOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class AdminTraderFollowUserOrderServiceImpl implements AdminTraderFollowUserOrderService {

	@Resource
	private TraderFollowUserOrderMapper traderFollowUserOrderMapper;

	public Page pagedQuery(Page page, String name, String username, String rolename) {
//		StringBuffer queryString = new StringBuffer(" SELECT trader.NAME trader_name,"
//				+ " trader_user.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,"
//				+ " trader_user_order.UUID id,trader_user_order.STATE state  ,  "
//				+ " trader_user_order.VOLUME  volume,trader_user_order.USER_ORDER_NO user_order_no,  "
//				+ " trader_user_order.TRADER_ORDER_NO trader_order_no ,DATE_FORMAT(trader_user_order.CREATE_TIME, '%Y-%m-%d %H:%i:%S') create_time   ");
//		queryString.append(" FROM T_TRADER_FOLLOW_USER_ORDER trader_user_order  ");
//		queryString.append(
//				" LEFT JOIN T_TRADER_FOLLOW_USER trader_user ON  trader_user.PARTY_ID  = trader_user_order.PARTY_ID  ");
//		queryString.append(" LEFT JOIN PAT_PARTY party ON  party.UUID  = trader_user_order.PARTY_ID  ");
//		queryString.append(" LEFT JOIN T_TRADER trader ON  trader.PARTY_ID   = trader_user_order.TRADER_PARTY_ID  ");
//		queryString.append("  WHERE 1 = 1 ");
//
//		Map<String, Object> parameters = new HashMap();
//		if (StringUtils.isNotEmpty(name)) {
//			queryString.append(" AND trader.NAME =:name ");
//			parameters.put("name", name);
//		}
//		if (StringUtils.isNotEmpty(rolename)) {
//			queryString.append(" and party.ROLENAME = :rolename  ");
//			parameters.put("rolename", rolename);
//
//		}
//		if (StringUtils.isNotEmpty(username)) {
//			queryString.append("AND (trader_user.USERNAME like:username OR party.USERCODE like:username ) ");
//			parameters.put("username", "%" + username + "%");
//		}
//
//		queryString.append(" order by trader_user_order.CREATE_TIME desc ");
//		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		List<Map<String, Object>> datas = traderFollowUserOrderMapper.listMDatas((page.getCurrent() - 1) * page.getSize(), page.getSize(), name, rolename, username);
		page.setRecords(datas);
		return page;
	}

}
