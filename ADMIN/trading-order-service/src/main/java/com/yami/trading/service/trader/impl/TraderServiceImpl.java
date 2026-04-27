package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.trader.TraderMapper;
import com.yami.trading.service.trader.TraderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TraderServiceImpl implements TraderService {

	@Resource
	private TraderMapper traderMapper;

	public Trader findById(String id) {

        return traderMapper.selectById(id);
	}

	public Trader findByPartyId(String partyId) {
		LambdaQueryWrapper<Trader> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(Trader::getPartyId, partyId);
		Trader trader = traderMapper.selectOne(lambdaQueryWrapper);
		return trader;
	}

	@Override
	public Trader findByPartyIdAndChecked(String partyId, int checked) {
		LambdaQueryWrapper<Trader> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(Trader::getPartyId, partyId).eq(Trader::getChecked, checked);
        return traderMapper.selectOne(lambdaQueryWrapper);
	}

	public List<Map<String, Object>> getPaged(Page<Trader> pageparam, String name, String state) {
		LambdaQueryWrapper<Trader> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//		StringBuffer queryString = new StringBuffer(" SELECT * ");
//		queryString.append(" FROM ");
//		queryString.append(" T_TRADER ");
//		queryString.append(" where 1=1 ");

		Map<String, Object> parameters = new HashMap();

		if (!StringUtils.isNullOrEmpty(name)) {
			lambdaQueryWrapper.like(Trader::getName, name);
//			queryString.append(" and name like :name");
//			parameters.put("name", "%" + name + "%");
		}

		if (!StringUtils.isNullOrEmpty(state)) {
			lambdaQueryWrapper.eq(Trader::getState, state);
//			queryString.append(" and state =:state ");
//			parameters.put("state", state);
		}
//		if (!StringUtils.isNullOrEmpty(orderBy_type) && !"create_time".equals(orderBy_type)) {
//			lambdaQueryWrapper.orderByDesc(Trader::getCreate_time);
////			queryString.append(" order by (" + orderBy_type + "+ DEVIATION_" + orderBy_type + ") desc ");
////			parameters.put("orderBy_type", orderBy_type);
//		} else {
////			queryString.append(" order by create_time desc ");
//			lambdaQueryWrapper.orderByDesc(Trader::getCreate_time);
//		}
		lambdaQueryWrapper.eq(Trader::getDelFlag, 0);
		lambdaQueryWrapper.orderByDesc(Trader::getCreateTime);

		IPage<Trader> page = traderMapper.selectPage(pageparam, new LambdaQueryWrapper<>());

//		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		List<Map<String, Object>> data = this.bulidData(page.getRecords());
		return data;

	}

	@Override
	public void save(Trader trader) {
		traderMapper.insert(trader);
	}




	private List<Map<String, Object>> bulidData(List<Trader> traders) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 鍚戜笅鍙栨暣
		if (traders == null) {
			return result_traders;
		}
		for (int i = 0; i < traders.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
//			Trader entity = BeanUtil.mapToBean(traders.get(i), Trader.class, true);
			Trader entity = traders.get(i);
			String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + entity.getImg();
			map.put("img", path);
			map.put("id", entity.getUuid());
			map.put("partyId", entity.getPartyId());
			map.put("name", entity.getName());
			map.put("remarks", entity.getRemarks());
//			map.put("symbols", "btc;eth");
			map.put("symbol_name", entity.getSymbols());
			map.put("follower_max", entity.getFollowerMax());

			/**
			 * 杩�3鍛ㄦ敹鐩妛eek_3_profit
			 */
			map.put("week_3_profit",
					df2.format(Arith.add(entity.getWeek3Profit(), entity.getDeviationWeek3Profit())));
			/**
			 * 杩�3鍛ㄧ疮璁￠噾棰漺eek_3_order_amount
			 */
			map.put("week_3_order_amount",
					df2.format(Arith.add(entity.getWeek3OrderAmount(), entity.getDeviationWeek3OrderAmount())));

			/**
			 * 杩�3鍛ㄧ泩鍒╃瑪鏁皐eek_3_order_profit
			 */
			map.put("week_3_order_profit",
					df2.format(Arith.add(entity.getWeek3OrderProfit(), entity.getDeviationWeek3OrderProfit())));
			/**
			 * 杩�3鍛ㄤ氦鏄撶瑪鏁皐eek_3_order_sum
			 */
			map.put("week_3_order_sum",
					df2.format(Arith.add(entity.getWeek3OrderSum(), entity.getDeviationWeek3OrderSum())));

			/**
			 * 绱閲戦order_amount
			 */
			map.put("order_amount",
					df2.format(Arith.add(entity.getOrderAmount(), entity.getDeviationOrderAmount())));

//			map.put("symbol_name", "BTC/USDT;ETH/USDT");
			map.put("profit", df2.format(Arith.add(entity.getProfit(), entity.getDeviationProfit())));

			map.put("order_profit", (int) Arith.add(entity.getOrderProfit(), entity.getDeviationOrderProfit()));

			map.put("order_loss", (int) Arith.add(entity.getOrderLoss(), entity.getDeviationOrderLoss()));
			map.put("order_sum", (int) Arith.add(Arith.add(entity.getOrderProfit(), entity.getOrderLoss()),
					Arith.add(entity.getDeviationOrderProfit(), entity.getDeviationOrderLoss())));
			map.put("follower_sum", (int) Arith.add(entity.getFollowerSum(), entity.getDeviationFollowerSum()));

			map.put("follower_now", (int) Arith.add(entity.getFollowerNow(), entity.getDeviationFollowerNow()));
			// Deviation_w

			/**
			 * 杩�3鍛ㄦ敹鐩婄巼 = 杩�3鍛ㄦ敹鐩�/杩�3鍛ㄧ疮璁￠噾棰� + 杩�3鍛ㄦ敹鐩婄巼鍋忓樊鍊�
			 */
			map.put("week_3_profit_ratio",
					df2.format(Arith.add(Arith.mul(entity.getDeviationWeek3ProfitRatio(), 100),
							Arith.mul(entity.getWeek3ProfitRatio(), 100))));
			/**
			 * 杩�3鍛ㄨ儨鐜� week_3_order_profit_ratio
			 */
			double week_3_order_profit_ratio = 0;
			if(Arith.add(entity.getWeek3OrderSum(), entity.getDeviationWeek3OrderSum()) == 0) {
				week_3_order_profit_ratio = 0;
			}else {
				week_3_order_profit_ratio = Arith.mul(
						Arith.div(Arith.add(entity.getWeek3OrderProfit(), entity.getDeviationWeek3OrderProfit()),
								Arith.add(entity.getWeek3OrderSum(), entity.getDeviationWeek3OrderSum())),
						100);
			}
			
			if (week_3_order_profit_ratio > 100) {
				week_3_order_profit_ratio = 100;
			}
			if (week_3_order_profit_ratio < 0) {
				week_3_order_profit_ratio = 0;
			}

			map.put("week_3_order_profit_ratio", df2.format(week_3_order_profit_ratio));

			/**
			 * 绱鏀剁泭鐜�
			 */
			map.put("profit_ratio", df2.format(Arith.add(Arith.mul(entity.getDeviationProfitRatio(), 100),
					Arith.mul(entity.getProfitRatio(), 100))));

			map.put("profit_share_ratio", df2.format(Arith.mul(entity.getProfitShareRatio(), 100)));

			result_traders.add(map);
		}

		return result_traders;

	}
	
	
	public void update(Trader entity) {
		int result = traderMapper.updateById(entity);
	}
	
	public void updateTrader(Trader entity, List<ContractOrder> orders) {
//		List<ContractOrder> orders =  contractOrderService.findSubmitted(entity.getPartyId().toString(),"","");
		double week_3_profit = 0;
		double week_3_order_amount = 0;;
		if(orders != null) {
			Date date_now = new Date();
			for(ContractOrder order : orders) {
				double last_days = 22;
				try {
					last_days = daysBetween(date_now, order.getCreateTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (last_days <= 21 ) {
					week_3_profit = Arith.add(week_3_profit, order.getProfit().doubleValue());
					week_3_order_amount = Arith.add(week_3_order_amount, Arith.mul(order.getVolumeOpen().doubleValue(), order.getUnitAmount().doubleValue()));
				} 
			}
			if(week_3_order_amount != 0) {
				entity.setWeek3ProfitRatio(Arith.div(week_3_profit, week_3_order_amount));
			}else {
				entity.setWeek3ProfitRatio(0);
			}
		}
		if( entity.getOrderAmount() != 0) {
			entity.setProfitRatio(Arith.div(entity.getProfit(), entity.getOrderAmount()));
		}else {
			entity.setProfitRatio(0);
		}
		
		update(entity);
	}
	
	
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}
	

}
