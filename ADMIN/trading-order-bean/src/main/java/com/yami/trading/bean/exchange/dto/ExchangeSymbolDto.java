package com.yami.trading.bean.exchange.dto;

import lombok.Data;

@Data
public class ExchangeSymbolDto {
    /**
     * 名称
     */
    private String name;
    /**
     * 币
     */
    private String symbol;
    private double volume; //可用
    private double marketValue;//市值
    private double openPrice; //开盘价

    private double positionVolume;//持仓数
    private double price;  //成本
    private double currentPrice;//现价

    private double profitLoss = 0;  //总盈亏
    /**
     * 今日盈亏（浮动）
     */
    private double toDayProfitLoss = 0; //今日盈亏

    private double profitLossPercentage; //总盈亏百分比

    private double toDayProfitLossPercentage; //今日盈亏百分比

    /**
     * "open":买入 "close":卖出
     */
    private String offset;

    private double yesterDayOpenPrice;//昨天开盘价格

    private double yesterDayProfitLoss = 0; //昨天盈亏
    /**
     * 收益
     */
    private double amount = 0;
    /**
     * 状态。submitted 已提交，canceled 已撤销， created 委托完成
     */
    private String state;
    /**
     * 累计盈亏
     */
    private double profitTotal;

    /**
     * 平仓时间戳
     */
    private Long closeTimeTs;

    /**
     * 实时价格毫秒时间戳
     */
    private Long ts;
    /**
     * 当日开盘价
     */
    private double todayOpenPrice;
    /**
     * 当日盈亏(结算)
     */
    private double todayProfit;
}
