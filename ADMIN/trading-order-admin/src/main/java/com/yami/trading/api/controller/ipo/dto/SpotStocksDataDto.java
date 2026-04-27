package com.yami.trading.api.controller.ipo.dto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class SpotStocksDataDto {

    @ApiModelProperty("市值")
    private double marketValue;

    @ApiModelProperty("库存损益")
    private  double inventoryGainsLosses;


    @ApiModelProperty("可用额度")
    private double availableLimit;





}
