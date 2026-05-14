package com.yami.trading.huobi.data.internal;

import cn.hutool.core.util.StrUtil;
import com.yami.trading.bean.data.domain.*;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("dataService")
@Slf4j
public class DataServiceImpl implements DataService {
	@Autowired
	SysparaService sysparaService;
	@Autowired
	ItemService itemService;
	@Autowired
	KlineService klineService;
	@Autowired
	com.yami.trading.huobi.hobi.internal.XueQiuDataServiceImpl xueQiuDataService;

	/**
	 * 根据币种分类 获取实时价格数据
	 */
	@Override
	public List<Realtime> realtimeByType(String symbolType) {
		List<Item> items = new ArrayList<>(itemService.list()).stream().filter(item -> item.getType().equals(symbolType)).collect(Collectors.toList());
		List<Realtime> list = new ArrayList<>();
		items.forEach(item -> {
			Realtime realtime = DataCache.getRealtime(item.getSymbol());
			if (null != realtime) {
				list.add(realtime);
			}
		});
		return list;
	}

	/**
	 * 行情实时价格
	 */
	@Override
	public List<Realtime> realtime(String symbols) {
		List<Realtime> list = new ArrayList<>();
		// 单个币种
		if (!StrUtil.isEmpty(symbols) && !symbols.contains(",")) {
			Realtime realtime = DataCache.getRealtime(symbols);
			if (realtime != null) {
				list.add(realtime);
			}
		}
		// 如果不传参数 返回全部币种行情
		else if (StrUtil.isEmpty(symbols)) {
			List<Item> items = new ArrayList<>(itemService.list());
			for (Item item : items) {
				Realtime realtime = DataCache.getRealtime(item.getSymbol());
				if (realtime != null) {
					list.add(realtime);
				}
			}
		}
		// 多个币种
		else if (!StrUtil.isEmpty(symbols) && symbols.contains(",")) {
			String[] symbolArr = symbols.split(",");
			for (String oneSymbol : symbolArr) {
				Realtime realtime = DataCache.getRealtime(oneSymbol);
				if (realtime != null) {
					list.add(realtime);
				} else {
				}
			}
		}
		return list;
	}

