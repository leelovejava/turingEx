package com.yami.trading.service.exchange.impl;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.exchange.ExchangeLever;
import com.yami.trading.bean.exchange.ExchangeLeverApplyOrder;
import com.yami.trading.bean.exchange.dto.ExchangeLeverApplyOrderDto;
import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.dto.ItemLeverageDTO;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.*;
import com.yami.trading.dao.exchange.ExchangeApplyOrderMapper;
import com.yami.trading.dao.exchangelever.ExchangeLeverApplyOrderMapper;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeApplyOrderService;
import com.yami.trading.service.exchange.ExchangeLeverApplyOrderService;
import com.yami.trading.service.exchange.ExchangeLeverOrderService;
import com.yami.trading.service.exchange.ExchangeLeverService;
import com.yami.trading.service.item.ItemLeverageService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class ExchangeLeverApplyOrderServiceImpl extends ServiceImpl<ExchangeLeverApplyOrderMapper, ExchangeLeverApplyOrder> implements ExchangeLeverApplyOrderService {

    @Autowired
    WalletService walletService;

    @Autowired
    MoneyLogService moneyLogService;

    @Autowired
    ExchangeLeverOrderService exchangeLeverOrderService;
    @Autowired
    DataService dataService;


    @Autowired
    SysparaService sysparaService;

    @Autowired
    ItemService itemService;

    @Autowired
    ItemLeverageService itemLeverageService;

    @Override
    @Transactional
    public void saveOpen(ExchangeLeverApplyOrder applyOrder, Realtime realtime) {

        Item item = this.itemService.findBySymbol(applyOrder.getSymbol());
        ExchangeLeverOrder exchangeLeverOrder= exchangeLeverOrderService.findBySymbol(applyOrder.getPartyId(),applyOrder.getSymbol());
        if (exchangeLeverOrder==null){
            ExchangeLeverOrder order = new ExchangeLeverOrder();
            order.setPartyId(applyOrder.getPartyId());
            order.setSymbol(applyOrder.getSymbol());
            order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
            order.setDirection(applyOrder.getDirection());
            order.setLeverRate(applyOrder.getLeverRate());
            order.setVolume(applyOrder.getVolume());
            order.setVolumeOpen(applyOrder.getVolumeOpen());
            order.setUnitAmount(applyOrder.getUnitAmount());
            order.setFee(applyOrder.getFee());
            order.setDeposit(applyOrder.getDeposit());
            order.setDepositOpen(new BigDecimal(applyOrder.getDeposit()).divide(new BigDecimal(applyOrder.getLeverRate())).setScale(8,BigDecimal.ROUND_UP).doubleValue());
            order.setTradeAvgPrice(realtime.getClose());
            order.setStopPriceProfit(applyOrder.getStop_price_profit());
            order.setStopPriceLoss(applyOrder.getStop_price_loss());
            order.setPips(item.getPips());
            order.setPipsAmount(item.getPipsAmount());
            order.setCreateTime(new Date());
            exchangeLeverOrderService.save(order);
        }
        else {
            exchangeLeverOrder.setVolumeOpen(exchangeLeverOrder.getVolumeOpen()+applyOrder.getVolumeOpen());
            exchangeLeverOrder.setVolume(exchangeLeverOrder.getVolume()+applyOrder.getVolume());
            double newDepositOpen=new  BigDecimal(applyOrder.getDeposit()).divide(new BigDecimal(applyOrder.getLeverRate())).setScale(8,BigDecimal.ROUND_UP).doubleValue();
            exchangeLeverOrder.setDepositOpen(exchangeLeverOrder.getDepositOpen()+newDepositOpen);
            exchangeLeverOrder.setDeposit(exchangeLeverOrder.getDeposit()+applyOrder.getDeposit());
            exchangeLeverOrder.setState(ExchangeLeverApplyOrder.STATE_SUBMITTED);
            exchangeLeverOrderService.updateById(exchangeLeverOrder);
        }

        /**
         * 进入市场
         */
        applyOrder.setVolume(0D);
        applyOrder.setState(ExchangeLeverApplyOrder.STATE_CREATED);
        updateById(applyOrder);
    }


    @Override
    public void saveCreate(ExchangeLeverApplyOrder order) {

        boolean order_open = sysparaService.find("order_open").getBoolean();
        if (!order_open) {
            throw new BusinessException("不在交易时段");
        }
        Item item = this.itemService.findBySymbol(order.getSymbol());
        if (item == null) {
            throw new BusinessException("币信息不存在");
        }
        if (order.getDeposit() <= 0) {
            throw new BusinessException("订单保证金小于0");
        }
        List<Realtime> list = this.dataService.realtime(order.getSymbol());
        if (list.size() == 0) {
            throw new BusinessException("行情获取失败，请稍后再试");
        }
        Realtime realtime = list.get(0);
        double close = realtime.getClose();
        order.setUnitAmount(close);// 每手金额
        if (ExchangeLeverApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
            // 买涨 用U 则 对应的保证金也是U，对应的张数需要换算
            if (ExchangeLeverApplyOrder.DIRECTION_BUY.equals(order.getDirection())) {
                order.setVolume(Arith.div(order.getDeposit(), close));
                order.setVolumeOpen(Arith.div(order.getDeposit(), close));
            } else {// 买跌 用指定币种 则 对应的保证金也是指定币种，对应的张数不需要换算
                order.setVolume(order.getDeposit());
                order.setVolumeOpen(order.getDeposit());
            }
//			order.setLever_rate(exchangeLever(order.getSymbol()));
            this.open(order);
        } else if (ExchangeLeverApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {
            order.setVolume(Arith.div(order.getDeposit(), close));
            order.setVolumeOpen(Arith.div(order.getDeposit(), close));
            this.close(order);
        }
    }

    @Override
    @Transactional
    public void saveCloseSymbol(ExchangeLeverApplyOrder applyOrder) {
        ExchangeLeverOrder exchangeLeverOrder =exchangeLeverOrderService.findBySymbol(
                applyOrder.getPartyId(), applyOrder.getSymbol());
        if (exchangeLeverOrder==null){
            return;
        }
        exchangeLeverOrder.setVolumeOpen(exchangeLeverOrder.getVolumeOpen()-applyOrder.getVolumeOpen());
        exchangeLeverOrder.setVolume(exchangeLeverOrder.getVolume()-applyOrder.getVolume());
        double leverRate= exchangeLeverOrder.getDeposit()/exchangeLeverOrder.getDepositOpen();
        exchangeLeverOrder.setDeposit(exchangeLeverOrder.getDeposit()-applyOrder.getDeposit());
        double money= applyOrder.getDeposit()/leverRate;
        exchangeLeverOrder.setDepositOpen(exchangeLeverOrder.getDeposit()/leverRate);
        if (exchangeLeverOrder.getDeposit()<0){
            throw  new BusinessException("可平仓保证金余额不足!");
        }
        applyOrder.setState(ExchangeLeverApplyOrder.OFFSET_CLOSE);
        updateById(applyOrder);
        double volume = applyOrder.getVolume();
        List<Realtime> realtime_list = this.dataService.realtime(applyOrder.getSymbol());
        Realtime realtime;
        if (realtime_list.size() > 0) {
            realtime = realtime_list.get(0);
        }
        else {
            throw  new BusinessException("未获取到行情数据!");
        }
      //  double profit = volume*realtime.getClose();

        String paySymbol = Constants.WALLET;
        Wallet wallet = walletService.saveWalletByPartyId(applyOrder.getPartyId());
        double  amountBefore = wallet.getMoney().doubleValue();
        double  profit= (realtime.getClose()*applyOrder.getVolumeOpen())- (exchangeLeverOrder.getTradeAvgPrice() * applyOrder.getVolumeOpen()) ;
        this.walletService.update(wallet.getUserId().toString(), money+profit);

        // 买涨 用U 则 对应的保证金也是U
//        if (ExchangeLeverOrder.DIRECTION_BUY.equals(applyOrder.getDirection())) {
//            Wallet wallet = walletService.saveWalletByPartyId(applyOrder.getPartyId());
//            amountBefore = wallet.getMoney().doubleValue();
//            if (Arith.add(wallet.getMoney().doubleValue(), profit) < 0) {
//                profit = Arith.sub(0, wallet.getMoney().doubleValue());
//            }
//            this.walletService.update(wallet.getUserId().toString(), profit);
//        } else {// 买跌 用指定币种 则 对应的保证金也是指定币种
//            paySymbol = applyOrder.getSymbol();
//            WalletExtend walletExtend = this.walletService.saveExtendByPara(applyOrder.getPartyId(), paySymbol);
//            amountBefore = walletExtend.getAmount();
//            if (Arith.add(walletExtend.getAmount(), profit) < 0) {
//                profit = Arith.sub(0, walletExtend.getAmount());
//            }
//            this.walletService.updateExtend(applyOrder.getPartyId().toString(), paySymbol, profit);
//        }


        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE_LEVER);
        moneylog.setAmountBefore(new BigDecimal(amountBefore));
        moneylog.setAmount(new BigDecimal(profit+money));
        moneylog.setAmountAfter(new BigDecimal(Arith.add(amountBefore, profit+profit)));
        moneylog.setLog("平仓，平仓现货杠杆数[" + volume + "],订单号[" + applyOrder.getOrderNo() + "]");
        moneylog.setUserId(applyOrder.getPartyId());
        moneylog.setWalletType(paySymbol);
        moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_LEVER_CLOSE);
        moneyLogService.save(moneylog);
        exchangeLeverOrderService.updateById(exchangeLeverOrder);
    }

    @Override
    public List<Map<String, Object>> getPaged(int pageNo, int pageSize, String partyId, String symbol, String type) {

        LambdaQueryWrapper<ExchangeLeverApplyOrder> lambdaQueryWrapper = Wrappers.<ExchangeLeverApplyOrder>query().lambda();
        lambdaQueryWrapper.eq(ExchangeLeverApplyOrder::getPartyId, partyId);
        if (!StringUtils.isNullOrEmpty(symbol)) {
            lambdaQueryWrapper.eq(ExchangeLeverApplyOrder::getSymbol, symbol);
        }
        if ("orders".equals(type)) {
            lambdaQueryWrapper.eq(ExchangeLeverApplyOrder::getState, ExchangeLeverApplyOrder.STATE_SUBMITTED);
        } else if ("hisorders".equals(type)) {
            lambdaQueryWrapper.in(ExchangeLeverApplyOrder::getState, ExchangeLeverApplyOrder.STATE_CREATED, ExchangeLeverApplyOrder.STATE_CANCELED);
        } else if ("opponent".equals(type)) {
            lambdaQueryWrapper.eq(ExchangeLeverApplyOrder::getState, ExchangeLeverApplyOrder.STATE_CREATED);
        } else if ("canceled".equals(type)) {
            lambdaQueryWrapper.eq(ExchangeLeverApplyOrder::getState, ExchangeLeverApplyOrder.STATE_CANCELED);
        }
        lambdaQueryWrapper.orderByDesc(ExchangeLeverApplyOrder::getCreateTime);
        Page page = new Page(pageNo, pageSize);
        page(page, lambdaQueryWrapper);
        List<Map<String, Object>> data = this.bulidData(page.getRecords());
        return data;
    }

    private List<Map<String, Object>> bulidData(List<ExchangeLeverApplyOrder> list) {

        List<Map<String, Object>> data = new ArrayList();
        DecimalFormat df = new DecimalFormat("#.##");
        for (int i = 0; i < list.size(); i++) {
            ExchangeLeverApplyOrder order = list.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("order_no", order.getOrderNo());
            map.put("name", itemService.findBySymbol(order.getSymbol()).getName());
            map.put("symbol", order.getSymbol());
            map.put("create_time", DateUtils.format(order.getCreateTime(), DateUtils.DF_yyyyMMddHHmmss));
            map.put("volume", order.getVolume());
            map.put("volume_open", order.getVolumeOpen());
            map.put("direction", order.getDirection());
            map.put("offset", order.getOffset());
            map.put("lever_rate", order.getLeverRate());
            map.put("price", order.getPrice());
            map.put("stop_price_profit", order.getStop_price_profit());
            map.put("stop_price_loss", order.getStop_price_loss());
            map.put("order_price_type", order.getOrder_price_type());
            map.put("state", order.getState());
//			map.put("amount", Arith.mul(order.getVolume(), order.getUnit_amount()));
//			map.put("amount_open", Arith.mul(order.getVolume_open(), order.getUnit_amount()));
//			if (order.getVolume().compareTo(order.getVolume_open()) != 0) {// 不相等
//				map.put("amount", Arith.mul(order.getVolume(), order.getUnit_amount()));
//			} else {
//				// 开仓委托才有杠杆
//				map.put("amount", Arith.mul(order.getDeposit(),
//						ExchangeLeverApplyOrder.OFFSET_OPEN.equals(order.getOffset()) ? order.getLever_rate() : 1d));
//			}
            map.put("amount", Arith.mul(order.getDeposit(),
                    ExchangeLeverApplyOrder.OFFSET_OPEN.equals(order.getOffset()) ? order.getLeverRate() : 1d));
            map.put("amount_open", Arith.mul(order.getDeposit(),
                    ExchangeLeverApplyOrder.OFFSET_OPEN.equals(order.getOffset()) ? order.getLeverRate() : 1d));
            map.put("fee", order.getFee());
            map.put("deposit", ExchangeLeverApplyOrder.OFFSET_CLOSE.equals(order.getOffset()) ? 0 : order.getDeposit());// 平仓委托保证金为0，和合约相同
            data.add(map);
        }
        return data;
    }

    /**
     * 开仓委托
     */
    @Transactional
    public void open(ExchangeLeverApplyOrder order) {
//		Item item = this.itemService.cacheBySymbol(order.getSymbol(), true);
        Item item = itemService.findBySymbol(order.getSymbol());
        if (item == null) {
            throw  new BusinessException("币信息不存在!");
        }
//        List<ExchangeLever> levers = exchangeLeverService.findBySymbol(order.getSymbol());
        List<ItemLeverageDTO> itemLeverageDTOS = itemLeverageService.findByItemId(item.getUuid());
        double exchange_lever_rate = this.sysparaService.find("exchange_lever_rate").getDouble();
        if (order.getLeverRate() != null && order.getLeverRate() != exchange_lever_rate) {
            boolean findlevers = false;
            /**
             * 杠杆有配置
             */
            for (int i = 0; i < itemLeverageDTOS.size(); i++) {
                if (order.getLeverRate() == Double.valueOf(itemLeverageDTOS.get(i).getLeverRate()).doubleValue()) {
                    findlevers = true;
                }
            }
            if (!findlevers) {
                throw new BusinessException("杠杆倍数配置不存在");
            }
        }
//        List<ExchangeLeverOrder> order_state0_list = exchangeLeverOrderService
//                .findSubmitted(order.getPartyId().toString(), order.getSymbol(), order.getDirection());
//        for (int i = 0; i < order_state0_list.size(); i++) {
//            Double source_lever_rate = order.getLeverRate();
//            source_lever_rate = source_lever_rate == null ? 0d : source_lever_rate;
//            Double target_lever_rate = order_state0_list.get(i).getLeverRate();
//            target_lever_rate = target_lever_rate == null ? 0d : target_lever_rate;
//            if (source_lever_rate.compareTo(target_lever_rate) != 0) {
//                throw new BusinessException("存在不同杠杆的持仓单");
//            }
//        }
//        List<ExchangeLeverApplyOrder> applyOrder_submitted_list = this.findSubmitted(order.getPartyId().toString(),
//                order.getSymbol(), "open", order.getDirection());
//        for (int i = 0; i < applyOrder_submitted_list.size(); i++) {
//            Double source_lever_rate = order.getLeverRate();
//            source_lever_rate = source_lever_rate == null ? 0d : source_lever_rate;
//            Double target_lever_rate = applyOrder_submitted_list.get(i).getLeverRate();
//            target_lever_rate = target_lever_rate == null ? 0d : target_lever_rate;
//            if (source_lever_rate.compareTo(target_lever_rate) != 0) {
//                throw new BusinessException("存在不同杠杆的持仓单");
//            }
//        }
        DecimalFormat df = new DecimalFormat("#.##");
        order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
//		order.setUnit_amount(item.getUnit_amount());
//		order.setFee(Arith.mul(item.getUnit_fee(), order.getVolume()));
        double exchange_lever_fee = this.sysparaService.find("exchange_lever_fee").getDouble();
//        order.setFee(Arith.mul(exchange_lever_fee, order.getDeposit()));
//		order.setDeposit(Arith.mul(order.getUnit_amount(), order.getVolume()));
//		order.setDeposit(order.getDeposit());
        if (order.getLeverRate() != null) {
            /**
             * 加上杠杆
             */
            order.setVolume(Arith.mul(order.getVolume(), order.getLeverRate()));
//            order.setFee(Arith.mul(order.getFee(), order.getLeverRate()));
        }
        order.setVolumeOpen(order.getVolume());
        order.setCreateTime(new Date());
        payOrder(order);
        save(order);
    }



    /**
     * 支付订单
     *
     * @param order
     */
    public void payOrder(ExchangeLeverApplyOrder order) {

        double amount_before = 0;
        double amount_fee_before = 0;
        String paySymbol = Constants.WALLET;
        // 买涨 用U 则 对应的保证金也是U
        if (ExchangeLeverApplyOrder.DIRECTION_BUY.equals(order.getDirection())) {
            Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
            amount_before = wallet.getMoney().doubleValue();
            BigDecimal   depositOpen=    new BigDecimal(order.getDeposit()).divide(new BigDecimal(order.getLeverRate())).setScale(2,BigDecimal.ROUND_UP);
            if (wallet.getMoney().doubleValue() < depositOpen.doubleValue()) {
                throw new BusinessException("委托失败,账号USDT可用保证金不足");
            }
            this.walletService.update(wallet.getUserId().toString(), Arith.sub(0, depositOpen.doubleValue()));
//            amount_fee_before = Arith.sub(amount_before, order.getDeposit());
//            this.walletService.update(wallet.getUserId().toString(), Arith.sub(0,));
        } else {// 买跌 用指定币种 则 对应的保证金也是指定币种
            paySymbol = order.getSymbol();
            WalletExtend walletExtend = this.walletService.saveExtendByPara(order.getPartyId(), paySymbol);
            amount_before = walletExtend.getAmount();
            if (walletExtend.getAmount() < Arith.add(order.getDeposit(), order.getFee())) {
                throw new BusinessException("委托失败,账号USDT可用保证金不足");
            }
            this.walletService.updateExtend(order.getPartyId().toString(), paySymbol, Arith.sub(0, order.getDeposit()));
            amount_fee_before = Arith.sub(amount_before, order.getDeposit());
            this.walletService.update(order.getPartyId().toString(), Arith.sub(0, order.getFee()));
        }

        /*
         * 保存资金日志
         */
        MoneyLog moneylog_deposit = new MoneyLog();
        moneylog_deposit.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE_LEVER);
        moneylog_deposit.setAmountBefore(new BigDecimal(amount_before));
        moneylog_deposit.setAmount(new BigDecimal(Arith.sub(0, order.getDeposit())));
        moneylog_deposit.setAmountAfter(new BigDecimal(Arith.sub(amount_before, order.getDeposit())));
        moneylog_deposit.setLog("委托单，订单号[" + order.getOrderNo() + "]");
        moneylog_deposit.setUserId(order.getPartyId());
        moneylog_deposit.setWalletType(paySymbol);
        moneylog_deposit.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_LEVER_OPEN);
        moneyLogService.save(moneylog_deposit);
