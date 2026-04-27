package com.yami.trading.api.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.split.SplitIter;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.data.domain.TradeDetails;
import com.yami.trading.bean.data.query.QueryRealtimeDTO;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.huobi.data.DataCache;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 行情实时价格 http接口
 *
 */
@RestController
@CrossOrigin
@Slf4j
@Api(tags = "实时行情数据")
public class RealtimeController {

	@Qualifier("dataService")
	@Autowired
	private DataService dataService;
	@Autowired
	private ItemService itemService;

	@ApiOperation(value = "行情")
	@GetMapping("api/hobi!getRealtime.action")
	public Result<List<Realtime>> getRealtime(@RequestParam String symbol) {
		try {
			List<Realtime> data = this.dataService.realtime(symbol);
			data.forEach(d->{
				Item bySymbol = itemService.findBySymbol(d.getSymbol());
				d.setType(bySymbol.getType());
				d.setName(bySymbol.getName());
				int decimals = bySymbol.getDecimals();
				d.setClose(BigDecimal.valueOf(d.getClose()).setScale(decimals, RoundingMode.HALF_UP).doubleValue());
				d.setOpen(BigDecimal.valueOf(d.getOpen()).setScale(decimals, RoundingMode.HALF_UP).doubleValue());
				d.setHigh(BigDecimal.valueOf(d.getHigh()).setScale(decimals, RoundingMode.HALF_UP).doubleValue());
				d.setLow(BigDecimal.valueOf(d.getLow()).setScale(decimals, RoundingMode.HALF_UP).doubleValue());
				BigDecimal bigDecimal = BigDecimal.valueOf(1, decimals);
				d.setAsk(BigDecimal.valueOf(d.getClose()).add(bigDecimal).setScale(decimals, RoundingMode.HALF_UP).doubleValue());
				d.setBid(BigDecimal.valueOf(d.getClose()).subtract(bigDecimal).setScale(decimals, RoundingMode.HALF_UP).doubleValue());
				d.setVolume(BigDecimal.valueOf(d.getVolume()).setScale(2, RoundingMode.HALF_UP).doubleValue());
				d.setAmount(BigDecimal.valueOf(d.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue());
				d.setSymbolData(bySymbol.getSymbolData());
			});
			return Result.ok(data);
		} catch (Exception e) {
			log.error("生成实时数据失败", e);
			throw  new YamiShopBindException("生成实时数据失败");
		}
	}
	@ApiOperation(value = "行情")
	@GetMapping("api/publicRealtimeTopAllStocksDCL")
	public Result<List<QueryRealtimeDTO>> publicRealtimeTopAllStocksDCL() {
		try {
			String symbols = "SH600519\n" +
					"SH601766\n" +
					"SZ000009\n" +
					"SH600118\n" +
					"AAPL\n" +
					"TSLA\n" +
					"BABA\n" +
					"BA\n" +
					"00700\n" +
					"01357\n" +
					"09868\n" +
					"09999";
			Set<String> symbolSet = Splitter.on("\n").omitEmptyStrings().splitToStream(symbols).collect(Collectors.toSet());
			List<Item> items = itemService.cacheGetAll().stream().filter(i->symbolSet.contains(i.getSymbol())).collect(Collectors.toList());

			List<QueryRealtimeDTO> list = new ArrayList<>();
			items.forEach(item -> {
				Realtime realtime = DataCache.getRealtime(item.getSymbol());
				if (realtime != null) {
					QueryRealtimeDTO dto = QueryRealtimeDTO.builder()
							.close(BigDecimal.valueOf(realtime.getClose()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.open(BigDecimal.valueOf(realtime.getOpen()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.high(BigDecimal.valueOf(realtime.getHigh()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.low(BigDecimal.valueOf(realtime.getLow()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.volume(BigDecimal.valueOf(realtime.getVolume()).setScale(2, RoundingMode.HALF_UP).doubleValue())
							.amount(BigDecimal.valueOf(realtime.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue())
							.symbol(realtime.getSymbol())
							.changeRatio(realtime.getChangeRatio())
							.currentTime(realtime.getCurrentTime())
							.enName(item.getEnName())
							.name(item.getName())
							.netChange(realtime.getNetChange())
							.ts(realtime.getTs())
							.sorted(item.getSorted())
							.chg(realtime.getChg())
							.type(item.getType())
							.category(item.getCategory())
							.build();
					list.add(dto);
				}
			});
			return Result.ok(list);
		} catch (Exception e) {
			log.error("获取热门币种实时价格失败", e);
			throw new YamiShopBindException("生成实时数据失败");
		}
	}

	@ApiOperation(value = "行情")
	@GetMapping("api/publicRealtimeTopAllStocks")
	public Result<List<QueryRealtimeDTO>> publicRealtimeTopAllStocks(@RequestParam(required = false) Integer size) {
		try {
			String[] types = {Item.SG_STOCKS,"US-stocks","UK-stocks","TW-stocks","JP-stocks","indices","INDIA-stocks","HK-stocks", "forex","cryptos","A-stocks",Item.CAD_STOCKS,Item.FR_STOCKS};
			List<Item> items = new ArrayList<>();
			if(size == null){
				size = 3;
			}
			for(String type : types){
				items.addAll(getTop(type, size));
			}
			List<QueryRealtimeDTO> list = new ArrayList<>();
			items.forEach(item -> {
				Realtime realtime = DataCache.getRealtime(item.getSymbol());
				if (realtime != null) {
					QueryRealtimeDTO dto = QueryRealtimeDTO.builder()
							.close(BigDecimal.valueOf(realtime.getClose()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.open(BigDecimal.valueOf(realtime.getOpen()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.high(BigDecimal.valueOf(realtime.getHigh()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.low(BigDecimal.valueOf(realtime.getLow()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.volume(BigDecimal.valueOf(realtime.getVolume()).setScale(2, RoundingMode.HALF_UP).doubleValue())
							.amount(BigDecimal.valueOf(realtime.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue())
							.symbol(realtime.getSymbol())
							.changeRatio(realtime.getChangeRatio())
							.currentTime(realtime.getCurrentTime())
							.enName(item.getEnName())
							.name(item.getName())
							.netChange(realtime.getNetChange())
							.ts(realtime.getTs())
							.sorted(item.getSorted())
							.chg(realtime.getChg())
							.type(item.getType())
							.category(item.getCategory())
							.build();
					list.add(dto);
				}
			});
			return Result.ok(list);
		} catch (Exception e) {
			log.error("获取热门币种实时价格失败", e);
			throw new YamiShopBindException("生成实时数据失败");
		}
	}
	/**
	 * 根据币种分类 获取热门币种 实时价格数据
	 * @param type 大类：ETF、外汇、加密货币、、、
	 */
	@ApiOperation(value = "行情")
	@GetMapping("api/publicRealtimeTop")
	public Result<List<QueryRealtimeDTO>> publicRealtimeTop(@RequestParam(required = false) String type) {
		try {
			if (null == type) {
				return Result.ok(publicRealtimeTopAll());
			}
			List<Item> items;

			items = getTop(type, 3);

			List<QueryRealtimeDTO> list = new ArrayList<>();
			items.forEach(item -> {
				Realtime realtime = DataCache.getRealtime(item.getSymbol());
				if (realtime != null) {
					QueryRealtimeDTO dto = QueryRealtimeDTO.builder()
							.close(BigDecimal.valueOf(realtime.getClose()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.open(BigDecimal.valueOf(realtime.getOpen()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.high(BigDecimal.valueOf(realtime.getHigh()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.low(BigDecimal.valueOf(realtime.getLow()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.volume(BigDecimal.valueOf(realtime.getVolume()).setScale(2, RoundingMode.HALF_UP).doubleValue())
							.amount(BigDecimal.valueOf(realtime.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue())
							.symbol(realtime.getSymbol())
							.changeRatio(realtime.getChangeRatio())
							.currentTime(realtime.getCurrentTime())
							.enName(item.getEnName())
							.name(item.getName())
							.netChange(realtime.getNetChange())
							.ts(realtime.getTs())
							.sorted(item.getSorted())
							.chg(realtime.getChg())
							.type(item.getType())
							.category(item.getCategory())
							.build();
					list.add(dto);
				}
			});
			return Result.ok(list);
		} catch (Exception e) {
			log.error("获取热门币种实时价格失败", e);
			throw new YamiShopBindException("生成实时数据失败");
		}
	}

	@NotNull
	private List<Item> getTop(String type, int size) {
		List<Item> items;
		// 不为指数，排序前3的
		List<Item> top3 = itemService.findByType(type).stream()
				.filter(item -> !Item.indices.equalsIgnoreCase(item.getCategory()))
				.sorted(Comparator.comparing(Item::getSorted).reversed()).limit(size)
				.collect(Collectors.toList());

		// ETF、港股 top6
		if ("HK-stocks".equals(type) ){
			items = new ArrayList<>();
			items.add(itemService.findBySymbol("HKHSI"));
			items.add(itemService.findBySymbol("HKHSCEI"));
			items.add(itemService.findBySymbol("HKHSTECH"));
			items.addAll(top3);
		}else if (Item.JP_STOCKS.equals(type) || Item.UK_STOCKS.equals(type)) {
			items = new ArrayList<>(itemService.cacheGetAll()).stream().filter(item -> item.getType().equals(type) && item.getCategory().equalsIgnoreCase(Item.indices)).limit(size)
					.collect(Collectors.toList());
			items.addAll(top3);
		}
		else if ("indices".equals(type) || "TW-stocks".equals(type)) {
			items = new ArrayList<>(itemService.cacheGetAll()).stream().filter(item -> item.getType().equals(type)).sorted(Comparator.comparing(Item::getSorted).reversed()).limit(6)
					.collect(Collectors.toList());
		} else if ("A-stocks".equals(type)) {
			items = new ArrayList<>(itemService.cacheGetAll()).stream().filter(item -> item.getType().equals(type)
							&& "indices".equals(item.getCategory())).limit(6)
					.collect(Collectors.toList());
			List<Item> itemsTop = new ArrayList<>(itemService.cacheGetAll()).stream().filter(item -> item.getType().equals(type)).sorted(Comparator.comparing(Item::getSorted).reversed()).limit(6)
					.collect(Collectors.toList());
			items.addAll(itemsTop);
		} else if ("US-stocks".equals(type)) {
			// 只查询指数top6
			items = itemService.cacheGetAll().stream().filter(item -> "global".equals(item.getCategory())).limit(3).collect(Collectors.toList());
			List<Item> itemsTop = itemService.cacheGetAll().stream().filter(item -> "technology,prominent".equals(item.getCategory())).sorted(Comparator.comparing(Item::getSorted).reversed()).limit(size)
					.collect(Collectors.toList());
			items.addAll(itemsTop);
		}
		// 加密货币、外汇、 top4
		else {
			items = new ArrayList<>(itemService.cacheGetAll()).stream().filter(item -> item.getType().equals(type)).limit(4)
					.collect(Collectors.toList());
		}
		return items;
	}

	/**
	 * 根据币种分类 分页获取实时价格数据
	 * @param type 大类：ETF、外汇、加密货币、、、
	 * @param category 小类 外汇-> 法币、大宗商品、指数
	 */
	@ApiOperation(value = "行情")
	@GetMapping("api/publicRealtimeByType")
	public Result<List<QueryRealtimeDTO>> getRealtime(@RequestParam(required = false) String type,
													  @RequestParam(required = false) String category,
													  @RequestParam(required = false) Integer pageNo,
													  @RequestParam(required = false) Integer pageSize) {
		try {
			pageSize = null == pageSize ? 20 : pageSize;
			List<Item> itemsTotal;
			List<Item> items = new ArrayList<>();
			//按字符串排序
			List<Item> itemList = itemService.cacheGetAll().stream().sorted(Comparator.comparing(Item::getSorted).reversed()).collect(Collectors.toList());
			//按数字排序
//			List<Item> itemList = itemService.cacheGetAll().stream().sorted(Comparator.comparing(Item::getSorted, (x,y)->{
//				int xInt = 0;
//				int yInt = 0;
//				if(StringUtils.isNotEmpty(x)){
//					xInt = Integer.parseInt(x);
//				}
//				if(StringUtils.isNotEmpty(y)){
//					yInt = Integer.parseInt(y);
//				}
//				return Integer.compare(yInt, xInt);
//			})).collect(Collectors.toList());

			if (null != type && null != category) {
				// 知名类 需要模糊查询
				if ("prominent".equals(category)) {
					itemsTotal = new ArrayList<>(itemList).stream().filter(item -> item.getType().equals(type)
							&& item.getCategory().contains(category)).collect(Collectors.toList());
				} else {
					itemsTotal = new ArrayList<>(itemList).stream().filter(item -> item.getType().equals(type)
							&& item.getCategory().equals(category)).collect(Collectors.toList());
				}
				if (pageNo * pageSize - pageSize < itemsTotal.size()) {
					int pages = itemsTotal.size() % pageSize == 0 ? itemsTotal.size() / pageSize : itemsTotal.size() / pageSize + 1;
					int start = pageNo <= 0 ? 0 : (pageNo > pages ? (pages - 1) * pageSize : (pageNo - 1) * pageSize);
					items = itemsTotal.stream().skip(start).limit(pageSize).collect(Collectors.toList());
				}
			} else if (null != type) {
				itemsTotal = new ArrayList<>(itemList).stream().filter(item -> item.getType().equals(type))
						.collect(Collectors.toList());
				if (pageNo * pageSize - pageSize < itemsTotal.size()) {
					int pages = itemsTotal.size() % pageSize == 0 ? itemsTotal.size() / pageSize : itemsTotal.size() / pageSize + 1;
					int start = pageNo <= 0 ? 0 : (pageNo > pages ? (pages - 1) * pageSize : (pageNo - 1) * pageSize);
					items = itemsTotal.stream().skip(start).limit(pageSize).collect(Collectors.toList());
				}
			} else {
				itemsTotal = new ArrayList<>(itemList);
				List<String> symbols = Item.types;
				for (String symbol : symbols) {
					List<Item> list = itemsTotal.stream().filter(item -> symbol.equals(item.getType())).limit(5).collect(Collectors.toList());
					if (list.size() > 0) {
						items.addAll(list);
					}
				}
			}

			List<QueryRealtimeDTO> list = new ArrayList<>();
			assert items != null;
			items.forEach(item -> {
				Realtime realtime = DataCache.getRealtime(item.getSymbol());
				if (realtime != null) {
					QueryRealtimeDTO dto = QueryRealtimeDTO.builder()
							.close(BigDecimal.valueOf(realtime.getClose()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.open(BigDecimal.valueOf(realtime.getOpen()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.high(BigDecimal.valueOf(realtime.getHigh()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.low(BigDecimal.valueOf(realtime.getLow()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
							.volume(BigDecimal.valueOf(realtime.getVolume()).setScale(2, RoundingMode.HALF_UP).doubleValue())
							.amount(BigDecimal.valueOf(realtime.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue())
							.symbol(realtime.getSymbol())
							.changeRatio(realtime.getChangeRatio())
							.currentTime(realtime.getCurrentTime())
							.enName(item.getEnName())
							.name(item.getName())
							.netChange(realtime.getNetChange())
							.ts(realtime.getTs())
							.sorted(item.getSorted())
							.chg(realtime.getChg())
							.category(item.getCategory())
							.type(item.getType())
							.build();
					list.add(dto);
				} else {
					log.error("获取行情接口，realtime is null；币种->{}", item.getSymbol());
				}
			});
			Result<List<QueryRealtimeDTO>> ok = Result.ok(list);
			ok.setTotal((long) itemsTotal.size());
			return ok;
		} catch (Exception e) {
			log.error("根据币种分类，分页获取实时价格数据", e);
			throw new YamiShopBindException("生成实时数据失败");
		}
	}

	@ApiOperation(value = "行情")
	@GetMapping("api/hobi!getStockTradeList.action")
	public Result<List<TradeDetails>> getTradeDetails(@RequestParam String symbol) {
		try {
			List<TradeDetails> stockTradeList = DataCache.getStockTradeList(symbol);

			Integer decimal = itemService.getDecimal(symbol);
			String format = "";
			if (decimal == 0) {
				format = "#";
			} else {
				format = "#.";
				for (int j = 0; j < decimal; j++) {
					format = format + "#";
				}
			}
			DecimalFormat df = new DecimalFormat(format);
			df.setRoundingMode(RoundingMode.FLOOR);
			if(CollectionUtil.isEmpty(stockTradeList)){
				return Result.ok(Lists.newArrayList());
			}
			for (TradeDetails tradeDetails : stockTradeList) {
				tradeDetails.setCurrentStr(df.format(tradeDetails.getCurrent()));
			}
			return Result.ok(stockTradeList);
		} catch (Exception e) {
			log.error("生成实时数据失败", e);
			throw  new YamiShopBindException("生成实时数据失败");
		}
	}

	private List<QueryRealtimeDTO> publicRealtimeTopAll() {
		List<Item> items = new ArrayList<>();
		List<Item> itemsTotal = new ArrayList<>(itemService.cacheGetAll());
		String[] symbols = {"US-stocks","UK-stocks","TW-stocks","JP-stocks","indices","INDIA-stocks","HK-stocks", "forex","cryptos","A-stocks"};
		for (String symbol : symbols) {
			List<Item> list = itemsTotal.stream().filter(item -> symbol.equals(item.getType())).limit(3).collect(Collectors.toList());
			if (list.size() > 0) {
				items.addAll(list);
			}
		}
		List<QueryRealtimeDTO> list = new ArrayList<>();
		items.forEach(item -> {
			Realtime realtime = DataCache.getRealtime(item.getSymbol());
			if (realtime != null) {
				QueryRealtimeDTO dto = QueryRealtimeDTO.builder()
						.close(BigDecimal.valueOf(realtime.getClose()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
						.open(BigDecimal.valueOf(realtime.getOpen()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
						.high(BigDecimal.valueOf(realtime.getHigh()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
						.low(BigDecimal.valueOf(realtime.getLow()).setScale(item.getDecimals(), RoundingMode.HALF_UP).doubleValue())
						.volume(BigDecimal.valueOf(realtime.getVolume()).setScale(2, RoundingMode.HALF_UP).doubleValue())
						.amount(BigDecimal.valueOf(realtime.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue())
						.symbol(realtime.getSymbol())
						.changeRatio(realtime.getChangeRatio())
						.currentTime(realtime.getCurrentTime())
						.enName(item.getEnName())
						.name(item.getName())
						.netChange(realtime.getNetChange())
						.ts(realtime.getTs())
						.sorted(item.getSorted())
						.chg(realtime.getChg())
						.category(item.getCategory())
						.type(item.getType())
						.build();
				list.add(dto);
			} else {
				log.error("获取行情接口，realtime is null；币种->{}", item.getSymbol());
			}
		});
		return list;
	}

}
