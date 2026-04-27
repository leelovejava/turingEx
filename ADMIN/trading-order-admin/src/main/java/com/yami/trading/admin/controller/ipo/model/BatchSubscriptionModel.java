package com.yami.trading.admin.controller.ipo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel
public class BatchSubscriptionModel {
    @ApiModelProperty("订单号 多个 ,分开")
    private List<String> ids;



}