	/**
	 * Kline
	 */
	@Override
	public List<Kline> kline(String symbol, String line) {
		Item bySymbol = itemService.findBySymbol(symbol);
		if(Item.cryptos.equals(bySymbol.getType())){
			return klineCryptos(symbol, line);
		}
		KlineTimeObject timeObject = DataCache.getKline(symbol, line);
		List<Kline> list = new ArrayList<Kline>();
		if (timeObject != null) {
			list = timeObject.getKline();
		}
		if (list.isEmpty()) {
			Map<String, List<Kline>> all = xueQiuDataService.getKlineFromTwelveData(symbol);
			list = all.getOrDefault(line, new ArrayList<>());
		}
		List<Kline> list_clone = new ArrayList<Kline>();
		try {
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i) == null){
					continue;
				}
				Kline kline = (Kline) list.get(i).clone();
				list_clone.add(kline);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		Realtime realtime = DataCache.getLatestRealTime(symbol);
		if (realtime != null) {
			Kline kline = null;
			if (KlineConstant.PERIOD_1MIN.equals(line)) {
				kline = klineService.bulidKline1Minute(realtime, KlineConstant.PERIOD_1MIN);
			} else if (KlineConstant.PERIOD_5MIN.equals(line)) {
				kline = klineService.bulidKline5Minute(realtime, KlineConstant.PERIOD_5MIN);
			} else if (KlineConstant.PERIOD_15MIN.equals(line)) {
				kline = klineService.bulidKline15Minute(realtime, KlineConstant.PERIOD_15MIN);
			} else if (KlineConstant.PERIOD_30MIN.equals(line)) {
				kline = klineService.bulidKline30Minute(realtime, KlineConstant.PERIOD_30MIN);
			} else if (KlineConstant.PERIOD_60MIN.equals(line)) {
				kline = klineService.bulidKline60Minute(realtime, KlineConstant.PERIOD_60MIN);
			} else if (KlineConstant.PERIOD_4HOUR.equals(line)) {
				kline = klineService.bulidKline4Hour(realtime, KlineConstant.PERIOD_4HOUR);
			} else if (KlineConstant.PERIOD_1DAY.equals(line)) {
				kline = klineService.bulidKline1Day(realtime, KlineConstant.PERIOD_1DAY);
			} else if (KlineConstant.PERIOD_5DAY.equals(line)) {
				kline = klineService.bulidKline5Day(realtime, KlineConstant.PERIOD_5DAY);
			} else if (KlineConstant.PERIOD_1WEEK.equals(line)) {
				kline = klineService.bulidKline1Week(realtime, KlineConstant.PERIOD_1WEEK);
			} else if (KlineConstant.PERIOD_1MON.equals(line)) {
				kline = klineService.bulidKline1Mon(realtime, KlineConstant.PERIOD_1MON);
			} else if (KlineConstant.PERIOD_QUARTER.equals(line)) {
				kline = klineService.bulidKline1Mon(realtime, KlineConstant.PERIOD_QUARTER);
			} else if (KlineConstant.PERIOD_YEAR.equals(line)) {
				kline = klineService.bulidKline1Mon(realtime, KlineConstant.PERIOD_YEAR);
			}
			if (null != kline) {
				list_clone.add(kline);
			}
		}
		// 按时间升序
		Collections.sort(list_clone);
		return list_clone;
	}

	/**
	 * 分时图
	 */
	@Override
	public List<Trend> trend(String symbol) {
		TrendTimeObject trendTimeObject = DataCache.getTrend(symbol);
		trendTimeObject = this.loadTrend(symbol, trendTimeObject);
		if (trendTimeObject != null) {
			return trendTimeObject.getTrend();
		}
		return new ArrayList<>();
	}
	/**
	 * 深度数据
	 */
	@Override
	public Depth depth(String symbol) {
		DepthTimeObject timeObject = DataCache.getDepth(symbol);
		if (timeObject != null) {
			Depth depth = timeObject.getDepth();
			depth.setSymbol(symbol);
			return depth;
		}
		timeObject = new DepthTimeObject();
		Depth depth = new Depth();
		depth.setSymbol(symbol);
		timeObject.setDepth(depth);
		DataCache.putDepth(symbol, timeObject);
		return depth;
	}

	/**
	 * 近期交易记录
	 */
	@Override
	public Trade trade(String symbol) {
		TradeTimeObject timeObject = DataCache.getTrade(symbol);
		// this.loadTrade(symbol, timeObject);
		if (timeObject != null) {
			return timeObject.getTrade();
		}
		timeObject = new TradeTimeObject();
		DataCache.putTrade(symbol, timeObject);
		return timeObject.getTrade();
	}

	private TrendTimeObject loadTrend(String symbol, TrendTimeObject trendTimeObject) {
		if (trendTimeObject == null) {
			// 秒
			int interval = 3;
			int num = (24 * 60 * 60) / interval;
			List<Trend> list = new ArrayList<>();
			// 24小时的历史记录
			List<Realtime> history = bulidNum(DataCache.getCryptosRealtimeHistory(symbol), num);
			history = this.take500(history);
			if (history.size() > 500) {
				// 按时间升序
				Collections.sort(history);
				List<Realtime> history_500 = new ArrayList<>();
				for (int i = 0; i < 500; i++) {
					history_500.add(history.get(i));
				}
				history = history_500;
			}
			for (int i = 0; i < history.size(); i++) {
				Realtime realtime = history.get(i);
				Trend trend = bulidTrend(realtime);
				list.add(trend);
			}
			Realtime realtime_last = DataCache.getRealtime(symbol);
			if (realtime_last != null) {
				list.add(bulidTrend(DataCache.getRealtime(symbol)));
			}
			trendTimeObject = new TrendTimeObject();
			trendTimeObject.setTrend(list);
			DataCache.putTrend(symbol, trendTimeObject);
		}
		return trendTimeObject;
	}

	private List<Realtime> bulidNum(List<Realtime> cacheList, int num) {
		List<Realtime> list = new ArrayList<>();
		if (cacheList == null) {
			return list;
		}
		if (num > cacheList.size()) {
			num = cacheList.size();
		}
		for (int i = cacheList.size() - num; i < cacheList.size(); i++) {
			list.add(cacheList.get(i));
		}
		return list;
	}

	/**
	 * 按平均频率取500个数据点
	 */
	private List<Realtime> take500(List<Realtime> history) {
		List<Realtime> list = new ArrayList<>();
		int num = history.size() / 500;
		if (num <= 0) {
			return history;
		}
		int i = 0;
		while (true) {
			if (num >= 1.0D) {
				if (i % num == 0) {
					list.add(history.get(i));
				}
			} else {
				list.add(history.get(i));
			}
			i++;
			if (i >= history.size()) {
				break;
			}
		}
		return list;
	}

	private Trend bulidTrend(Realtime realtime) {
		Trend trend = new Trend();
		trend.setSymbol(realtime.getSymbol());
		trend.setTs(realtime.getTs());
		trend.setTrend(realtime.getClose());
		trend.setVolume(realtime.getVolume());
		trend.setAmount(realtime.getAmount());
		return trend;
	}

	public List<Kline> klineCryptos(String symbol, String line) {
		KlineTimeObject timeObject = DataCache.getKline(symbol, line);
		List<Kline> list = new ArrayList<>();
		if (timeObject != null) {
			list = timeObject.getKline();
		}
		List<Kline> list_clone = new ArrayList<>();
		try {
			for (int i = 0; i < list.size(); i++) {
				Kline kline = (Kline) list.get(i).clone();
				list_clone.add(kline);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		Realtime realtime = DataCache.getRealtime(symbol);
		Kline hobiOne = DataCache.getKlineHoBi(symbol + "_" + line);

		Kline lastOne = null;
		if (list.size() > 0) {
			lastOne = list.get(list.size() - 1);
		}
		if (realtime != null && hobiOne != null && lastOne != null) {
			list_clone.add(this.klineService.bulidKline(realtime, lastOne, hobiOne, line));
		}
		// 按时间升序
		Collections.sort(list_clone);
		return list_clone;
	}
}
