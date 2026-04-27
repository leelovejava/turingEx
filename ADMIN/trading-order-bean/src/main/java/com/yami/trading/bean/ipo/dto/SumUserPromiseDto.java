package com.yami.trading.bean.ipo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SumUserPromiseDto {

    private  int count;

    private BigDecimal sumUdst=new BigDecimal(0);

    private BigDecimal deductNumber=new BigDecimal(0);
}
