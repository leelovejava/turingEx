package com.yami.trading.api.controller.ipo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class NewSharesDto {

    @ApiModelProperty("申购状态  1 申购中 2已中签 3 未中签")
    private  int status;

    @ApiModelProperty("股票代码")
    private  String symbolCode;

    @ApiModelProperty("股票名称")
    private  String symbolName;

    @ApiModelProperty("申购价格")
    private BigDecimal subPrice;

    @ApiModelProperty("申购股数")
    private  BigDecimal subNumber;

    @ApiModelProperty("申购需认缴")
    private  BigDecimal  requiredSubscribe;
    @ApiModelProperty("中签数量")
    private  BigDecimal winningNumber;

    @ApiModelProperty("中签应认缴")
    private  BigDecimal requiredNumber;
    @ApiModelProperty("已认缴次数")
    private  int subscribedCount;

    @ApiModelProperty("已认缴金额")
    private  BigDecimal  subscribedAmount;

    private String symbolType;

    @ApiModelProperty("库存损益%")
    private  double inventoryGainsLossesValue;
    @ApiModelProperty("现价")
    private  BigDecimal closePrice;

    @ApiModelProperty("市值")
    private  double marketValue;

    @ApiModelProperty("库存损益")
    private  double inventoryGainsLosses;

    private  String orderNo;

}
