package com.yami.trading.bean.exchange.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class TodayTransactionDto {

    private  String  symbolName;

    private  String  symbol;

    private  double volume;

    private  double avgPrice;
}
