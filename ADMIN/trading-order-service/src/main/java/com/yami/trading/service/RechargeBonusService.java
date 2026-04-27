package com.yami.trading.service;

import com.yami.trading.bean.model.RechargeBlockchainOrder;

import java.util.List;
import java.util.Map;

public interface RechargeBonusService {

    /**
     * 充值时计算收益
     * @param entity
     * 币种usdt价值
     */
    public void saveBounsHandle(RechargeBlockchainOrder entity, double transfer_usdt, List<RechargeBlockchainOrder> orders);


    /**
     * 充值时计算收益
     * @param entity
     * 币种usdt价值(新)
     */
    public void saveUsdtBounsHandle(RechargeBlockchainOrder entity, double transfer_usdt, List<RechargeBlockchainOrder> orders, Map<String,String> itemMap);


    /**
     * 首次彩金计算收益
     * @param entity
     * 币种usdt价值(新)
     */
    public void saveFirstUsdtBounsHandle(RechargeBlockchainOrder entity, double transfer_usdt, long number,String[] recharges);


//    ——————————————————————————————-
//    ICE盘定制交易手续费返佣
//    2.三代返佣
//    1代：用户推荐新用户时，从被推荐人的每笔交易手续费中获得15%作为返佣。
//    2代：被推荐人（第一代）再推荐新用户时，原推荐人（第一代推荐者）将从第二代交易手续费中获得10%作为返佣。
//    3代：第二代被推荐人再推荐新用户时，原推荐人（第一代推荐者）将从第三代交易手续费中获得5%作为返佣。
//    3. 当月用户交易量低于10000交易量禁止提取返佣收益部分。
    public void saveTradeBounsHandle(String userId,double transferUsdt,double volume,String orderNo,String symbol);

}
