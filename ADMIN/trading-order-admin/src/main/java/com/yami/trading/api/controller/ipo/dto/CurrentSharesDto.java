package com.yami.trading.api.controller.ipo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class CurrentSharesDto {

    @ApiModelProperty("股票代码")
    private  String symbolCode;

    @ApiModelProperty("股票名称")
    private  String symbolName;

    @ApiModelProperty("申购价格")
    private BigDecimal subPrice;

    @ApiModelProperty("申购股数")
    private  BigDecimal subNumber;

    @ApiModelProperty("中签数量")
    private  BigDecimal winningNumber;

    @ApiModelProperty("申购需认缴")
    private  BigDecimal  requiredSubscribe;
    @ApiModelProperty("中签应认缴")
    private  BigDecimal requiredNumber;
    @ApiModelProperty("库存损益")
    private  String inventoryGainsLosses;

    @ApiModelProperty("库存损益百分比")
    private  String inventoryGainsLossesPercentage;


}
