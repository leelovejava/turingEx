package com.yami.trading.api.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yami.trading.api.dto.BoardDto;
import com.yami.trading.api.dto.RelatedStocksDto;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.data.dto.StocksDto;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.dto.ItemDTO;
import com.yami.trading.bean.item.dto.Symbol2DTO;
import com.yami.trading.bean.item.dto.SymbolDTO;
import com.yami.trading.bean.item.query.ItemQuery;
import com.yami.trading.bean.item.query.ItemQueryByScene;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.lang.LangUtils;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.item.ItemUserOptionalItemService;
import com.yami.trading.service.syspara.LocalSysparaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Api(tags = "产品")
@RestController
@CrossOrigin
@ApiOperation("api/")
public class ApiItemController {

    public static final String ITEM = "/api/item!";
    @Autowired
    private ItemService itemService;
    @Autowired
    private LocalSysparaService localSysparaService;
    @Autowired
    private ItemUserOptionalItemService itemUserOptionalItemService;
    @Autowired
    @Qualifier("dataService")
    private DataService dataService;

    /**
     * 产品列表数据
     */
    @ApiOperation(value = "获取资金流向")
    @GetMapping(ITEM + "moneyFlow.action")
    public Result<List> moneyFlow() {
        String cnNames = "沪深成交,沪,深,北向资金,沪股通,深股通,南向资金,港股通-沪,港股通-深";
        String twNames = "滬深成交,滬,深,北向資金,滬股通,深股通,南向資金,港股通-滬,港股通-深";
        String enNames = "Shanghai and Shenzhen transactions, Shanghai, Shenzhen, northbound funds, Shanghai Stock Connect, Shenzhen Stock Connect, southbound funds, Hong Kong Stock Connect - Shanghai, Hong Kong Stock Connect - Shenzhen";
        BigDecimal bigDecimal1 = new BigDecimal(7000 + RandomUtil.random(10, 1000) * 0.9).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bigDecimal2 = new BigDecimal(15 + RandomUtil.random(10, 30) * 0.9).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bigDecimal3 = new BigDecimal(50 + RandomUtil.random(10, 20) * 0.9).setScale(2, RoundingMode.HALF_UP);
        List<List<Pair<String, String>>> result = new ArrayList<>();

        List<Pair<String, String>> pairList1 = new ArrayList<>();
        List<Pair<String, String>> pairList2 = new ArrayList<>();
        List<Pair<String, String>> pairList3 = new ArrayList<>();
        List<String> cnNameList = Splitter.on(",").splitToList(cnNames);
        List<String> enNameList = Splitter.on(",").splitToList(enNames);
        List<String> twNameList = Splitter.on(",").splitToList(twNames);
        List<String> names = enNameList;

        if (LangUtils.isCnItem()) {
            names = cnNameList;
        }

        if (LangUtils.isTWItem()) {
            names = twNameList;
        }

        BigDecimal random = BigDecimal.valueOf(1500.00 / DateUtils.getYear(new Date()));
        BigDecimal randomLeft = BigDecimal.valueOf(1.0 - (1500.00 / DateUtils.getYear(new Date())));

        pairList1.add(Pair.of(names.get(0), bigDecimal1.toPlainString()));
        pairList1.add(Pair.of(names.get(1), bigDecimal1.multiply(random).setScale(2, RoundingMode.HALF_UP).toPlainString()));
        pairList1.add(Pair.of(names.get(2), bigDecimal1.multiply(randomLeft).setScale(2, RoundingMode.HALF_UP).toPlainString()));

        pairList2.add(Pair.of(names.get(3), bigDecimal2.toPlainString()));
        pairList2.add(Pair.of(names.get(4), bigDecimal2.multiply(random).setScale(2, RoundingMode.HALF_UP).toPlainString()));
        pairList2.add(Pair.of(names.get(5), bigDecimal2.multiply(randomLeft).setScale(2, RoundingMode.HALF_UP).toPlainString()));

        pairList3.add(Pair.of(names.get(6), bigDecimal3.toPlainString()));
        pairList3.add(Pair.of(names.get(7), bigDecimal3.multiply(random).setScale(2, RoundingMode.HALF_UP).toPlainString()));
        pairList3.add(Pair.of(names.get(8), bigDecimal3.multiply(randomLeft).setScale(2, RoundingMode.HALF_UP).toPlainString()));


        result.add(pairList1);
        result.add(pairList2);
        result.add(pairList3);

        return Result.ok(result);

    }

