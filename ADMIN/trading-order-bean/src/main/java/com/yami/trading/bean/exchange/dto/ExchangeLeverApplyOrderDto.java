package com.yami.trading.bean.exchange.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel
public class ExchangeLeverApplyOrderDto {

    private String uuid;

    private String symbol;

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty(" buy:多 sell:空")
    private String direction;

    @ApiModelProperty("open:开 close")
    private String offset;

    @ApiModelProperty("委托数量(张)")
    private double volumeOpen;

    @ApiModelProperty("杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]")
    private double leverRate;

    private String state;

    @ApiModelProperty("订单报价类型。 limit:限价 opponent:对手价（市价）")
    private String orderPriceType;

    @ApiModelProperty("止损触发价格")
    private double stopPriceLoss;

    private String createTime;
    @ApiModelProperty("止盈触发价格")
    private double stopPriceProfit;

    @ApiModelProperty("limit order的交易价格")
    private double price;

    @ApiModelProperty("品种")
    private String itemName;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("uuid")
    private String userCode;

    @ApiModelProperty("角色")
    private String roleName;

}
