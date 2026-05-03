package com.yami.trading.service.contract;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.contract.domain.ContractApplyOrder;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.contract.domain.ContractOrderProfit;
import com.yami.trading.bean.contract.dto.ContractOrderDTO;
import com.yami.trading.bean.contract.query.ContractOrderQuery;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.FollowWallet;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.bean.trader.domain.Trader;
import com.yami.trading.bean.trader.domain.TraderDaysSetting;
import com.yami.trading.bean.trader.domain.TraderFollowUser;
import com.yami.trading.bean.trader.domain.TraderFollowUserOrder;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.ContractRedisKeys;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.constants.TipConstants;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.contract.ContractOrderMapper;
import com.yami.trading.service.FollowWalletService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.TipService;
import com.yami.trading.service.trader.TraderDaysSettingService;
import com.yami.trading.service.trader.TraderFollowUserOrderService;
import com.yami.trading.service.trader.TraderFollowUserService;
import com.yami.trading.service.trader.TraderService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 非按金额订单Service
 *
 * @author lucas
 * @version 2023-03-29
 */
@Service
@Transactional
@Slf4j
public class ContractOrderService extends ServiceImpl<ContractOrderMapper, ContractOrder> {

    private Logger logger = LogManager.getLogger(ContractOrderService.class);

    private final ConcurrentMap<String, ContractOrder> map = new ConcurrentHashMap<>();

    // 上次批量更新更新过去订单id和订单数据
    Map<String, ContractOrder> oldOrderMap = new ConcurrentHashMap<>();

//    @Autowired
//    private ExchangeRateService exchangeRateService;

    @PostConstruct
    public void init() {
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
        executorService.scheduleAtFixedRate(this::flush, 1, 3, TimeUnit.MINUTES);
    }

    // 强平价格计算
    public synchronized void flush() {
        List<ContractOrder> newOrders = new ArrayList<>(map.values());
        if (!newOrders.isEmpty()) {
            List<ContractOrder> changedOrders = new ArrayList<>();
            for (ContractOrder newOrder : newOrders) {
                ContractOrder oldOrder = oldOrderMap.get(newOrder.getUuid());
                if (oldOrder == null || ObjectUtil.notEqual(oldOrder.getForceClosePrice(), newOrder.getForceClosePrice())) {
                    changedOrders.add(newOrder);
                }
            }
            if (CollectionUtil.isNotEmpty(changedOrders)) {
                getBaseMapper().batchUpdateBuffer(changedOrders);
                for (ContractOrder order : changedOrders) {
                    // 注意： 此处的 order 对象和 map 集合里的 order 对象是同一个对象，如果 map 里的订单状态修改了，
                    // oldOrderMap 里的也是同样的，这会导致上面通过判断订单 forceClosePrice 的值不相等的逻辑没有效果
                    oldOrderMap.put(order.getUuid(), order);
                    // 优化批量更新，或者直接更新这个用户所有的
                }
            }
        }
    }


    @Autowired
    private ItemService itemService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private FollowWalletService followWalletService;


    @Autowired
    private UserService userService;
    @Autowired
    private TipService tipService;
    @Autowired
    private UserDataService userDataService;

    @Autowired
    private TraderService traderService;

    @Autowired
    private TraderDaysSettingService traderDaysSettingService;

    @Autowired
    private TraderFollowUserOrderService traderFollowUserOrderService;

    @Autowired
    private TraderFollowUserService traderFollowUserService;

    @Autowired
    @Lazy
    private ContractApplyOrderService contractApplyOrderService;
    @Autowired
    private SysparaService sysparaService;

    public IPage<ContractOrderDTO> listRecordCur(Page page, ContractOrderQuery query) {
        if (query.getStartTime() != null) {
            query.setStartTimeStr(DateUtil.formatDateTime(query.getStartTime()));
        }
        if (query.getEndTime() != null) {
            query.setEndTimeStr(DateUtil.formatDateTime(query.getEndTime()));
        }

        return baseMapper.listRecordCur(page, query);
    }

    public IPage<ContractOrderDTO> listRecordHistory(Page page, ContractOrderQuery query) {
        if (query.getStartTime() != null) {
            query.setStartTimeStr(DateUtil.formatDateTime(query.getStartTime()));
        }
        if (query.getEndTime() != null) {
            query.setEndTimeStr(DateUtil.formatDateTime(query.getEndTime()));
        }

        return baseMapper.listRecordHistory(page, query);
    }

