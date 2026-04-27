package com.yami.trading.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class RefreshTokenModel {

    @NotBlank
    @ApiModelProperty("刷新token")
    private  String refreshToken;
}
