package com.yami.trading.admin.controller.googleAuth.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class AdminGoogleAuthModel {
    @ApiModelProperty("超级谷歌验证码")
    private  String superGoogleAuthCode;

    @ApiModelProperty("谷歌验证码")
    private  String googleAuthCode;

    @ApiModelProperty("谷歌密钥")
    private  String googleAuthSecret;

}
