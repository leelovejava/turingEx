package com.yami.trading.bean.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_userdata")
public class UserData extends UUIDEntity {

    @TableField(exist = false)
    private String itemType;

    /**
     * 角色
     */
    private String rolename;

    private String userId;

    /**
     * 充值_DAPP
     */
    private double rechargeDapp;

    /**
     * 提现_DAPP
     */
    private double withdrawDapp;

    /*
     * 充提
     */
    /**
     * 充值金额
     */
    private double recharge;
    /**
     * 充值金额-Recharge_USDT
     */
    private double rechargeUsdt;
    /**
     * 充值金额-Recharge_ETH
     */
    private double rechargeEth;
    /**
     * 充值金额- Recharge_BTC
     */
    private double rechargeBtc;
    /**
     * 充值金额- Recharge_HT
     */
    private double rechargeHt;
    /**
     * 充值金额- Recharge_LTC
     */
    private double rechargeLtc;

    /**
     * 充值金额- Recharge_USDC
     */
    private  double rechargeUsdc;

    /**
     * 充值-返佣
     */
    private double rechargeRecom;

    /**
     * 提现金额（所有都换算成u）
     */
    private double withdrawAll;
    /**
     * 提现金额(usdt)
     */
    private double withdraw;
    /**
     * 提现eth
     */
    private double withdrawEth;
    /**
     * 提现btc
     */
    private double withdrawBtc;
    /**
     * 充提手续费
     */
    private double rechargeWithdrawalFee;
    /**
     * 礼金
     */
    private double giftMoney;

    /*
     * 永续
     */
    /**
     * 永续合约下单金额
     */
    private double amount;
    /**
     * 永续合约手续费
     */
    private double fee;
    /**
     * 永续合约收益
     */
    private double orderIncome;


    /**
     * 永续合约下单金额-外汇
     */
    private double forexAmount;
    /**
     * 永续合约手续费-外汇
     */
    private double forexFee;
    /**
     * 永续合约收益-外汇
     */
    private double forexOrderIncome;


    /**
     * 永续合约下单金额-ETF
     */
    private double indicesAmount;
    /**
     * 永续合约手续费-ETF
     */
    private double indicesFee;
    /**
     * 永续合约收益-ETF
     */
    private double indicesOrderIncome;

    /**
     * 永续合约下单金额-虚拟货币
     */
    private double cryptosAmount;
    /**
     * 永续合约手续费-虚拟货币
     */
    private double cryptosFee;
    /**
     * 永续合约收益-虚拟货币
     */
    private double cryptosOrderIncome;

    /**
     * 永续合约下单金额-美股
     */
    private double usStocksAmount;
    /**
     * 永续合约手续费-美股
     */
    private double usStocksFee;
    /**
     * 永续合约收益-美股
     */
    private double usStocksOrderIncome;

    /**
     * 理财买入金额
     */

    private double financeAmount;

    /**
     * 理财收益
     */
    private double financeIncome;

    /**
     * 交易金额（买入和卖出），USDT计价
     */
    private double exchangeAmount;
    /**
     * 币币手续费
     */
    private double exchangeFee;
    /**
     * 币币收益
     */
    private double exchangeIncome;
    /**
     * 自发币收益
     */
    private double coinIncome;
    /**
     * 交割合约下单金额
     */
    private double furturesAmount;
    /**
     * 交割合约手续费
     */
    private double furturesFee;
    /**
     * 交割合约收益
     */
    private double furturesIncome;

    /**
     * 交割合约下单金额-外汇
     */
    private double forexFurturesAmount;
    /**
     * 交割合约下单金额-外汇
     */
    private double forexFurturesFee;
    /**
     * 交割合约下单金额-外汇
     */
    private double forexFurturesIncome;


    /**
     * 交割合约下单金额-ETF
     */
    private double indicesFurturesAmount;
    /**
     * 交割合约下单金额-ETF
     */
    private double indicesFurturesFee;
    /**
     * 交割合约下单金额-ETF
     */
    private double indicesFurturesIncome;

    /**
     * 交割合约下单金额-虚拟货币
     */
    private double cryptosFurturesAmount;
    /**
     * 交割合约下单金额-虚拟货币
     */
    private double cryptosFurturesFee;
    /**
     * 交割合约下单金额-虚拟货币
     */
    private double cryptosFurturesIncome;

    /**
     * 交割合约下单金额-美股
     */
    private double usStocksFurturesAmount;
    /**
     * 交割合约下单金额-美股
     */
    private double usStocksFurturesFee;
    /**
     * 交割合约下单金额-美股
     */
    private double usStocksFurturesIncome;

    /**
     * 矿机下单金额
     */
    private double minerAmount;
    /**
     * 矿机收益
     */
    private double minerIncome;

    // 质押2.0金额
    private double galaxyAmount;

    // 质押2.0收益
    private double galaxyIncome;

    /**
     * 三方充值(USDT)
     */
    private double thirdRechargeAmount;
    /**
     * 持有金额数量
     */
    private double holdingMoney;
    /**
     * 转入金额USDT计价
     */
    private double transferInMoney;
    /**
     * 转出金额USDT计价
     */
    private double transferOutMoney;
    /*
     * 币币杠杆
     */
    /**
     * 币币杠杆下单金额
     */
    private double exchangeLeverAmount;
    /**
     * 币币杠杆手续费
     */
    private double exchangeLeverFee;
    /**
     * 币币杠杆收益
     */
    private double exchangeLeverOrderIncome;
    /*
     * 伞下推荐用户计划
     */

    /**
     * 推荐人数（伞下）（目前是4级）
     */
    private int recoNum;

    /**
     * 日期
     */
    private Date createTime;

}
