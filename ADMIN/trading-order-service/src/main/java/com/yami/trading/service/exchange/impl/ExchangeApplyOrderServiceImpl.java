package com.yami.trading.service.exchange.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.exchange.dto.ExchangeApplyOrderDto;
import com.yami.trading.bean.exchange.dto.ExchangeSymbolDto;
import com.yami.trading.bean.exchange.dto.TodayTransactionDto;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.bean.purchasing.dto.ExchangeRecord;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.UTCDateUtils;
import com.yami.trading.dao.exchange.ExchangeApplyOrderMapper;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeApplyOrderService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.rate.ExchangeRateService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExchangeApplyOrderServiceImpl extends ServiceImpl<ExchangeApplyOrderMapper, ExchangeApplyOrder> implements ExchangeApplyOrderService {
    @Autowired
    SysparaService sysparaService;
    @Autowired
    ItemService itemService;
    @Autowired
    DataService dataService;
    @Autowired
    WalletService walletService;
    @Autowired
    MoneyLogService moneyLogService;
    @Autowired
    UserDataService userDataService;
    @Resource
    ExchangeApplyOrderMapper exchangeApplyOrderMapper;
    @Autowired
    private ExchangeRateService exchangeRateService;

    @Override
    public List<ExchangeApplyOrder> findSubmitted() {
        return list(Wrappers.<ExchangeApplyOrder>query().lambda().eq(ExchangeApplyOrder::getState, ExchangeApplyOrder.STATE_SUBMITTED));
    }

    @Override
    @Transactional
    public void saveSpotTradOpenCreated(ExchangeApplyOrder order, Double orderPrice) {
        // 针对限价部分成交做特殊处理
        double realValue = order.getSymbolValue();
        if (order.getSuccessVolume() != 0) {
            realValue = order.getSuccessVolume();
            // 退回部分下单金额
            double returnValue = order.getSymbolValue() - order.getSuccessVolume();
            Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
            double amountBefore = wallet.getMoney().doubleValue();
            BigDecimal returnAmount = BigDecimal.valueOf(returnValue).multiply(BigDecimal.valueOf(order.getPrice()))
                    .setScale(2, RoundingMode.HALF_DOWN);

            String type = itemService.findBySymbol(order.getSymbol()).getType();
            returnAmount = exchangeRateService.getUsdtByType(returnAmount, type);
            walletService.update(wallet.getUserId(), returnAmount.doubleValue());
            MoneyLog log = new MoneyLog();
            log.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
            String name = Constants.MONEYLOG_MAP.get(type);
            String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
            log.setAmountBefore(new BigDecimal(amountBefore));
            log.setAmount(returnAmount);
            log.setAmountAfter(BigDecimal.valueOf(amountBefore).add(returnAmount));
            log.setLog(name + type2 + "买入订单限价成交，回退金额。订单号[" + order.getOrderNo() + "]");
            log.setUserId(order.getPartyId());
            log.setSymbol(order.getSymbol());
            log.setWalletType(order.getSymbol());
            log.setContentType(type2 + Constants.MONEYLOG_CONTENT_CANCEL);
            moneyLogService.save(log);
        }
        WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());
        double amountBeforeExtend = walletExtend.getAmount();
        this.walletService.updateExtend(walletExtend.getPartyId(), walletExtend.getWallettype(), realValue);
        String type = itemService.findBySymbol(order.getSymbol()).getType();
        MoneyLog log = new MoneyLog();
        log.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
        String name = Constants.MONEYLOG_MAP.get(type);
        String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
        log.setAmountBefore(new BigDecimal(amountBeforeExtend));
        log.setAmount(BigDecimal.valueOf(realValue));
        log.setAmountAfter(BigDecimal.valueOf(amountBeforeExtend + realValue));
        log.setLog(name + type2 + "现货交易买入委托单成交，订单号[" + order.getOrderNo() + "]");
        log.setUserId(order.getPartyId());
        log.setSymbol(order.getSymbol());
        log.setWalletType(order.getSymbol());
        log.setContentType(type2 + Constants.MONEYLOG_CONTENT_OPEN);
        // 记录账变日志
        moneyLogService.save(log);
        order.setCloseTime(new Date());
        // 竞价单的要求按照前端给的价格成交
        order.setClosePrice(order.getOrderNo().length() == 21 ? order.getPrice() : orderPrice);
        order.setState(ExchangeApplyOrder.STATE_CREATED);
        order.setAmount(order.getVolume());
        order.setWalletFee(order.getFee());
        // 更新订单信息
        updateById(order);
    }

    @Override
    @Transactional
    public void saveSpotTradCloseCreated(ExchangeApplyOrder order, Double orderPrice) {
        // 针对限价部分成交做特殊处理
        double realValue = order.getVolume();
        if (order.getSuccessVolume() != 0) {
            realValue = order.getSuccessVolume();
            // 退回部分下单金额
            double returnValue = order.getSymbolValue() - order.getSuccessVolume();
            WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());
            double amountBeforeExtend = walletExtend.getAmount();
            walletService.updateExtend(walletExtend.getPartyId(), walletExtend.getWallettype(), returnValue);
            String type = itemService.findBySymbol(order.getSymbol()).getType();
            MoneyLog log = new MoneyLog();
            log.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
            String name = Constants.MONEYLOG_MAP.get(type);
            String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
            log.setAmountBefore(new BigDecimal(amountBeforeExtend));
            log.setAmount(BigDecimal.valueOf(returnValue));
            log.setAmountAfter(BigDecimal.valueOf(amountBeforeExtend + returnValue));
            log.setLog(name + type2 + "卖出订单限价成交，回退数量。订单号[" + order.getOrderNo() + "]");
            log.setUserId(order.getPartyId());
            log.setSymbol(order.getSymbol());
            log.setWalletType(order.getSymbol());
            log.setContentType(type2 + Constants.MONEYLOG_CONTENT_CANCEL);
            moneyLogService.save(log);
        }
        double sub = Arith.sub(realValue, order.getFee());
        double amount = Arith.mul(sub, orderPrice);
        Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
        double amountBefore = wallet.getMoney().doubleValue();
        String type = itemService.findBySymbol(order.getSymbol()).getType();
        amount = exchangeRateService.getUsdtByType(BigDecimal.valueOf(amount), type).doubleValue();
        this.walletService.update(wallet.getUserId(), amount);
        MoneyLog log = new MoneyLog();
        log.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
        String name = Constants.MONEYLOG_MAP.get(type);
        String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
        log.setAmountBefore(new BigDecimal(amountBefore));
        log.setAmount(new BigDecimal(amount));
        log.setAmountAfter(wallet.getMoney().add(BigDecimal.valueOf(amount)));
        log.setLog(name + type2 + "现货交易卖出委托单成交，订单号[" + order.getOrderNo() + "]");
        log.setUserId(order.getPartyId());
        log.setWalletType(Constants.WALLET);
        log.setContentType(type2 + Constants.MONEYLOG_CONTENT_CLOSE);
        log.setSymbol(order.getSymbol());
        moneyLogService.save(log);
        order.setCloseTime(new Date());
        order.setClosePrice(orderPrice);
        order.setWalletFee(Arith.mul(order.getFee(), orderPrice));
        order.setState(ExchangeApplyOrder.STATE_CREATED);
        updateById(order);
    }

    @Override
    public ExchangeApplyOrder findByOrderNo(String orderNo) {
        return getOne(Wrappers.<ExchangeApplyOrder>query().lambda().eq(ExchangeApplyOrder::getOrderNo, orderNo));
    }

    @Override
    public List<ExchangeSymbolDto> querySpotTradPositionList(String userId, String type) {
        List<String> symbols = itemService.findByType(type).stream().map(Item::getSymbol).collect(Collectors.toList());
        symbols.add("-1");
        LambdaQueryWrapper<ExchangeApplyOrder> lambdaQueryWrapper = Wrappers.<ExchangeApplyOrder>query().lambda();
        lambdaQueryWrapper.eq(ExchangeApplyOrder::getPartyId, userId);
        lambdaQueryWrapper.in(ExchangeApplyOrder::getSymbol, symbols);
        lambdaQueryWrapper.eq(ExchangeApplyOrder::getState, ExchangeApplyOrder.STATE_CREATED);
        lambdaQueryWrapper.orderByDesc(ExchangeApplyOrder::getCreateTime);
        List<ExchangeApplyOrder> list = list(lambdaQueryWrapper);
        return getDataList(list);
    }

    @Override
    public List<ExchangeSymbolDto> querySpotTradPositionList(String userId) {
        LambdaQueryWrapper<ExchangeApplyOrder> lambdaQueryWrapper = Wrappers.<ExchangeApplyOrder>query().lambda();
        lambdaQueryWrapper.eq(ExchangeApplyOrder::getPartyId, userId);
        lambdaQueryWrapper.eq(ExchangeApplyOrder::getState, ExchangeApplyOrder.STATE_CREATED);
        lambdaQueryWrapper.orderByDesc(ExchangeApplyOrder::getCreateTime);
        List<ExchangeApplyOrder> list = list(lambdaQueryWrapper);
        return getSettleList(list);
    }

    public List<ExchangeSymbolDto> getSettleList(List<ExchangeApplyOrder> dbList) {
        List<ExchangeSymbolDto> result = new ArrayList<>();
        Map<String, List<ExchangeSymbolDto>> map = new HashMap<>();
        for (ExchangeApplyOrder order : dbList) {
            List<ExchangeSymbolDto> exchangeSymbolDtos = map.get(order.getSymbol());
            if (exchangeSymbolDtos == null) {
                exchangeSymbolDtos = new ArrayList<>();
            }
//            List<Realtime> symbolList = dataService.realtime(order.getSymbol());
//            if (!CollectionUtil.isEmpty(symbolList)) {
//                Realtime realtime = symbolList.get(0);
            ExchangeSymbolDto exchangeSymbolDto = new ExchangeSymbolDto();
            exchangeSymbolDto.setVolume(order.getSuccessVolume() != 0 ? order.getSuccessVolume() : order.getSymbolValue());
//                exchangeSymbolDto.setName(realtime.getName());
            exchangeSymbolDto.setOpenPrice(order.getPrice());
//                exchangeSymbolDto.setCurrentPrice(realtime.getClose());
            exchangeSymbolDto.setPrice(order.getClosePrice());
            exchangeSymbolDto.setOffset(order.getOffset());
            exchangeSymbolDto.setState(order.getState());
            exchangeSymbolDto.setCloseTimeTs(order.getCreateTimeTs());
            exchangeSymbolDtos.add(exchangeSymbolDto);
            map.put(order.getSymbol(), exchangeSymbolDtos);
//            }
        }
        for (String key : map.keySet()) {
            List<ExchangeSymbolDto> list = map.get(key);
            Item item = itemService.findBySymbol(key);
            double volume = 0; //可用
            double costValue = 0.0;//总价值
            double marketValue = 0; //市值
            double currentPrice = 0; //当前价格
            double profitLoss = 0; //浮盈
            double toDayProfitLoss = 0; //今日盈亏(浮动)
            double todayProfit = 0; //今日盈亏(结算)
            double profitTotal = 0; //累计盈亏
            double openPrice = 0;//开仓价格
            String name = "";
            BigDecimal price = BigDecimal.ZERO;//成本价
            List<ExchangeSymbolDto> sort = list.stream().sorted(Comparator.comparing(ExchangeSymbolDto::getCloseTimeTs)).collect(Collectors.toList());
            for (ExchangeSymbolDto dto : sort) {
                name = dto.getName();
                currentPrice = dto.getCurrentPrice();
                openPrice = dto.getPrice();
                //成本价值
                double cost = dto.getVolume() * dto.getPrice();

                if (dto.getOffset().equals(ExchangeApplyOrder.OFFSET_CLOSE)) {
                    volume -= dto.getVolume();

                    if (ExchangeApplyOrder.STATE_CREATED.equalsIgnoreCase(dto.getState())) {
                        profitTotal += dto.getVolume() * (dto.getPrice() - price.doubleValue());
                        if (dto.getCloseTimeTs() != null && UTCDateUtils.isTimestampFromToday(dto.getCloseTimeTs(), ZoneId.systemDefault())) {
                            todayProfit += dto.getVolume() * (dto.getPrice() - price.doubleValue());
                        }
                        costValue -= (dto.getVolume() * dto.getPrice());
                        marketValue -= (dto.getCurrentPrice() * dto.getVolume());
                    }
                    if (volume == 0) {
                        price = BigDecimal.ZERO;
                    }
                } else {
                    volume += dto.getVolume();
                    if (volume != 0) {
                        price = price.multiply(BigDecimal.valueOf(volume - dto.getVolume()));
                        price = price.add(BigDecimal.valueOf(cost));
                        price = price.divide(BigDecimal.valueOf(volume), item.getDecimals(), RoundingMode.FLOOR);
                    } else {
                        price = BigDecimal.ZERO;
                    }
                    costValue += (dto.getVolume() * dto.getPrice());
                    marketValue += (dto.getCurrentPrice() * dto.getVolume());
                    //当日浮动盈亏
                    List<Kline> kline = dataService.kline(key, Kline.PERIOD_1DAY);
                    kline = kline.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                    double diff = 0.0;
                    if (CollectionUtil.isNotEmpty(kline)) {
                        Kline day = kline.get(0);
                        Long ts = day.getTs() / 1000;
                        Long timeTs = dto.getCloseTimeTs();
                        if (timeTs > ts) {
                            diff = currentPrice - openPrice;
                        } else {
                            diff = currentPrice - day.getClose();
                        }
                    }
                    toDayProfitLoss += diff * dto.getVolume();
                }
            }
            //总浮动盈亏
            profitLoss = marketValue - costValue;
            ExchangeSymbolDto exchangeSymbolDto = new ExchangeSymbolDto();
            exchangeSymbolDto.setVolume(new BigDecimal(volume).setScale(4, RoundingMode.HALF_UP).doubleValue());
            exchangeSymbolDto.setPositionVolume(new BigDecimal(volume).setScale(4, RoundingMode.HALF_UP).doubleValue());
            exchangeSymbolDto.setPrice(price.setScale(4, RoundingMode.HALF_UP).doubleValue());
            exchangeSymbolDto.setName(name);
            exchangeSymbolDto.setSymbol(key);
            exchangeSymbolDto.setToDayProfitLoss(new BigDecimal(toDayProfitLoss).setScale(4, RoundingMode.HALF_UP).doubleValue());
            exchangeSymbolDto.setToDayProfitLossPercentage(calculateProfitPercentage(openPrice, currentPrice));
            exchangeSymbolDto.setCurrentPrice(new BigDecimal(currentPrice).setScale(4, RoundingMode.HALF_UP).doubleValue());
            exchangeSymbolDto.setOpenPrice(new BigDecimal(openPrice).setScale(4, RoundingMode.HALF_UP).doubleValue());
            exchangeSymbolDto.setMarketValue(new BigDecimal(marketValue).setScale(4, RoundingMode.HALF_UP).doubleValue());
            exchangeSymbolDto.setProfitLoss(new BigDecimal(profitLoss).setScale(4, RoundingMode.HALF_UP).doubleValue());
            exchangeSymbolDto.setProfitLossPercentage(calculateProfitPercentage(price.doubleValue(), currentPrice));
            exchangeSymbolDto.setProfitTotal(new BigDecimal(profitTotal).setScale(4, RoundingMode.HALF_UP).doubleValue());
            exchangeSymbolDto.setTodayProfit(todayProfit);
            result.add(exchangeSymbolDto);
        }
        return result;
    }

    public List<ExchangeSymbolDto> getDataList(List<ExchangeApplyOrder> dbList) {
        List<ExchangeSymbolDto> result = new ArrayList<>();
        Map<String, List<ExchangeSymbolDto>> map = new HashMap<>();
        for (ExchangeApplyOrder order : dbList) {
            List<ExchangeSymbolDto> exchangeSymbolDtos = map.get(order.getSymbol());
            if (exchangeSymbolDtos == null) {
                exchangeSymbolDtos = new ArrayList<>();
            }
            List<Realtime> symbolList = dataService.realtime(order.getSymbol());
            if (!CollectionUtil.isEmpty(symbolList)) {
                Realtime realtime = symbolList.get(0);
                ExchangeSymbolDto exchangeSymbolDto = new ExchangeSymbolDto();
                exchangeSymbolDto.setVolume(order.getSuccessVolume() != 0 ? order.getSuccessVolume() : order.getSymbolValue());
                exchangeSymbolDto.setName(realtime.getName());
                exchangeSymbolDto.setOpenPrice(order.getPrice());
                exchangeSymbolDto.setCurrentPrice(realtime.getClose());
                exchangeSymbolDto.setPrice(order.getClosePrice());
                exchangeSymbolDto.setOffset(order.getOffset());
                exchangeSymbolDto.setState(order.getState());
                exchangeSymbolDto.setCloseTimeTs(order.getCreateTimeTs());
                exchangeSymbolDtos.add(exchangeSymbolDto);
                map.put(order.getSymbol(), exchangeSymbolDtos);
            }
        }
        for (String key : map.keySet()) {
            List<ExchangeSymbolDto> list = map.get(key);
            Item item = itemService.findBySymbol(key);
            double volume = 0; //可用
            double costValue = 0.0;//总价值
            double marketValue = 0; //市值
            double currentPrice = 0; //当前价格
            double profitLoss = 0; //浮盈
            double toDayProfitLoss = 0; //今日盈亏(浮动)
            double todayProfit = 0; //今日盈亏(结算)
            double profitTotal = 0; //累计盈亏
            double openPrice = 0;//开仓价格
            String name = "";
            BigDecimal price = BigDecimal.ZERO;//成本价
            List<ExchangeSymbolDto> sort = list.stream().sorted(Comparator.comparing(ExchangeSymbolDto::getCloseTimeTs)).collect(Collectors.toList());
            for (ExchangeSymbolDto dto : sort) {
                name = dto.getName();
                currentPrice = dto.getCurrentPrice();
                openPrice = dto.getPrice();
                //成本价值
                double cost = dto.getVolume() * dto.getPrice();

                if (dto.getOffset().equals(ExchangeApplyOrder.OFFSET_CLOSE)) {
                    volume -= dto.getVolume();

                    if (ExchangeApplyOrder.STATE_CREATED.equalsIgnoreCase(dto.getState())) {
                        profitTotal += dto.getVolume() * (dto.getPrice() - price.doubleValue());
                        if (dto.getCloseTimeTs() != null && UTCDateUtils.isTimestampFromToday(dto.getCloseTimeTs(), ZoneId.systemDefault())) {
                            todayProfit += dto.getVolume() * (dto.getPrice() - price.doubleValue());
                        }
                        costValue -= (dto.getVolume() * dto.getPrice());
                        marketValue -= (dto.getCurrentPrice() * dto.getVolume());
                    }
                    if (volume == 0) {
                        price = BigDecimal.ZERO;
                    }
                } else {
                    volume += dto.getVolume();
                    if (volume != 0) {
                        price = price.multiply(BigDecimal.valueOf(volume - dto.getVolume()));
                        price = price.add(BigDecimal.valueOf(cost));
                        price = price.divide(BigDecimal.valueOf(volume), item.getDecimals(), RoundingMode.FLOOR);
                    } else {
                        price = BigDecimal.ZERO;
                    }
                    costValue += (dto.getVolume() * dto.getPrice());
                    marketValue += (dto.getCurrentPrice() * dto.getVolume());
                    //当日浮动盈亏
                    List<Kline> kline = dataService.kline(key, Kline.PERIOD_1DAY);
                    kline = kline.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                    double diff = 0.0;
                    if (CollectionUtil.isNotEmpty(kline)) {
                        Kline day = kline.get(0);
                        Long ts = day.getTs() / 1000;
                        Long timeTs = dto.getCloseTimeTs();
                        if (timeTs > ts) {
                            diff = currentPrice - openPrice;
                        } else {
                            diff = currentPrice - day.getClose();
                        }
                    }
                    toDayProfitLoss += diff * dto.getVolume();
                }
            }
            if (volume > 0) {
                //总浮动盈亏
                profitLoss = marketValue - costValue;
                ExchangeSymbolDto exchangeSymbolDto = new ExchangeSymbolDto();
                exchangeSymbolDto.setVolume(new BigDecimal(volume).setScale(4, RoundingMode.HALF_UP).doubleValue());
                exchangeSymbolDto.setPositionVolume(new BigDecimal(volume).setScale(4, RoundingMode.HALF_UP).doubleValue());
                exchangeSymbolDto.setPrice(price.setScale(4, RoundingMode.HALF_UP).doubleValue());
                exchangeSymbolDto.setName(name);
                exchangeSymbolDto.setSymbol(key);
                exchangeSymbolDto.setToDayProfitLoss(new BigDecimal(toDayProfitLoss).setScale(4, RoundingMode.HALF_UP).doubleValue());
                exchangeSymbolDto.setToDayProfitLossPercentage(calculateProfitPercentage(openPrice, currentPrice));
                exchangeSymbolDto.setCurrentPrice(new BigDecimal(currentPrice).setScale(4, RoundingMode.HALF_UP).doubleValue());
                exchangeSymbolDto.setOpenPrice(new BigDecimal(openPrice).setScale(4, RoundingMode.HALF_UP).doubleValue());
                exchangeSymbolDto.setMarketValue(new BigDecimal(marketValue).setScale(4, RoundingMode.HALF_UP).doubleValue());
                exchangeSymbolDto.setProfitLoss(new BigDecimal(profitLoss).setScale(4, RoundingMode.HALF_UP).doubleValue());
                exchangeSymbolDto.setProfitLossPercentage(calculateProfitPercentage(price.doubleValue(), currentPrice));
                exchangeSymbolDto.setProfitTotal(new BigDecimal(profitTotal).setScale(4, RoundingMode.HALF_UP).doubleValue());
                exchangeSymbolDto.setTodayProfit(todayProfit);
                result.add(exchangeSymbolDto);
            }
        }
        return result;
    }

    public static double calculateProfitPercentage(double buyPrice, double currentPrice) {
        if (buyPrice == 0D) {
            log.error("计算收益率数据异常{}", buyPrice);
            return 0D;
        }
        double profit = currentPrice - buyPrice;
        double profitPercentage = (profit / buyPrice) * 100;
        return new BigDecimal(profitPercentage).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public void saveCancel(String partyId, String orderNo) {
        ExchangeApplyOrder order = findByOrderNo(orderNo);
        if (order == null || !"submitted".equals(order.getState()) || !partyId.equals(order.getPartyId())) {
            return;
        }
        String symbol = order.getSymbol();
        Item item = itemService.findBySymbol(symbol);
        String itemType = item.getType();
        if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
            Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
            double amountBefore = wallet.getMoney().doubleValue();
            double realAmount = order.getVolume() + order.getFee();
            realAmount = exchangeRateService.getUsdtByType(BigDecimal.valueOf(realAmount), itemType).doubleValue();
            walletService.update(wallet.getUserId(), realAmount);
            String type = itemService.findBySymbol(order.getSymbol()).getType();
            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
            String name = Constants.MONEYLOG_MAP.get(type);
            String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
            moneylog.setAmountBefore(new BigDecimal(amountBefore));
            moneylog.setAmount(BigDecimal.valueOf(realAmount));
            moneylog.setAmountAfter(wallet.getMoney());
            moneylog.setLog(name + type2 + "现货交易买入撤单，订单号[" + order.getOrderNo() + "]");
            moneylog.setUserId(order.getPartyId());
            moneylog.setWalletType(Constants.WALLET);
            moneylog.setContentType(type2 + Constants.MONEYLOG_CONTENT_CANCEL);
            moneylog.setSymbol(order.getSymbol());
            moneylog.setCreateTime(new Date());
            moneylog.setUpdateTime(new Date());
            moneyLogService.save(moneylog);
        } else if (ExchangeApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {
            WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());
            double amountBefore = walletExtend.getAmount();
            walletService.updateExtend(walletExtend.getPartyId(), walletExtend.getWallettype(), order.getVolume());
            String type = itemService.findBySymbol(order.getSymbol()).getType();
            MoneyLog moneylog = new MoneyLog();
            moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
            String name = Constants.MONEYLOG_MAP.get(type);
            String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
            moneylog.setAmountBefore(new BigDecimal(amountBefore));
            moneylog.setAmount(BigDecimal.valueOf(order.getVolume()));
            moneylog.setAmountAfter(BigDecimal.valueOf(Arith.add(walletExtend.getAmount(), order.getVolume())));
            moneylog.setLog(name + type2 + "现货交易卖出撤单，订单号[" + order.getOrderNo() + "]");
            moneylog.setUserId(order.getPartyId());
            moneylog.setWalletType(order.getSymbol());
            moneylog.setSymbol(order.getSymbol());
            moneylog.setContentType(type2 + Constants.MONEYLOG_CONTENT_CANCEL);
            moneylog.setCreateTime(new Date());
            moneylog.setUpdateTime(new Date());
            moneyLogService.save(moneylog);
        }
        order.setState(ExchangeApplyOrder.STATE_CANCELED);
        updateById(order);
    }

    @Override
    public Page<ExchangeApplyOrderDto> listPage(Page page, String rolename, String userName, String orderNo, String state,
                                                String offset, String symbolType, String userCode, String symbol, List<String> userIds) {
        return baseMapper.listPage(page, rolename, userName, orderNo, state, offset, symbolType, userCode, symbol, userIds);
    }

    @Override
    public ExchangeApplyOrder findByOrderNoAndPartyId(String order_no, String userId) {
        return getOne(Wrappers.<ExchangeApplyOrder>query().lambda().eq(ExchangeApplyOrder::getOrderNo, order_no).eq(ExchangeApplyOrder::getPartyId, userId));
    }

    @Override
    public List<TodayTransactionDto> getTodayTransaction(String symbol, String userId, Date startDate,
                                                         Date endDate) {
        Date startTime = DateUtil.minDate(startDate);
        Date endTime = DateUtil.maxDate(endDate);
        List<String> symbols = new ArrayList<>();
//        if (StringUtils.isNotEmpty(symbolType)) {
//            symbols.add("-1");
        // }
        symbols.add(symbol);
        return baseMapper.getTodayTransaction(userId, ExchangeApplyOrder.STATE_CREATED, startTime, endTime, symbols);
    }

    @Override
    public List<Map<String, Object>> getPaged(int pageNo, int size, String userId, String symbol, String type, String isAll, String startTime, String endTime, String symbolType,
                                              String orderPriceType) {
        LambdaQueryWrapper<ExchangeApplyOrder> lambdaQueryWrapper = Wrappers.<ExchangeApplyOrder>query().lambda();
        lambdaQueryWrapper.eq(ExchangeApplyOrder::getPartyId, userId);
        if (!StringUtils.isNullOrEmpty(symbol)) {
            lambdaQueryWrapper.eq(ExchangeApplyOrder::getSymbol, symbol);
        }
        if (StringUtils.isNotEmpty(orderPriceType)) {
            lambdaQueryWrapper.eq(ExchangeApplyOrder::getOrderPriceType, orderPriceType);
        }
        if (null != isAll) {
            List<String> items = itemService.getAllSymbol();
            lambdaQueryWrapper.in(ExchangeApplyOrder::getSymbol, items);
        }
        if ("orders".equals(type)) {
            lambdaQueryWrapper.eq(ExchangeApplyOrder::getState, ExchangeApplyOrder.STATE_SUBMITTED);
        } else if ("hisorders".equals(type)) {
            lambdaQueryWrapper.in(ExchangeApplyOrder::getState, ExchangeApplyOrder.STATE_CREATED, ExchangeApplyOrder.STATE_CANCELED);
        } else if ("opponent".equals(type)) {
            lambdaQueryWrapper.eq(ExchangeApplyOrder::getState, ExchangeApplyOrder.STATE_CREATED);
        } else if ("canceled".equals(type)) {
            lambdaQueryWrapper.eq(ExchangeApplyOrder::getState, ExchangeApplyOrder.STATE_CANCELED);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            lambdaQueryWrapper.ge(ExchangeApplyOrder::getCreateTime, DateUtil.minDate(DateUtil.stringToDate(startTime, "yyyy-MM-dd")));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            lambdaQueryWrapper.le(ExchangeApplyOrder::getCreateTime, DateUtil.maxDate(DateUtil.stringToDate(endTime, "yyyy-MM-dd")));
        }
        List<String> symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        symbols.add("-1");
        if (StringUtils.isNotEmpty(symbolType)) {
            lambdaQueryWrapper.in(ExchangeApplyOrder::getSymbol, symbols);
        }
        lambdaQueryWrapper.orderByDesc(ExchangeApplyOrder::getCreateTime);
        Page page = new Page(pageNo, size);
        page(page, lambdaQueryWrapper);
        return this.entrustBulidData(page.getRecords());
    }

    private List<Map<String, Object>> entrustBulidData(List<ExchangeApplyOrder> list) {
        List<Map<String, Object>> data = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            ExchangeApplyOrder order = list.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            String symbol = itemService.findBySymbol(order.getSymbol()).getName();
            map.put("profitLoss", 0);
            List<Realtime> symbolList = dataService.realtime(symbol);
            if (!CollectionUtil.isEmpty(symbolList)) {
                Realtime realtime = symbolList.get(0);
                double oldPrie = order.getClosePrice().doubleValue() * order.getVolume().doubleValue();
                double newPrie = realtime.getClose() * order.getVolume().doubleValue();
                map.put("profitLoss", oldPrie - newPrie); //盈亏
            }
            map.put("order_no", order.getOrderNo());
            map.put("name", symbol);
            map.put("symbol", order.getSymbol());
            Item bySymbol = itemService.findBySymbol(symbol);
            if (bySymbol != null) {
                map.put("symbol_data", bySymbol.getSymbolData());
            }
            map.put("create_time", order.getCreateTime());
            // 买的时候total 为volume，卖的时候total = volume*closeTime
            if (ExchangeApplyOrder.OFFSET_OPEN.equalsIgnoreCase(order.getOffset())) {
                double realVolume = order.getSuccessVolume() != 0 ? order.getSuccessVolume() : order.getSymbolValue();
                map.put("total", BigDecimal.valueOf(realVolume * order.getClosePrice()).setScale(2, RoundingMode.HALF_UP));
                map.put("volume", realVolume);
            } else {
                double realVolume = order.getSuccessVolume() != 0 ? order.getSuccessVolume() : order.getVolume();
                map.put("total", BigDecimal.valueOf(realVolume * order.getClosePrice()).setScale(2, RoundingMode.HALF_UP));
                map.put("volume", realVolume);
            }
            map.put("offset", order.getOffset());
            map.put("price", order.getPrice());
            map.put("order_price_type", order.getOrderPriceType());
            map.put("state", order.getState());
            map.put("closePrice", order.getClosePrice());
            map.put("fee", order.getFee());
            long showCreateTimeTs = DateTimeTools.transferShowTimeToClientTime(order.getCreateTimeTs());
            map.put("create_time_ts", showCreateTimeTs);
            long showUpdateTimeTs = DateTimeTools.transferShowTimeToClientTime(order.getUpdateTimeTs());
            map.put("update_time_ts", showUpdateTimeTs);
            data.add(map);
        }
        return data;
    }

    /**
     * 现货交易买入
     */
    @Override
    public void saveSpotTradOpen(ExchangeApplyOrder order) {
        order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        double fee = Arith.mul(order.getVolume(), sysparaService.find("exchange_apply_order_buy_fee").getDouble());
        Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
        String symbol = order.getSymbol();
        Item item = itemService.findBySymbol(symbol);
        double amountBefore = wallet.getMoney().doubleValue();
        double realAmount = order.getVolume() + fee;
        String type = item.getType();
        realAmount = exchangeRateService.getUsdtByType(BigDecimal.valueOf(realAmount), type).doubleValue();
        if (wallet.getMoney().doubleValue() < realAmount) {
            log.error("现货交易买入余额不足，钱包余额：{},订单额度：{},手续费{}", wallet.getMoney(), order.getVolume(), fee);
            throw new YamiShopBindException("余额不足");
        }
        // 休市期间，下市价订单, 叫 "竞价单",竞价单订单号长度为21,其他订单号长度为20位
        if (ExchangeApplyOrder.ORDER_PRICE_TYPE_OPPONENT.equals(order.getOrderPriceType())) {
            boolean isOpen = MarketOpenChecker.isMarketOpenByItemCloseType(item.getOpenCloseType());
            if (!isOpen) {
                order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(9));
            }
        }
        this.walletService.update(wallet.getUserId(), Arith.sub(0, realAmount));
        MoneyLog log = new MoneyLog();
        log.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
        String name = Constants.MONEYLOG_MAP.get(type);
        String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
        log.setAmountBefore(new BigDecimal(amountBefore));
        log.setAmount(BigDecimal.valueOf(-realAmount));
        log.setAmountAfter(BigDecimal.valueOf(Arith.sub(amountBefore, realAmount)));
        log.setLog(name + type2 + "现货交易买入" + "，订单号[" + order.getOrderNo() + "]");
        log.setUserId(order.getPartyId());
        log.setWalletType(Constants.WALLET);
        log.setSymbol(symbol);
        log.setContentType(type2 + Constants.MONEYLOG_CONTENT_OPEN);
        log.setCreateTime(new Date());
        log.setUpdateTime(new Date());
        moneyLogService.save(log);
        order.setFee(fee);
        order.setCreateTime(new Date());
        save(order);
        userDataService.saveBuy(order);
    }

    /**
     * 现货交易卖出
     */
    @Override
    @Transactional
    public void saveSpotTradClose(ExchangeApplyOrder order) {
        order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        order.setCreateTime(new Date());
        order.setFee(Arith.mul(order.getVolume(), sysparaService.find("exchange_apply_order_sell_fee").getDouble()));
        Item item = itemService.findBySymbol(order.getSymbol());
        // 休市期间，不让下市价卖出单
        if (ExchangeApplyOrder.ORDER_PRICE_TYPE_OPPONENT.equals(order.getOrderPriceType())) {
            boolean isOpen = MarketOpenChecker.isMarketOpenByItemCloseType(item.getOpenCloseType());
            if (!isOpen) {
                throw new YamiShopBindException("The current stock market is closed");
            }
        }
        WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());
        double amount_before = walletExtend.getAmount();
        walletService.updateExtend(walletExtend.getPartyId(), walletExtend.getWallettype(), Arith.sub(0, order.getVolume()));
        String type = item.getType();
        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
        String name = Constants.MONEYLOG_MAP.get(type);
        String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
        moneylog.setAmountBefore(new BigDecimal(amount_before));
        moneylog.setAmount(BigDecimal.valueOf(-order.getVolume()));
        moneylog.setAmountAfter(BigDecimal.valueOf(Arith.sub(amount_before, order.getVolume())));
        moneylog.setLog(name + type2 + "现货交易卖出，订单号[" + order.getOrderNo() + "]");
        moneylog.setUserId(order.getPartyId());
        moneylog.setWalletType(order.getSymbol());
        moneylog.setSymbol(order.getSymbol());
        moneylog.setContentType(type2 + Constants.MONEYLOG_CONTENT_CLOSE);
        moneylog.setCreateTime(new Date());
        moneylog.setUpdateTime(new Date());
        moneyLogService.save(moneylog);
        save(order);
        userDataService.saveSell(order);
    }
    //================================================闪兑==============================================================

    /**
     * 生成闪兑订单
     */
    @Override
    public void saveCreate(ExchangeApplyOrder order) {
        Realtime realtime = dataService.realtime(order.getSymbol()).get(0);
        order.setClosePrice(realtime.getClose());
        if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
            open(order);
        } else if (ExchangeApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {
            close(order);
        }
    }

    /**
     * 闪兑-买入
     */
    public void open(ExchangeApplyOrder order) {
        order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        order.setFee(Arith.mul(order.getVolume(), sysparaService.find("exchange_apply_order_buy_fee").getDouble()));
        order.setCreateTime(new Date());
        // 买入数量 - 手续费 = 到账
        double sub = Arith.sub(order.getVolume(), order.getFee());
        // 可以买的数量
        double amount = Arith.div(sub, order.getClosePrice(), 8);
        order.setSymbolValue(amount);
        Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
        double amount_before = wallet.getMoney().doubleValue();
        if (wallet.getMoney().doubleValue() < order.getVolume()) {
            throw new YamiShopBindException("余额不足");
        }
        this.walletService.update(wallet.getUserId(), Arith.sub(0, order.getVolume()));
        String type = itemService.findBySymbol(order.getSymbol()).getType();
        MoneyLog log = new MoneyLog();
        log.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
        String name = Constants.MONEYLOG_MAP.get(type);
        String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
        log.setAmountBefore(new BigDecimal(amount_before));
        log.setAmount(new BigDecimal(Arith.sub(0, order.getVolume().doubleValue())));
        log.setAmountAfter(new BigDecimal(Arith.sub(wallet.getMoney().doubleValue(), order.getVolume())));
        log.setLog(name + "闪兑买入，订单号[" + order.getOrderNo() + "]");
        log.setUserId(order.getPartyId());
        log.setWalletType(Constants.WALLET);
        log.setSymbol(order.getSymbol());
        log.setContentType(type2 + Constants.MONEYLOG_CONTENT_OPEN);
        log.setCreateTime(new Date());
        log.setUpdateTime(new Date());
        moneyLogService.save(log);
        save(order);
        userDataService.saveBuy(order);
    }

    /**
     * 闪兑-卖出
     */
    @Transactional
    public void close(ExchangeApplyOrder order) {
        order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        order.setCreateTime(new Date());
        order.setFee(Arith.mul(order.getVolume(), sysparaService.find("exchange_apply_order_sell_fee").getDouble()));
        double sub = Arith.sub(order.getVolume(), order.getFee());
        double amount = Arith.mul(sub, order.getClosePrice());
        order.setSymbolValue(amount);
        WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());
        double amount_before = walletExtend.getAmount();
        walletService.updateExtend(walletExtend.getPartyId(), walletExtend.getWallettype(), Arith.sub(0, order.getVolume()));
        Item item = itemService.findBySymbol(order.getSymbol());
        String type = item.getType();
        order.setSuccessVolume(order.getVolume());
        MoneyLog log = new MoneyLog();
        log.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
        String name = Constants.MONEYLOG_MAP.get(type);
        String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
        log.setAmountBefore(new BigDecimal(amount_before));
        log.setAmount(new BigDecimal(Arith.sub(0, order.getVolume())));
        log.setAmountAfter(new BigDecimal(Arith.sub(walletExtend.getAmount(), order.getVolume())));
        log.setLog(name + "闪兑卖出，订单号[" + order.getOrderNo() + "]");
        log.setUserId(order.getPartyId());
        log.setWalletType(order.getSymbol());
        log.setSymbol(order.getSymbol());
        log.setContentType(type2 + Constants.MONEYLOG_CONTENT_CLOSE);
        log.setCreateTime(new Date());
        log.setUpdateTime(new Date());
        moneyLogService.save(log);
        save(order);
        userDataService.saveSell(order);
    }

    @Override
    @Transactional
    public void saveOpenCreated(ExchangeApplyOrder order, Double orderPrice) {
        // 买入数量-手续费=到账
        double sub = Arith.sub(order.getVolume(), order.getFee());
        // 可以买的数量
        double amount = Arith.div(sub, orderPrice, 8);
        order.setCloseTime(new Date());
        order.setClosePrice(orderPrice);
        WalletExtend walletExtend = walletService.saveExtendByPara(order.getPartyId(), order.getSymbol());
        double amountBefore = walletExtend.getAmount();
        this.walletService.updateExtend(walletExtend.getPartyId(), walletExtend.getWallettype(), amount);
        String type = itemService.findBySymbol(order.getSymbol()).getType();
        MoneyLog log = new MoneyLog();
        log.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
        String name = Constants.MONEYLOG_MAP.get(type);
        String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
        log.setAmountBefore(new BigDecimal(amountBefore));
        log.setAmount(new BigDecimal(amount));
        log.setAmountAfter(new BigDecimal(amountBefore + amount));
        log.setLog(name + "闪兑买入成交，订单号[" + order.getOrderNo() + "]");
        log.setUserId(order.getPartyId());
        log.setSymbol(order.getSymbol());
        log.setWalletType(order.getSymbol());
        log.setContentType(type2 + Constants.MONEYLOG_CONTENT_OPEN);
        moneyLogService.save(log);
        order.setState(ExchangeApplyOrder.STATE_CREATED);
        order.setAmount(order.getVolume());
        order.setWalletFee(order.getFee());
        updateById(order);
    }

    @Override
    @Transactional
    public void saveCloseCreated(ExchangeApplyOrder order, Double orderPrice) {
        double sub = Arith.sub(order.getVolume(), order.getFee());
        double amount = Arith.mul(sub, orderPrice);
        order.setCloseTime(new Date());
        order.setClosePrice(orderPrice);
        Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
        double amount_before = wallet.getMoney().doubleValue();
        String type = itemService.findBySymbol(order.getSymbol()).getType();
        amount = exchangeRateService.getUsdtByType(BigDecimal.valueOf(amount), type).doubleValue();
        this.walletService.update(wallet.getUserId(), amount);
        MoneyLog log = new MoneyLog();
        log.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE);
        String name = Constants.MONEYLOG_MAP.get(type);
        String type2 = Constants.MONEYLOG_MAP_TYPE.get(type);
        log.setAmountBefore(new BigDecimal(amount_before));
        log.setAmount(new BigDecimal(amount));
        log.setAmountAfter(wallet.getMoney());
        log.setLog(name + "闪兑卖出成交，订单号[" + order.getOrderNo() + "]");
        log.setUserId(order.getPartyId());
        log.setWalletType(Constants.WALLET);
        log.setContentType(type2 + Constants.MONEYLOG_CONTENT_CLOSE);
        log.setSymbol(order.getSymbol());
        moneyLogService.save(log);
        order.setAmount(amount);
        order.setWalletFee(Arith.mul(order.getFee(), orderPrice));
        order.setState(ExchangeApplyOrder.STATE_CREATED);
        updateById(order);
    }

    /**
     * 闪兑记录列表
     */
    @Override
    public List<Map<String, Object>> queryExchangeListPage(int pageNo, int size, String userId) {
        QueryWrapper<ExchangeApplyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", userId);
        queryWrapper.eq("state", ExchangeApplyOrder.STATE_CREATED);
        queryWrapper.isNotNull("relation_order_no");
        List<ExchangeApplyOrder> list = exchangeApplyOrderMapper.selectList(queryWrapper);
        return bulidData(list);
    }

    private List<Map<String, Object>> bulidData(List<ExchangeApplyOrder> list) {
        Map<String, ExchangeRecord> recordMap = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ExchangeApplyOrder order = list.get(i);
            ExchangeRecord record = new ExchangeRecord();
            if (recordMap.containsKey(order.getRelationOrderNo())) {
                record = recordMap.get(order.getRelationOrderNo());
            }
            record.setClosePrice(order.getClosePrice());
            record.setState(order.getState());
            record.setCreate_time(order.getCreateTime());
            // 开仓 买入**币
            if (ExchangeApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {
                if (recordMap.containsKey(order.getRelationOrderNo())) {
                    record.setSymbol(order.getSymbol());
                    record.setAmount(order.getVolume());
                } else {
                    record.setSymbol(order.getSymbol());
                    record.setAmount(order.getVolume());
                    // 针对 **币 --兑换-- usdt
                    record.setAmount_to(order.getSymbolValue());
                }
            }
            // 平仓 卖出**币
            else if (ExchangeApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
                if (recordMap.containsKey(order.getRelationOrderNo())) {
                    record.setSymbol_to(order.getSymbol());
                    record.setAmount_to(order.getSymbolValue());
                } else {
                    record.setSymbol_to(order.getSymbol());
                    record.setAmount(order.getVolume());
                    record.setAmount_to(order.getSymbolValue());
                }
            }
            recordMap.put(order.getRelationOrderNo(), record);
        }
        for (ExchangeRecord entry : recordMap.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("symbol", entry.getSymbol());
            map.put("symbol_to", entry.getSymbol_to());
            map.put("amount", entry.getAmount());
            map.put("amount_to", entry.getAmount_to());
            map.put("create_time", entry.getCreate_time());
            map.put("state", entry.getState());
            map.put("closePrice", entry.getClosePrice());
            data.add(map);
        }
        if (data.size() > 0) {
            Collections.sort(data, (o1, o2) -> {
                String time1 = String.valueOf(o1.get("create_time"));
                String time2 = String.valueOf(o2.get("create_time"));
                return time2.compareTo(time1);
            });
            return data;
        }
        return data;
    }
}
