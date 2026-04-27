package com.yami.trading.bean.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserDataWithdrawLimitDto {

    @ApiModelProperty("用户ID")
    private String userId;

    private Double amount;

    private Double financeAmount;

    private Double furturesAmount;

    private Double minerAmount;
}
