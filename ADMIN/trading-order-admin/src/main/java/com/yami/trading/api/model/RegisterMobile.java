package com.yami.trading.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class RegisterMobile extends RegisterModel {

    @ApiModelProperty("验证码")
    @NotBlank
    private String verifcode;
}
