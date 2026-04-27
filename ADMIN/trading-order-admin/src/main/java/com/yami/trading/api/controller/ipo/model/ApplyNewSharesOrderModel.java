package com.yami.trading.api.controller.ipo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyNewSharesOrderModel {


    private  String code;


    private BigDecimal amount;
}
