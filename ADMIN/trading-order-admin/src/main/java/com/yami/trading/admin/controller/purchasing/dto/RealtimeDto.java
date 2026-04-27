package com.yami.trading.admin.controller.purchasing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class RealtimeDto {

    @ApiModelProperty(" 成交量 币个数")
    private Double amount;

    /**
     * 最高价
     */
    @ApiModelProperty("最高价")
    private Double high;
    /**
     * 最低价
     */
    @ApiModelProperty("最低价")
    private Double low;
    /**
     * 最新价
     */
    @ApiModelProperty("当前价格")
    private Double close;

    @ApiModelProperty("持仓量")
    private Double openInterest;


    /**
     * 开盘价
     */
    @ApiModelProperty("开盘价")
    private Double open;


    @ApiModelProperty("日增量")
    private Double dailyIncrement;
}
