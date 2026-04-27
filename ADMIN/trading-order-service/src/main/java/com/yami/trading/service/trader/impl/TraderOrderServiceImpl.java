package com.yami.trading.service.trader.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.trader.domain.TraderOrder;
import com.yami.trading.dao.trader.TraderOrderMapper;
import com.yami.trading.service.trader.TraderOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TraderOrderServiceImpl implements TraderOrderService {

	@Resource
	private TraderOrderMapper traderOrderMapper;

	public List<Map<String, Object>> getPaged(Page page, String partyId) {
//		StringBuffer queryString = new StringBuffer(" " + " SELECT trader_order.SYMBOL symbol , "
//				+ " trader_order.TRADE_AVG_PRICE trade_avg_price,  " + " trader_order.DIRECTION direction , "
//				+ "  trader_order.STATE state,trader_order.PROFIT profit, "
//				+ " trader_order.CLOSE_AVG_PRICE close_avg_price,trader_order.CHANGE_RATIO change_ratio , "
//				+ "  DATE_FORMAT(trader_order.CLOSE_TIME, '%Y-%m-%d %H:%i:%S') close_time,DATE_FORMAT(trader_order.CREATE_TIME, '%Y-%m-%d %H:%i:%S') create_time, "
//				+ "  trader_order.VOLUME_OPEN volume_open,item.NAME itemname, " + " trader_order.ORDER_NO order_no   ");
//		queryString.append(" FROM T_TRADER_ORDER trader_order  ");
//		queryString.append(" LEFT JOIN T_ITEM item ON trader_order.SYMBOL = item.SYMBOL  ");
//		queryString.append("  WHERE 1 = 1 ");
//		Map<String, Object> parameters = new HashMap();
//		queryString.append(" and trader_order.PARTY_ID =:partyId");
//		parameters.put("partyId", partyId);
//
//		queryString.append(" order by trader_order.CREATE_TIME desc ");
		List<Map<String, Object>> traders = traderOrderMapper.listDatas((page.getCurrent() - 1) * page.getSize(), page.getSize(), partyId);

		List<Map<String, Object>> data = this.bulidData(traders);
		return data;
	}

	private List<Map<String, Object>> bulidData(List<Map<String, Object>> traders) {
		List<Map<String, Object>> result_traders = new ArrayList();
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.FLOOR);// 向下取整
		if (traders == null) {
			return result_traders;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < traders.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> entity = traders.get(i);
			map.put("order_no", entity.get("order_no"));
			map.put("name", entity.get("itemname"));
			map.put("close_avg_price", entity.get("close_avg_price"));
			map.put("trade_avg_price", entity.get("trade_avg_price"));
			map.put("direction", entity.get("direction"));
			map.put("state", entity.get("state"));
			map.put("profit", entity.get("profit"));
			map.put("lever_rate", entity.get("lever_rate"));
			map.put("follow_now", entity.get("follow_now"));
			map.put("follow_max", entity.get("follow_max"));
			if (entity.get("close_time") != null) {
				try {
					map.put("close_time", sdf.parse(entity.get("close_time").toString()));
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			} else {
				map.put("close_time", "");
			}
			// 验证下以 map 方式返回的日期类型字段，在 java 里是使用 String 来接，还是 Date 来接 TODO
			if (entity.get("create_time") != null) {
				try {
					map.put("create_time", sdf.parse(entity.get("create_time").toString()));
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			} else {
				map.put("create_time", "");
			}

			map.put("volume_open", entity.get("volume_open"));
			map.put("itemname", entity.get("itemname"));
			map.put("change_ratio", entity.get("change_ratio"));

			result_traders.add(map);
		}

		return result_traders;

	}

	@Override
	public void delete(String id) {
//		TraderOrder entity = findById(id);
//		if (entity != null) {
//			ApplicationUtil.executeDelete(entity);
//		}
		traderOrderMapper.deleteById(id);
	}

	@Override
	public void update(TraderOrder entity) {
//		ApplicationUtil.executeUpdate(entity);
		traderOrderMapper.updateById(entity);
	}

	@Override
	public void save(TraderOrder entity) {
//		ApplicationUtil.executeSaveOrUpdate(entity);
		traderOrderMapper.insert(entity);
	}

	@Override
	public TraderOrder findById(String id) {
//		return ApplicationUtil.executeGet(id, TraderOrder.class);
		TraderOrder traderOrder = traderOrderMapper.selectById(id);
		return traderOrder;
	}

}
