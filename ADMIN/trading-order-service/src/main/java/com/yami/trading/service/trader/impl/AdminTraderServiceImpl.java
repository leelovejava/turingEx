package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.dao.trader.TraderMapper;
import com.yami.trading.service.trader.AdminTraderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class AdminTraderServiceImpl implements AdminTraderService {

	@Resource
	private TraderMapper traderMapper;

	@Override
	public void save(Trader entity) {
		int result = traderMapper.insert(entity);
//		ApplicationUtil.executeSaveOrUpdate(entity);

	}

	@Override
	public void update(Trader entity) {
		int result = traderMapper.updateById(entity);
//		ApplicationUtil.executeUpdate(entity);

	}


	
	public Trader findByPartyId(String partyId) {
//		StringBuffer queryString = new StringBuffer(" where PARTY_ID=?");
//		String sql = queryString.toString();
//		List<Trader> list = ApplicationUtil.executeSelect(Trader.class, sql, new Object[] { partyId });
//		if (list.size() > 0) {
//			return list.get(0);
//		}
//		return null;
        return traderMapper.selectOne(Wrappers.<Trader>lambdaQuery().eq(Trader::getPartyId, partyId));
	}
	
	public Trader findById(String id) {
        return traderMapper.selectById(id);
//		return ApplicationUtil.executeGet(id, Trader.class);
	}

	@Override
	public void delete(String id) {
//		Trader entity = findById(id);
//		if (entity != null) {
//			ApplicationUtil.executeDelete(entity);
//		}
		traderMapper.deleteById(id);
	}

	public Page pagedQuery(Page page, String name,String username) {
//		StringBuffer queryString = new StringBuffer(" SELECT trader.NAME name,"
//				+ " party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,"
//				+ " trader.UUID id,trader.REMARKS remarks ,trader.SYMBOLS  symbols,"
//				+ " trader.PROFIT profit ,trader.PROFIT_RATIO profit_ratio ,trader.ORDER_PROFIT  order_profit,"
//				+ " trader.ORDER_LOSS order_loss  , trader.ORDER_SUM order_sum  , trader.FOLLOWER_SUM follower_sum  ,"
//				+ " trader.FOLLOWER_NOW follower_now  ,"
//				+ " trader.DEVIATION_PROFIT deviation_profit ,trader.DEVIATION_PROFIT_RATIO deviation_profit_ratio ,"
//				+ "trader.DEVIATION_ORDER_PROFIT  deviation_order_profit,"
//				+ " trader.DEVIATION_ORDER_LOSS deviation_order_loss  , trader.DEVIATION_ORDER_SUM deviation_order_sum  , "
//				+ "trader.DEVIATION_FOLLOWER_SUM deviation_follower_sum  ,"
//				+ " trader.DEVIATION_FOLLOWER_NOW deviation_follower_now  ,"
//				+ " trader.PROFIT_SHARE_RATIO profit_share_ratio  ,trader.STATE state  ,  "
//				+ "trader.FOLLOWER_MAX  follower_max,  "
//				+ " trader.IMG img  ,DATE_FORMAT(trader.CREATE_TIME, '%Y-%m-%d %H:%i:%S') create_time   ");
//		queryString.append(" FROM T_TRADER trader  ");
//		queryString.append(" LEFT JOIN PAT_PARTY party ON  party.UUID  = trader.PARTY_ID  ");
//		queryString.append("  WHERE 1 = 1 ");
//
//		Map<String, Object> parameters = new HashMap();
//		if (StringUtils.isNotEmpty(name)) {
//			queryString.append(" AND trader.NAME =:name ");
//			parameters.put("name", name);
//		}
//		if (StringUtils.isNotEmpty(username)) {
//			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
//			parameters.put("username","%"+username+"%");
//		}
//
//		queryString.append(" order by trader.CREATE_TIME desc ");
		List<Map<String, Object>> datas = traderMapper.listDatas((page.getCurrent() - 1) * page.getSize(), page.getSize(), name, username);
		page.setRecords(datas);
		return page;
	}


}