//        MoneyLog moneylog_fee = new MoneyLog();
//        moneylog_fee.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE_LEVER);
//        moneylog_fee.setAmountBefore(new BigDecimal(amount_fee_before));
//        moneylog_fee.setAmount(new BigDecimal(Arith.sub(0, order.getFee())));
//        moneylog_fee.setAmountAfter(new BigDecimal(Arith.sub(amount_fee_before, order.getFee())));
//        moneylog_fee.setLog("手续费，订单号[" + order.getOrderNo() + "]");
//        moneylog_fee.setUserId(order.getPartyId());
//        moneylog_fee.setWalletType(paySymbol);
//        moneylog_fee.setContent_type(Constants.MONEYLOG_CONTENT_FEE);
//        moneyLogService.save(moneylog_fee);
    }

    @Override
    public Page<ExchangeLeverApplyOrderDto> listPage(Page page, String roleName, String status, String userName, String orderNo, List<String> userIds) {

        return baseMapper.listPage(page, roleName, status, userName, orderNo, userIds);
    }

    @Override
    public ExchangeLeverApplyOrder findByOrderNo(String order_no) {

        List<ExchangeLeverApplyOrder> list = list(Wrappers.<ExchangeLeverApplyOrder>query().lambda().eq(
                ExchangeLeverApplyOrder::getOrderNo,
                order_no));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<ExchangeLeverApplyOrder> findSubmitted() {

        return list(Wrappers.<ExchangeLeverApplyOrder>query().lambda().eq(ExchangeLeverApplyOrder::getState, "submitted"));
    }

    /**
     * 未处理状态的委托单
     */
    public List<ExchangeLeverApplyOrder> findSubmitted(String partyId, String symbol, String offset, String direction) {

        return list(Wrappers.<ExchangeLeverApplyOrder>query()
                .lambda().eq(ExchangeLeverApplyOrder::getPartyId, partyId).eq(ExchangeLeverApplyOrder::getOffset, offset)
                .eq(ExchangeLeverApplyOrder::getState, "submitted")
                .eq(ExchangeLeverApplyOrder::getDirection, direction)
                .eq(ExchangeLeverApplyOrder::getSymbol, symbol));
    }

    /**
     * 平仓委托
     */
    public void close(ExchangeLeverApplyOrder order) {
//		Item item = this.itemService.cacheBySymbol(order.getSymbol(), false);
        order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        order.setCreateTime(new Date());
//		order.setUnit_amount(item.getUnit_amount());
        double volume = 0;
        List<ExchangeLeverOrder> order_state0_list = exchangeLeverOrderService.findSubmitted(order.getPartyId(), order.getSymbol(), ExchangeLeverApplyOrder.DIRECTION_BUY);
        for (int i = 0; i < order_state0_list.size(); i++) {
            volume = Arith.add(volume, order_state0_list.get(i).getVolume());
        }
        List<ExchangeLeverApplyOrder> applyOrder_submitted_list = this.findSubmitted(order.getPartyId().toString(),
                order.getSymbol(), ExchangeLeverApplyOrder.OFFSET_CLOSE, ExchangeLeverApplyOrder.DIRECTION_BUY);
        for (int i = 0; i < applyOrder_submitted_list.size(); i++) {
            volume = Arith.sub(volume, applyOrder_submitted_list.get(i).getVolume());
            order.setLeverRate(applyOrder_submitted_list.get(i).getLeverRate());
        }
        if (order.getVolume() > volume) {
            throw new BusinessException("可平仓合约张数不足");
        }
        save(order);
    }

    /**
     * 撤单
     *
     * @param partyId
     * @param order_no
     */
    public void saveCancel(String partyId, String order_no) {

        ExchangeLeverApplyOrder order = findByOrderNo(order_no);
        if (order == null || !"submitted".equals(order.getState()) || !partyId.equals(order.getPartyId().toString())) {
            return;
        }
        order.setState("canceled");
        Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
        double amount_before = wallet.getMoney().doubleValue();
//		wallet.setMoney(Arith.add(wallet.getMoney(), Arith.add(order.getDeposit(), order.getFee())));
//		this.walletService.update(wallet);
        this.walletService.update(wallet.getUserId().toString(), Arith.add(order.getDeposit(), order.getFee()));
        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_CONTRACT);
        moneylog.setAmount_before(new BigDecimal(amount_before));
        moneylog.setAmount(new BigDecimal(Arith.add(order.getDeposit(), order.getFee())));
        moneylog.setAmountAfter(new BigDecimal(Arith.add(wallet.getMoney().doubleValue(), Arith.add(order.getDeposit(), order.getFee()))));
        moneylog.setLog("撤单，订单号[" + order.getOrderNo() + "]");
        moneylog.setUserId(order.getPartyId());
        moneylog.setWalletType(Constants.WALLET);
        moneylog.setContent_type(Constants.MONEYLOG_CONTENT_CONTRACT_CONCEL);
        moneyLogService.save(moneylog);
        updateById(order);
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
        double rate = Arith.div(volume, order.getVolumeOpen());
        profit = Arith.mul(Arith.add(order.getDeposit(), order.getProfit()), rate);
        order.setAmountClose(Arith.add(order.getAmountClose(), profit));
        order.setVolume(Arith.sub(order.getVolume(), volume));
        order.setDeposit(Arith.sub(order.getDeposit(), Arith.mul(order.getDepositOpen(), rate)));
        if (order.getVolume() <= 0) {
            order.setState(ExchangeLeverOrder.STATE_CREATED);
        }
        return profit;
    }

    @Override
    public ExchangeLeverApplyOrder saveClose(ExchangeLeverApplyOrder applyOrder, Realtime realtime, String order_no) {

        ExchangeLeverOrder order = exchangeLeverOrderService.findByOrderNo(order_no);
        if (order == null || !ExchangeLeverOrder.STATE_SUBMITTED.equals(order.getState()) || order.getVolume() <= 0) {
            /**
             * 状态已改变，退出处理
             */
            return applyOrder;
        }
        double volume;
        if (applyOrder.getVolume() > order.getVolume()) {
            volume = order.getVolume();
        } else {
            volume = applyOrder.getVolume();
        }
        double before_deposit = order.getDeposit();
        /**
         * 平仓退回的金额
         */
        double profit = this.settle(order, volume);
        /**
         * 修正保证金
         */
        // 订单平仓比率 =平仓委托金额/ (订单开仓金额*杠杆)
        // 实际平仓保证金 = 订单平仓比率*订单开仓金额
        // 实际平仓保证金 = 平仓委托金额/ 杠杆
//		double rate = Arith.div(applyOrder.getDeposit(), Arith.mul(order.getDeposit_open(), order.getLever_rate()));
//		order.setDeposit(Arith.sub(order.getDeposit(), Arith.mul(order.getDeposit_open(), rate)));
        order.setDeposit(Arith.sub(before_deposit, Arith.div(applyOrder.getDeposit(), order.getLeverRate())));
        if (order.getDeposit() <= 0) {
            order.setState(ExchangeLeverOrder.STATE_CREATED);
        }
        exchangeLeverOrderService.updateById(order);
//		if (profit > 0) {
        payCloseOrder(order, profit, volume);
//		Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
//		double amount_before = wallet.getMoney();
//
////		wallet.setMoney(Arith.add(wallet.getMoney(), profit));/
//		if (Arith.add(wallet.getMoney(), profit) < 0) {
//			profit = Arith.sub(0, wallet.getMoney());
//		}
//
//		this.walletService.update(wallet.getPartyId().toString(), profit);
//
//		MoneyLog moneylog = new MoneyLog();
//		moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE_LEVER);
//		moneylog.setAmount_before(amount_before);
//		moneylog.setAmount(profit);
//		moneylog.setAmount_after(Arith.add(wallet.getMoney(), profit));
//		moneylog.setLog("平仓，平仓合约数[" + volume + "],订单号[" + order.getOrder_no() + "]");
//		moneylog.setPartyId(order.getPartyId());
//		moneylog.setWallettype(Constants.WALLET);
//		moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_LEVER_CLOSE);
//
//		moneyLogService.save(moneylog);
//		}
        applyOrder.setVolume(Arith.sub(applyOrder.getVolume(), volume));
        if (applyOrder.getVolume() <= 0) {
            applyOrder.setState(ExchangeLeverApplyOrder.STATE_CREATED);
        }
        updateById(applyOrder);
//		this.userDataService.saveExchangeLeverClose(order);
//		/**
//		 * 交易员带单,用户跟单
//		 */
//		this.traderFollowUserOrderService.traderClose(order);
        return applyOrder;
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
        MoneyLog moneylog = new MoneyLog();
        moneylog.setCategory(Constants.MONEYLOG_CATEGORY_EXCHANGE_LEVER);
        moneylog.setAmountBefore(new BigDecimal(amountBefore));
        moneylog.setAmount(new BigDecimal(profit));
        moneylog.setAmountAfter(new BigDecimal(Arith.add(amountBefore, profit)));
        moneylog.setLog("平仓，平仓合约数[" + volume + "],订单号[" + order.getOrderNo() + "]");
        moneylog.setUserId(order.getPartyId());
        moneylog.setWalletType(paySymbol);
        moneylog.setContent_type(Constants.MONEYLOG_CONTENT_EXCHANGE_LEVER_CLOSE);
        moneyLogService.save(moneylog);
    }

}
