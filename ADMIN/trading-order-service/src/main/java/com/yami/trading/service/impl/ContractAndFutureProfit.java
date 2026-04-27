package com.yami.trading.service.impl;

import lombok.Data;

import java.time.ZoneId;

/**
 * 合约盈利，包含永续和交割
 */
@Data
public class ContractAndFutureProfit {
    private String partyId;
    /**
     * 时区
     */
    private ZoneId zone = ZoneId.systemDefault();
    /**
     * 币对类型
     */
    private String symbolType;

    /**
     * 已经平仓的永续订单利润
     */
    private double closedOrderProfit;

    /**
     * 今日已经平仓的永续订单利润
     */
    private double closedOrderProfitToday;

    /**
     * 持仓单总盈利
     */
    private double holderOrderProfit;

    /**
     * 今日持仓单总盈利
     */
    private double holderOrderProfitToday;

    /**
     * 已经交割的交割合约利润
     */
    private double closeFutureOrderProfit;

    /**
     * 当天交割的合约利润
     */
    private double closeFutureOrderProfitToday;

    /**
     * 获取浮动盈亏
     */
    private double orderProfit;

    /**
     * 获取当日盈亏
     */
    private double orderProfitToday;
    /**
     * 获取累计盈亏
     */
    private double orderProfitTotal;
}
