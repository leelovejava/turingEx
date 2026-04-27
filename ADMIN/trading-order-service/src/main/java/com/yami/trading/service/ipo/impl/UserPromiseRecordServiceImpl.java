package com.yami.trading.service.ipo.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.NewSharesConfig;
import com.yami.trading.bean.ipo.UserPromiseRecord;
import com.yami.trading.bean.ipo.dto.PromiseTopDto;
import com.yami.trading.bean.ipo.dto.SumUserPromiseDto;
import com.yami.trading.bean.ipo.dto.UserListDto;
import com.yami.trading.bean.ipo.dto.UserPromiseListDto;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.dao.ipo.UserPromiseRecordMapper;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.ipo.ApplyNewSharesOrderService;
import com.yami.trading.service.ipo.NewSharesConfigService;
import com.yami.trading.service.ipo.UserPromiseRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class UserPromiseRecordServiceImpl   extends ServiceImpl<UserPromiseRecordMapper, UserPromiseRecord> implements UserPromiseRecordService {

    @Autowired
    WalletService walletService;
    @Autowired
    ApplyNewSharesOrderService applyNewSharesOrderService;
    @Autowired
    DataService dataService;

    @Autowired
    NewSharesConfigService newSharesConfigService;

    public SumUserPromiseDto sumPromise(String userId, String productCode){
        SumUserPromiseDto sumUserPromiseDto=   baseMapper.sumPromise(userId,productCode);
        if (sumUserPromiseDto == null) {
            sumUserPromiseDto=new SumUserPromiseDto();
        }
        return sumUserPromiseDto;
    }

    @Override
    public Page<UserPromiseListDto> pageUserPromiseData(Page page, String userId,  List<String> symbols) {
         return baseMapper.pageUserPromiseData(page,userId,symbols);
    }

    @Override
    public PromiseTopDto topData(String userId) {
        return baseMapper.topData(userId);
    }

    @Override
    public Page<UserListDto> pagePromiseData(Page page, String userCode,
                                             String productName,
                                             String roleName, String productCode, String name, Integer status , List<String> children) {
        return baseMapper.pagePromiseData(page,userCode,productName,roleName,productCode,name,status , children);

    }


    @Override
    @Transactional
    public void sell(String orderNo, String userId) {
        ApplyNewSharesOrder applyNewSharesOrder= applyNewSharesOrderService.findByOrderNo(orderNo);
         if (applyNewSharesOrder==null){
            throw new YamiShopBindException("现股不存在");
         }
         if (applyNewSharesOrder.getStatus()!=2){
            throw new YamiShopBindException("现股未中签");
         }
        NewSharesConfig  newSharesConfig=   newSharesConfigService.getByProductCode(applyNewSharesOrder.getSymbolCode());
        if (newSharesConfig==null){
            throw new YamiShopBindException("现股已被删除");
        }
         applyNewSharesOrderService.updateById(applyNewSharesOrder);
         List<UserPromiseRecord> userPromiseRecords= findByOrderNo(orderNo);
         for (UserPromiseRecord u:userPromiseRecords){
            if ( u.getStatus()==1){
                throw new BusinessException("认缴订单待确认中无法卖出");
            }
        }
         if (CollectionUtil.isEmpty(userPromiseRecords)){
             throw new YamiShopBindException("未提交认缴记录");
         }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(userPromiseRecords.get(0).getCreateTime());
        calendar.add(Calendar.DAY_OF_YEAR,newSharesConfig.getLockDay());
        if (System.currentTimeMillis() < calendar.getTimeInMillis()){
               throw new YamiShopBindException("新股锁定中无法卖出");
        }
         BigDecimal usdt=new BigDecimal("0");
         if (CollectionUtil.isNotEmpty(userPromiseRecords)){
             List<Realtime> realtimes = dataService.realtime(userPromiseRecords.get(0).getProductCode());
             if (CollectionUtil.isEmpty(realtimes)){
                 throw  new BusinessException("获取行情数据失败");
             }
             for (UserPromiseRecord userPromiseRecord:userPromiseRecords){
                 if (userPromiseRecord.getShowFlag() == 1){
                     throw new YamiShopBindException("现股已卖出");
                 }
                 Realtime realtime= realtimes.get(0);
                 usdt = userPromiseRecord.getDeductNumber().multiply(BigDecimal.valueOf(realtime.getClose()));
                 userPromiseRecord.setShowFlag(1);
             }
         }
        else {
             throw new YamiShopBindException("认缴记录不存在!");
         }
        updateBatchById(userPromiseRecords);
        walletService.updateMoney("USDT",userId, usdt, BigDecimal.ZERO,
                Constants.MONEYLOG_CATEGORY_IPO_SEll, Constants.WALLET, Constants.MONEYLOG_CATEGORY_IPO_SEll, applyNewSharesOrder.getSymbolName()+
                        "新股卖出新增USDT"+usdt.doubleValue());

//        BigDecimal initMarketValue = order.getWinningNumber().multiply(order.getSubPrice());
//        if (CollectionUtil.isNotEmpty(realtimes)) {
//            Realtime realtime = realtimes.get(0);
//            BigDecimal newMarketValue = order.getWinningNumber().multiply(realtime.getClose());
//            marketValue = marketValue.add(newMarketValue);
//            availableLimit = availableLimit.add(order.getWinningNumber());
//            inventoryGainsLosses = inventoryGainsLosses.add(newMarketValue.subtract(initMarketValue));
//        } else {
//            marketValue = marketValue.add(initMarketValue);
//            availableLimit = availableLimit.add(order.getSubNumber());
//        }

        // 委托数量
//        String volume = applyNewSharesOrder.getWinningNumber().doubleValue()+"";
//        String symbol = applyNewSharesOrder.getSymbolCode();



//        String partyId =userId;
//        User party = userService.getById(partyId);
//        if (!party.isEnabled()) {
//            throw new YamiShopBindException("用户已禁用");
//        }
//        Syspara syspara = sysparaService.find("stop_user_internet");
//        String stopUserInternet = syspara.getSvalue();
//        if(org.apache.commons.lang3.StringUtils.isNotEmpty(stopUserInternet)) {
//            String[] stopUsers = stopUserInternet.split(",");
//            if(Arrays.asList(stopUsers).contains(party.getUserName())){
//                throw new YamiShopBindException("无网络");
//            }
//        }
//        List<Realtime> realtimes = dataService.realtime(symbol);
//        double close = 1;
//        if (CollectionUtil.isNotEmpty(realtimes)) {
//            close = realtimes.get(0).getClose().doubleValue();
//        } else {
//            throw new YamiShopBindException("行情数据，获取失败!");
//        }
//        ExchangeApplyOrder order = new ExchangeApplyOrder();
//        order.setPartyId(partyId);
//        order.setSymbol(symbol);
//        order.setOffset(ExchangeApplyOrder.OFFSET_CLOSE);
//        order.setVolume(Double.valueOf(volume));
//        order.setPrice(close);
//        order.setTriggerOrder(true);
//        order.setTriggerPrice(close);
//        order.setOrderPriceType("opponent");
//        order.setRelationOrderNo(UUID.randomUUID().toString());
//        order.setClosePrice(close);
//        // 限价单 && limit order的交易价格 为空
//        if ("limit".equals(order.getOrderPriceType()) && order.getPrice() == null) {
//            order.setPrice(close);
//        }
//        exchangeApplyOrderService.saveCreate(order);
    }

    @Override
    @Transactional
    public void applyPromise(NewSharesConfig newSharesConfig, UserPromiseRecord userPromiseRecord,
                             ApplyNewSharesOrder applyNewSharesOrder) {
        if (applyNewSharesOrder.getStatus()!=2){
            throw  new BusinessException("未中签!不需要认缴");
        }
        BigDecimal sumWinningNumber= applyNewSharesOrder.getWinningNumber();
        SumUserPromiseDto sumUserPromiseDto=  sumPromise(userPromiseRecord.getUserId(),newSharesConfig.getProductCode());
         BigDecimal addAfterNumber= sumUserPromiseDto.getDeductNumber().add(userPromiseRecord.getDeductNumber());
        if (sumWinningNumber.subtract(addAfterNumber).doubleValue()<0){
            throw  new BusinessException("中签认缴金额(股数)不足");
        }
        sumUserPromiseDto.setCount(sumUserPromiseDto.getCount()+1);
        sumUserPromiseDto.setSumUdst(sumUserPromiseDto.getSumUdst().add(userPromiseRecord.getDeductUsdt()));
        applyNewSharesOrder.setSubscribedCount(sumUserPromiseDto.getCount());
        applyNewSharesOrder.setSubscribedAmount(sumUserPromiseDto.getSumUdst());
        applyNewSharesOrderService.updateById(applyNewSharesOrder);
        userPromiseRecord.setProductCode(newSharesConfig.getProductCode());
        userPromiseRecord.setProductName(newSharesConfig.getProductName());
        userPromiseRecord.setName(newSharesConfig.getName());
        userPromiseRecord.setOrderNo(applyNewSharesOrder.getOrderNo());
        walletService.updateMoney("USDT",userPromiseRecord.getUserId(), userPromiseRecord.getDeductUsdt().negate(), BigDecimal.ZERO,
                Constants.MONEYLOG_CATEGORY_IPO, Constants.WALLET, Constants.MONEYLOG_CATEGORY_IPO, newSharesConfig.getProductName()+
                        "新股认缴USDT"+userPromiseRecord.getDeductUsdt().doubleValue());
        save(userPromiseRecord);
    }

    @Override
    public List<UserPromiseRecord> findByOrderNo(List<String> orderNos) {
        return list(Wrappers.<UserPromiseRecord>query().lambda().in(UserPromiseRecord::getOrderNo,orderNos));
    }

    @Override
    public List<UserPromiseRecord> findByProductCodeAndUserId(String productCode, String userId) {
        return list(Wrappers.<UserPromiseRecord>query().lambda().eq(UserPromiseRecord::getProductCode,productCode)
                .eq(UserPromiseRecord::getUserId,userId));
    }

    @Override
    public List<UserPromiseRecord> findByProductCode(String productCode) {
        return list(Wrappers.<UserPromiseRecord>query().lambda().eq(UserPromiseRecord::getProductCode,productCode)
                .eq(UserPromiseRecord::getShowFlag, 0));
    }

    @Override
    public List<UserPromiseRecord> findByOrderNo(String orderNo) {
        return list(Wrappers.<UserPromiseRecord>query().lambda().eq(UserPromiseRecord::getOrderNo,orderNo));
    }
}
