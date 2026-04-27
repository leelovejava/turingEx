package com.yami.trading.admin.controller.c2c.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class C2ccComputeModel {


    @ApiModelProperty("")
    private  String deposit_total;

    private  String currency;

    private  String symbol;

    private  String coin_amount;

    private  String symbol_value;

}
