package com.yami.trading.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yami.trading.bean.constans.WalletConstants;
import com.yami.trading.bean.contract.domain.ContractApplyOrder;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.bean.future.domain.FuturesOrder;
import com.yami.trading.bean.future.domain.FuturesRedisKeys;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.common.constants.WalletRedisKeys;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.*;
import com.yami.trading.dao.user.WalletMapper;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.contract.ContractApplyOrderService;
import com.yami.trading.service.contract.ContractOrderCalculationService;
import com.yami.trading.service.contract.ContractOrderService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeApplyOrderService;
import com.yami.trading.service.finance.service.FinanceOrderService;
import com.yami.trading.service.future.FuturesOrderService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.miner.service.MinerOrderService;
import com.yami.trading.service.rate.ExchangeRateService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.WalletExtendService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {
    private static Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);
    @Qualifier("dataService")
    @Autowired
    @Lazy
    DataService dataService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    ItemService itemService;
    @Autowired
    MoneyLogService moneyLogService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    WalletExtendService walletExtendService;
    @Autowired
    @Lazy
    FinanceOrderService financeOrderService;
    @Autowired
    @Lazy
    MinerOrderService minerOrderService;
    @Autowired
    private ExchangeRateService exchangeRateService;

    @Override
    public Wallet findByUserId(String userId) {
        Wallet wallet = getOne(Wrappers.<Wallet>query().lambda().eq(Wallet::getUserId, userId));
        return wallet;
    }

    @Override
    public List<WalletExtend> findExtend(String partyId) {
        List<WalletExtend> walletExtends = walletExtendService.findByUserId(partyId);
        return walletExtends;
    }

    @Override
    public List<WalletExtend> findExtend(String partyId, List<String> list_symbol) {
        if (CollUtil.isEmpty(list_symbol)) {
            return Lists.newArrayList();
        }
        return walletExtendService.findByUserIdAndWallettype(partyId, list_symbol);
    }

    @Override
    public WalletExtend saveExtendByPara(String userId, String wallettype) {
        if (StringUtils.isEmptyString(wallettype) || userId == null || StringUtils.isEmptyString(userId.toString())) {
            log.error("saveExtendByPara fail,partyId:{},wallettype:{}", new Object[]{userId, wallettype});
            throw new RuntimeException("saveExtendByPara fail");
        }
        if (wallettype.toUpperCase().equals("USDT")) {
            throw new RuntimeException("USDT  no save fail");
        }
        List<WalletExtend> walletExtends = walletExtendService.findByUserIdAndWallettype(userId, wallettype);
        if (CollectionUtils.isNotEmpty(walletExtends)) {
            return walletExtends.get(0);
        }
        WalletExtend walletExtend = new WalletExtend();
        walletExtend.setPartyId(userId);
        walletExtend.setWallettype(wallettype);
        walletExtendService.save(walletExtend);
        return walletExtend;
    }


    @Override
    public void updateExtend(String partyId, String walletType, double amount, double frozenAmount) {
        List<WalletExtend> walletExtends = walletExtendService.findByUserIdAndWallettype(partyId, walletType);
        WalletExtend walletExtend = null;
        if (CollectionUtils.isNotEmpty(walletExtends)) {
            walletExtend = walletExtends.get(0);
        }
        if (walletExtend == null) {
            walletExtend = new WalletExtend();
            walletExtend.setPartyId(partyId);
            walletExtend.setWallettype(walletType);
            walletExtendService.save(walletExtend);
        }
        walletExtend.setAmount(Arith.add(walletExtend.getAmount(), amount));
        walletExtend.setFreezeAmount(Arith.add(walletExtend.getFreezeAmount(), frozenAmount));
        if (walletExtend.getAmount() < 0) {
            log.error("钱包操作失败，类型：{}，余额：{}", walletType, walletExtend.getAmount());
            throw new YamiShopBindException("余额不足");
        }
        if (!walletExtendService.updateById(walletExtend)) {
            log.error("钱包操作失败，类型：{}，余额：{}", walletType, walletExtend.getAmount());
            throw new YamiShopBindException("余额不足");
        }
        redisTemplate.opsForValue().set(WalletRedisKeys.WALLET_EXTEND_PARTY_ID + partyId + walletType, walletExtend);
    }

    @Override
    public void updateExtend(String partyId, String walletType, double amount) {
        List<WalletExtend> walletExtends = walletExtendService.findByUserIdAndWallettype(partyId, walletType);
        WalletExtend walletExtend = null;
        if (CollectionUtils.isNotEmpty(walletExtends)) {
            walletExtend = walletExtends.get(0);
        }
        if (walletExtend == null) {
            walletExtend = this.saveExtendByPara(partyId, walletType);
        }
        log.info(JSONUtil.toJsonStr(walletExtend));
        walletExtend.setAmount(Arith.add(walletExtend.getAmount(), amount));
        if (walletExtend.getAmount() < 0) {
            log.error("钱包操作失败，用户：{}，类型：{}，余额：{}", partyId, walletType, walletExtend.getAmount());
            throw new YamiShopBindException("余额不足");
        }
        if (!walletExtendService.updateById(walletExtend)) {
            log.error("钱包操作失败，用户：{}，类型：{}，余额：{}", partyId, walletType, walletExtend.getAmount());
            throw new YamiShopBindException("余额不足");
        }
        redisTemplate.opsForValue().set(WalletRedisKeys.WALLET_EXTEND_PARTY_ID + partyId + walletType, walletExtend);
    }

    @Override
    public Wallet saveWalletByPartyId(String partyId) {
        Wallet wallet = findByUserId(partyId);
        if (wallet != null) {
            return wallet;
        } else {
            Date now = new Date();
            wallet = new Wallet();
            wallet.setCreateTime(now);
            wallet.setCreateTimeTs(now.getTime() / 1000L);
            wallet.setUpdateTime(now);
            wallet.setUpdateTimeTs(now.getTime() / 1000L);
            wallet.setUserId(partyId);
            save(wallet);
            return wallet;
        }
    }

    @Override
    public BigDecimal sumMoney(List<String> userIds) {
        return baseMapper.sumMoney(userIds);
    }

    @Override
    @Transactional
    public void updateMoney(String symbol, String userId, BigDecimal money, BigDecimal amountFee,
                            String category, String walletType, String contentType, String log) {

        Date now = new Date();
        Wallet wallet = saveWalletByPartyId(userId);
        BigDecimal amountBefore = wallet.getMoney();
        wallet.setMoney(wallet.getMoney().add(money));
        wallet.setUpdateTime(now);
        wallet.setUpdateTimeTs(now.getTime() / 1000L);
        if (wallet.getMoney().doubleValue() < 0) {
            throw new YamiShopBindException("余额不足");
        }
        updateById(wallet);

        // 账变日志
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCreateTime(now);
        moneyLog.setUpdateTimeTs(now.getTime() / 1000L);
        moneyLog.setUpdateTime(now);
        moneyLog.setUpdateTimeTs(now.getTime() / 1000L);
        moneyLog.setSymbol(symbol);
        moneyLog.setCategory(category);
        moneyLog.setAmountBefore(amountBefore);
        moneyLog.setAmount(money);
        moneyLog.setAmountAfter(wallet.getMoney());
        moneyLog.setUserId(userId);
        moneyLog.setWalletType(walletType);
        moneyLog.setContentType(contentType);
        moneyLog.setLog(log);
        moneyLogService.save(moneyLog);


    }

    /*
     * 获取 所有订单 永续合约总资产、总保证金、总未实现盈利,当日盈利
     */
    @Override
    public Map<String, Double> getMoneyContract(Serializable partyId, String symbolType) {
        ContractOrderCalculationService contractOrderCalculationService = ApplicationUtil.getBean(ContractOrderCalculationService.class);
        double money_contract = 0;
        double money_contract_deposit = 0;
        double money_contract_profit = 0;
        double money_contract_profit_today = 0;

        ContractOrderService contractOrderService = ApplicationUtil.getBean(ContractOrderService.class);
        List<ContractOrder> contractOrders = contractOrderService.findSubmitted(partyId.toString(), "", "");
        if (contractOrders != null) {
            for (ContractOrder order : contractOrders) {
                contractOrderService.wrapProfit(order);
                String symbol = order.getSymbol();
                Item bySymbol = itemService.findBySymbol(symbol);
                if (bySymbol == null) {
                    continue;
                }
                // 类型不对直接continue
                if (StringUtils.isNotEmpty(symbolType)) {
                    if (!bySymbol.getType().equalsIgnoreCase(symbolType)) {
                        continue;
                    }

                }
                // 真正下单里
                double order_volume = 1;

                if (order.getLeverRate() != null && order.getLeverRate().compareTo(BigDecimal.ZERO) != 0) {
                    order_volume = order.getVolumeOpen().divide(order.getLeverRate(), 6, RoundingMode.FLOOR).doubleValue();
                } else {
                    order_volume = order.getVolumeOpen().doubleValue();
                }

                double amount = Arith.add(Arith.mul(order_volume, order.getUnitAmount().doubleValue()), order.getProfit().doubleValue());
                money_contract = Arith.add(amount, money_contract);
                money_contract_deposit = Arith.add(order.getDeposit().doubleValue(), money_contract_deposit);
                money_contract_profit = Arith.add(order.getProfit().doubleValue(), money_contract_profit);
                // 只需要计算当日盈亏比例*金额就是当日盈亏
                money_contract_profit_today = contractOrderCalculationService.calculateTodayProfit(order, ZoneId.systemDefault()).doubleValue();

            }
        }

        Map<String, Double> moneys_contract = new HashMap<String, Double>();
        moneys_contract.put("money_contract", money_contract);
        moneys_contract.put("money_contract_deposit", money_contract_deposit);
        moneys_contract.put("money_contract_profit", money_contract_profit);
        moneys_contract.put("money_contract_profit_today", money_contract_profit_today);
        // usdt余额
        Wallet wallet = new Wallet();
        if (!"".equals(partyId) && partyId != null) {
            wallet = findByUserId(partyId.toString());
        }
        moneys_contract.put("money_wallet", wallet.getMoney().doubleValue());

        return moneys_contract;
    }

    @Override
    public Map<String, Object> getMoneyAll(Serializable partyId) {

        Map<String, Object> data = new HashMap<String, Object>();
        DecimalFormat df2 = new DecimalFormat("#.##");

        double money = 0;
        double money_wallet = 0;
        double money_coin = 0;
        double money_all_coin = 0;
        double money_finance = 0;
        double money_miner = 0;
        double money_contractApply = 0;
        double money_contract = 0;
        double money_contract_deposit = 0;
        double money_contract_profit = 0;
        double money_futures = 0;
        double money_futures_profit = 0;

        // 先获取一次所有币种的数据来计算
        String data_symbol = "";
        List<String> list_symbol = new ArrayList<String>();

        List<Item> list_items = this.itemService.cacheGetByMarket("");
        for (int i = 0; i < list_items.size(); i++) {
            Item items = list_items.get(i);
            list_symbol.add(items.getSymbol());
            if (i != 0) {
                data_symbol = data_symbol + "," + items.getSymbol();
            } else {
                data_symbol = items.getSymbol();
            }
        }

        List<Realtime> realtime_all = this.dataService.realtime(data_symbol);
        if (realtime_all.size() <= 0) {
            throw new BusinessException("系统错误，请稍后重试  getMoneyAll");
        }

        // usdt余额
        Wallet wallet = new Wallet();
        if (!"".equals(partyId) && partyId != null) {
            wallet = saveWalletByPartyId(partyId.toString());
        }

        money = wallet.getMoney().doubleValue();
        // 钱包USDT
        money_wallet = wallet.getMoney().doubleValue();
        // 币余额
        money_coin = this.getWalletExtendConvertUsdt(partyId.toString(), realtime_all, list_symbol);
        money = money + money_coin;
        // 钱包USDT+币余额
        money_all_coin = money;
        // 理财 todo
        money_finance = this.getMoneyFinance(partyId, realtime_all);
        money = money + money_finance;
        // 矿机 todo
        money_miner = this.getMoneyMiner(partyId, realtime_all);
//		money_miner = this.getMoneyMinerRedis(partyId, realtime_all);
        money = money + money_miner;
        // 永续委托
        money_contractApply = getMoneyContractApply(partyId);
        money = money + money_contractApply;

//		Map<String, Object> moneys_contract = this.getMoneyContract(partyId);
//		// 永续
//		money_contract = (Double) moneys_contract.get("money_contract");
//		// 永续总保证金
//		money_contract_deposit = (Double) moneys_contract.get("money_contract_deposit");
//		// 永续总未实现盈亏
//		money_contract_profit = (Double) moneys_contract.get("money_contract_profit");

        Map<String, BigDecimal> moneys_contract = this.getMoneyContractDB(partyId);
        if (null != moneys_contract && 0 != moneys_contract.size()) {
            // 永续
            Object money_contract1 = moneys_contract.get("money_contract");
            if (money_contract1 instanceof BigDecimal) {
                money_contract = ((BigDecimal) money_contract1).doubleValue();
            } else {
                money_contract = (Double) money_contract1;
            }
            // 永续总保证金
            Object money_contract_deposit1 = moneys_contract.get("money_contract_deposit");
            if (money_contract_deposit1 instanceof BigDecimal) {
                money_contract_deposit = ((BigDecimal) money_contract_deposit1).doubleValue();
            } else {
                money_contract_deposit = (Double) money_contract_deposit1;
            }
            // 永续总未实现盈亏
            Object money_contract_profit1 = moneys_contract.get("money_contract_profit");
            if (money_contract_deposit1 instanceof BigDecimal) {
                money_contract_profit = ((BigDecimal) money_contract_profit1).doubleValue();
            } else {
                money_contract_deposit = (Double) money_contract_deposit1;
            }
        }

        money = money + money_contract;

//		Map<String, Object> moneys_futures = this.getMoneyFutures(partyId);
//		// 交割
//		money_futures = (Double) moneys_futures.get("money_futures");
//		// 交割未实现盈亏
//		money_futures_profit = (Double) moneys_futures.get("money_futures_profit");

        Map<String, Object> moneys_futures = this.getMoneyFuturesRedis(partyId);
        if (null != moneys_futures && 0 != moneys_futures.size()) {
            // 交割
            money_futures = (Double) moneys_futures.get("money_futures");
            // 交割未实现盈亏
            money_futures_profit = (Double) moneys_futures.get("money_futures_profit");
        }

        money = money + money_futures;

        // 币币交易
        money = money + this.getMoneyexchangeApplyOrders(partyId, realtime_all);

        data.put("total", df2.format(money));
        //锁定金额
        data.put("lock_money", df2.format(wallet.getLockMoney()));
        //冻结金额
        data.put("freeze_money", df2.format(wallet.getFreezeMoney()));
        data.put("money_wallet", df2.format(money_wallet));
        data.put("money_coin", df2.format(money_coin));
        data.put("money_all_coin", df2.format(money_all_coin));
        data.put("money_miner", df2.format(money_miner));
        data.put("money_finance", df2.format(money_finance));
        data.put("money_contract", df2.format(Arith.add(money_contract, money_contractApply)));
        data.put("money_contract_deposit", df2.format(money_contract_deposit));
        data.put("money_contract_profit", df2.format(money_contract_profit));
        data.put("money_futures", df2.format(money_futures));
        data.put("money_futures_profit", df2.format(money_futures_profit));


        data.put("forex_money_contract", df2.format(getMoneyContractTotal(partyId.toString(), "forex")));
        data.put("forex_money_futures", df2.format(getMoneyFutureTotal(partyId.toString(), "forex")));

        data.put("indices_money_contract", df2.format(getMoneyContractTotal(partyId.toString(), "indices")));
        data.put("indices_money_futures", df2.format(getMoneyFutureTotal(partyId.toString(), "indices")));

        data.put("cryptos_money_contract", df2.format(getMoneyContractTotal(partyId.toString(), "cryptos")));
        data.put("cryptos_money_futures", df2.format(getMoneyFutureTotal(partyId.toString(), "cryptos")));

        data.put("us_stocks_money_contract", df2.format(getMoneyContractTotal(partyId.toString(), "US-stocks")));
        data.put("us_stocks_money_futures", df2.format(getMoneyFutureTotal(partyId.toString(), "US-stocks")));


        data.put("stocks_money_contract", df2.format(getMoneyContractTotal(partyId.toString(), "US-stocks")));
        data.put("stocks_money_asserts", df2.format(getMoneyFutureTotal(partyId.toString(), "US-stocks")));

//        //etf
//        Map<String, Object> moneyIndices =getMoneyAll(partyId, "indices");
//        data.put("indices_money_asserts",df2.format(Double.parseDouble(moneyIndices.get("symbol_type_asserts").toString())));
//        //加密
//        Map<String, Object> moneyCryptos =getMoneyAll(partyId, "cryptos");
//        data.put("cryptos_money_asserts",df2.format(Double.parseDouble(moneyCryptos.get("symbol_type_asserts").toString())));
//        //外汇
//        Map<String, Object> moneyForex =getMoneyAll(partyId, "forex");
//        data.put("forex_money_asserts",df2.format(Double.parseDouble(moneyForex.get("symbol_type_asserts").toString())));
//        //美股
//        Map<String, Object> moneyUsStocks =getMoneyAll(partyId, "US-stocks");
//        data.put("us_stocks_money_asserts",df2.format(Double.parseDouble(moneyUsStocks.get("symbol_type_asserts").toString())));
//        //港股
//        Map<String, Object> moneyHkStocks =getMoneyAll(partyId, "HK-stocks");
//        data.put("hk_stocks_money_asserts",df2.format(Double.parseDouble(moneyHkStocks.get("symbol_type_asserts").toString())));
//        //台股
//        Map<String, Object> moneyTwStocks =getMoneyAll(partyId, "TW-stocks");
//        data.put("tw_stocks_money_asserts",df2.format(Double.parseDouble(moneyTwStocks.get("symbol_type_asserts").toString())));

//        data.put("commodities_money_contract", df2.format(getMoneyContractTotal(partyId.toString(), "commodities")));
//        data.put("commodities_money_futures", df2.format(getMoneyFutureTotal(partyId.toString(), "commodities")));

        return data;
    }

    public double getMoneyContractTotal(String partyId, String symbolType) {
        Map<String, Double> moneys_contract = getMoneyContract(partyId, symbolType);
        double money_contract = 0;
        if (null != moneys_contract && 0 != moneys_contract.size()) {
            // 永续
            money_contract = moneys_contract.get("money_contract");
        }
        double money_contractApply = getMoneyContractApply(partyId, symbolType);
        return money_contract + money_contractApply;

    }

    public double getMoneyFutureTotal(String partyId, String symbolType) {
        Map<String, Double> moneys_futures = this.getMoneyFutures(partyId, symbolType);
        // 交割
        if (moneys_futures == null) {
            return 0;
        }
        return moneys_futures.get("money_futures");

    }


    @Override
    public Map<String, Object> getMoneyAll(Serializable partyId, String symbolType) {

        Map<String, Object> data = new HashMap<String, Object>();
        DecimalFormat df2 = new DecimalFormat("#.##");

        double money = 0;
        double money_wallet = 0;
        double money_coin = 0;
        double money_all_coin = 0;
        double money_finance = 0;
        double money_miner = 0;
        double money_contractApply = 0;
        double money_contract = 0;
        double money_contract_deposit = 0;
        double money_contract_profit = 0;
        double money_futures = 0;
        double money_futures_profit = 0;
        // 当前类型，持有的资产
        double symbol_type_asserts = 0;

        // 先获取一次所有币种的数据来计算
        String data_symbol = "";
        List<String> list_symbol = new ArrayList<String>();

        List<Item> list_items = this.itemService.findByType(symbolType);
        for (int i = 0; i < list_items.size(); i++) {
            Item items = list_items.get(i);
            list_symbol.add(items.getSymbol());
            if (i != 0) {
                data_symbol = data_symbol + "," + items.getSymbol();
            } else {
                data_symbol = items.getSymbol();
            }
        }

        List<Realtime> realtime_all = this.dataService.realtime(data_symbol);
        if (realtime_all.size() <= 0) {
//            throw new BusinessException("系统错误，请稍后重试 getMoneyAll => "+data_symbol);
        }

        // usdt余额
        Wallet wallet = new Wallet();
        if (!"".equals(partyId) && partyId != null) {
            wallet = saveWalletByPartyId(partyId.toString());
        }

        money = wallet.getMoney().doubleValue();
        // 钱包USDT
        money_wallet = wallet.getMoney().doubleValue();
        // 币余额
        if (CollectionUtils.isEmpty(list_symbol)) {
            money_coin = 0;
        } else {
            money_coin = this.getWalletExtendConvertUsdt(partyId.toString(), realtime_all, list_symbol);
        }
        money = money + money_coin;
        symbol_type_asserts = symbol_type_asserts + money_coin;
        // 钱包USDT+币余额
        money_all_coin = money;
        // 理财 todo
//       money_finance = this.getMoneyFinance(partyId, realtime_all);
//       money = money + money_finance;
//       // 矿机 todo
//       money_miner = this.getMoneyMiner(partyId, realtime_all);
//		money_miner = this.getMoneyMinerRedis(partyId, realtime_all);
        money = money + money_miner;
        // 永续委托
        money_contractApply = getMoneyContractApply(partyId, list_symbol);
        money = money + money_contractApply;
        symbol_type_asserts = symbol_type_asserts + money_contractApply;

//		Map<String, Object> moneys_contract = this.getMoneyContract(partyId);
//		// 永续
//		money_contract = (Double) moneys_contract.get("money_contract");
//		// 永续总保证金
//		money_contract_deposit = (Double) moneys_contract.get("money_contract_deposit");
//		// 永续总未实现盈亏
//		money_contract_profit = (Double) moneys_contract.get("money_contract_profit");

        Map<String, BigDecimal> moneys_contract = this.getMoneyContractDB(partyId, list_symbol);
        if (null != moneys_contract && 0 != moneys_contract.size()) {
            // 永续
            Object money_contract1 = moneys_contract.get("money_contract");
            if (money_contract1 instanceof BigDecimal) {
                money_contract = ((BigDecimal) money_contract1).doubleValue();
            } else {
                money_contract = (Double) money_contract1;
            }
            // 永续总保证金
            Object money_contract_deposit1 = moneys_contract.get("money_contract_deposit");
            if (money_contract_deposit1 instanceof BigDecimal) {
                money_contract_deposit = ((BigDecimal) money_contract_deposit1).doubleValue();
            } else {
                money_contract_deposit = (Double) money_contract_deposit1;
            }
            // 永续总未实现盈亏
            Object money_contract_profit1 = moneys_contract.get("money_contract_profit");
            if (money_contract_deposit1 instanceof BigDecimal) {
                money_contract_profit = ((BigDecimal) money_contract_profit1).doubleValue();
            } else {
                money_contract_deposit = (Double) money_contract_deposit1;
            }
        }

        money = money + money_contract;
        symbol_type_asserts = symbol_type_asserts + money_contract;

//		Map<String, Object> moneys_futures = this.getMoneyFutures(partyId);
//		// 交割
//		money_futures = (Double) moneys_futures.get("money_futures");
//		// 交割未实现盈亏
//		money_futures_profit = (Double) moneys_futures.get("money_futures_profit");

        Map<String, Double> moneys_futures = this.getMoneyFuturesDB(partyId, list_symbol);
        if (null != moneys_futures && 0 != moneys_futures.size()) {
            // 交割
            money_futures = (Double) moneys_futures.get("money_futures");
            // 交割未实现盈亏
            money_futures_profit = (Double) moneys_futures.get("money_futures_profit");
        }

        money = money + money_futures;
        symbol_type_asserts = symbol_type_asserts + money_futures;

        // 币币交易
        double moneyexchangeApplyOrders = 0;
        if (CollectionUtils.isEmpty(list_symbol)) {
            moneyexchangeApplyOrders = this.getMoneyexchangeApplyOrders(partyId, realtime_all);
        }
        money = money + moneyexchangeApplyOrders;
        symbol_type_asserts = symbol_type_asserts + moneyexchangeApplyOrders;

        data.put("total", df2.format(money));
        data.put("symbol_type_asserts", df2.format(symbol_type_asserts));
        //锁定金额
        data.put("lock_money", df2.format(wallet.getLockMoney()));
        //冻结金额
        data.put("freeze_money", df2.format(wallet.getFreezeMoney()));
        data.put("money_wallet", df2.format(money_wallet));
        data.put("money_coin", df2.format(money_coin));
        data.put("money_all_coin", df2.format(money_all_coin));
        data.put("money_miner", df2.format(money_miner));
        data.put("money_finance", df2.format(money_finance));
        data.put("money_contract", df2.format(Arith.add(money_contract, money_contractApply)));
        data.put("money_contract_deposit", df2.format(money_contract_deposit));
        data.put("money_contract_profit", df2.format(money_contract_profit));
        data.put("money_futures", df2.format(money_futures));
        data.put("money_futures_profit", df2.format(money_futures_profit));

        return data;
    }

    /**
     * 获取币种分类总资产 和账户USDT余额
     *
     * @param partyId
     * @param symbolType
     * @return
     */
    @Override
    public Map<String, Object> getTotalAssets(String partyId, String symbolType) {
        Map<String, Object> data = new HashMap<>();
        // 主钱包
        Wallet wallet = saveWalletByPartyId(partyId);

        // 统计拓展钱包
        List<Item> items = itemService.findByType(symbolType);
        List<String> symbolList = items.stream().map(Item::getSymbol).collect(Collectors.toList());
        List<Realtime> realtimeList = dataService.realtimeByType(symbolType);
        double convertUsdt = getWalletExtendConvertUsdt(partyId, realtimeList, symbolList);


        // logger.info("getTotalAssets ==> "+partyId + " ===> "+symbolType+"====>"+convertUsdt);
        // logger.info("getTotalAssets2 ==> "+ wallet.getMoney().add(BigDecimal.valueOf(convertUsdt)).setScale(2, BigDecimal.ROUND_DOWN));

        BigDecimal money = wallet.getMoney().setScale(2, BigDecimal.ROUND_DOWN);
        data.put("usdtBalance", money);
        data.put("totalAssets", money.add(BigDecimal.valueOf(convertUsdt)).setScale(2, BigDecimal.ROUND_DOWN));
        return data;
    }

    /*
     * 获取 所有订单 永续合约总资产、总保证金、总未实现盈利 redis
     */
    public Map<String, BigDecimal> getMoneyContractDB(Serializable partyId) {

        List<ContractOrder> list = ApplicationUtil.getBean(ContractOrderService.class).findSubmitted(partyId.toString());

        // 永续合约：总资产、总保证金、总未实现盈利
        Map<String, Map<String, BigDecimal>> contractAssetsMap = new ConcurrentHashMap<>();

        for (ContractOrder order : list) {
            if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
                // 获取 单个订单 永续合约总资产、总保证金、总未实现盈利
                Map<String, BigDecimal> contractAssetsOrder = getMoneyContractByOrder(order);

                if (contractAssetsMap.containsKey(order.getPartyId())) {
                    Map<String, BigDecimal> contractAssetsOld = contractAssetsMap.get(order.getPartyId());
                    if (null == contractAssetsOld) {
                        contractAssetsOld = new HashMap<>();
                        contractAssetsOld.put("money_contract", BigDecimal.ZERO);
                        contractAssetsOld.put("money_contract_deposit", BigDecimal.ZERO);
                        contractAssetsOld.put("money_contract_profit", BigDecimal.ZERO);
                    }
                    contractAssetsOld.put("money_contract", contractAssetsOld.get("money_contract").add(contractAssetsOrder.get("money_contract")));
                    contractAssetsOld.put("money_contract_deposit", contractAssetsOld.get("money_contract_deposit").add(contractAssetsOrder.get("money_contract_deposit")));
                    contractAssetsOld.put("money_contract_profit", contractAssetsOld.get("money_contract_profit").add(contractAssetsOrder.get("money_contract_profit")));
                    contractAssetsMap.put(order.getPartyId(), contractAssetsOld);
                } else {
                    contractAssetsMap.put(order.getPartyId(), contractAssetsOrder);
                }
            }
        }
        return contractAssetsMap.get(partyId);
    }

    public Map<String, BigDecimal> getMoneyContractDB(Serializable partyId, List<String> symbols) {
        List<ContractOrder> list = ApplicationUtil.getBean(ContractOrderService.class).findSubmitted(partyId.toString(), symbols);

        // 永续合约：总资产、总保证金、总未实现盈利
        Map<String, Map<String, BigDecimal>> contractAssetsMap = new ConcurrentHashMap<>();

        for (ContractOrder order : list) {
            if (ContractOrder.STATE_SUBMITTED.equals(order.getState())) {
                // 获取 单个订单 永续合约总资产、总保证金、总未实现盈利
                Map<String, BigDecimal> contractAssetsOrder = getMoneyContractByOrder(order);

                if (contractAssetsMap.containsKey(order.getPartyId())) {
                    Map<String, BigDecimal> contractAssetsOld = contractAssetsMap.get(order.getPartyId());
                    if (null == contractAssetsOld) {
                        contractAssetsOld = new HashMap<>();
                        contractAssetsOld.put("money_contract", BigDecimal.ZERO);
                        contractAssetsOld.put("money_contract_deposit", BigDecimal.ZERO);
                        contractAssetsOld.put("money_contract_profit", BigDecimal.ZERO);
                    }
                    contractAssetsOld.put("money_contract", contractAssetsOld.get("money_contract").add(contractAssetsOrder.get("money_contract")));
                    contractAssetsOld.put("money_contract_deposit", contractAssetsOld.get("money_contract_deposit").add(contractAssetsOrder.get("money_contract_deposit")));
                    contractAssetsOld.put("money_contract_profit", contractAssetsOld.get("money_contract_profit").add(contractAssetsOrder.get("money_contract_profit")));
                    contractAssetsMap.put(order.getPartyId(), contractAssetsOld);
                } else {
                    contractAssetsMap.put(order.getPartyId(), contractAssetsOrder);
                }
            }
        }
        return contractAssetsMap.get(partyId);

    }

    public double getMoneyContractApply(Serializable partyId) {
        double money_contractApply = 0;
        ContractApplyOrderService contractApplyOrderService = ApplicationUtil.getBean(ContractApplyOrderService.class);

        List<ContractApplyOrder> contractApplyOrders = contractApplyOrderService.findSubmitted(partyId.toString(), "", "", "");
        if (contractApplyOrders != null) {

            for (ContractApplyOrder order : contractApplyOrders) {
                double amount = Arith.mul(order.getVolumeOpen().doubleValue(), order.getUnitAmount().doubleValue());
                money_contractApply = Arith.add(amount, money_contractApply);
            }
        }

        return money_contractApply;
    }

    public double getMoneyContractApply(Serializable partyId, String symbolType) {
        double money_contractApply = 0;
        ContractApplyOrderService contractApplyOrderService = ApplicationUtil.getBean(ContractApplyOrderService.class);

        List<ContractApplyOrder> contractApplyOrders = contractApplyOrderService.findSubmitted(partyId.toString(), "", "", "");
        if (contractApplyOrders != null) {

            for (ContractApplyOrder order : contractApplyOrders) {
                // 不是这个类型的，跳过
                Item bySymbol = itemService.findBySymbol(order.getSymbol());
                if (bySymbol == null) {
                    log.error("getMoneyContractApply 找不到币对 [{}]", order.getSymbol());
                    continue;
                }
                if (!ObjectUtil.equals(bySymbol.getType(), symbolType)) {
                    continue;
                }
                double amount = Arith.mul(order.getVolumeOpen().doubleValue(), order.getUnitAmount().doubleValue());
                money_contractApply = Arith.add(amount, money_contractApply);
            }
        }

        return money_contractApply;
    }

    public double getMoneyContractApply(Serializable partyId, List<String> symbols) {
        double money_contractApply = 0;
        ContractApplyOrderService contractApplyOrderService = ApplicationUtil.getBean(ContractApplyOrderService.class);

        List<ContractApplyOrder> contractApplyOrders = contractApplyOrderService.findSubmitted(partyId.toString(), "", "", "");
        if (contractApplyOrders != null) {

            for (ContractApplyOrder order : contractApplyOrders) {
                if (symbols.contains(order.getSymbol())) {
                    double amount = Arith.mul(order.getVolumeOpen().doubleValue(), order.getUnitAmount().doubleValue());
                    money_contractApply = Arith.add(amount, money_contractApply);
                }

            }
        }

        return money_contractApply;
    }

    @Override
    public Map<String, BigDecimal> getMoneyContractByOrder(ContractOrder order) {
        Map<String, BigDecimal> moneysContract = new HashMap<String, BigDecimal>();
        if (null == order) {
            moneysContract.put("money_contract", BigDecimal.ZERO);
            moneysContract.put("money_contract_deposit", BigDecimal.ZERO);
            moneysContract.put("money_contract_profit", BigDecimal.ZERO);
            return moneysContract;
        }
        ApplicationUtil.getBean(ContractOrderService.class).wrapProfit(order);
        BigDecimal orderVolume = BigDecimal.ONE;

        if (order.getLeverRate() != null && order.getLeverRate().compareTo(BigDecimal.ZERO) != 0) {
            orderVolume = order.getVolumeOpen().divide(order.getLeverRate(), 2, RoundingMode.FLOOR);
        } else {
            orderVolume = order.getVolumeOpen();
        }
        BigDecimal moneyContract = orderVolume.multiply(order.getUnitAmount()).add(order.getProfit());
        BigDecimal moneyContractDeposit = order.getDeposit();
        BigDecimal moneyContractProfit = order.getProfit();


        moneysContract.put("money_contract", moneyContract);
        moneysContract.put("money_contract_deposit", moneyContractDeposit);
        moneysContract.put("money_contract_profit", moneyContractProfit);
        return moneysContract;
    }

    @Override
    public Map<String, Double> getMoneyFuturesByOrder(FuturesOrder order) {
        Map<String, Double> moneysFutures = new HashMap<String, Double>();

        if (null == order) {
            moneysFutures.put("money_futures", 0.0);
            moneysFutures.put("money_futures_profit", 0.0);
            return moneysFutures;
        }

        Double moneyFutures = order.getVolume();
        Double moneyFuturesProfit = order.getProfit();

        moneysFutures.put("money_futures", moneyFutures);
        moneysFutures.put("money_futures_profit", moneyFuturesProfit);
        return moneysFutures;
    }

    @Override
    public void update(String userId, double amount) {
        Wallet wallet = findByUserId(userId);
        wallet.setMoney(new BigDecimal(Arith.add(wallet.getMoney().doubleValue(), amount)));
        try {
            if (!updateById(wallet)) {
                log.error("钱包操作失败，用户：{}，变更：{}，余额：{}", userId, amount, wallet.getMoney());
                throw new YamiShopBindException("余额不足");
            }
        } catch (Exception e) {
            log.error("钱包操作失败", e);
            throw new YamiShopBindException("余额不足");
        }

    }


    @Override
    public void updateExtendWithLockAndFreeze(String partyId, String walletType, double amount, double lockAmount, double freezeAmount) {
        List<WalletExtend> walletExtends = walletExtendService.findByUserIdAndWallettype(partyId, walletType);
        WalletExtend walletExtend = walletExtends.get(0);
        walletExtend.setAmount(Arith.add(walletExtend.getAmount(), amount));
        walletExtend.setLockAmount(Arith.add(walletExtend.getLockAmount(), lockAmount));
        walletExtend.setFreezeAmount(Arith.add(walletExtend.getFreezeAmount(), freezeAmount));
        walletExtendService.updateById(walletExtend);
    }


    @Override
    public void updateWithLockAndFreeze(String partyId, double amount, double lockAmount, double freezeAmount) {

        Wallet wallet = (Wallet) findByUserId(partyId);
        wallet.setMoney(new BigDecimal(Arith.add(wallet.getMoney().doubleValue(), amount)));
        wallet.setLockMoney(new BigDecimal(Arith.add(wallet.getLockMoney().doubleValue(), lockAmount)));
        wallet.setFreezeMoney(new BigDecimal(Arith.add(wallet.getFreezeMoney().doubleValue(), freezeAmount)));
        updateById(wallet);
    }

    @Override
    public ContractAndFutureProfit calculateContractAndFutureProfit(String partyId, String symbolType) {
        ContractAndFutureProfit contractAndFutureProfit = new ContractAndFutureProfit();
        contractAndFutureProfit.setPartyId(partyId);
        contractAndFutureProfit.setSymbolType(symbolType);
        contractAndFutureProfit.setZone(ZoneId.systemDefault());
        List<String> symbols = null;
        if (StringUtils.isNotEmpty(symbolType)) {
            symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        }
//        if (null == symbols || symbols.size() <= 0) {
//            return contractAndFutureProfit;
//        }

        double closedOrderProfit = 0;
        double closedOrderProfitToday = 0;
        double holderOrderProfit = 0;
        double holderOrderProfitToday = 0;
        double closeFutureOrderProfit = 0;
        double closeFutureOrderProfitToday = 0;

        QueryWrapper<ContractOrder> contractOrderQueryWrapper = new QueryWrapper<>();
        contractOrderQueryWrapper.eq(StrUtil.isNotBlank(partyId), "party_id", partyId);
        contractOrderQueryWrapper.in(StringUtils.isNotEmpty(symbolType), "symbol", symbols);
        List<ContractOrder> contractOrderList = ApplicationUtil.getBean(ContractOrderService.class).list(contractOrderQueryWrapper);
        for (ContractOrder contractOrder : contractOrderList) {
            ApplicationUtil.getBean(ContractOrderService.class).wrapProfit(contractOrder);
            BigDecimal profit = contractOrder.getProfit();
            double profitDouble = 0;
            if (profit != null) {
                profitDouble = profit.doubleValue();
            }

            List<Realtime> list = this.dataService.realtime(contractOrder.getSymbol());
            BigDecimal close = null;
            Realtime realtime = null;
            if (list.size() != 0) {
                realtime = list.get(0);
                close = BigDecimal.valueOf(realtime.getClose());
            }
            // 持仓单
            if (ContractOrder.STATE_SUBMITTED.equalsIgnoreCase(contractOrder.getState())) {
                // 累加持仓利润
                holderOrderProfit += profitDouble;
                // 如果是今日持仓单,需要计算下盈亏
                if (realtime != null && close != null && close.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal open = BigDecimal.valueOf(realtime.getOpen());
                    if (UTCDateUtils.isTimestampFromToday(contractOrder.getCreateTimeTs(), contractAndFutureProfit.getZone())) {
                        open = contractOrder.getTradeAvgPrice();
                    }
                    // 永续利润计算方式
                    BigDecimal point = close.subtract(open).divide(contractOrder.getPips(), 10, RoundingMode.HALF_UP);
                    BigDecimal amount = contractOrder.getPipsAmount().multiply(point).multiply(contractOrder.getVolume());
                    if (contractOrder.getDirection().equalsIgnoreCase(ContractOrder.DIRECTION_BUY)) {
                        holderOrderProfitToday += amount.doubleValue();
                    } else {
                        holderOrderProfitToday += amount.negate().doubleValue();
                    }
                }
            } else if (ContractOrder.STATE_CREATED.equalsIgnoreCase(contractOrder.getState())) {
                // 已经平仓
                // 累加平仓利润
                closedOrderProfit += profitDouble;
                // 如果平仓时间是今天的话，需要把加到今日盈亏
                if (contractOrder.getCloseTimeTs() != null && UTCDateUtils.isTimestampFromToday(contractOrder.getCloseTimeTs(), contractAndFutureProfit.getZone())) {
                    closedOrderProfitToday += profitDouble;
                }
            }
        }


        QueryWrapper<FuturesOrder> futureOrderQueryWrapper = new QueryWrapper<>();
        futureOrderQueryWrapper.eq(StrUtil.isNotBlank(partyId), "party_id", partyId);
        futureOrderQueryWrapper.in(StringUtils.isNotEmpty(symbolType), "symbol", symbols);
        List<FuturesOrder> futuresOrderList = ApplicationUtil.getBean(FuturesOrderService.class).list(futureOrderQueryWrapper);
        for (FuturesOrder futuresOrder : futuresOrderList) {
            if (FuturesOrder.STATE_CREATED.equalsIgnoreCase(futuresOrder.getState())) {
                closeFutureOrderProfit += futuresOrder.getProfit();
                if (futuresOrder.getCloseTime() != null && UTCDateUtils.isTimestampFromToday(futuresOrder.getCloseTime(), contractAndFutureProfit.getZone())) {
                    closedOrderProfitToday += futuresOrder.getProfit();
                }
            }

        }
        //累计收益只算结算
        contractAndFutureProfit.setClosedOrderProfit(closedOrderProfit);
        contractAndFutureProfit.setClosedOrderProfitToday(closedOrderProfitToday);
        contractAndFutureProfit.setHolderOrderProfit(holderOrderProfit);
        contractAndFutureProfit.setHolderOrderProfitToday(holderOrderProfitToday);
        contractAndFutureProfit.setCloseFutureOrderProfit(closeFutureOrderProfit);
        contractAndFutureProfit.setCloseFutureOrderProfitToday(closeFutureOrderProfitToday);
//        contractAndFutureProfit.setOrderProfit(holderOrderProfit + closedOrderProfit + closeFutureOrderProfit);
        //浮亏收益只算持仓
        contractAndFutureProfit.setOrderProfit(holderOrderProfit);
        //今日收益只算结算
        contractAndFutureProfit.setOrderProfitToday(closedOrderProfitToday + closeFutureOrderProfitToday);
        //累计盈利只算结算
        contractAndFutureProfit.setOrderProfitTotal(Arith.add(closedOrderProfit, closeFutureOrderProfit));
        return contractAndFutureProfit;
    }


    @Override
    public void updateMoney(String symbol, String userId, BigDecimal moneyRevise) {
        Wallet wallet = findByUserId(userId);
        BigDecimal amountBefore = wallet.getMoney();
        wallet.setMoney(wallet.getMoney().add(moneyRevise));
        wallet.setUpdateTime(new Date());
        updateById(wallet);

        // 账变日志
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(WalletConstants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmountBefore(amountBefore);
        moneyLog.setAmount(moneyRevise);
        moneyLog.setAmountAfter(wallet.getMoney().add(moneyRevise));
        moneyLog.setUserId(userId);
        moneyLog.setWalletType(WalletConstants.WALLET_USDT);
        moneyLog.setContentType(WalletConstants.MONEYLOG_CONTENT_RECHARGE);
        moneyLogService.save(moneyLog);
        //钱包日志
//        WalletLog walletLog = new WalletLog();
//        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
//        walletLog.setPartyId(partyId);
//        walletLog.setOrder_no("");
//        walletLog.setStatus(1);
//        walletLog.setAmount(money_revise);
//        walletLog.setWallettype("USDT");
    }

    public double getWalletExtendConvertUsdt(String partyId, List<Realtime> realtimeList, List<String> symbolList) {
        double walletExtendAll = 0;
        List<WalletExtend> walletExtends = findExtend(partyId, symbolList);
        for (WalletExtend walletExtend : walletExtends) {
            for (Realtime realtime : realtimeList) {
                String symbol = realtime.getSymbol();
                if (symbol.equalsIgnoreCase(walletExtend.getWallettype())) {
                    Item item = itemService.findBySymbol(symbol);
                    String type = item.getType();
                    double currency = Arith.mul(realtime.getClose(), walletExtend.getAmount());
                    BigDecimal usdtByType = exchangeRateService.getUsdtByType(BigDecimal.valueOf(currency), type);
                    walletExtendAll = Arith.add(walletExtendAll, usdtByType.doubleValue());
                }
            }
        }
        return walletExtendAll;
    }

    /*
     * 获取 所有订单 交割合约总资产、总未实现盈利 redis
     */
    public Map<String, Object> getMoneyFuturesRedis(Serializable partyId) {

        Double futuresAssets = (Double) RedisUtil.get(FuturesRedisKeys.FUTURES_ASSETS_PARTY_ID + partyId.toString());
        Double futuresAssetsProfit = (Double) RedisUtil.get(FuturesRedisKeys.FUTURES_ASSETS_PROFIT_PARTY_ID + partyId.toString());

        Map<String, Object> moneys_futures = new HashMap<String, Object>();
        moneys_futures.put("money_futures", null == futuresAssets ? 0.000D : futuresAssets);
        moneys_futures.put("money_futures_profit", null == futuresAssetsProfit ? 0.000D : futuresAssetsProfit);

        return moneys_futures;
    }


    /*
     * 获取 所有订单 交割合约总资产、总未实现盈利 redis
     */
    public Map<String, Double> getMoneyFutures(Serializable partyId, String symbolType) {
        List<String> symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        ;
        return getMoneyFuturesDB(partyId, symbols);
    }

    public Map<String, Double> getMoneyFuturesDB(Serializable partyId, List<String> symbolos) {

        List<FuturesOrder> list = ApplicationUtil.getBean(FuturesOrderService.class).findSubmitted(partyId.toString(), symbolos);
        // 交割合约：总资产、总未实现盈利
        Map<String, Map<String, Double>> futuresAssetsMap = new ConcurrentHashMap<String, Map<String, Double>>();

        for (FuturesOrder order : list) {
            // 获取 单个订单 交割合约总资产、总未实现盈利
            Map<String, Double> futuresAssetsOrder = getMoneyFuturesByOrder(order);

            if (futuresAssetsMap.containsKey(order.getPartyId())) {
                Map<String, Double> futuresAssetsOld = futuresAssetsMap.get(order.getPartyId().toString());
                if (null == futuresAssetsOld) {
                    futuresAssetsOld = new HashMap<String, Double>();
                    futuresAssetsOld.put("money_futures", 0.000D);
                    futuresAssetsOld.put("money_futures_profit", 0.000D);
                }
                futuresAssetsOld.put("money_futures", Arith.add(futuresAssetsOld.get("money_futures"), futuresAssetsOrder.get("money_futures")));
                futuresAssetsOld.put("money_futures_profit", Arith.add(futuresAssetsOld.get("money_futures_profit"), futuresAssetsOrder.get("money_futures_profit")));
                futuresAssetsMap.put(order.getPartyId().toString(), futuresAssetsOld);
            } else {
                futuresAssetsMap.put(order.getPartyId().toString(), futuresAssetsOrder);
            }
        }

        return futuresAssetsMap.get(partyId);
    }

    /*
     * 获取 所有订单 交割合约总资产、总未实现盈利
     */
    public Map<String, Object> getMoneyFutures(Serializable partyId) {
        double money_futures = 0;
        double money_futures_profit = 0;
        FuturesOrderService futuresOrderService = ApplicationUtil.getBean(FuturesOrderService.class);
        List<FuturesOrder> futuresOrders = futuresOrderService.cacheSubmitted();
        if (futuresOrders != null) {

            for (FuturesOrder order : futuresOrders) {
                if (partyId.equals(order.getPartyId().toString())) {
                    money_futures = Arith.add(order.getVolume(), money_futures);
                    money_futures_profit = Arith.add(order.getProfit(), money_futures_profit);
                }
            }
        }

        Map<String, Object> moneys_futures = new HashMap<String, Object>();
        moneys_futures.put("money_futures", money_futures);
        moneys_futures.put("money_futures_profit", money_futures_profit);

        return moneys_futures;
    }


    public double getMoneyexchangeApplyOrders(Serializable partyId, List<Realtime> realtimeall) {
        double moneyExchange = 0;
        ExchangeApplyOrderService exchangeApplyOrderService = ApplicationUtil.getBean(ExchangeApplyOrderService.class);

        List<ExchangeApplyOrder> exchangeApplyOrders = exchangeApplyOrderService.findSubmitted();

        if (exchangeApplyOrders != null) {

            for (ExchangeApplyOrder order : exchangeApplyOrders) {

                if (partyId.equals(order.getPartyId())) {

                    if ("open".equals(order.getOffset())) {
                        moneyExchange = Arith.add(moneyExchange, order.getVolume());
                    }

                    if ("close".equals(order.getOffset())) {
                        double realtimeClose = 0D;
                        if (realtimeall.size() <= 0) {
                            List<Realtime> realtime_list = this.dataService.realtime(order.getSymbol());
                            if (realtime_list.size() > 0) {
                                realtimeClose = realtime_list.get(0).getClose();
                            } else {
                                throw new BusinessException("系统错误，请稍后重试 getMoneyexchangeApplyOrders");
                            }
                        } else {
                            for (Realtime real : realtimeall) {
                                if (real.getSymbol().equalsIgnoreCase(order.getSymbol())) {
                                    realtimeClose = real.getClose();
                                    break;
                                }
                            }
                        }
                        moneyExchange = Arith.add(moneyExchange, Arith.mul(order.getVolume(), realtimeClose));
                    }
                }
            }
        }
        return moneyExchange;
    }

    public double getMoneyFinance(Serializable partyId, List<Realtime> realtimeall) {
        double money_finance = 0;
        List<FinanceOrder> financeOrders = financeOrderService.findByState(partyId.toString(), "1");
        String finance_profit_symbol = sysparaService.find("finance_profit_symbol").getSvalue();
        if (financeOrders != null) {
            double realtimeClose = 0D;
            if (!"".equals(finance_profit_symbol) && finance_profit_symbol != null && finance_profit_symbol != "usdt") {
                if (realtimeall.size() <= 0) {
                    List<Realtime> realtime_list = this.dataService.realtime(finance_profit_symbol);
                    if (realtime_list.size() > 0) {
                        realtimeClose = realtime_list.get(0).getClose();
                    } else {
                        throw new BusinessException("系统错误，请稍后重试");
                    }
                } else {
                    for (Realtime real : realtimeall) {
                        if (real.getSymbol().equals(finance_profit_symbol)) {
                            realtimeClose = real.getClose();
                            break;
                        }
                    }
                }
            }
            for (FinanceOrder order : financeOrders) {
                double amount = 0;
                if (!"".equals(finance_profit_symbol) && finance_profit_symbol != null
                        && finance_profit_symbol != "usdt") {
                    amount = Arith.mul(order.getAmount(), realtimeClose);
                } else {
                    amount = order.getAmount();
                }
                money_finance = Arith.add(amount, money_finance);
            }
        }
        return money_finance;
    }

    /*
     * 获取 所有订单 矿机总资产
     */
    public double getMoneyMiner(Serializable partyId, List<Realtime> realtimeall) {
        double money_miner = 0;
        List<Map<String, Object>> minerOrders = minerOrderService.findByState(partyId.toString(), "1");
        if (minerOrders != null) {
            for (Map<String, Object> map1 : minerOrders) {
                MinerOrder order = JSON.parseObject(JSON.toJSONString(map1), MinerOrder.class);
                double amount = Arith.add(order.getAmount(), 0);
                money_miner = Arith.add(amount, money_miner);
            }
            String minerBuySymbol = sysparaService.find("miner_buy_symbol").getSvalue();
            if (!StringUtils.isEmptyString(minerBuySymbol)) {
                double realtimeClose = 0D;
                if (realtimeall.size() <= 0) {
                    List<Realtime> realtime_list = this.dataService.realtime(minerBuySymbol);
                    if (realtime_list.size() > 0) {
                        realtimeClose = realtime_list.get(0).getClose();
                    } else {
                        throw new BusinessException("系统错误，请稍后重试");
                    }
                } else {
                    for (Realtime real : realtimeall) {
                        if (real.getSymbol().equals(minerBuySymbol)) {
                            realtimeClose = real.getClose();
                            break;
                        }
                    }
                }
                money_miner = Arith.mul(money_miner, realtimeClose);
            }
        }
        return money_miner;
    }
}