    @ApiOperation(value = "获取资金流向")
    @GetMapping(ITEM + "board.action")
    public Result<List<BoardDto>> board() {
        List<Item> items = itemService.findByType(Item.A_STOCKS);
        Map<String, Double> categorySum = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCategory(),
                        Collectors.summingDouble(item -> getPrice(item.getSymbol()))));

        List<Map.Entry<String, Double>> topCategories = categorySum.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(6)
                .collect(Collectors.toList());
        Map<String, List<Item>> itemsGroupedByCategory = items.stream()
                .collect(Collectors.groupingBy(Item::getCategory));

        List<BoardDto> boardDtos = new ArrayList<>();

        Map<String, String> categoryMap = new HashMap();
        categoryMap.put("technology", "科技板块");
        categoryMap.put("rawMaterials", "工业板块");
        categoryMap.put("industry", "工业板块");
        categoryMap.put("consumerServices", "消费板块");
        categoryMap.put("manufacturing", "工业板块");
        categoryMap.put("utility", "消费板块");
        categoryMap.put("estate", "工业板块");
        categoryMap.put("information", "科技板块");
        categoryMap.put("indices", "科技板块");
        categoryMap.put("dinancial", "金融板块");
        categoryMap.put("healthcare", "医药板块");
        categoryMap.put("energy", "能源业板块");
        categoryMap.put("manufacturing", "工业板块");
        categoryMap.put("indices", "指数板块");

        for (Map.Entry<String, Double> entry : topCategories) {
            BoardDto boardDto = new BoardDto();
            String category = entry.getKey();
            if (LangUtils.isCnItem()) {
                boardDto.setBoardName(categoryMap.get(category));
            } else {
                boardDto.setBoardName(category);
            }

            Random rand = new Random();
            // 生成-3000到3000之间的随机整数
            int randomInt = rand.nextInt(6001) - 3000; // 6001是因为[0,6000]包含6001个数字
            // 转为小数并四舍五入到两位
            BigDecimal decimal = new BigDecimal(randomInt).divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP);

            boardDto.setBoardPrice(entry.getValue().toString());
            boardDto.setBoardRatio(decimal.toPlainString());
            DecimalFormat df = new DecimalFormat("#.##");
            boardDto.setBoardPrice(df.format(entry.getValue()));
            boardDto.setRealtime(dataService.realtime(itemsGroupedByCategory.get(category).get(0).getSymbol()).get(0));
            boardDtos.add(boardDto);
        }

        return Result.ok(boardDtos);
    }

    public double getPrice(String symbol) {
        List<Realtime> realtime = dataService.realtime(symbol);
        if (CollectionUtil.isEmpty(realtime)) {
            return 0D;
        }
        return realtime.get(0).getClose();
    }

    /**
     * 产品列表数据
     */
    @ApiOperation(value = "获取板块指数成分股")
    @GetMapping(ITEM + "relateStocks.action")
    public Result<RelatedStocksDto> relateStocks(String symbol) {
        Item bySymbol = itemService.findBySymbol(symbol);
        if (bySymbol == null) {
// 币对不存在
            throw new YamiShopBindException(symbol + " does not exist");
        }

        List<StocksDto> stocks = findRealTimeByBoard(symbol);
        RelatedStocksDto relatedStocksDto = new RelatedStocksDto(symbol, stocks);
        return Result.ok(relatedStocksDto);
    }

    /**
     * 获取一个板块相关的股票价格
     *
     * @param symbol
     * @return
     */
    public List<StocksDto> findRealTimeByBoard(String symbol) {
        QueryWrapper<Item> wrapper = new QueryWrapper();
        wrapper.like("board", symbol);
        wrapper.eq("show_status", "1");
        List<Item> list = itemService.list(wrapper);
        String symbols = list.stream().map(Item::getSymbol).collect(Collectors.joining(","));
        List<Realtime> realtime = dataService.realtime(symbols);
        List<StocksDto> stocksDtos = BeanUtil.copyToList(realtime, StocksDto.class);
        for (StocksDto stocksDto : stocksDtos) {
            Item bySymbol = itemService.findBySymbol(stocksDto.getSymbol());
            stocksDto.setName(bySymbol.getName());
        }

        return stocksDtos;
    }

    /**
     * 产品列表数据
     */
    @ApiOperation(value = "列表查询")
    @GetMapping(ITEM + "list.action")
    public Result<List<Symbol2DTO>> list(ItemQuery itemQuery) throws Exception {
        String userId = SecurityUtils.getCurrentUserId();
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        String symbol = itemQuery.getSymbol();
        String type = itemQuery.getType();
        String category = itemQuery.getCategory();
        if (StringUtils.isNotEmpty(type) && type.equalsIgnoreCase(Item.commodities)) {
            type = Item.forex;
            category = Item.commodities;
        }
        queryWrapper.eq("show_status", "1");

        // 如果type为etf，需要排除大盘的
        List<String> symbolsNotCotnains = Lists.newArrayList(".DJI", ".IXIC", ".INX");
        if (type != null && type.equalsIgnoreCase("indices") && StringUtils.isEmptyString(symbol)) {
            queryWrapper.notIn(ObjectUtil.notEqual("1", itemQuery.getMarketIndex()), "symbol", symbolsNotCotnains);
        }

        List<String> symbols = Lists.newArrayList();
        if (StrUtil.isNotEmpty(symbol)) {
            symbols = Splitter.on(",").splitToList(symbol);
        }

        if ("1".equalsIgnoreCase(itemQuery.getBoardType())) {
            queryWrapper.like("category", "global");
        } else if ("2".equalsIgnoreCase(itemQuery.getBoardType())) {
            queryWrapper.ne("category", "global");
        }
        queryWrapper.in(CollectionUtil.isNotEmpty(symbols), "symbol", symbols);
        String name = itemQuery.getName();
        queryWrapper.and(StringUtils.isNotEmpty(name), itemWrapper -> itemWrapper.like("name", name).or().like("symbol", name).or().like("en_name", name));
        queryWrapper.eq(StrUtil.isNotBlank(type), "type", type);
        queryWrapper.like(StrUtil.isNotBlank(category), "category", category);
        // 倒序吗？ TODO
        queryWrapper.orderByDesc("sorted");
        long current = itemQuery.getCurrent() == 0 ? 1 : itemQuery.getCurrent();
        long size = itemQuery.getSize() == 0 ? 20 : itemQuery.getSize();
        Page<Item> page = new Page<>(current, size);
        IPage<Item> result = itemService.page(page, queryWrapper);
        List<Item> records = result.getRecords();

        List<Symbol2DTO> dtos = new ArrayList<>();
        Map<String, Object> topPara = localSysparaService.find("index_top_symbols");
        String indexTopSymbols = topPara.get("index_top_symbols").toString();
        List<String> symbolList = Arrays.asList(indexTopSymbols.split(","));
        for (Item record : records) {
            String isTop = "0";
            String recordSymbol = record.getSymbol();
            if (symbolList.contains(recordSymbol)) {
                isTop = "1";
            }
            Symbol2DTO symbolDTO = BeanUtil.copyProperties(record, Symbol2DTO.class);
            symbolDTO.setIsTop(isTop);
            List<Realtime> realtimes = dataService.realtime(recordSymbol);
            if (CollectionUtil.isNotEmpty(realtimes)) {
                // 此处的时间戳如何处理？ TODO
                Realtime realtime = realtimes.get(0);
                symbolDTO.setChangeRatio(realtime.getChangeRatio());
                symbolDTO.setAmount(realtime.getAmount());
                symbolDTO.setVolume(realtime.getVolume());
                symbolDTO.setClose(realtime.getClose());
                symbolDTO.setTs(realtime.getTs());
                symbolDTO.setCurrent_time(realtime.getTs());
                symbolDTO.setChg(Arith.sub(realtime.getClose(), realtime.getOpen()));
            }
            boolean hasAddGlobal = false;
            if (StringUtils.isNotEmpty(userId)) {
                hasAddGlobal = itemUserOptionalItemService.findOne(userId, recordSymbol);
            }
            symbolDTO.setHasAddGlobal(hasAddGlobal);
            dtos.add(symbolDTO);
        }
        Result<List<Symbol2DTO>> ok = Result.ok(dtos);
        ok.setTotal(result.getTotal());
        return ok;
    }

    /**
     * 产品列表数据
     */
    @ApiOperation(value = "按厂家分类")
    @GetMapping(ITEM + "queryByScene")
    public Result<List<SymbolDTO>> queryByScene(ItemQueryByScene itemQueryByScene) {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<Item>();
        String type = itemQueryByScene.getType();
        if (StringUtils.isEmptyString(type)) {
            type = Item.forex;
        }
        queryWrapper.eq("show_status", "1");

        // 如果type为etf，需要排除大盘的
        List<String> symbolsNotCotnains = Lists.newArrayList(".DJI", ".IXIC", ".INX");
        if (type != null && type.equalsIgnoreCase("indices")) {
            queryWrapper.notIn(ObjectUtil.notEqual("1", itemQueryByScene.getMarketIndex()), "symbol", symbolsNotCotnains);
        }
        queryWrapper.eq(StrUtil.isNotBlank(type), "type", type);
        queryWrapper.orderByDesc("sorted");
        Page<Item> page = new Page<>(1, 1000);
        IPage<Item> result = itemService.page(page, queryWrapper);
        List<Item> records = result.getRecords();
        List<SymbolDTO> dtos = new ArrayList<>();
        for (Item record : records) {

            SymbolDTO symbolDTO = BeanUtil.copyProperties(record, SymbolDTO.class);
            List<Realtime> realtimes = dataService.realtime(record.getSymbol());
            if (CollectionUtil.isNotEmpty(realtimes)) {
                // 此处的时间戳如何处理？ TODO
                Realtime realtime = realtimes.get(0);
                symbolDTO.setChangeRatio(realtime.getChangeRatio());
                symbolDTO.setAmount(realtime.getAmount());
                symbolDTO.setVolume(realtime.getVolume());
                symbolDTO.setClose(realtime.getClose());
                symbolDTO.setTs(realtime.getTs());
                symbolDTO.setCurrent_time(realtime.getTs());
                symbolDTO.setChg(Arith.sub(realtime.getClose(), realtime.getOpen()));
                symbolDTO.setEnName(record.getEnName());
                if (symbolDTO.getChangeRatio() != null && symbolDTO.getAmount() != null && symbolDTO.getVolume() != null) {
                    dtos.add(symbolDTO);
                }
            }
        }
        String scene = StringUtils.isEmptyString(itemQueryByScene.getScene()) ? ItemQueryByScene.gains : itemQueryByScene.getScene();
        Comparator<SymbolDTO> comparator = Comparator.comparing(SymbolDTO::getChangeRatio).reversed();
        if (ItemQueryByScene.gains.equalsIgnoreCase(scene)) {
            comparator = Comparator.comparing(SymbolDTO::getChangeRatio).reversed();
        } else if (ItemQueryByScene.decline.equalsIgnoreCase(scene)) {
            comparator = Comparator.comparing(SymbolDTO::getChangeRatio);
        } else if (ItemQueryByScene.preMarket.equalsIgnoreCase(scene)) {
            comparator = Comparator.comparing(SymbolDTO::getAmount).reversed();
        } else if (ItemQueryByScene.afterHours.equalsIgnoreCase(scene)) {
            comparator = Comparator.comparing(SymbolDTO::getVolume).reversed();
        }
        dtos.sort(comparator);
        return Result.ok(dtos);
    }

    /**
     * 根据Id获取产品数据
     */
    @ApiOperation(value = "根据Id获取产品数据")
    @GetMapping(ITEM + "queryById")
    public Result<ItemDTO> queryById(String id) {
        ItemDTO byId = itemService.findById(id);
        return Result.succeed(byId);
    }

    /**
     * 根据Id获取产品数据
     */
    @ApiOperation(value = "根据symbol获取币对详情")
    @GetMapping(ITEM + "queryBySymbol.action")
    public Result<Item> queryBySymbol(String symbol) {
        Item item = itemService.findBySymbol(symbol);
        item.setOpen(MarketOpenChecker.isMarketOpenByItemCloseType(item.getOpenCloseType()));
        return Result.succeed(item);
    }

}
