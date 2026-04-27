package com.yami.trading.bean.ipo.dto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PromiseTopDto {
    @ApiModelProperty("中签额度")
    private double winningQuota;

    @ApiModelProperty("认缴额度")
    private  double subscriptionLimit;


    @ApiModelProperty("可用额度")
    private double availableLimit;
}
