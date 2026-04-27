package com.yami.trading.bean.ipo;

import lombok.Data;

@Data
public class XueQiuNewStocks {
    private String name;
    private String type;
    private String symbol;

    /**
     * 上市日期
     */
    private String listingDate;

    /**
     * 发行价格
     */
    private String issuePrice;

    /**
     * 发行后每股净资产
     */
    private String netAssetsPerShareAfterIssuance;


    /**
     * 首日開盤價
     */
    private String firstDayOpeningPrice;


    /**
     * 首日漲幅
     */
    private String firstDayChangeRatio;

    /**
     * 首日漲幅
     */
    private String changeRatio;
    /**
     * 當前價格
     */
    private String curPrice;

    /**
     * 每簽獲利
     */
    private String profitPerDraw;

    /**
     * 市盈率
     */
    private String ttm;
}
