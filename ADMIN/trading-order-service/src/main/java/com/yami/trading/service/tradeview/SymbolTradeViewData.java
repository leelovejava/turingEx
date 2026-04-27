package com.yami.trading.service.tradeview;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SymbolTradeViewData {
    private String symbol;
    /**
     * 所属行业
     */
    private String industry;

    /**
     * 市值
     */
    private BigDecimal marketCap;

    /**
     * 涨跌幅
     */
    private BigDecimal rate;


}