    /**
     * 持仓单，调用本方法的业务服务，会更新相关业务缓存和订单状态
     *
     * @param partyId
     * @param symbol
     * @param direction
     * @return
     */
    public List<ContractOrder> findSubmitted(String partyId, String symbol, String direction) {
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(partyId), "party_id", partyId);
        queryWrapper.eq(StrUtil.isNotBlank(symbol), "symbol", symbol);
        queryWrapper.eq(StrUtil.isNotBlank(direction), "direction", direction);
        queryWrapper.eq("state", "submitted");
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    /**
     * ，调用本方法的业务方法主要是提取数据，不会用于更新 ContractOrder 记录
     *
     * @param partyId
     * @param symbols
     * @return
     */
    public List<ContractOrder> findSubmitted(String partyId, List<String> symbols) {
        symbols.add("-1");
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", partyId);
        queryWrapper.in("symbol", symbols);
        queryWrapper.eq("state", "submitted");
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    public List<ContractOrder> findSubmitted(String partyId) {
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", partyId);
        queryWrapper.eq("state", "submitted");
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }


    public List<ContractOrder> findSubmitted(String partyId, String symbol, String direction, String startTime, String endTime, String symbolType) {
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(partyId), "party_id", partyId);
        queryWrapper.eq(StrUtil.isNotBlank(symbol), "symbol", symbol);
        queryWrapper.eq(StrUtil.isNotBlank(direction), "direction", direction);
        queryWrapper.eq("state", "submitted");
        List<String> symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        symbols.add("-1");
        if (StringUtils.isNotEmpty(symbolType) && StringUtils.isEmptyString(symbol)) {
            queryWrapper.in(StringUtils.isNotEmpty(symbolType), "symbol", symbols);
        }
        queryWrapper.ge(StringUtils.isNotEmpty(startTime), "date_format(create_time,'%Y-%m-%d')", startTime);
        queryWrapper.le(StringUtils.isNotEmpty(endTime), "date_format(create_time,'%Y-%m-%d')", endTime);

        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    public List<Map<String, Object>> findSubmittedRedis(String partyId, String symbol, String startTime, String endTime, String symbolType) {
        List<ContractOrder> list = findSubmitted(partyId, symbol, null, startTime, endTime, symbolType);
        return this.bulidData(list);
    }

    public List<Map<String, Object>> findSubmittedRedis(String partyId, String symbol) {
        List<ContractOrder> list = findSubmitted(partyId, symbol, null);
        return this.bulidData(list);
    }

    public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type, String queryDate) {
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(partyId), "party_id", partyId);
        queryWrapper.eq(StrUtil.isNotBlank(symbol), "symbol", symbol);
        if ("orders".equals(type)) {
            queryWrapper.eq("state", "submitted");
            queryWrapper.eq(StringUtils.isNotEmpty(queryDate), "date_format(create_time,'%Y-%m-%d')", queryDate);

        } else if ("hisorders".equals(type)) {
            queryWrapper.eq("state", "created");
            queryWrapper.eq(StringUtils.isNotEmpty(queryDate), "date_format(create_time,'%Y-%m-%d')", queryDate);

        }
        queryWrapper.orderByDesc("create_time");

        Date date = DateUtils.addDay(new Date(), -1);

