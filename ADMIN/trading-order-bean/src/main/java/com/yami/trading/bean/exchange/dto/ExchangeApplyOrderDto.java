package com.yami.trading.bean.exchange.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Data
@ApiModel
public class ExchangeApplyOrderDto {

    private  String id;

    @ApiModelProperty("用户")
    private  String userName;

    @ApiModelProperty("UID")
    private  String userCode;

    @ApiModelProperty("账户类型")
    private  String roleName;

    @ApiModelProperty("推荐人")
    private  String usernameParent;

    @ApiModelProperty("品种")
    private  String symbol;

    @ApiModelProperty("品种名称")
    private  String symbolName;

    @ApiModelProperty(" 操作  open:买入 close 卖出 ")
    private String offset;

    @ApiModelProperty("币种数量委托数量")
    private Double symbolValue;

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("委托数量")
    private Double volume;

    @ApiModelProperty(" 订单报价类型。 limit:限价  opponent:对手价（市价）")
    private String orderPriceType;

    @ApiModelProperty("限价")
    private Double price;

    @ApiModelProperty("状态。submitted 已提交，canceled 已撤销， created 委托完成")
    private String state = "submitted";

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("成交价格")
    private Double closePrice;

    @ApiModelProperty("成交时间")
    private Date closeTime;

    private Double orderedVolume;
    
    private double successVolume;

    // 已成交数量
    public Double getSuccessVolume() {
        if (state.equals("submitted") || state.equals("canceled")) {
            return 0D;
        } else {
            return successVolume != 0 ? successVolume : symbolValue;
        }
    }

    // 委托价值
    public Double getVolume() {
        if (offset.equals("open")) {
            return volume;
        } else if (offset.equals("close")){
            return BigDecimal.valueOf(volume * price).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        }
        return volume;
    }

    public Double getOrderedVolume() {
        if(this.state.equalsIgnoreCase("submitted")){
            return 0d;
        }else if(this.state.equalsIgnoreCase("canceled")){
            return 0d;
        }
        return volume;
    }
}
