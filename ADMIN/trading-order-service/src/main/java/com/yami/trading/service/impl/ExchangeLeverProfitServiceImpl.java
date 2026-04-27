package com.yami.trading.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.bean.purchasing.dto.ExchangeLeverLock;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.ExchangeLeverProfitService;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeLeverOrderService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ExchangeLeverProfitServiceImpl implements ExchangeLeverProfitService {
    @Autowired
    ExchangeLeverOrderService exchangeLeverOrderService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    ItemService itemService;
    @Autowired
    WalletService walletService;
    @Autowired
    MoneyLogService moneyLogService;
    @Autowired
    DataService dataService;

    public void handleOrders() {
//		if (!isComputeInterest())
//			return;
        List<ExchangeLeverOrder> list = exchangeLeverOrderService.findSubmitted();
        List<ExchangeLeverOrder> delayList = new ArrayList<>();
        double interestRate = sysparaService.find("exchange_lever_interest_rate").getDouble();// 利率
        for (ExchangeLeverOrder order : list) {
            boolean lock = false;
            try {
                if (!ExchangeLeverLock.add(order.getOrderNo())) {
                    delayList.add(order);
                    continue;
                }
                lock = true;
                order = exchangeLeverOrderService.findByOrderNo(order.getOrderNo());
                if (order == null || !ExchangeLeverOrder.STATE_SUBMITTED.equals(order.getState())) {
                    /**
                     * 状态已改变，退出处理
                     */
                    continue;
                }
                List<Realtime> realtimes = dataService.realtime(order.getSymbol());
                if (CollectionUtil.isEmpty(realtimes)) {
                    log.info(order.getSymbol() + "未获取到行情数据");
                } else {
                    saveOrderProfit(order, interestRate);

                }
                //    interestRate=item.getBorrowingRate()>0?item.getBorrowingRate():interestRate;
            } catch (Throwable e) {
                log.error("error:", e);
            } finally {
                if (lock) {
                    /**
                     * 每秒处理20个订单
                     */
                    ThreadUtils.sleep(50);
                    ExchangeLeverLock.remove(order.getOrderNo());
                }
            }
        }
        while (delayList.size() > 0) {// 延迟处理的订单表
            List<ExchangeLeverOrder> tempList = new ArrayList<ExchangeLeverOrder>(delayList);
            for (ExchangeLeverOrder order : tempList) {
                boolean lock = false;
                try {
                    if (!ExchangeLeverLock.add(order.getOrderNo())) {
                        continue;
                    }
                    lock = true;
                    order = exchangeLeverOrderService.findByOrderNo(order.getOrderNo());
                    if (order == null || !ExchangeLeverOrder.STATE_SUBMITTED.equals(order.getState())) {
                        /**
                         * 状态已改变，退出处理
                         */
                        delayList.remove(order);
                        continue;
                    }
                    Item item = itemService.findBySymbol(order.getSymbol());
                    interestRate = item.getBorrowingRate() > 0 ? item.getBorrowingRate() : interestRate;
                    saveOrderProfit(order, interestRate);
                    delayList.remove(order);
                } catch (Throwable e) {
                    log.error("error:", e);
                } finally {
                    if (lock) {
                        /**
                         * 每秒处理20个订单
                         */
                        ThreadUtils.sleep(50);
                        ExchangeLeverLock.remove(order.getOrderNo());
                    }
                }
            }
        }
    }

    /**
     * 是否计算利息
     *
     * @return
     */
    public boolean isComputeInterest() {
        boolean trade_time_button = sysparaService.find("trade_time_button").getBoolean();
        if (!trade_time_button)
            return false;// 不处于交易日时不计算利息
        /**
         * 周六或周日
         */
        if (DateUtil.getWeek(new Date()) == 6 || DateUtil.getWeek(new Date()) == 7) {
            return false;
        }
        return true;
    }

    public static long getHours(Date startDate, Date endDate) {
        Instant startInstant = startDate.toInstant();
        LocalDateTime startTime = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
        Instant instant = endDate.toInstant();
        LocalDateTime endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        Duration duration = Duration.between(startTime, endTime);
        // 获取小时数
        long hours = duration.toHours();
        return hours;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @param interestRate  汇率
     * @param deposit  押金
     * @return
     */
    public static double compute(Date startDate, Date endDate,
                                 double interestRate, double deposit) {
        // 获取小时数
        long hours = getHours(startDate, endDate);
        // 借款金額 *5* 小時利率 * 借款小時數
        return deposit * (hours * interestRate);
    }

    /**
     * @param positionAssetValue  仓位总资产估值
     * @param totalLiabilities 总负债
     * @param unpaidInterest 未归还利息
     * @return
     */
    // 计算风险率
    public static double calculateRiskRate(double positionAssetValue, double totalLiabilities, double unpaidInterest) {
        if (totalLiabilities==0){
            return 0;
        }
        return ((positionAssetValue) / (totalLiabilities + unpaidInterest) - 1) * 100;
    }

    /**
     * 收取利息
     *
     * @param order
     * @param interestRate 利率
     */
    public void saveOrderProfit(ExchangeLeverOrder order, double interestRate) {
        double  unpaidInterest=compute(order.getCreateTime(),new Date(),interestRate,order.getDeposit()-
                order.getDepositOpen());
        double debt=order.getDeposit()-
                order.getDepositOpen();
        double riskRate=     new BigDecimal(calculateRiskRate(order.getDeposit(),
                debt,unpaidInterest)).setScale(2,BigDecimal.ROUND_UP).doubleValue();
        //风险率<= 平仓
        if (riskRate<=10){
            double profit =settle(order, unpaidInterest);
            double depositOpen=order.getDepositOpen();
            if (profit>depositOpen){
                profit=depositOpen;
            }
            else {
                profit=  profit+depositOpen;
            }
            exchangeLeverOrderService.updateById(order);
            closeOrder(order,profit ,unpaidInterest);
        }
    }

    /**
     * 支付订单
     *
     * @param order
     */
    public void closeOrder(ExchangeLeverOrder order, double profit, double interest) {
        double amountBefore = 0;
        String paySymbol = Constants.WALLET;
        // 买涨 用U 则 对应的保证金也是U
        if (ExchangeLeverOrder.DIRECTION_BUY.equals(order.getDirection())) {
            Wallet wallet = walletService.saveWalletByPartyId(order.getPartyId());
            amountBefore = wallet.getMoney().doubleValue();
            if (Arith.add(wallet.getMoney().doubleValue(), profit) < 0) {
                profit = Arith.sub(0, wallet.getMoney().doubleValue());
            }
            this.walletService.update(wallet.getUserId().toString(), profit);
        } else {// 买跌 用指定币种 则 对应的保证金也是指定币种
            paySymbol = order.getSymbol();
            WalletExtend walletExtend = this.walletService.saveExtendByPara(order.getPartyId(), paySymbol);
            amountBefore = walletExtend.getAmount();
            if (Arith.add(walletExtend.getAmount(), profit) < 0) {
                profit = Arith.sub(0, walletExtend.getAmount());
            }
            this.walletService.updateExtend(order.getPartyId().toString(), paySymbol, profit);
        }

        /*
         * 保存资金日志
         */
        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE_LEVER);
        moneylog.setAmountBefore(new BigDecimal(amountBefore));
        moneylog.setAmount(new BigDecimal(profit));
        moneylog.setAmountAfter(new BigDecimal(Arith.add(amountBefore, profit)));
        moneylog.setLog("全仓杠杆,保证金中余额不足无法支付利息,直接平仓,利息[" + interest + "],订单号["
                + order.getOrderNo() + "]");
        moneylog.setUserId(order.getPartyId());
        moneylog.setWalletType(paySymbol);
        moneylog.setContentType(Constants.MONEYLOG_CONTENT_EXCHANGE_LEVER_CLOSE);
        moneyLogService.save(moneylog);
    }

    /**
     * 利息账变记录，仅在订单中扣除保证金，不扣除实际钱包
     *
     * @param order
     * @param interest
     */
    public void interestLog(ExchangeLeverOrder order, double interest) {
        double amountBefore = 0;
        String paySymbol = Constants.WALLET;
        // 买涨 用U 则 对应的保证金也是U
        if (ExchangeLeverOrder.DIRECTION_BUY.equals(order.getDirection())) {
            Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
            amountBefore = wallet.getMoney().doubleValue();
        } else {// 买跌 用指定币种 则 对应的保证金也是指定币种
            paySymbol = order.getSymbol();
            WalletExtend walletExtend = this.walletService.saveExtendByPara(order.getPartyId(), paySymbol);
            amountBefore = walletExtend.getAmount();
        }
        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE_LEVER);
        moneylog.setAmountBefore(new BigDecimal(amountBefore));
        moneylog.setAmount(new BigDecimal(Arith.sub(0, interest)));
        moneylog.setAmountAfter(new BigDecimal(amountBefore));
        moneylog.setLog("全仓杠杆,仅在订单保证金中扣除利息,利息[" + interest + "],剩余保证金[" + order.getDeposit() + "],订单号["
                + order.getOrderNo() + "]");
        moneylog.setUserId(order.getPartyId());
        moneylog.setWalletType(paySymbol);
        moneylog.setContentType(Constants.MONEYLOG_CONTENT_EXCHANGE_LEVER_INTEREST);
        moneyLogService.save(moneylog);
    }

    /**
     * 收益结算，平仓时计算
     *
     * @param amount 平仓的金额
     */
    public double settle(ExchangeLeverOrder order, double amount) {
        double profit = 0;
        List<Realtime> realtimes = dataService.realtime(order.getSymbol());
        if (CollectionUtil.isEmpty(realtimes)) {
           throw  new BusinessException(order.getSymbol()+"未获取行情数据");
        }
        Realtime realtime= realtimes.get(0);
        profit=   (realtime.getClose()*order.getVolumeOpen())-  (order.getTradeAvgPrice() * order.getVolumeOpen());
        order.setAmountClose(Arith.add(order.getAmountClose(), profit));
        // 修正平仓张数
        order.setVolume(0d);
        // 保证金置空
        order.setDeposit(0d);
        order.setState(ExchangeLeverOrder.STATE_CREATED);
        return new BigDecimal(profit).setScale(4,BigDecimal.ROUND_UP).doubleValue();
    }
}
