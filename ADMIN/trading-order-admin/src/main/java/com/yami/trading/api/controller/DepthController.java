package com.yami.trading.api.controller;

import com.yami.trading.bean.data.domain.Depth;
import com.yami.trading.bean.data.domain.DepthEntry;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 市场深度数据 20档深度，根据页面取5档或20档
 *
 */
@RestController
@CrossOrigin
@Slf4j
public class DepthController {

	@Autowired
	@Qualifier("dataService")
	private DataService dataService;
	@Autowired
	private ItemService itemService;

	@RequestMapping("api/hobi!getDepth.action")
	public ResultObject getDepth(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		try {
			String symbol = request.getParameter("symbol");
			if (StringUtils.isNullOrEmpty(symbol)) {
				resultObject.setCode("400");
				resultObject.setMsg("[symbol]参数为空");
				return resultObject;
			}
			// 数据处理
			Depth data = this.dataService.depth(symbol);
			Realtime realtime = DataCache.getRealtime(symbol);
			resultObject.setData(depthRevise(data, symbol, realtime.getClose(), true));
			return resultObject;
		} catch (BusinessException e) {
			resultObject.setCode("402");
			resultObject.setMsg(e.getMessage());
			return resultObject;
		} catch (Throwable e) {
			resultObject.setCode("500");
			resultObject.setMsg("服务器错误(500)");
			log.error(e.getMessage() , e);
			return resultObject;
		}
	}

	/**
	 * 市场深度数据 解析
	 */
	public Map<String, Object> depthRevise(Depth data, String symbol, Double close, boolean random) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("symbol", symbol);
		Item item = this.itemService.findBySymbol(data.getSymbol());
		List<Map<String, Object>> asks_list = new ArrayList<Map<String, Object>>();
		int asksSize = data.getAsks().size();
		for (int i = 0; i < 20 - asksSize; i++) {
			DepthEntry e = new DepthEntry();
			e.setAmount(RandomUtil.randomFloat(10, 100, 0));
			e.setPrice(close);
			data.getAsks().add(e);
		}

		int bidsSize = data.getBids().size();
		for (int i = 0; i < 20 - bidsSize; i++) {
			DepthEntry e = new DepthEntry();
			e.setAmount(RandomUtil.randomFloat(10, 100, 0));
			e.setPrice(close);
			data.getBids().add(e);
		}

