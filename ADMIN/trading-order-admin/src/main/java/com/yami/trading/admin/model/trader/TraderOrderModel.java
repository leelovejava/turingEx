package com.yami.trading.admin.model.trader;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TraderOrderModel {
    @ApiModelProperty("修改传id  新增不传")
    private String uuid;

    @ApiModelProperty("用户编码")
    private String usercode;

    @ApiModelProperty("币种")
    private String symbol;

    @ApiModelProperty("收益")
    private String profit;

    @ApiModelProperty("涨跌幅")
    private String changeRatio;

    @ApiModelProperty("平仓均价")
    private String closeAvgPrice;

    @ApiModelProperty("成交均价(成本)")
    private String tradeAvgPrice;

    @ApiModelProperty("平仓时间")
    private String closeTime;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("\"buy\":买(多) \"sell\":卖(空)")
    private String direction;

    @ApiModelProperty("杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]")
    private String leverRate;

    @ApiModelProperty("状态。submitted 已提交（持仓）， created 完成（平仓）  ")
    private String state;

    @ApiModelProperty("委托数量(张)")
    private String volumeOpen;

}
