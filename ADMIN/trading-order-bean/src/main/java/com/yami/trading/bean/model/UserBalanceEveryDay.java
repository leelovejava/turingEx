package com.yami.trading.bean.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserBalanceEveryDay {
    private int id;
    private String partyId;
    private BigDecimal balance;
    private String dateStr;
}