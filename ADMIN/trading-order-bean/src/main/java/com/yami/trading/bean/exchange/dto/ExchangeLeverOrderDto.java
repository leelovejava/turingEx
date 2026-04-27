package com.yami.trading.bean.exchange.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class ExchangeLeverOrderDto {
    private String uuid;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("uuid")
    private String userCode;
    @ApiModelProperty("角色")
    private String roleName;
    @ApiModelProperty("订单 号")
    private String orderNo;
    @ApiModelProperty("品种")
    private String itemName;
    @ApiModelProperty("操作 open 买 close 卖  buy 涨  sell 跌")
    private String offset;
    @ApiModelProperty("成交均价(成本)")
    private double tradeAvgPrice;
    @ApiModelProperty("止盈止损")
    private double stopPriceProfit;
    @ApiModelProperty("支付单位")
    private String symbol;
    /**
     * 委托数量(剩余)(张)
     */
    @ApiModelProperty("委托数量(剩余)(张)")
    private Double volume;
    /**
     * 委托数量(张)
     */
    @ApiModelProperty("委托数量(张)")
    private Double volumeOpen;
    /**
     * 每手金额
     */
    @ApiModelProperty("每手金额")
    private double unitAmount;
    /**
     * 下单金额
     */
    @ApiModelProperty("下单金额")
    private Double amount;
    /**
     * 保证金(剩余)
     */
    @ApiModelProperty("保证金(剩余)")
    private double deposit;
    /**
     * 保证金
     */
    @ApiModelProperty("保证金")
    private double depositOpen;
    /**
     * 平仓退回金额
     */
    @ApiModelProperty("平仓退回金额")
    private double amountClose;
    /**
     * 收益
     */
    @ApiModelProperty("收益")
    private double profit;
    @ApiModelProperty("用户钱包余额")
    private BigDecimal money;
    private String partyId;
    /**
     * "buy":买(多) "sell":卖(空)
     */
    @ApiModelProperty("buy:买(多) sell:卖(空)")
    private String direction;
    /**
     * 手续费
     */
    @ApiModelProperty("手续费")
    private double fee;
    /**
     * 平仓均价
     */
    @ApiModelProperty("平仓均价")
    private Double closeAvgPrice;
    /**
     * 止损触发价格
     */
    @ApiModelProperty("止损触发价格")
    private Double stopPriceLoss;
    /**
     * 最小浮动
     */
    @ApiModelProperty("最小浮动")
    private double pips;
    /**
     * 最小浮动金额（以交易金额计算）
     */
    @ApiModelProperty("最小浮动金额（以交易金额计算）")
    private double pipsAmount;
    /**
     * 状态。submitted 已提交（持仓）， created 完成（平仓）
     */
    @ApiModelProperty("状态。submitted 已提交（持仓）， created 完成（平仓）")
    private String state = "submitted";
    private Date createTime;
    /**
     * 平仓时间
     */
    @ApiModelProperty("平仓时间")
    private Date closeTime;
    /**
     * 杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
     */
    @ApiModelProperty("杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]")
    private Double leverRate;
    /**
     * 涨跌幅
     */
    @ApiModelProperty("涨跌幅")
    private double changeRatio;
}