        Page page = new Page(pageNo, pageSize);
        List<ContractOrder> list = baseMapper.selectPage(page, queryWrapper).getRecords();
        List<Map<String, Object>> data = this.bulidData(list);
        return data;
    }


    public Long getOrdersCount(String type, String partyId, String symbol, String symbolType) {
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(partyId), "party_id", partyId);
        queryWrapper.eq(StrUtil.isNotBlank(symbol), "symbol", symbol);
        if ("orders".equals(type)) {
            queryWrapper.eq("state", "submitted");
        } else if ("hisorders".equals(type)) {
            queryWrapper.eq("state", "created");
        }
        List<String> symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        symbols.add("-1");
        if (StringUtils.isNotEmpty(symbolType) && StringUtils.isEmptyString(symbol)) {
            queryWrapper.in(StringUtils.isNotEmpty(symbolType), "symbol", symbols);
        }
        return count(queryWrapper);
    }


    public List<ContractOrder> findSubmitted() {
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state", ContractOrder.STATE_SUBMITTED);
        return list(queryWrapper);
    }

    public ContractOrder findByOrderNoRedis(String orderNo) {
        return RedisUtil.get(ContractRedisKeys.CONTRACT_ORDERNO + orderNo);

    }

    public ContractOrder findByOrderNo(String orderNo) {
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        queryWrapper.last("limit 1");
        List<ContractOrder> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type, String startTime, String endTime, String symbolType) {
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(partyId), "party_id", partyId);
        queryWrapper.eq(StrUtil.isNotBlank(symbol), "symbol", symbol);
        if ("orders".equals(type)) {
            queryWrapper.eq("state", "submitted");
        } else if ("hisorders".equals(type)) {
            queryWrapper.eq("state", "created");
        }
        List<String> symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        symbols.add("-1");
        if (StringUtils.isNotEmpty(symbolType) && StringUtils.isEmptyString(symbol)) {
            queryWrapper.in(StringUtils.isNotEmpty(symbolType), "symbol", symbols);
        }
        queryWrapper.ge(StrUtil.isNotBlank(startTime), "create_time", startTime + " 00:00:00");
        queryWrapper.le(StrUtil.isNotBlank(endTime), "create_time", endTime + " 23:59:59");
        queryWrapper.orderByDesc("create_time");

        Page page = new Page(pageNo, pageSize);
        List<ContractOrder> list = baseMapper.selectPage(page, queryWrapper).getRecords();
        List<Map<String, Object>> data = this.bulidData(list);
        return data;
    }


    /**
     * 平仓，按订单进行平仓
     */
    public ContractOrder saveClose(String partyId, String orderNo) {
        /*
         * 平仓
         */
        ContractOrder order = this.findByOrderNo(orderNo);
        if (order == null
                || !ContractOrder.STATE_SUBMITTED.equals(order.getState())
                || !partyId.equals(order.getPartyId())
                || order.getVolume().compareTo(BigDecimal.ZERO) <= 0) {
            /**
             * 状态已改变，退出处理
             */
            return null;
        }

        /**
         * 收益
         */
        BigDecimal volume = order.getVolume();
        BigDecimal profit = settle(order, order.getVolume());
        String symbol = order.getSymbol();
//        Item item = itemService.findBySymbol(symbol);
//        profit = exchangeRateService.getUsdtByType(profit, item.getType());
        if (ContractOrder.ORDER_FOLLOW == order.getFollow()) { // 跟单订单
//		if (profit > 0) {
            FollowWallet wallet = followWalletService.findByUserId(order.getPartyId());
            BigDecimal amount_before = wallet.getMoney();

//		wallet.setMoney(Arith.add(wallet.getMoney(), profit));

            if (wallet.getMoney().add(profit).compareTo(BigDecimal.ZERO) < 0) {
                profit = wallet.getMoney().negate();
            }

            followWalletService.updateMoney(order.getSymbol(), partyId, profit, BigDecimal.ZERO,
                    Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET_USDT, Constants.MONEYLOG_CONTENT_CONTRACT_CLOSE, "平仓，平仓合约数[" + volume + "],订单号[" + order.getOrderNo() + "]");
        } else {
            //		if (profit > 0) {
            Wallet wallet = walletService.findByUserId(order.getPartyId());
            BigDecimal amount_before = wallet.getMoney();

//		wallet.setMoney(Arith.add(wallet.getMoney(), profit));
            if (wallet.getMoney().add(profit).compareTo(BigDecimal.ZERO) < 0) {
                profit = wallet.getMoney().negate();
            }
            walletService.updateMoney(order.getSymbol(), partyId, profit, BigDecimal.ZERO,
                    Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET_USDT, Constants.MONEYLOG_CONTENT_CONTRACT_CLOSE, "平仓，平仓合约数[" + volume + "],订单号[" + order.getOrderNo() + "]");
        }
        order.setState(ContractOrder.STATE_CREATED);
        order.setVolume(BigDecimal.ZERO);
        order.setDeposit(BigDecimal.ZERO);
        order.setCloseTime(DateUtil.currentSeconds());
        order.setCloseTimeTs(DateUtil.currentSeconds());
//        List<Realtime> list = this.dataService.realtime(order.getSymbol());
//        // 平仓时候把当前价格先更新回去
//        if (list.size() != 0) {
//            Realtime realtime = list.get(0);
//            BigDecimal close = realtime.getClose();
//            order.setCloseAvgPrice(close);
//        }
        if (order.getProfit().signum() == 0) {
            order.setCloseAvgPrice(order.getTradeAvgPrice());
        }

        update(order);

        /**
         * 交易员带单,用户跟单
         */
        traderFollowUserOrderService.traderClose(order, this);

        /**
         * 合约产品平仓后添加当前流水setWithdraw_limit_now_amount
         */
        User party = userService.getById(order.getPartyId());
        party.setWithdrawLimitNowAmount(party.getWithdrawLimitNowAmount().add(order.getDepositOpen()));
        userService.updateById(party);
        if (ObjectUtils.isEmpty(order.getCloseAvgPrice())) {
            order.setCloseAvgPrice(BigDecimal.ZERO);
        }

        //平仓-删除提示
//        tipService.deleteTip(order.getUuid());
        return order;

    }

    /**
     * 前台发起的，直接不缓存
     * updateByIdBuffer 只是更新利润强平价格的，不要处理state状态更新
     *
     * @param entity
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateByIdBuffer(ContractOrder entity) {
        updateProfit(entity);
        map.put(entity.getUuid(), entity);

    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void wrapProfit(ContractOrder contractOrder) {
        if (ContractOrder.STATE_SUBMITTED.equalsIgnoreCase(contractOrder.getState())) {
            ContractOrderProfit cacheProfit = getCacheProfit(contractOrder.getUuid());
            if (cacheProfit != null) {
                contractOrder.setProfit(cacheProfit.getProfit());
                contractOrder.setCloseAvgPrice(cacheProfit.getCloseAvgPrice());
            } else {
                contractOrder.setProfit(BigDecimal.ZERO);

            }

        }
    }

    public ContractOrderProfit getCacheProfit(String uuid) {
        return RedisUtil.get(ContractRedisKeys.CONTRACT_PROFIT_V1 + uuid);
    }


    public void updateProfit(ContractOrder order) {
        if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
            RedisUtil.set(ContractRedisKeys.CONTRACT_PROFIT_V1 + order.getUuid(), BeanUtil.copyProperties(order, ContractOrderProfit.class));
            Map<String, ContractOrder> map =
                    RedisUtil.get(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId());
            if (null == map) {
                map = new ConcurrentHashMap<>();
            }

            ContractOrder orderOld = map.get(order.getOrderNo());

            // 部分更新时候
            if (orderOld == null) {
                return;
            }
            orderOld.setCloseAvgPrice(order.getCloseAvgPrice());
            orderOld.setProfit(order.getProfit());
            orderOld.setForceClosePrice(order.getForceClosePrice());
            map.put(order.getOrderNo(), orderOld);


            RedisUtil.set(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId(), map);

            // 获取单个订单的合约总资产、总保证金、总未实现盈利
            Map<String, BigDecimal> contractAssetsOrder = this.walletService.getMoneyContractByOrder(order);
            Map<String, BigDecimal> contractAssetsOrderOld = this.walletService.getMoneyContractByOrder(orderOld);

            BigDecimal contractAssets = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId());
            if (contractAssets == null) {
                contractAssets = BigDecimal.ZERO;
            }
            BigDecimal contractAssetsDeposit = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId());
            if (contractAssetsDeposit == null) {
                contractAssetsDeposit = BigDecimal.ZERO;
            }
            BigDecimal contractAssetsProfit = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId());
            if (contractAssetsProfit == null) {
                contractAssetsProfit = BigDecimal.ZERO;
            }

            RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId(),
                    contractAssets.add(contractAssetsOrder.get("money_contract")).subtract(contractAssetsOrderOld.get("money_contract")));
            RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId(),
                    contractAssetsDeposit.add(contractAssetsOrder.get("money_contract_deposit")).subtract(contractAssetsOrderOld.get("money_contract_deposit")));
            RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId(),
                    contractAssetsProfit.add(contractAssetsOrder.get("money_contract_profit")).subtract(contractAssetsOrderOld.get("money_contract_profit")));


        }
    }

    public void stopLossAndProfit(ContractOrder order) {
        // 强制刷新
        updateById(order);
        // this.getHibernateTemplate().merge(order);
        RedisUtil.set(ContractRedisKeys.CONTRACT_ORDERNO + order.getOrderNo(), order);
    }

    public void update(ContractOrder order) {
        // 强制刷新
        updateById(order);

        // this.getHibernateTemplate().merge(order);
        RedisUtil.set(ContractRedisKeys.CONTRACT_ORDERNO + order.getOrderNo(), order);
        if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {

            Map<String, ContractOrder> map =
                    RedisUtil.get(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId());
            if (null == map) {
                map = new ConcurrentHashMap<>();
            }

            ContractOrder orderOld = map.get(order.getOrderNo());
            map.put(order.getOrderNo(), order);
            RedisUtil.set(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId(), map);

            // 获取单个订单的合约总资产、总保证金、总未实现盈利
            Map<String, BigDecimal> contractAssetsOrder = this.walletService.getMoneyContractByOrder(order);
            Map<String, BigDecimal> contractAssetsOrderOld = this.walletService.getMoneyContractByOrder(orderOld);

            BigDecimal contractAssets = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId());
            if (contractAssets == null) {
                contractAssets = BigDecimal.ZERO;
            }
            BigDecimal contractAssetsDeposit = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId());
            if (contractAssetsDeposit == null) {
                contractAssetsDeposit = BigDecimal.ZERO;
            }
            BigDecimal contractAssetsProfit = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId());
            if (contractAssetsProfit == null) {
                contractAssetsProfit = BigDecimal.ZERO;
            }

            RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId(),
                    contractAssets.add(contractAssetsOrder.get("money_contract")).subtract(contractAssetsOrderOld.get("money_contract")));
            RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId(),
                    contractAssetsDeposit.add(contractAssetsOrder.get("money_contract_deposit")).subtract(contractAssetsOrderOld.get("money_contract_deposit")));
            RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId(),
                    contractAssetsProfit.add(contractAssetsOrder.get("money_contract_profit")).subtract(contractAssetsOrderOld.get("money_contract_profit")));

        } else if (ContractOrder.STATE_CREATED.equals(order.getState())) {
            // 平仓后，移除持仓列表

            Map<String, ContractOrder> map = RedisUtil.get(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId());
            ContractOrder orderOld = null;
            if (map != null && !map.isEmpty()) {
                orderOld = map.get(order.getOrderNo());
                map.remove(order.getOrderNo());
            }
            RedisUtil.set(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId(), map);

            // 获取单个订单的合约总资产、总保证金、总未实现盈利
            Map<String, BigDecimal> contractAssetsOrderOld = walletService.getMoneyContractByOrder(orderOld);

            BigDecimal contractAssets = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId());
            if (contractAssets == null) {
                contractAssets = BigDecimal.ZERO;
            }
            BigDecimal contractAssetsDeposit = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId());
            if (contractAssetsDeposit == null) {
                contractAssetsDeposit = BigDecimal.ZERO;
            }
            BigDecimal contractAssetsProfit = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId());
            if (contractAssetsProfit == null) {
                contractAssetsProfit = BigDecimal.ZERO;
            }

            RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId(),
                    contractAssets.subtract(contractAssetsOrderOld.get("money_contract")));
            RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId(),
                    contractAssetsDeposit.subtract(contractAssetsOrderOld.get("money_contract_deposit")));
            RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId(),
                    contractAssetsProfit.subtract(contractAssetsOrderOld.get("money_contract_profit")));

            // 平仓则纪录数据（委托平仓，订单直接平仓）
            this.userDataService.saveClose(order);
            User party = userService.getById(order.getPartyId());
            //if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {
            logger.info("delete tip -> " + order.getUuid() + " --> " + order.getOrderNo());
            tipService.deleteTip(order.getUuid());
            //}
        }
    }


    /**
     * 合约订单收益结算
     * 平仓时计算盈亏，确定退还给用户的金额（保证金+盈亏）
     *
     * @param order   持仓订单对象
     * @param volume  平仓的张数
     * @return 退还用户的总金额（保证金+盈亏）
     */
    public BigDecimal settle(ContractOrder order, BigDecimal volume) {
        /**
         * 平仓比率（注释的旧代码）
         */
//        BigDecimal rate = volume.divide(order.getVolumeOpen(), 10, RoundingMode.HALF_UP);

        // 从缓存获取该订单的实时收益数据
        ContractOrderProfit cacheProfit = getCacheProfit(order.getUuid());
        BigDecimal originProfit = BigDecimal.ZERO;
        if (cacheProfit != null) {
            // 如果缓存有收益数据，则使用缓存的收益
            originProfit = cacheProfit.getProfit();
            // 设置平仓均价
            order.setCloseAvgPrice(cacheProfit.getCloseAvgPrice());
        }

        // 计算退还金额：当前保证金 + 收益
        BigDecimal profit = order.getDeposit().add(originProfit);

        // 如果是跟单订单，需要扣除跟单利息（当前这段代码逻辑未完全实现）
        if (ContractOrder.ORDER_FOLLOW == order.getFollow()) { // 跟单还得减去利息收益
            BigDecimal orderAmount = order.getUnitAmount().multiply(order.getTradeAvgPrice()).multiply(order.getLeverRate()); //订单总金额
            // 查询跟单关系
            TraderFollowUserOrder traderFollowUserOrder = traderFollowUserOrderService.findByPartyIdAndOrderNo(order.getPartyId(), order.getOrderNo());
            if (null != traderFollowUserOrder) {
                TraderFollowUser traderFollowUser = traderFollowUserService.findByPartyIdAndTrader_partyId(order.getPartyId(), traderFollowUserOrder.getTraderPartyId());
                if (StringUtils.isNotEmpty(traderFollowUser.getDaysSetting())) {
                    // 查询按天计费配置
                    TraderDaysSetting traderDaysSetting = traderDaysSettingService.selectById(traderFollowUser.getDaysSetting());
                    if (null != traderDaysSetting) { // 借款利率
                        int days = 0;
                        try {
                            // 计算持仓天数
                            days = daysBetween(order.getCreateTime(), new Date());
                        } catch (ParseException e) {
//                            throw new RuntimeException(e);
                            log.error(e.getMessage());
                        }
                        if (days < 0) {
                            days = 0;
                        }
                    }
                }
            }

        }

        // 更新订单的累计平仓金额
        order.setAmountClose(order.getAmountClose().add(profit));
        // 减少持仓数量
        order.setVolume(order.getVolume().subtract(volume));
        // 减少保证金（全额减去开仓保证金）
        order.setDeposit(order.getDeposit().subtract(order.getDepositOpen()));
        // 如果持仓数量减到0，标记订单为已平仓
        if (order.getVolume().compareTo(BigDecimal.ZERO) <= 0) {
            order.setState(ContractOrder.STATE_CREATED);
            order.setCloseTime(DateUtil.currentSeconds());
            order.setCloseTimeTs(DateUtil.currentSeconds());
        }
        // 设置订单最终盈亏
        order.setProfit(originProfit);
        // 返回应退还用户的总金额
        return profit;
    }

    /**
     * 合约订单开仓处理
     * 将委托订单（ContractApplyOrder）转换为持仓订单（ContractOrder），保存到数据库和Redis缓存
     *
     * @param applyOrder  委托订单（用户提交的开仓申请）
     * @param realtime    实时价格数据
     */
    @Transactional
    public void saveOpen(ContractApplyOrder applyOrder, Realtime realtime) {
        // 获取交易品种的配置信息（包含小数点精度、最小波动点等）
        Item item = this.itemService.findBySymbol(applyOrder.getSymbol());

        // 创建持仓订单对象
        ContractOrder order = new ContractOrder();
        order.setPartyId(applyOrder.getPartyId());                // 设置用户ID
        order.setSymbol(applyOrder.getSymbol());                   // 设置交易品种
        // 生成订单号：格式=年月日时分秒(12位)+8位随机数
        String orderNo = com.yami.trading.common.util.DateUtil.formatDate(new Date(), "yyMMddHHmmss") + RandomUtil.getRandomNum(8);
        order.setOrderNo(orderNo);
        order.setDirection(applyOrder.getDirection());             // 设置交易方向：0=买涨，1=买跌
        order.setLeverRate(applyOrder.getLeverRate());             // 设置杠杆倍数
        order.setVolume(applyOrder.getVolume());                   // 设置当前持仓数量
        order.setVolumeOpen(applyOrder.getVolumeOpen());           // 设置开仓数量
        order.setOrderPriceType(applyOrder.getOrderPriceType());   // 设置订单价格类型
        order.setUnitAmount(applyOrder.getUnitAmount());           // 设置每张合约面值
        order.setFee(applyOrder.getFee());                         // 设置手续费
        order.setDeposit(applyOrder.getDeposit());                 // 设置当前保证金
        order.setDepositOpen(applyOrder.getDeposit());             // 设置开仓保证金

        // 设置开仓成交均价（使用当前实时价格）
        order.setTradeAvgPrice(BigDecimal.valueOf(realtime.getClose()));
        // 设置止盈价格
        order.setStopPriceProfit(applyOrder.getStopPriceProfit());
        // 设置止损价格
        order.setStopPriceLoss(applyOrder.getStopPriceLoss());

        // 设置品种最小波动点配置
        order.setPips(BigDecimal.valueOf(item.getPips()));         // 最小波动点（如 0.01）
        order.setPipsAmount(BigDecimal.valueOf(item.getPipsAmount())); // 最小波动点对应金额（如 1）
        order.setFollow(applyOrder.getFollow());                   // 设置是否跟单：0=不是，1=是
        // 复制订单级别的期权预设结果（-1=亏损，0=未设置，1=盈利）
        order.setOptionPreResult(applyOrder.getOptionPreResult());
        // 爆仓是爆整个钱包（注释的旧代码）
//        BigDecimal forceClose = BigDecimal.ZERO;
//        BigDecimal base = order.getDepositOpen().multiply(order.getPips()).divide(order.getPipsAmount(), 10, RoundingMode.HALF_UP).divide(order.getVolume(),10, RoundingMode.HALF_UP);
//        if(order.getDirection().equalsIgnoreCase(ContractOrder.DIRECTION_BUY)){
//            forceClose = order.getTradeAvgPrice().subtract(base).setScale(item.getDecimals(), RoundingMode.HALF_UP);
//        }else if(order.getDirection().equalsIgnoreCase(ContractOrder.DIRECTION_SELL)) {
//            forceClose = order.getTradeAvgPrice().add(base).setScale(item.getDecimals(), RoundingMode.HALF_UP);
//        }
//        if(forceClose.compareTo(BigDecimal.ZERO) <0 ){
//            forceClose  =  BigDecimal.ZERO;
//        }
//        order.setForceClosePrice(forceClose.toPlainString());

        // 保存订单到数据库
        save(order);
        // 缓存订单到Redis（订单号为Key）
        RedisUtil.set(ContractRedisKeys.CONTRACT_ORDERNO + order.getOrderNo(), order);

        // 从Redis获取该用户的持仓订单列表，如果不存在则新建
        Map<String, ContractOrder> map = RedisUtil
                .get(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId());
        if (map == null) {
            map = new ConcurrentHashMap<String, ContractOrder>();
        }
        // 将新订单加入用户持仓列表
        map.put(order.getOrderNo(), order);
        RedisUtil.set(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + order.getPartyId(), map);

        // 获取单个订单的合约总资产、总保证金、总未实现盈利
        Map<String, BigDecimal> contractAssetsOrder = this.walletService.getMoneyContractByOrder(order);

        // 从Redis获取用户已有的合约总资产，如果不存在则初始化为0
        BigDecimal contractAssets = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId());
        if (contractAssets == null) {
            contractAssets = BigDecimal.ZERO;
        }
        // 从Redis获取用户已有的总保证金，如果不存在则初始化为0
        BigDecimal contractAssetsDeposit = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId());
        if (contractAssetsDeposit == null) {
            contractAssetsDeposit = BigDecimal.ZERO;
        }
        // 从Redis获取用户已有的总未实现盈利，如果不存在则初始化为0
        BigDecimal contractAssetsProfit = RedisUtil.get(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId());
        if (contractAssetsProfit == null) {
            contractAssetsProfit = BigDecimal.ZERO;
        }
        // 更新Redis缓存：将新订单的资产数据累加到用户总数据
        RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + order.getPartyId(),
                contractAssets.add(contractAssetsOrder.get("money_contract")));
        RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + order.getPartyId(),
                contractAssetsDeposit.add(contractAssetsOrder.get("money_contract_deposit")));
        RedisUtil.set(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + order.getPartyId(),
                contractAssetsProfit.add(contractAssetsOrder.get("money_contract_profit")));

        /**
         * 委托订单处理：标记已完成，不再在任务中重复处理
         */
        applyOrder.setVolume(BigDecimal.ZERO);
        applyOrder.setState(ContractApplyOrder.STATE_CREATED);

        // 更新委托订单状态为已创建（已完成）
        contractApplyOrderService.updateById(applyOrder);
        // contractApplyOrder 状态改了，此处立即刷新缓存，防止在 ContractApplyOrderHandleJob 处重复处理
        RedisUtil.sremove(RedisKeys.NEW_CONTRACT_APPLY_ORDERS, applyOrder.getUuid());

        /**
         * 跟单业务处理：交易员开仓后，跟随者自动跟单
         */
        /**
         * 交易员带单逻辑：检查该用户是否是已审核的交易员
         */
        Trader trader = traderService.findByPartyIdAndChecked(applyOrder.getPartyId(), 1); // 交易员存在
        if (trader != null) {
            // 交易员开仓，通知所有跟随者自动跟单开仓
            traderFollowUserOrderService.traderOpen(order, contractApplyOrderService, this, 1); // 交易员跟随者开启永续合约委托, 加个跟单标识
        }

        /**
         * 检查是否是跟单订单，如果是需要将TraderFollowUserOrder里的用户委托单号修改成用户持仓单号
         */
        TraderFollowUserOrder traderFollowUserOrder = traderFollowUserOrderService.findByPartyIdAndOrderNo(applyOrder.getPartyId(), applyOrder.getOrderNo());
        if (traderFollowUserOrder != null) {
            // 将持仓订单号更新到跟单关系表中，方便后续平仓时处理
            traderFollowUserOrder.setUserOrderNo(order.getOrderNo());
            traderFollowUserOrderService.update(traderFollowUserOrder);
        }

        // 获取用户信息，用于发送开仓提示
        User party = this.userService.getById(order.getPartyId());
        // 如果是会员角色，发送开仓成功提示
        if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {

            Syspara isUnionStocks = sysparaService.find("is-union-stocks");

            // 根据系统配置判断使用哪种提示类型（普通/联合股票）
            if(isUnionStocks != null && isUnionStocks.getBoolean()){
                if(TipConstants.ACTION_CONTRACT_ITEM_MAP_NEW.get(item.getType()) != null){
                    tipService.saveTip(order.getUuid(), TipConstants.ACTION_CONTRACT_ITEM_MAP_NEW.get(item.getType()),party.getUserId());
                }
            }else{
                if(TipConstants.ACTION_CONTRACT_ITEM_MAP.get(item.getType()) != null){
                    logger.info("ACTION_CONTRACT_ITEM_MAP saveOpen1 -> " + item.getType() + "-->" + order.getOrderNo() + "-->" + order.getUuid());
                    tipService.saveTip(order.getUuid(), TipConstants.ACTION_CONTRACT_ITEM_MAP.get(item.getType()));
                }
            }
        }
        // 删除委托订单的提示，避免重复提示
        tipService.deleteTip(applyOrder.getUuid());
    }

    /**
     * 合约订单平仓处理
     * 将持仓订单（ContractOrder）进行平仓，结算收益，归还保证金
     *
     * @param applyOrder  委托订单（用户提交的平仓申请）
     * @param realtime    实时价格数据
     * @param order_no    持仓订单号
     * @return 更新后的委托订单
     */
    public ContractApplyOrder saveClose(ContractApplyOrder applyOrder, Realtime realtime, String order_no) {
        // 根据订单号查询持仓订单
        ContractOrder order = this.findByOrderNo(order_no);
        // 校验订单状态：订单不存在、非持仓状态、持仓数量为0，则直接返回
        if (order == null || !ContractOrder.STATE_SUBMITTED.equals(order.getState()) || order.getVolume().compareTo(BigDecimal.ZERO) <= 0) {
            /**
             * 状态已改变，退出处理
             */
            return applyOrder;
        }
        // 确定实际平仓数量：如果委托平仓数量不能超过当前持仓数量
        BigDecimal volume;
        if (applyOrder.getVolume().compareTo(order.getVolume()) > 0) {
            volume = order.getVolume();  // 全部平仓
        } else {
            volume = applyOrder.getVolume();  // 部分平仓
        }
        /**
         * 平仓退回的金额（保证金+收益）
         */
        BigDecimal profit = this.settle(order, volume);
        // 更新订单状态到数据库
        update(order);
        // 查询用户钱包
        Wallet wallet = this.walletService.findByUserId(order.getPartyId());
        BigDecimal amount_before = wallet.getMoney();  // 平仓前的余额

        String symbol = order.getSymbol();
//        Item item = itemService.findBySymbol(symbol);
//        profit = exchangeRateService.getUsdtByType(profit, item.getType());
        // 防止钱包余额出现负数：如果余额+收益 < 0，则收益调整为 -余额
        if (wallet.getMoney().add(profit).compareTo(BigDecimal.ZERO) < 0) {
            profit = wallet.getMoney().negate();
        }
        // 更新钱包余额：将收益（保证金+盈亏）加回用户钱包，并记录资金流水
        walletService.updateMoney(symbol, order.getPartyId(), profit, BigDecimal.ZERO,
                Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET_USDT, Constants.MONEYLOG_CONTENT_CONTRACT_CLOSE, "平仓，平仓合约数[" + volume + "],订单号[" + order.getOrderNo() + "]");


        // 更新委托订单剩余数量
        applyOrder.setVolume(applyOrder.getVolume().subtract(volume));
        // 如果委托单剩余数量为0，标记委托订单为已完成
        if (applyOrder.getVolume().compareTo(BigDecimal.ZERO) <= 0) {
            applyOrder.setState(ContractApplyOrder.STATE_CREATED);
        }
        // 更新委托订单到数据库
        contractApplyOrderService.updateById(applyOrder);

        /**
         * 交易员带单：交易员平仓，跟随者也自动平仓
         */
        traderFollowUserOrderService.traderClose(order, this);


        // 删除订单提示
        tipService.deleteTip(order.getUuid());

        return applyOrder;
    }

    public boolean lock(String order_no) {
        return ContractLock.add(order_no);

    }

    public void unlock(String order_no) {
        ContractLock.remove(order_no);

    }

    private List<Map<String, Object>> bulidData(List<ContractOrder> list) {
        List<Map<String, Object>> data = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            ContractOrder order = list.get(i);
            Map<String, Object> map = bulidOne(order);
            data.add(map);
        }
        return data;
    }

    public Map<String, Object> bulidOne(ContractOrder order) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order_no", order.getOrderNo());
        Item bySymbol = itemService.findBySymbol(order.getSymbol());
        String name = "---";
        if (bySymbol != null) {
            name = bySymbol.getName();
        }
        if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
            wrapProfit(order);
        }
        map.put("name", name);
        map.put("symbol", order.getSymbol());
        map.put("create_time", order.getCreateTime());
        // 在最外层做时区时间转换
        map.put("create_time_ts", String.valueOf(order.getCreateTimeTs()));

        if (order.getCloseTime() != null) {
            // 在最外层做时区时间转换
            // 注意： 有的方法返回值中 close_time 是日期字符串类型，有的是时间戳类型！！！ TODO
            map.put("close_time", String.valueOf(order.getCloseTime()));
        } else {
            map.put("close_time", "");
        }

        String orderPriceType = order.getOrderPriceType();
        if (StringUtils.isEmptyString(orderPriceType)) {
            orderPriceType = "opponent";
        }
        map.put("order_price_type", orderPriceType);

        map.put("direction", order.getDirection());
        map.put("lever_rate", order.getLeverRate());
        map.put("trade_avg_price", order.getTradeAvgPrice());
        map.put("close_avg_price", order.getCloseAvgPrice());
        map.put("force_close_price", order.getForceClosePrice());
        if (order.getStopPriceProfit() != null) {
            map.put("stop_price_profit", order.getStopPriceProfit().setScale(4, RoundingMode.HALF_UP));
        } else {
            map.put("stop_price_profit", order.getStopPriceProfit());

        }
        if (order.getStopPriceLoss() != null) {
            map.put("stop_price_loss", order.getStopPriceLoss().setScale(4, RoundingMode.HALF_UP));
        } else {
            map.put("stop_price_loss", order.getStopPriceLoss());
        }
        map.put("state", order.getState());
        map.put("amount", order.getVolume().multiply(order.getUnitAmount()));
        map.put("amount_open", order.getVolumeOpen().multiply(order.getUnitAmount()));
        map.put("fee", order.getFee());
        map.put("deposit", order.getDeposit());
        map.put("deposit_open", order.getDepositOpen());
        map.put("change_ratio", order.getChangeRatio());
        /**
         * 收益
         */
