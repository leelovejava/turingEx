package com.yami.trading.admin.model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserAllRecomUpdateModel {

    @NotBlank
    private  String  userId;

    @ApiModelProperty("新推荐人userCode")
    @NotBlank
    private  String userCode;


    @ApiModelProperty("登录人资金密码")
    @NotBlank
    private  String loginSafeword;
}
