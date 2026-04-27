package com.yami.trading.huobi.hobi.internal;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LiveRatesRecord {

    /**
     * 卖出价
     */
    private double ask;

    /**
     * 买入价
     */
    private double bid;

    /**
     * 实时价
     */

    @JSONField(name = "current")
    private double mid;

    /**
     * 收盘价
     */
    private double close;

    /**
     * 最高
     */
    @JSONField(name = "high")
    private double high;

    /**
     * 最低
     */
    @JSONField(name = "low")
    private double low;

    /**
     * 开盘价
     */
    @JSONField(name = "open")
    private double open;

    /**
     * 交易对
     */
    @JSONField(name = "symbol")
    private String currency;

    /**
     * 时间戳
     */
    @JSONField(name = "time")
    private long timestamp;

    private BigDecimal volume;

    private BigDecimal amount;

    /**
     * 市值
     */
    @JSONField(name = "market_capital")
    private Long marketCapital;

    /**
     * 流通市值
     */
    @JSONField(name = "float_market_capital")
    private Long floatMarketCapital;

    /**
     * 市盈率
     */
    @JSONField(name = "pe_forecast")
    private BigDecimal peForecast;

    /**
     * 量比
     */
    @JSONField(name = "volume_ratio")
    private BigDecimal volumeRatio ;

    /**
     * 换手率
     */
    @JSONField(name = "turnover_rate")
    private BigDecimal turnoverRate ;
    /**
     * 没股净资产
     */
    @JSONField(name = "navps")
    private BigDecimal navps;

    /**
     * 市净率
     */
    @JSONField(name = "pb")
    private BigDecimal pb;
    /**
     * 震幅
     */
    @JSONField(name = "amplitude")
    private BigDecimal amplitude;

    /**
     * 震幅
     */
    @JSONField(name = "eps")
    private BigDecimal eps;

    /**
     * 盘前波动
     */
    @JSONField(name = "chg")
    private BigDecimal chg;
    /**
     * 盘前波动比
     */
    @JSONField(name = "percent")
    private BigDecimal percent;

    // 均價
    private BigDecimal averagePrice;

    // 本益比
    private BigDecimal earningsRatio;
    // 發行股
    private Long issuedShares;

    //漲停
    private BigDecimal dailyLimit;


    //跌停
    private BigDecimal limitDown;
    // 52W高
    private BigDecimal w52High;
    // 52W低
    private BigDecimal w52Low;

    // 内盘量
    private BigDecimal internalVolume;
    // wai盘量
    private BigDecimal externalVolume;

    //近四季EPS
    private BigDecimal epsYear;
    //當季EPS
    private BigDecimal epsQuarter;

    //毛利率
    private BigDecimal grossProfitMargin;
    // 每股淨值
    private BigDecimal netValuePerShare;
    // 本淨比
    private BigDecimal priceToNetRatio;
    // 營益率
    private BigDecimal profitMargin;
    // 年股利
    private BigDecimal annualDividend;
    // 殖利率
    private BigDecimal yield;
    // 淨利率
    private BigDecimal netProfitMargin;
}
