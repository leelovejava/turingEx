package com.yami.trading.api.controller.ipo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class ApplyPromiseModel {

    @ApiModelProperty("认缴金额(股数)")
    private BigDecimal deductNumber;

    @ApiModelProperty("订单号")
    private  String orderNo;
}
