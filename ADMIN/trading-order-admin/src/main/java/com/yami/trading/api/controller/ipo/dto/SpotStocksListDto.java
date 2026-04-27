package com.yami.trading.api.controller.ipo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class SpotStocksListDto {

    @ApiModelProperty("市值")
    private String marketValue;

    @ApiModelProperty("库存损益")
    private  String inventoryGainsLosses;


    @ApiModelProperty("可用额度")
    private  String availableLimit;





}
