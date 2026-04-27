package com.yami.trading.admin.controller.googleAuth.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class AdminGoogleAuthUnBindModel {


    @ApiModelProperty("超级谷歌验证码")
    private  String superGoogleAuthCode;
}
