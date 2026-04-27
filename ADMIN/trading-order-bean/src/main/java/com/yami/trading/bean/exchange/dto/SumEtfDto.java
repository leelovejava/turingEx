package com.yami.trading.bean.exchange.dto;

import lombok.Data;

@Data
public class SumEtfDto {
    private String profitLoss="0";  //盈亏

    private String toDayProfitLoss="0"; //今日盈亏

    private String sumPrice="0";  //总资产

    private String sumVolume="0"; //可用
}
