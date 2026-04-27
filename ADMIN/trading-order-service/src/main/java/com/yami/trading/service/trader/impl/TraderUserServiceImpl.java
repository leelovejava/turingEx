package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.trader.domain.TraderUser;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.dao.trader.TraderUserMapper;
import com.yami.trading.service.trader.TraderUserService;
import com.yami.trading.service.user.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TraderUserServiceImpl implements TraderUserService {
	@Resource
	private UserService userService;

	@Resource
	private TraderUserMapper traderUserMapper;
	
	
	public List<Map<String, Object>> getPaged(Page pageparam, String partyId, String type){
		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM ");
		queryString.append(" T_TRADER_USER ");
		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();

			queryString.append(" and PARTY_ID = :partyId");
			parameters.put("partyId",  partyId );

//		if (!StringUtils.isNullOrEmpty(state)) {
//			queryString.append(" and state =:state ");
//			parameters.put("state", state);
//		}

		Page page = traderUserMapper.selectPage(pageparam, Wrappers.<TraderUser>lambdaQuery().eq(TraderUser::getPartyId, partyId));
//		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		List<Map<String, Object>> data = this.bulidData(page.getRecords());
		return data;
	}
	
	private List<Map<String, Object>> bulidData(List<TraderUser> traderUsers) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 向下取整
		if (traderUsers == null) {
			return result_traders;
		}
		for (int i = 0; i < traderUsers.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			TraderUser entity = traderUsers.get(i);
			map.put("name", entity.getName());
			/**
			 * 累计金额order_amount
			 */
			map.put("amount_sum", df2.format(entity.getAmountSum()));

			map.put("profit", df2.format(entity.getProfit()));

			result_traders.add(map);
		}

		return result_traders;

	}
	

	@Override
	public TraderUser saveTraderUserByPartyId(Serializable partyId) {
		List<TraderUser> list = traderUserMapper.selectList(Wrappers.<TraderUser>lambdaQuery().eq(TraderUser::getPartyId, partyId));
		if (list.size() > 0) {
			TraderUser traderUser = list.get(0);
			return traderUser;

		} else {
			User party = userService.cacheUserBy(partyId.toString());
			TraderUser traderUser = new TraderUser();
			traderUser.setUuid(ApplicationUtil.getCurrentTimeUUID());
			traderUser.setPartyId(partyId.toString());
			traderUser.setName(party.getUserName());
			traderUser.setCreateTime(party.getCreateTime());
			save(traderUser);
			return traderUser;
		}
	}

	@Override
	public void save(TraderUser traderUser) {
		traderUserMapper.insert(traderUser);
		
	}

	@Override
	public void update(TraderUser traderUser) {
		traderUserMapper.updateById(traderUser);
		
	}

}