//		if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
//			map.put("profit",
//					df.format(Arith.sub(
//							Arith.add(Arith.add(order.getAmount_close(), order.getProfit()), order.getDeposit()),
//							order.getDeposit_open())));
//		} else {
//			map.put("profit", df.format(
//					Arith.sub(Arith.add(order.getAmount_close(), order.getDeposit()), order.getDeposit_open())));
//		}
        if (order.getProfit() != null) {
            map.put("profit", order.getProfit().setScale(4, RoundingMode.HALF_UP));
        } else {
            map.put("profit", order.getProfit());
        }
        map.put("volume", order.getVolume());
        map.put("volume_open", order.getVolumeOpen());

        return map;
    }

    /**
     * 根据用户批量赎回订单
     *
     * @param partyId
     */
    public void saveCloseRemoveAllByPartyId(String partyId) {
        QueryWrapper<ContractOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", partyId);

        List<ContractOrder> orders = list(queryWrapper);
        List<ContractOrder> findSubmittedContractOrders = findSubmitted(partyId, null, null);
        if (!CollectionUtils.isEmpty(findSubmittedContractOrders)) {
            for (ContractOrder order : orders) {
                if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
                    saveClose(order.getPartyId(), order.getOrderNo());
                }
                RedisUtil.del(ContractRedisKeys.CONTRACT_ORDERNO + order.getOrderNo());
            }
            RedisUtil.del(ContractRedisKeys.CONTRACT_SUBMITTED_ORDER_PARTY_ID + partyId);

            RedisUtil.del(ContractRedisKeys.CONTRACT_ASSETS_PARTY_ID + partyId);
            RedisUtil.del(ContractRedisKeys.CONTRACT_ASSETS_DEPOSIT_PARTY_ID + partyId);
            RedisUtil.del(ContractRedisKeys.CONTRACT_ASSETS_PROFIT_PARTY_ID + partyId);
        }
    }

    /**
     * 根据用户ID、是否跟单以及订单状态查询订单
     *
     * @param partyId
     * @param orderFollow
     * @param state
     * @return
     */
    public List<ContractOrder> selectContractOrderByUserIdAndFollowAndState(String partyId, int orderFollow, String state) {
        LambdaQueryWrapper<ContractOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractOrder::getPartyId, partyId);
        queryWrapper.eq(ContractOrder::getFollow, orderFollow);
        queryWrapper.eq(ContractOrder::getState, state);
        return list(queryWrapper);
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
