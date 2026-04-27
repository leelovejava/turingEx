package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.TraderUser;
import com.yami.trading.dao.trader.TraderUserMapper;
import com.yami.trading.service.trader.AdminTraderUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class AdminTraderUserServiceImpl implements AdminTraderUserService {

	@Resource
	private TraderUserMapper traderUserMapper;

	@Override
	public void save(TraderUser entity) {
		traderUserMapper.insert(entity);
//		ApplicationUtil.executeSaveOrUpdate(entity);

	}

	@Override
	public void update(TraderUser entity) {
		traderUserMapper.updateById(entity);
//		ApplicationUtil.executeUpdate(entity);

	}

	public List<TraderUser> findByPartyId(String partyId) {
		List<TraderUser> traderUsers = traderUserMapper.selectList(Wrappers.<TraderUser>lambdaQuery().eq(TraderUser::getPartyId, partyId));
		return traderUsers;
//		StringBuffer queryString = new StringBuffer(" where PARTY_ID=?");
//		List<TraderUser> list = ApplicationUtil.executeSelect(TraderUser.class, queryString.toString(), new Object[] { partyId });
//		if (list.size() > 0) {
//			return list;
//		}
//		return null;
	}

//	public List<TraderUser> findByTraderPartyId(String trader_partyId) {
//		StringBuffer queryString = new StringBuffer(" FROM TraderUser where PARTY_ID=?");
//		List<TraderUser> list = ApplicationUtil.executeSelect(TraderUser.class, queryString.toString(), new Object[] { trader_partyId });
//		if (list.size() > 0) {
//			return list;
//		}
//		return null;
//	}

	public TraderUser findById(String id) {
		TraderUser traderUser = traderUserMapper.selectById(id);
//		return ApplicationUtil.executeGet(id, TraderUser.class);
		return traderUser;
	}

	@Override
	public void delete(String id) {
//		TraderUser entity = findById(id);
//		if (entity != null) {
//			ApplicationUtil.executeDelete(entity);
//		}
		traderUserMapper.deleteById(id);
	}

	public Page pagedQuery(Page page, String name, String username) {
//		StringBuffer queryString = new StringBuffer(" SELECT trader_user.NAME name,"
//				+ " party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,"
//				+ " trader_user.UUID id,trader_user.PROFIT profit ,trader_user.AMOUNT_SUM  amount_sum,"
//				+ " DATE_FORMAT(trader_user.CREATE_TIME, '%Y-%m-%d %H:%i:%S') create_time   ");
//		queryString.append(" FROM T_TRADER_USER trader_user  ");
//		queryString.append(" LEFT JOIN PAT_PARTY party ON  party.UUID  = trader_user.PARTY_ID  ");
//		queryString.append("  WHERE 1 = 1 ");
//
//		Map<String, Object> parameters = new HashMap();
//		if (StringUtils.isNotEmpty(name)) {
//			queryString.append(" AND T_TRADER_USER.NAME =:name ");
//			parameters.put("name", name);
//		}
//		if (StringUtils.isNotEmpty(username)) {
//			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
//			parameters.put("username", "%" + username + "%");
//		}
//
//		queryString.append(" order by trader_user.CREATE_TIME desc ");
		List<Map<String, Object>> datas = traderUserMapper.listDatas((page.getCurrent() - 1) * page.getSize(), page.getSize(), name, username);
		page.setRecords(datas);
		return page;
	}

}