		Set<String> asksPrices = new HashSet<>();
		asksSize = data.getAsks().size();
		bidsSize = data.getBids().size();
		for (int i = 0; i < asksSize; i++) {
			DepthEntry depthEntry = data.getAsks().get(i);
			Map<String, Object> asks_map = new HashMap<String, Object>();
			double price;
			double amount;
			if (random) {
				double addPriceValue = getRandomValue(String.valueOf(depthEntry.getPrice()));
				double addAmountValue = getRandomValue((int) depthEntry.getAmount().doubleValue());

				price = Arith.add(depthEntry.getPrice(), addPriceValue);
				if (price < close) {
					price = Arith.add(close, addPriceValue);
				} else {
					price = Arith.add(close, addPriceValue / 10);

				}

				amount = Arith.add(depthEntry.getAmount(), addAmountValue);
			} else {
				price = depthEntry.getPrice();
				amount = depthEntry.getAmount();
			}

			if(price <=0 ){
				continue;
			}

			if (item.getDecimals() < 0) {
				asks_map.put("price", price);
				asks_map.put("amount", amount);
			} else {
				String format = "";
				if (item.getDecimals() == 0) {
					format = "#";
				} else {
					format = "#.";
					for (int j = 0; j < item.getDecimals(); j++) {
						format = format + "#";
					}
				}

				DecimalFormat df = new DecimalFormat(format);
				df.setRoundingMode(RoundingMode.FLOOR);// 向下取整

				asks_map.put("price", df.format(price));
				if (asksPrices.contains(df.format(price))) {
					continue;
				} else {
					asksPrices.add(df.format(price));
				}
				asks_map.put("amount", df.format(amount));

			}
			asks_list.add(asks_map);

		}
		// buy
		map.put("asks", asks_list);
		Set<String> bidPriceSet = new HashSet<>();
		List<Map<String, Object>> bids_list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < bidsSize; i++) {
			DepthEntry depthEntry = data.getBids().get(i);
			String priceTemp = new BigDecimal(String.valueOf(depthEntry.getPrice())).toPlainString();

			double addPriceValue = getRandomValue(priceTemp);
			double addAmountValue = getRandomValue((int) depthEntry.getAmount().doubleValue());
			double price;
			double amount;
			if (random) {
				price = Arith.add(depthEntry.getPrice(), -addPriceValue);
				if (price >= close) {
					price = Arith.add(close, -addPriceValue);
				} else {
					price = Arith.add(close, -addPriceValue / 10);
				}

				amount = Arith.add(depthEntry.getAmount(), addAmountValue);
			} else {
				price = depthEntry.getPrice();
				amount = depthEntry.getAmount();
			}

			if(price <=0 ){
				continue;
			}
			Map<String, Object> bids_map = new HashMap<>();
			if (item.getDecimals() < 0) {
				bids_map.put("price", price);
				bids_map.put("amount", amount);
			} else {
				String format = "";
				if (item.getDecimals() == 0) {
					format = "#";
				} else {
					format = "#.";
					for (int j = 0; j < item.getDecimals(); j++) {
						format = format + "#";
					}
				}

				DecimalFormat df = new DecimalFormat(format);
				bids_map.put("price", df.format(price));
				if (bidPriceSet.contains(df.format(price))) {
					continue;
				} else {
					bidPriceSet.add(df.format(price));
				}
				bids_map.put("amount", df.format(amount));

			}
			bids_list.add(bids_map);

		}
		// sell
		map.put("bids", bids_list);
		return map;
	}

	private double getRandomValue(int value) {
		double addValue;
		if (value > 0) {
			int count = 0;
			while (value > 0) {
				value = value / 10;
				count++;
			}
			// 个
			if (count == 1) {
				addValue = RandomUtil.randomFloat(0.01, 0.1999, 4);
				return addValue;
			}
			// 十
			if (count == 2) {
				addValue = RandomUtil.randomFloat(0.1, 0.5999, 4);
				return addValue;
			}
			// 百
			if (count == 3) {
				addValue = RandomUtil.randomFloat(0.1, 2.9999, 4);
				return addValue;
			}
			// 千
			if (count == 4) {
				addValue = RandomUtil.randomFloat(1, 3.9999, 4);
				return addValue;
			}
			// 万
			if (count == 5) {
				addValue = RandomUtil.randomFloat(1, 5.9999, 4);
				return addValue;
			}
			// 十万
			else {
				addValue = RandomUtil.randomFloat(1, 5.9999, 4);
				return addValue;
			}
		} else {
			addValue = RandomUtil.randomFloat(0.01, 0.2999, 4);
			return addValue;
		}
	}

	private double getRandomValue(String value) {
		double addValue;
		double d = Double.valueOf(value);
		int val = (int) d;
		// 个位数>0
		if (val > 0) {
			int count = 0;
			while (val > 0) {
				val = val / 10;
				count++;
			}
			// 个
			if (count == 1) {
				addValue = RandomUtil.randomFloat(0.01, 0.1999, 4);
				return addValue;
			}
			// 十
			if (count == 2) {
				addValue = RandomUtil.randomFloat(0.1, 0.5999, 4);
				return addValue;
			}
			// 百
			if (count == 3) {
				addValue = RandomUtil.randomFloat(0.1, 2.9999, 4);
				return addValue;
			}
			// 千
			if (count == 4) {
				addValue = RandomUtil.randomFloat(1, 3.9999, 4);
				return addValue;
			}
			// 万
			if (count == 5) {
				addValue = RandomUtil.randomFloat(1, 5.9999, 4);
				return addValue;
			}
			// 十万
			else {
				addValue = RandomUtil.randomFloat(1, 5.9999, 4);
				return addValue;
			}
		}
		// 个位=0
		else {
			String[] valueSplit = value.split("\\.");
			int valueLength = valueSplit[1].length();
			if (valueLength <= 4) {
				addValue = RandomUtil.randomFloat(0.001, 0.003, 3);
				return addValue;
			}

			if (4 < valueLength && valueLength <= 6) {
				addValue = RandomUtil.randomFloat(0.00001, 0.00003, 5);
				return addValue;
			}

			if (6 < valueLength && valueLength <= 8) {
				addValue = RandomUtil.randomFloat(0.0000001, 0.0000003, 7);
				return addValue;
			}

			if (8 < valueLength && valueLength <= 10) {
				addValue = RandomUtil.randomFloat(0.0000001, 0.0000003, 9);
				return addValue;
			} else {
				addValue = RandomUtil.randomFloat(0.0000000001, 0.0000000003, 10);
				return addValue;
			}
		}
	}

	private Map<String, Object> revise(Depth data) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("symbol", data.getSymbol());
		Item item = this.itemService.findBySymbol(data.getSymbol());
		List<Map<String, Object>> asks_list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < data.getAsks().size(); i++) {
			DepthEntry depthEntry = data.getAsks().get(i);
			Map<String, Object> asks_map = new HashMap<>();

			if (item.getDecimals() < 0) {
				asks_map.put("price", depthEntry.getPrice());
				asks_map.put("amount", depthEntry.getAmount());
			} else {
				String format = "";
				if (item.getDecimals() == 0) {
					format = "#";
				} else {
					format = "#.";
					for (int j = 0; j < item.getDecimals(); j++) {
						format = format + "#";
					}
				}
				DecimalFormat df = new DecimalFormat(format);
				// 向下取整
				// df.setRoundingMode(RoundingMode.FLOOR);
				asks_map.put("price", df.format(depthEntry.getPrice()));
				asks_map.put("amount", df.format(depthEntry.getAmount()));
			}
			asks_list.add(asks_map);
		}
		map.put("asks", asks_list);

		List<Map<String, Object>> bids_list = new ArrayList<>();
		for (int i = 0; i < data.getBids().size(); i++) {
			DepthEntry depthEntry = data.getBids().get(i);
			Map<String, Object> bids_map = new HashMap<>();
			if (item.getDecimals() < 0) {
				bids_map.put("price", depthEntry.getPrice());
				bids_map.put("amount", depthEntry.getAmount());
			} else {
				String format = "";
				if (item.getDecimals() == 0) {
					format = "#";
				} else {
					format = "#.";
					for (int j = 0; j < item.getDecimals(); j++) {
						format = format + "#";
					}
				}
				DecimalFormat df = new DecimalFormat(format);
				bids_map.put("price", df.format(depthEntry.getPrice()));
				bids_map.put("amount", df.format(depthEntry.getAmount()));
			}
			bids_list.add(bids_map);
		}
		map.put("bids", bids_list);
		return map;
	}
}
