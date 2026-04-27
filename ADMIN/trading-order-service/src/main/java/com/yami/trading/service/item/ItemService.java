package com.yami.trading.service.item;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.dto.ItemDTO;
import com.yami.trading.bean.item.dto.ItemLeverageDTO;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.item.ItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 产品Service
 *
 * @author lucas
 * @version 2023-03-10
 */
@Service
@Transactional
@Slf4j
public class ItemService extends ServiceImpl<ItemMapper, Item> {
    @Autowired
    private ItemLeverageService itemLeverageService;
    @Autowired
    @Lazy
    private ItemService proxyItemService;

    // 做成全局模式，减少动态创建对象的次数
    private Map<String, Integer> symbolDecimal = Maps.newHashMap();
    // 做成全局模式，减少动态创建对象的次数
    private Map<String, Item> symbolItem = Maps.newHashMap();
    // 做成全局模式，减少动态创建对象的次数
    private Map<String, String> remarksSymbol = Maps.newHashMap();
    // 做成全局模式，减少动态创建对象的次数
    private List<Item> itemList = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 同时初始化多组全局缓存： symbolDecimal symbolItem remarksSymbol itemList
        itemList = new ArrayList<>(list());
    }

    public String getSymbolByKey(String key) {
        if(StringUtils.isEmptyString(key)){
            return key;
        }
        for(Item item : itemList ){
            if(item.getSymbol().equalsIgnoreCase(key)){
                return item.getSymbol();
            }
            if(item.getName().equalsIgnoreCase(key)){
                return item.getSymbol();
            }
            if(item.getEnName().equalsIgnoreCase(key)){
                return item.getSymbol();
            }
        }
        return  key;
    }
    public List<Item> findDBByType(String type) {
        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<Item>()
                .eq(Item::getType, type);
        return super.baseMapper.selectList(queryWrapper);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Item> findByType(String type) {
        // 大宗商品取外汇大宗商品数据
        if (StringUtils.isNotEmpty(type) && type.equalsIgnoreCase(Item.commodities)) {
            LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<Item>()
                    .eq(Item::getType, Item.forex)
                    .eq(Item::getCategory, Item.commodities);
            return super.baseMapper.selectList(queryWrapper);
        }
        if (itemList.size() == 0) {
            LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<Item>()
                    .eq(Item::getType, type);
            return super.baseMapper.selectList(queryWrapper);

        }
        return itemList.stream().filter(i -> i.getType().equalsIgnoreCase(type)).collect(Collectors.toList());

    }

    /**
     * 获取多个币对，每个类型的数量
     *
     * @param symbols
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Integer> typeCountGroupByType(Collection<String> symbols) {
        // 避免为空时候报错
        symbols.add("-1");
        Map<String, Integer> typeCount = new HashMap<>();
        for (String type : Item.types) {
            typeCount.put(type, 0);
        }
        if (CollectionUtil.isEmpty(symbols)) {
            return typeCount;
        }
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("SYMBOL", symbols);
//        queryWrapper.select("SYMBOL", "TYPE", "count(*) as count")
//                .groupBy("TYPE");
        queryWrapper.select("TYPE", "count(*) as count")
                .groupBy("TYPE");
        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        int sum = 0;
        for (Map<String, Object> data : maps) {
            typeCount.put(data.get("TYPE").toString(), Integer.parseInt(data.get("count").toString()));
            sum += Integer.parseInt(data.get("count").toString());
        }

        typeCount.put("all", sum);

        return typeCount;
    }

    /**
     * 通过 symbol 找去缓存对象，如果第一时间没找到，就将该值当做 remarks 值再尝试一遍；
     * 如果缓存中未能提取到 item 对象，则直接去数据库查询.
     *
     * @param symbol
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Item findBySymbol(String symbol) {
        Item item;
        if (CollectionUtil.isNotEmpty(symbolItem)) {
            item = symbolItem.get(symbol);
            if (item != null) {
                return item;
            }
            item = findByRemarks(symbol);
            if (item != null) {
                return item;
            }
        }
        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<Item>()
                .eq(Item::getSymbol, symbol)
                .last("LIMIT 1");
        return super.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 基于 remarks 字段去缓存提取 item 对象，特性是基于 remarks 映射 symbol，从缓存中提取 item；
     * 如果缓存中没有数据，则直接去数据库查询返回。
     *
     * @param remarks
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Item findByRemarks(String remarks) {
        if (CollectionUtil.isNotEmpty(symbolItem)) {
            // 虚拟货币symbol为btc，remarks 是btcusdt
            if (StringUtils.isNotEmpty(remarks)) {
                String symbol = remarksSymbol.get(remarks);
                if (StringUtils.isNotEmpty(symbol)) {
                    return symbolItem.get(symbol);
                }
            }
            return null;
        }
        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<Item>()
                .eq(Item::getRemarks, remarks)
                .last("LIMIT 1");
        return super.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 基于 remarks 映射 symbol 的缓存，提取 remarks 对应的 symbol 信息；
     * 如果缓存中未能提取到映射信息，则直接去数据库提取。
     *
     * @param remarks
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String getSymbolByRemarks(String remarks) {
        if (CollectionUtil.isNotEmpty(remarksSymbol)) {
            // 虚拟货币symbol为btc，remarks 是btcusdt
            if (StringUtils.isNotEmpty(remarks)) {
                return remarksSymbol.get(remarks);
            }
            return null;
        }
        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<Item>()
                .eq(Item::getRemarks, remarks)
                .last("LIMIT 1");
        return super.baseMapper.selectOne(queryWrapper).getSymbol();
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ItemDTO findById(String id) {
        ItemDTO item = baseMapper.findById(id);
        if (item != null) {
            QueryWrapper wrapper = new QueryWrapper();
            List<ItemLeverageDTO> levels = itemLeverageService.findByItemId(id);
            item.setLevels(levels);
        }

        return item;
    }

    /**
     * 自定义分页检索
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    public IPage<ItemDTO> findPage(Page<ItemDTO> page, QueryWrapper queryWrapper) {
        // 排除已经删除
        queryWrapper.eq("a.del_flag", 0);
        return baseMapper.findList(page, queryWrapper);
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Item> list() {
        List<Item> list = super.list();
        list = list.stream().filter(Item::isActive).collect(Collectors.toList());
        for (Item item : list) {
            symbolDecimal.put(item.getSymbol(), item.getDecimals());
            if (item.getRemarks() != null) {
                symbolDecimal.put(item.getRemarks(), item.getDecimals());
                symbolDecimal.put(item.getRemarks().replace("usdt", ""), item.getDecimals());
            }
        }

        symbolItem = list.stream().collect(Collectors.toMap(Item::getSymbol, Function.identity(), (s1, s2) -> s2));

        for (Item item : list) {
            if (item.getRemarks() != null) {
                remarksSymbol.put(item.getRemarks(), item.getSymbol());
                remarksSymbol.put(item.getSymbol(), item.getSymbol());
                remarksSymbol.put(item.getRemarks().replace("usdt", ""), item.getSymbol());
            }
        }
        return list;
    }

    public List<Item> listManualActive() {
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("crawl_status", Item.ACTIVE);
        List<Item> list = list(queryWrapper);
        return list;
    }
    public List<Item> listWithOutCache() {
        List<Item> list = super.list();
        for (Item item : list) {
            symbolDecimal.put(item.getSymbol(), item.getDecimals());
            if (item.getRemarks() != null) {
                symbolDecimal.put(item.getRemarks(), item.getDecimals());
                symbolDecimal.put(item.getRemarks().replace("usdt", ""), item.getDecimals());
            }
        }
        symbolItem = list.stream()
                .collect(Collectors.toMap(Item::getSymbol, Function.identity(), (s1, s2) -> s2));
        for (Item item : list) {
            if (item.getRemarks() != null) {
                remarksSymbol.put(item.getRemarks(), item.getSymbol());
                remarksSymbol.put(item.getRemarks().replace("usdt", ""), item.getSymbol());
            }
        }
        return list;
    }


    public boolean updateById(Item item) {
        boolean b = super.updateById(item);
        itemList = new ArrayList<>(list());
        return b;
    }

    @Override
    public boolean saveOrUpdate(Item item) {
        boolean b = super.saveOrUpdate(item);
        itemList = new ArrayList<>(list());

        return b;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean b = super.removeById(id);
        itemList = new ArrayList<>(list());
        return b;
    }


    /**
     * 将币对冻结
     * @param symbol
     */
    public void crawFreeze(String symbol) {
        Item bySymbol = findBySymbol(symbol);
        // 只有被激活过的才会被冻结
        if(Item.ACTIVE.equalsIgnoreCase(bySymbol.getCrawlStatus()) ){
            log.info("正在冻结采集状态 {}", symbol);
            bySymbol.setCrawlStatus(Item.FREEZE);
            updateById(bySymbol);
            // 主动刷新缓存
            log.info("冻结采集状态 {} 完成", symbol);

        }
    }

    /**
     * 将币对激活
     * @param symbol
     */
    public void crawlActive(String symbol) {
        Item bySymbol = findBySymbol(symbol);
        // 只有空和被冻结才能激活
        String crawlStatus = bySymbol.getCrawlStatus();
        if(StrUtil.isEmptyIfStr(crawlStatus) || Item.FREEZE.equalsIgnoreCase(crawlStatus) ){
            bySymbol.setCrawlStatus(Item.ACTIVE);
            updateById(bySymbol);
        }
    }

    @Override
    public boolean removeByIds(Collection<?> list) {
        List<Serializable> itemIdList = new ArrayList<>();
        for (Object id : list) {
            if (id == null) {
                continue;
            }
            itemIdList.add((Serializable)id);
        }
        if (CollectionUtil.isEmpty(itemIdList)) {
            return true;
        }
        List<Item> itemList = this.listByIds(itemIdList);
        if (CollectionUtil.isEmpty(itemList)) {
            return true;
        }

        boolean b = super.removeByIds(list);
        // 主动刷新缓存
        proxyItemService.list();
        return b;
    }

    /**
     * 获取品种精度
     *
     * @param symbol
     * @return
     */
    public Integer getDecimal(String symbol) {
        return symbolDecimal.getOrDefault(symbol, 2);
    }

    public List<String> getAllSymbol() {
        List<String> result = new ArrayList<>();
        for (Item item : itemList) {
            result.add(item.getSymbol());
        }
        return result;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Item> cacheGetAll() {
        return itemList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Item> cacheGetByMarket(String symbol) {
        List<Item> cacheGetAll = cacheGetAll();
        if (StringUtils.isNullOrEmpty(symbol)) {
            return cacheGetAll;
        }
        List<Item> result = new ArrayList<Item>();
        for (Item item : cacheGetAll) {
            if (symbol.equals(item.getSymbol()))
                result.add(item);
        }
        return result;
    }

    /**
     * 当前是否开盘
     *
     * @param symbol
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean isOpen(String symbol) {
        Item bySymbol = findBySymbol(symbol);
        return MarketOpenChecker.isMarketOpenByItemCloseType(bySymbol.getOpenCloseType());
    }


    /**
     * 根据前端传的symbol 精确计算干净的symbol
     *
     * @param symbol
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String getCleanSymbol(String symbol) {
        if (symbol.equalsIgnoreCase("usdt")) {
            return "usdt";
        }
        if (symbolItem.containsKey(symbol)) {
            return symbol;
        } else if (symbolItem.containsKey(symbol.toLowerCase())) {
            return symbol.toLowerCase();
        } else if (symbolItem.containsKey(symbol.toUpperCase())) {
            return symbol.toUpperCase();
        }

        return symbol;
    }
}
