package com.yami.trading.admin.model.trader;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class TraderModel {
    @ApiModelProperty("修改传id  新增不传")
    private String uuid;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("头像")
    private String img;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("币种")
    private String symbols;

    @ApiModelProperty("累计盈利笔数")
    private int orderProfit;

    @ApiModelProperty("累计盈利笔数-偏差")
    private int deviationOrderProfit;

    @ApiModelProperty("累计亏损笔数")
    private int orderLoss;

    @ApiModelProperty("累计亏损笔数-偏差")
    private int deviationOrderLoss;

    @ApiModelProperty("3周累计金额")
    private double week3OrderAmount;

    @ApiModelProperty("3周累计金额-偏差")
    private double deviationWeek3OrderAmount;

    @ApiModelProperty("3周盈利笔数")
    private int week3OrderProfit;

    @ApiModelProperty("3周盈利笔数-偏差")
    private int deviationWeek3OrderProfit;

    @ApiModelProperty("3周交易笔数")
    private int week3OrderSum;

    @ApiModelProperty("3周交易笔数-偏差")
    private int deviationWeek3OrderSum;

    @ApiModelProperty("累计金额")
    private double orderAmount;

    @ApiModelProperty("累计金额-偏差")
    private double deviationOrderAmount;

    @ApiModelProperty("累计跟随人数")
    private int followerSum;

    @ApiModelProperty("累计跟随人数-偏差")
    private int deviationFollowerSum;

    @ApiModelProperty("当前跟随人数")
    private int followerNow;

    @ApiModelProperty("当前跟随人数-偏差")
    private int deviationFollowerNow;

    @ApiModelProperty("利润分成比例")
    private double profitShareRatio;

    @ApiModelProperty("此次跟单最多跟随人数")
    private int followerMax;

    @ApiModelProperty("跟单最小下单数")
    private int followVolumnMin;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("用户编码")
    private String usercode;

    @ApiModelProperty("3周收益")
    private double week3Profit;

    @ApiModelProperty("3周收益")
    private double week3ProfitRatio;

    @ApiModelProperty("累计收益")
    private double profit;

    @ApiModelProperty("累计收益率")
    private double profitRatio;

    @ApiModelProperty("3周收益-偏差")
    private double deviationWeek3Profit;

    @ApiModelProperty("3周收益率-偏差")
    private double deviationWeek3ProfitRatio;

    @ApiModelProperty("累计收益-偏差")
    private double deviationProfit;

    @ApiModelProperty("累计收益率-偏差")
    private double deviationProfitRatio;

    @ApiModelProperty("状态 0-关闭带单 1-开启带单")
    private String state;
}
