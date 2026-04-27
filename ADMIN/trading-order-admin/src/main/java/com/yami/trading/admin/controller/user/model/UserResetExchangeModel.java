package com.yami.trading.admin.controller.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserResetExchangeModel {


    private  String id;

    @ApiModelProperty("账变金额USDT")
    private  String moneyRevise;


    private  String loginSafeword;


    @ApiModelProperty("修改余额的方式。1. recharge--充值有记录报表 2.change----增加余额，不记录报表 3.withdraw----平台扣款，不记录报表")
    private  String changeType;


    private  String coinType;

    private  String resetType;

}
