package com.yami.trading.admin.model.c2c;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class C2cPaymentMethodConfigDeleteModel {


    @ApiModelProperty("更新传id")
    @NotBlank
    private  String id;

    @ApiModelProperty("登录资金人密码")
    @NotBlank
    private  String  loginSafeword;

}
