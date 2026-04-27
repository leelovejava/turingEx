package com.yami.trading.bean.ipo.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class SumSpotStockDto {

    private  String  symbolCode;

    private BigDecimal sumWinningNumber;
}
