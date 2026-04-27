package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.bean.trader.domain.TraderFollowUser;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.dao.trader.TraderFollowUserMapper;
import com.yami.trading.service.trader.TraderFollowUserService;
import com.yami.trading.service.trader.TraderService;
import com.yami.trading.service.trader.TraderUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class TraderFollowUserServiceImpl implements TraderFollowUserService {
	@Resource
	private TraderService traderService;
	@Resource
	private TraderUserService traderUserService;

	@Resource
	private TraderFollowUserMapper traderFollowUserMapper;

	public List<Map<String, Object>> getPaged(Page pageparam, String partyId, String profit) {

//		StringBuffer queryString = new StringBuffer("");
//		queryString.append(" SELECT * FROM ");
//		queryString.append(" T_TRADER_FOLLOW_USER ");
//		queryString.append(" where 1=1 ");
//
//		Map<String, Object> parameters = new HashMap();
//
//		queryString.append(" and TRADER_PARTY_ID = :partyId");
//		parameters.put("partyId", partyId);

//		if (!StringUtils.isNullOrEmpty(profit)) {
//			queryString.append(" and PROFIT >= 0 ");
//		}
//
//		queryString.append(" order by PROFIT desc ");
		Page page = traderFollowUserMapper.selectPage(pageparam, Wrappers.<TraderFollowUser>lambdaQuery().eq(TraderFollowUser::getTraderPartyId, partyId).ge(TraderFollowUser::getProfit, 0).orderByDesc(TraderFollowUser::getProfit));
//		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		List<Map<String, Object>> data = this.bulidData(page.getRecords());
		return data;
	}

	private List<Map<String, Object>> bulidData(List<TraderFollowUser> traderFollowUsers) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 向下取整
		if (traderFollowUsers == null) {
			return result_traders;
		}
		for (int i = 0; i < traderFollowUsers.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			TraderFollowUser entity = traderFollowUsers.get(i);
			map.put("name", entity.getUsername());
			map.put("profit", df2.format(entity.getProfit()));
			map.put("amount_sum", df2.format(entity.getAmountSum()));

			result_traders.add(map);
		}

		return result_traders;

	}

	@Override
	public void save(TraderFollowUser entity, String trader_id) {
		if (entity.getVolume() % 1 != 0 || entity.getVolume() <= 0 || entity.getVolumeMax() % 1 != 0) {
			throw new BusinessException(1, "跟单参数输入错误");
		}
		if (entity.getFollowType() == "1" && (entity.getVolume() > 3000 || entity.getVolume() < 1)) {
			throw new BusinessException(1, "跟单参数输入错误");
		}
		if (entity.getFollowType() == "2" && (entity.getVolume() > 5 || entity.getVolume() < 1)) {
			throw new BusinessException(1, "跟单倍数输入错误");
		}
		Trader trader = this.traderService.findById(trader_id);
		if (trader == null) {
			throw new BusinessException(1, "交易员不存在");
		}
		if ("0".equals(trader.getState())) {
			throw new BusinessException(1, "交易员未开启带单");
		}
		if (findByStateAndPartyId(entity.getPartyId(), trader.getPartyId(), "1") != null) {
			throw new BusinessException(1, "用户已跟随交易员");
		}
		if (Arith.sub(trader.getFollowerMax(), trader.getFollowerNow()) < 1) {
			throw new BusinessException(1, "交易员跟随人数已满");
		}
		if (entity.getPartyId().equals(trader.getPartyId())) {
			throw new BusinessException(1, "交易员不能跟随自己");
		}
		Trader trader_user = this.traderService.findByPartyId(entity.getPartyId());
		if (trader_user != null) {
			throw new BusinessException(1, "交易员无法跟随另一个交易员");
		}
		// 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
		if (trader.getFollowVolumnMin() > 0) {
			switch (entity.getFollowType()) {
			case "1":
				if (entity.getVolume() < trader.getFollowVolumnMin()) {
					throw new BusinessException(1, "跟单参数输入错误");
				}
				if (entity.getVolumeMax() < trader.getFollowVolumnMin()) {
					throw new BusinessException(1, "跟单参数输入错误");
				}
				break;
			case "2":
				throw new BusinessException(1, "交易员已设置最小下单数，无法通过固定比例跟单");
			default:
				break;
			}
		}

		entity.setTraderPartyId(trader.getPartyId());
		entity.setCreateTime(new Date());

		trader.setFollowerNow((int) Arith.add(trader.getFollowerNow(), 1));
		trader.setFollowerSum((int) Arith.add(trader.getFollowerSum(), 1));
		traderService.update(trader);
		/**
		 * 创建累计用户跟随累计表
		 */
		traderUserService.saveTraderUserByPartyId(entity.getPartyId());

//		ApplicationUtil.executeSaveOrUpdate(entity);
		traderFollowUserMapper.insert(entity);

	}

	@Override
	public void save(TraderFollowUser entity) {
		traderFollowUserMapper.insert(entity);
	}

	@Override
	public void update(TraderFollowUser entity) {
		if (entity.getVolume() % 1 != 0 || entity.getVolume() <= 0 || entity.getVolumeMax() % 1 != 0) {
			throw new BusinessException(1, "跟单参数输入错误");
		}
		if (entity.getFollowType() == "1" && (entity.getVolume() > 3000 || entity.getVolume() < 1)) {
			throw new BusinessException(1, "跟单参数输入错误");
		}
		if (entity.getFollowType() == "2" && (entity.getVolume() > 5 || entity.getVolume() < 1)) {
			throw new BusinessException(1, "跟单倍数输入错误");
		}

//		ApplicationUtil.executeUpdate(entity);
		traderFollowUserMapper.updateById(entity);
	}

	@Override
	public void deleteCancel(String id) {
		TraderFollowUser entity = findById(id);
		/**
		 * 将旧的交易员跟随用户-1
		 */
		Trader trader_before = this.traderService.findByPartyId(entity.getTraderPartyId().toString());
		trader_before.setFollowerNow((int) Arith.sub(trader_before.getFollowerNow(), 1));
		this.traderService.update(trader_before);

		if (entity != null) {
//			ApplicationUtil.executeDelete(entity);
			traderFollowUserMapper.deleteById(entity);
		}

	}

	public List<TraderFollowUser> findByStateAndPartyId(String partyId, String trader_partyId, String state) {
		List<TraderFollowUser> list = traderFollowUserMapper.selectList(Wrappers.<TraderFollowUser>lambdaQuery().eq(TraderFollowUser::getPartyId, partyId).eq(TraderFollowUser::getTraderPartyId, trader_partyId).eq(TraderFollowUser::getState, state));
//		List<TraderFollowUser> list = ApplicationUtil.executeSelect(TraderFollowUser.class,
//				" WHERE PARTY_ID = ? AND TRADER_PARTY_ID = ?  AND STATE = ?  ",
//				new Object[] { partyId, trader_partyId, state });
		if (list.size() > 0)
			return list;
		return null;
	}

	public List<TraderFollowUser> findByTrader_partyId(String trader_partyId) {
		List<TraderFollowUser> list = traderFollowUserMapper.selectList(Wrappers.<TraderFollowUser>lambdaQuery().eq(TraderFollowUser::getTraderPartyId, trader_partyId));
//		List<TraderFollowUser> list = ApplicationUtil.executeSelect(TraderFollowUser.class, " WHERE TRADER_PARTY_ID = ? ",
//				new Object[] { trader_partyId });
		if (list.size() > 0)
			return list;
		return null;
	}

	public List<TraderFollowUser> findByPartyId(String partyId) {
		List<TraderFollowUser> list = traderFollowUserMapper.selectList(Wrappers.<TraderFollowUser>lambdaQuery().eq(TraderFollowUser::getPartyId, partyId));
//		List<TraderFollowUser> list = ApplicationUtil.executeSelect(TraderFollowUser.class, " WHERE PARTY_ID = ? ",
//				new Object[] { partyId });
		if (list.size() > 0)
			return list;
		return null;
	}

	public TraderFollowUser findByPartyIdAndTrader_partyId(String partyId, String trader_partyId) {
		List<TraderFollowUser> list = traderFollowUserMapper.selectList(Wrappers.<TraderFollowUser>lambdaQuery().eq(TraderFollowUser::getPartyId, partyId).eq(TraderFollowUser::getTraderPartyId, trader_partyId));
//		List<TraderFollowUser> list = ApplicationUtil.executeSelect(TraderFollowUser.class,
//				" WHERE PARTY_ID= ? and TRADER_PARTY_ID = ? ",
//				new Object[] { partyId, trader_partyId });
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public TraderFollowUser findById(String id) {
//		return ApplicationUtil.executeGet(id, TraderFollowUser.class);
		TraderFollowUser traderFollowUser = traderFollowUserMapper.selectById(id);
		return traderFollowUser;
	}

}
