package com.yami.trading.admin.model.trader;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TraderFollowUserModel {
    @ApiModelProperty("修改传id  新增不传")
    private String uuid;

    @ApiModelProperty("用户编码")
    private String usercode;

    @ApiModelProperty("用户类型")
    private String userType;

    @ApiModelProperty("交易员用户编码")
    private String traderUsercode;

    @ApiModelProperty("用户名模糊查找")
    private String username;

    @ApiModelProperty("跟单固定张数/固定比例---选择 1,固定张数，2，固定比例")
    private String followType;

    @ApiModelProperty("止盈百分比")
    private String stopProfit;

    @ApiModelProperty("止损百分比")
    private String stopLoss;

    @ApiModelProperty("跟随购买品种")
    private String symbol;

    @ApiModelProperty("跟单张数或比例")
    private String volume;

    @ApiModelProperty("累计跟单收益")
    private String profit;

    @ApiModelProperty("累计跟单本金")
    private String amountSum;

    @ApiModelProperty("最大持仓张数")
    private String volumeMax;

}
