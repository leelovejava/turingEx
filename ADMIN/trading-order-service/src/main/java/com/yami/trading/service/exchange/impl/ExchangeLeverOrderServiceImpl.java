package com.yami.trading.service.exchange.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeLeverApplyOrder;
import com.yami.trading.bean.exchange.dto.ExchangeLeverOrderDto;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.exchangelever.ExchangeLeverOrderMapper;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeLeverOrderService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ExchangeLeverOrderServiceImpl extends ServiceImpl<ExchangeLeverOrderMapper, ExchangeLeverOrder> implements ExchangeLeverOrderService {
    //    @Autowired
//    ExchangeLeverApplyOrderService exchangeLeverApplyOrderService;
    @Autowired
    ItemService itemService;
    @Autowired
    MoneyLogService moneyLogService;
    @Autowired
    WalletService walletService;

    @Autowired
    DataService dataService;

    @Autowired
    SysparaService sysparaService;

    public ExchangeLeverOrder findByOrderNo(String order_no) {
        return getOne(Wrappers.<ExchangeLeverOrder>query().lambda().eq(ExchangeLeverOrder::getOrderNo, order_no));
    }

    @Override
    public Page<ExchangeLeverOrderDto> listPage(Page page, String roleName,
                                                String status,
                                                String userName,
                                                Date startTime,
                                                Date endTime,
                                                String orderNo,
                                                List<String> userIds) {
        return baseMapper.listPage(page, roleName, status, userName, startTime, endTime, orderNo, userIds);
    }

    @Override
    @Transactional
    public ExchangeLeverOrder saveClose(String partyId, String order_no) {
        /*
         * 平仓
         */
        ExchangeLeverOrder order = this.findByOrderNo(order_no);
        if (order == null || !ExchangeLeverOrder.STATE_SUBMITTED.equals(order.getState())
                || !partyId.equals(order.getPartyId().toString()) || order.getVolume() <= 0) {
            /**
             * 状态已改变，退出处理
             */
            return null;
        }
        double interestRate = sysparaService.find("exchange_lever_interest_rate").getDouble();// 利率
        /**
         * 收益
         */
        double volume = order.getVolume();
        double profit = this.settle(order, order.getVolume());
        double  unpaidInterest=compute(order.getCreateTime(),new Date(),interestRate,order.getDeposit()-
                order.getDepositOpen());
        payCloseOrder(order, profit+ order.getDepositOpen(), volume);
        payInterestOrder(order,unpaidInterest);
        order.setState(ExchangeLeverOrder.STATE_CREATED);
        order.setVolume(0D);
        order.setDeposit(0);
        order.setDepositOpen(0);
        order.setCloseTime(new Date());
        updateById(order);
        return order;
    }

     public void payInterestOrder(ExchangeLeverOrder order ,double unpaidInterest){
        if (unpaidInterest<=0){
            return;
        }
         double amountBefore = 0;
         String paySymbol = Constants.WALLET;
         // 买涨 用U 则 对应的保证金也是U
         Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
         amountBefore = wallet.getMoney().doubleValue();
         if (Arith.add(wallet.getMoney().doubleValue(), unpaidInterest) < 0) {
             unpaidInterest = Arith.sub(0, wallet.getMoney().doubleValue());
         }
         this.walletService.update(wallet.getUserId().toString(), unpaidInterest);
         MoneyLog moneylog = new MoneyLog();
         moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE_LEVER);
         moneylog.setAmountBefore(new BigDecimal(amountBefore));
         moneylog.setAmount(new BigDecimal(unpaidInterest));
         moneylog.setAmountAfter(new BigDecimal(Arith.add(amountBefore, unpaidInterest)));
         moneylog.setLog("全仓杠杆平仓支付利息["+unpaidInterest+"],订单号[" + order.getOrderNo() + "]");
         moneylog.setUserId(order.getPartyId());
         moneylog.setWalletType(paySymbol);
         moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_LEVER_INTEREST);
         moneyLogService.save(moneylog);
     }

    /**
     * 支付平仓订单
     *
     * @param order
     * @param profit
     * @param volume
     */
    public void payCloseOrder(ExchangeLeverOrder order, double profit, double volume) {
        double amountBefore = 0;
        String paySymbol = Constants.WALLET;
        // 买涨 用U 则 对应的保证金也是U
        if (ExchangeLeverOrder.DIRECTION_BUY.equals(order.getDirection())) {
            Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
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

        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE_LEVER);
        moneylog.setAmountBefore(new BigDecimal(amountBefore));
        moneylog.setAmount(new BigDecimal(profit));
        moneylog.setAmountAfter(new BigDecimal(Arith.add(amountBefore, profit)));
        moneylog.setLog("平仓，平仓全仓杠杆[" + volume + "],订单号[" + order.getOrderNo() + "]");
        moneylog.setUserId(order.getPartyId());
        moneylog.setWalletType(paySymbol);
        moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_LEVER_CLOSE);
        moneyLogService.save(moneylog);

    }



    /**
     * 收益结算，平仓时计算
     *
     * @param volume 平仓的张数
     */
    public double settle(ExchangeLeverOrder order, double volume) {
        double profit = 0;
        /**
         * 平仓比率
         */
        List<Realtime> realtimes = dataService.realtime(order.getSymbol());
        if (CollectionUtil.isEmpty(realtimes)) {
            throw  new BusinessException(order.getSymbol()+"未获取行情数据");
        }
        Realtime realtime= realtimes.get(0);
        profit= (realtime.getClose()*order.getVolumeOpen())- (order.getTradeAvgPrice() * order.getVolumeOpen()) ;
        order.setAmountClose(Arith.add(order.getDepositOpen(), profit));

//        order.setVolume(Arith.sub(order.getVolume(), volume));
//        order.setDeposit(Arith.sub(order.getDeposit(), Arith.mul(order.getDepositOpen(), rate)));

        if (order.getVolume() <= 0) {
            order.setState(ExchangeLeverOrder.STATE_CREATED);
        }

        return profit;

    }

    @Override
    public List<ExchangeLeverOrder> findSubmitted() {
        return list(Wrappers.<ExchangeLeverOrder>query().lambda()
                .eq(ExchangeLeverOrder::getState,ExchangeLeverOrder.STATE_SUBMITTED));
    }

    @Override
    public ExchangeLeverOrder findBySymbol(String partyId, String symbol) {
       List<ExchangeLeverOrder> exchangeLeverOrders=   list(Wrappers.<ExchangeLeverOrder>query().lambda()
               .eq(ExchangeLeverOrder::getPartyId,partyId)
               .eq(ExchangeLeverOrder::getSymbol,symbol)
               .eq(ExchangeLeverOrder::getState,ExchangeLeverOrder.STATE_SUBMITTED));
        if (CollectionUtil.isNotEmpty(exchangeLeverOrders)){
             return exchangeLeverOrders.get(0);
         }
         return null;
    }



    @Override
    public double sumVolume(String currentUserId,String symbol) {
        LambdaQueryWrapper<ExchangeLeverOrder> lambdaQueryWrapper=    Wrappers.<ExchangeLeverOrder>query().lambda()
                .eq(ExchangeLeverOrder::getState,ExchangeLeverOrder.STATE_SUBMITTED);
        if (StringUtils.isNotEmpty(symbol)){
            lambdaQueryWrapper.eq(ExchangeLeverOrder::getSymbol,symbol);
        }

        List<ExchangeLeverOrder>  list=  list(lambdaQueryWrapper);
        double volume=0;
        for (ExchangeLeverOrder exchangeLeverOrder: list){
            volume+=exchangeLeverOrder.getVolume();
        }
        return volume;
    }

    @Override
    public List<ExchangeLeverOrder> findSubmitted(String partyId, String symbol, String direction) {
        LambdaQueryWrapper<ExchangeLeverOrder> lambdaQueryWrapper = Wrappers.<ExchangeLeverOrder>query().lambda();
        if (!StringUtils.isNullOrEmpty(partyId)) {
            lambdaQueryWrapper.eq(ExchangeLeverOrder::getPartyId, partyId);
        }
        if (!StringUtils.isNullOrEmpty(symbol)) {
            lambdaQueryWrapper.eq(ExchangeLeverOrder::getSymbol, symbol);
        }
        if (!StringUtils.isNullOrEmpty(direction)) {
            lambdaQueryWrapper.eq(ExchangeLeverOrder::getDirection, direction);
        }
        lambdaQueryWrapper.eq(ExchangeLeverOrder::getState, "submitted");
        return list(lambdaQueryWrapper);
    }

    public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type) {
        Page page = new Page(pageNo, pageSize);
        LambdaQueryWrapper<ExchangeLeverOrder> lambdaQueryWrapper = Wrappers.<ExchangeLeverOrder>query().lambda();
        lambdaQueryWrapper.eq(ExchangeLeverOrder::getPartyId, partyId);
        if (!StringUtils.isNullOrEmpty(symbol)) {
            lambdaQueryWrapper.eq(ExchangeLeverOrder::getSymbol, symbol);
        }
        Date date = DateUtils.addDay(new Date(), -1);
        if ("orders".equals(type)) {
            lambdaQueryWrapper.eq(ExchangeLeverOrder::getState, "submitted");
        } else if ("hisorders".equals(type)) {
            lambdaQueryWrapper.eq(ExchangeLeverOrder::getState, "created");
        }
        lambdaQueryWrapper.orderByDesc(ExchangeLeverOrder::getCreateTime);
        page(page,lambdaQueryWrapper);
        Wallet wallet=   walletService.findByUserId(partyId);
        double sumVolume= sumVolume(partyId,null);
        sumVolume+=wallet.getMoney().doubleValue();

        List<Map<String, Object>> data = this.bulidData(page.getRecords(),new BigDecimal(sumVolume));
        return data;
    }

    private List<Map<String, Object>> bulidData(List<ExchangeLeverOrder> list,BigDecimal money) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ExchangeLeverOrder order = list.get(i);
            Map<String, Object> map = bulidOne(order,money.doubleValue());
            data.add(map);
        }
        return data;
    }

    public Map<String, Object> bulidOne(ExchangeLeverOrder order,double money) {
        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormat df8 = new DecimalFormat("#.########");
        Map<String, Object> map = new HashMap<>();
        map.put("order_no", order.getOrderNo());
        map.put("name", itemService.findBySymbol(order.getSymbol()).getName());
        map.put("symbol", order.getSymbol());
        map.put("create_time", DateUtils.format(order.getCreateTime(), DateUtils.DF_yyyyMMddHHmmss));
        if (order.getCloseTime() != null) {
            map.put("close_time", DateUtils.format(order.getCloseTime(), DateUtils.DF_yyyyMMddHHmmss));
        } else {
            map.put("close_time", "");
        }
        map.put("direction", order.getDirection());
        map.put("lever_rate", order.getLeverRate());
        map.put("trade_avg_price", order.getTradeAvgPrice());
        map.put("close_avg_price", order.getCloseAvgPrice());
        map.put("stop_price_profit", order.getStopPriceProfit());
        map.put("stop_price_loss", order.getStopPriceLoss());
        List<Realtime> list= dataService.realtime(order.getSymbol());
        double closePrice= CollectionUtil.isEmpty(list)?0:list.get(0).getClose();
        map.put("close_price",closePrice);
        map.put("state", order.getState());
        double debt=order.getDeposit()-
                order.getDepositOpen();
        map.put("debt",debt);//负债
        map.put("deposit_open", order.getDepositOpen());
            //风险率
            double interestRate = sysparaService.find("exchange_lever_interest_rate").getDouble();// 利率
            double  unpaidInterest= compute(order.getCreateTime(),new Date(),interestRate,order.getDeposit()-
                    order.getDepositOpen());
            map.put("deposit_open", order.getDepositOpen()-unpaidInterest);
            map.put("riskRate", new BigDecimal(calculateRiskRate(order.getDeposit(),
                    debt,unpaidInterest)).setScale(2,BigDecimal.ROUND_UP));

        if (order.getVolume().compareTo(order.getVolumeOpen()) != 0) {// 不相等
            map.put("amount", Arith.mul(order.getDeposit(), order.getLeverRate()));
        } else {
            map.put("amount", Arith.mul(order.getDepositOpen(), order.getLeverRate()));
        }
        map.put("amount_open", Arith.mul(order.getDepositOpen(), order.getLeverRate()));
        map.put("fee", order.getFee());
        map.put("deposit", order.getDeposit());
        map.put("change_ratio", order.getChangeRatio());
        map.put("liquidationPrice", calculateLiquidationPrice(money+order.getDepositOpen(),closePrice,order.getVolume()));
        double  profit=0;
        if (!CollectionUtil.isEmpty(list)){
            profit =  (list.get(0).getClose()*order.getVolumeOpen())- (order.getTradeAvgPrice() * order.getVolumeOpen());
        }
        map.put("profit",new BigDecimal(profit).setScale(4,BigDecimal.ROUND_UP));
        map.put("volume", order.getVolume());
        map.put("volume_open", order.getVolumeOpen());
        if (CollectionUtil.isEmpty(list)){
            throw  new BusinessException(order.getSymbol()+"获取行情数据失败");
        }
        map.put("profit_rate", calculateProfitPercentage(order.getTradeAvgPrice(),list.get(0).getClose()));
        return map;
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

    public static double compute(Date startDate, Date endDate,
                                 double interestRate, double deposit) {
        // 获取小时数
        long hours = getHours(startDate, endDate);
        System.out.println(hours + "");
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

        return ((positionAssetValue) / (totalLiabilities + unpaidInterest) - 1) * 100;
    }

    public static void main(String[] args) {
        double interestRate =0.0016;// 利率
        double debt=400;
        double positionAssetValue=100;
        Date  date= DateUtil.stringToDate("2023-12-30 12:00:00","yyyy-MM-dd HH:mm:ss");
        double  unpaidInterest= compute(date,new Date(),interestRate,400);
        System.out.println(unpaidInterest);

        double dd= calculateRiskRate(10000,debt,unpaidInterest);
        System.out.println(dd);
    }

    /**
     *   计算强平价格
     * @param initMargin  仓位初始保证金
     * @param currentPrice 当前价格
     *@param currentPrice 维持保证金
     *  @param positionQty  仓位数量
     */
    public static double calculateLiquidationPrice(double initMargin, double currentPrice,
                                                   double positionQty) {
        // 定义变量和常量
        double maintenanceMarginRate =calculateMaintenanceMarginRate(initMargin,currentPrice,positionQty);
        // 计算强平价格
        double liquidationPrice = (initMargin + (currentPrice * positionQty - initMargin) * maintenanceMarginRate) / positionQty;
        if (liquidationPrice<=0){
            return 0;
        }
        return  liquidationPrice;
    }

    /**
     * 维持保证金率
     * @return
     */
    public  static  double calculateMaintenanceMarginRate(double maintenanceMargin,
                                                          double currentPrice,double positionQty ){


        // 计算维持保证金率
        double maintenanceMarginRate = maintenanceMargin / (currentPrice * positionQty);
        return  maintenanceMarginRate;
    }

    /**
     * 计算收益
     * @param buyPrice
     * @param currentPrice
     * @return
     */
    public static double calculateProfitPercentage(double buyPrice, double currentPrice) {
        double profit = currentPrice - buyPrice;
        double profitPercentage = (profit / buyPrice) * 100;
        System.out.println(profitPercentage);
        return new BigDecimal(profitPercentage).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算负债
     * @param positionValue
     * @param leverageRatio
     * @return
     */
    public static double calculateDebt(double positionValue, double leverageRatio) {
        // 计算负债
        double debt = positionValue * (leverageRatio - 1);

        return debt;
    }
}
