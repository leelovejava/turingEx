package com.yami.trading.service.ipo.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.NewSharesConfig;
import com.yami.trading.bean.ipo.dto.SumSpotStockDto;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.dao.ipo.ApplyNewSharesOrderSharesMapper;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.data.RealtimeService;
import com.yami.trading.service.exchange.ExchangeApplyOrderService;
import com.yami.trading.service.ipo.ApplyNewSharesOrderService;
import com.yami.trading.service.ipo.NewSharesConfigService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class ApplyNewSharesOrderServiceImpl extends ServiceImpl<ApplyNewSharesOrderSharesMapper, ApplyNewSharesOrder> implements ApplyNewSharesOrderService {

    @Autowired
    NewSharesConfigService newSharesConfigService;


    @Autowired
    RealtimeService realtimeService;

    @Autowired
    ExchangeApplyOrderService exchangeApplyOrderService;

    @Autowired
    DataService dataService;


    @Autowired
    SysparaService sysparaService;

    @Autowired
    UserService userService;


    @Autowired
    WalletService  walletService;



    @Override
    @Transactional
    public void apply(BigDecimal amount, String userId,String code) {
        NewSharesConfig newSharesConfig=  newSharesConfigService.getByProductCode(code);
        if (newSharesConfig==null){
            throw new BusinessException("新股不存在或未发布");
        }
        newSharesConfig.setAppliedSubscribeNumber(amount.add(new BigDecimal(newSharesConfig.getAppliedSubscribeNumber())).intValue());
        if (newSharesConfig.getAppliedSubscribeNumber()<=0){
            throw new BusinessException("股票数申购数不足!");
        }

        if (amount.doubleValue()>newSharesConfig.getDefaultLimit().doubleValue()){
            throw new BusinessException("股票数抽签数大于限购数!");
        }
        if (newSharesConfig.getAppliedSubscribeNumber()>newSharesConfig.getSubscribeTotalNumber()){
            throw  new BusinessException("抽签数量大于总抽签股数");
        }
        List<ApplyNewSharesOrder>  applyNewSharesOrders=  findByProductCodeAndUserId(newSharesConfig.getProductCode(),userId);
        if (CollectionUtil.isNotEmpty(applyNewSharesOrders)){
            throw new BusinessException("已经有申购订单在申购中,请勿重复申请");
        }
        newSharesConfigService.updateById(newSharesConfig);
        ApplyNewSharesOrder applyNewSharesOrder=new ApplyNewSharesOrder();
        applyNewSharesOrder.setSubNumber(amount);
        applyNewSharesOrder.setRequiredSubscribe(amount.multiply(newSharesConfig.getUnderwritingPrice()));
        applyNewSharesOrder.setSymbolName(newSharesConfig.getName());
        applyNewSharesOrder.setSymbolCode(newSharesConfig.getProductCode());
        applyNewSharesOrder.setSubPrice(newSharesConfig.getUnderwritingPrice());
        applyNewSharesOrder.setUserId(userId);
        applyNewSharesOrder.setStatus(1);
        applyNewSharesOrder.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        save(applyNewSharesOrder);
    }

    public  List<ApplyNewSharesOrder>  findByProductCodeAndUserId(String productCode, String userId){


         return  list(Wrappers.<ApplyNewSharesOrder>query().lambda().eq(ApplyNewSharesOrder::getSymbolCode,productCode).
                 eq(ApplyNewSharesOrder::getUserId,userId).eq(ApplyNewSharesOrder::getStatus,1));

    }

    @Override
    public List<ApplyNewSharesOrder> findByOrderNo(List<String> orderNo) {
        return list(Wrappers.<ApplyNewSharesOrder>query().lambda().in(ApplyNewSharesOrder::getOrderNo,orderNo));}

    @Override
    public Page pageData(Page page, String orderNo, String symbolCode, String symbolName, String userName, int status , List<String> children) {
       return    baseMapper.pageData(page,orderNo,symbolCode,symbolName,userName,status , children);
    }

    @Override
    public ApplyNewSharesOrder findByOrderNo(String orderNo) {
        return getOne(Wrappers.<ApplyNewSharesOrder>query().lambda().eq(ApplyNewSharesOrder::getOrderNo,orderNo));
    }

    @Override
    public  List<SumSpotStockDto> sumSpotStock(String userId) {

       return baseMapper.sumSpotStock(userId);
    }

    @Override
    public BigDecimal sumWinningNumberByUserId(String userId,String symbolCode) {
        BigDecimal sumWinningNumber= baseMapper.sumWinningNumberByUserId(userId,symbolCode);
        if (sumWinningNumber==null){
            sumWinningNumber=new BigDecimal(0);
        }
        return  sumWinningNumber;
    }

    @Transactional
    public void sell(String orderNo, String userId) {
        ApplyNewSharesOrder applyNewSharesOrder= findByOrderNo(orderNo);
        if (applyNewSharesOrder==null){
            throw new YamiShopBindException("现股不存在");
        }
        if (applyNewSharesOrder.getStatus()!=2){
            throw new YamiShopBindException("现股未中签");
        }
        applyNewSharesOrder.setSell(1);
        updateById(applyNewSharesOrder);
//        List<UserPromiseRecord>     list= userPromiseRecordService.findByOrderNo(applyNewSharesOrder.getOrderNo());
//        for (UserPromiseRecord userPromiseRecord:list){
//            userPromiseRecord.setShow(0);
//        }
//        userPromiseRecordService.updateBatchById(list);
        // 委托数量
        String volume = applyNewSharesOrder.getWinningNumber().doubleValue()+"";
        String symbol = applyNewSharesOrder.getSymbolCode();
        String partyId =userId;
        User party = userService.getById(partyId);
        if (!party.isEnabled()) {
            throw new YamiShopBindException("用户已禁用");
        }
        Syspara syspara = sysparaService.find("stop_user_internet");
        String stopUserInternet = syspara.getSvalue();
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(stopUserInternet)) {
            String[] stopUsers = stopUserInternet.split(",");
            if(Arrays.asList(stopUsers).contains(party.getUserName())){
                throw new YamiShopBindException("无网络");
            }
        }
        List<Realtime> realtimes = dataService.realtime(symbol);
        double close = 1;
        if (CollectionUtil.isNotEmpty(realtimes)) {
            close = realtimes.get(0).getClose();
        } else {
            throw new YamiShopBindException("行情数据，获取失败!");
        }
        ExchangeApplyOrder order = new ExchangeApplyOrder();
        order.setPartyId(partyId);
        order.setSymbol(symbol);
        order.setOffset(ExchangeApplyOrder.OFFSET_CLOSE);
        order.setVolume(Double.valueOf(volume));
        order.setPrice(close);
        order.setOrderPriceType("opponent");
        order.setClosePrice(close);
        // 限价单 && limit order的交易价格 为空
        if ("limit".equals(order.getOrderPriceType()) && order.getPrice() == null) {
            order.setPrice(close);
        }
        // 该方法已经修改成闪兑用了 币币交易联系卡尔
        // exchangeApplyOrderService.saveCreate(order);
    }

    @Override
    public List<ApplyNewSharesOrder> findByStatus(String productCode,int status) {
        return list(Wrappers.<ApplyNewSharesOrder>query().lambda().eq(ApplyNewSharesOrder::getSymbolCode,productCode).eq(ApplyNewSharesOrder::getStatus,status));
    }

}
