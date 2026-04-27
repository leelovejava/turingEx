package com.yami.trading.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PaymentMethodModel {

    @ApiModelProperty("语言")
    private  String  language;

}
