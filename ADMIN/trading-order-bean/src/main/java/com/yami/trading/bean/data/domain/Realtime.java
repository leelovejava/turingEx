package com.yami.trading.bean.data.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.yami.trading.common.util.Arith;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 实时价格Entity
 *
 * @author lucas
 * @version 2023-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_realtime")
@ApiModel
@Slf4j
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Realtime implements Comparable<Realtime>, Cloneable, Serializable {
    private static final long serialVersionUID = 6908987076526939154L;

    private String uuid;

    /**
     * 产品代码
     */
    @ApiModelProperty("产品代码")
    private String symbol;

    /**
     * 时间戳
     */
    @ApiModelProperty("时间戳")
    private Long ts;

    /**
     * 产品名称
     */
    @ApiModelProperty("产品名称")
    private String name;

    /**
     * 开盘价
     */
    @ApiModelProperty("开盘价")
    private double open;

    /**
     * 最高价
     */
    @ApiModelProperty("最高价")
    private double high;

    /**
     * 最低价
     */
    @ApiModelProperty("最低价")
    private double low;

    /**
     * 最新价
     */
    @ApiModelProperty("最新价")
    private double close;

    /**
     * 成交量 币个数
     */

    @ApiModelProperty(" 成交量 币个数")
    private double amount;

    /**
     * 成交额 金额
     */

    @ApiModelProperty(" 成交额 金额")
    private double volume;

    /**
     * type
     */
    private String type;

    /**
     * 涨跌幅
     */
    @TableField(exist = false)
    @ApiModelProperty("涨跌幅")
    private double changeRatio;

    /**
     * 净值涨跌幅
     */
    @TableField(exist = false)
    @ApiModelProperty("净值涨跌幅")
    private double netChange;

    /**
     * 市值
     */
    @TableField(exist = false)
    @JSONField(name = "market_capital")
    private Long marketCapital;

    /**
     * 没股净资产
     */
    @TableField(exist = false)
    @JSONField(name = "navps")
    private double navps;

    /**
     * 市净率
     */
    @TableField(exist = false)
    @JSONField(name = "pb")
    private double pb;
    /**
     * 震幅
     */
    @TableField(exist = false)
    @JSONField(name = "amplitude")
    private double amplitude;

    /**
     * 震幅
     */
    @TableField(exist = false)
    @JSONField(name = "eps")
    private double eps;

    /**
     * 盘前波动
     */
    @TableField(exist = false)
    @JSONField(name = "chg")
    private double chg;
    /**
     * 盘前波动比
     */
    @TableField(exist = false)
    @JSONField(name = "percent")
    private double percent;

    /**
     * 流通市值
     */
    @TableField(exist = false)
    @JSONField(name = "float_market_capital")
    private Long floatMarketCapital;

    /**
     * 市盈率
     */
    @TableField(exist = false)
    @JSONField(name = "pe_forecast")
    private double peForecast;

    /**
     * 量比
     */
    @TableField(exist = false)
    @JSONField(name = "volume_ratio")
    private double volumeRatio;

    /**
     * 换手率
     */
    @TableField(exist = false)
    @JSONField(name = "turnover_rate")
    private double turnoverRate;

    @TableField(exist = false)
    @ApiModelProperty("产品代码")
    private String symbolData;

    /**
     * 时区切换问题需要留意，TODO
     *
     * 时间戳的"yyyy-MM-dd HH:mm:ss"格式
     */
    @TableField(exist = false)
    private String currentTime;

    /**
     * 卖价
     */
    private double bid;

    /**
     * 买价格
     */
    private double ask;

    /**
     * 均价
     */
    @TableField(exist = false)
    private double averagePrice;

    /**
     * 本益比
     */
    @TableField(exist = false)
    private double earningsRatio;

    /**
     * 发行股
     */
    @TableField(exist = false)
    private Long issuedShares;

    /**
     * 涨停
     */
    @TableField(exist = false)
    private double dailyLimit;

    /**
     * 跌停
     */
    @TableField(exist = false)
    private double limitDown;

    /**
     * 52W高
     */
    @TableField(exist = false)
    private double w52High;

    /**
     * 52W低
     */
    @TableField(exist = false)
    private double w52Low;

    /**
     * 内盘量
     */
    @TableField(exist = false)
    private double internalVolume;

    /**
     * wai盘量
     */
    @TableField(exist = false)
    private double externalVolume;

    /**
     * 近四季EPS
     */
    @TableField(exist = false)
    private double epsYear;

    /**
     * 當季EPS
     */
    @TableField(exist = false)
    private double epsQuarter;

    /**
     * 毛利率
     */
    @TableField(exist = false)
    private double grossProfitMargin;

    /**
     * 每股淨值
     */
    @TableField(exist = false)
    private double netValuePerShare;

    /**
     * 本淨比
     */
    @TableField(exist = false)
    private double priceToNetRatio;

    /**
     * 營益率
     */
    @TableField(exist = false)
    private double profitMargin;

    /**
     * 年股利
     */
    @TableField(exist = false)
    private double annualDividend;

    /**
     * 殖利率
     */
    @TableField(exist = false)
    private double yield;

    /**
     * 淨利率
     */
    @TableField(exist = false)
    private double netProfitMargin;

    /**
     * 分表标记
     */
    @TableField(exist = false)
    private int tableIndex;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int compareTo(Realtime realtime) {
        if (this.ts > realtime.getTs()) {
            return 1;
        } else if (this.ts < realtime.getTs()) {
            return -1;
        }
        return 0;
    }

    public double getChangeRatio() {
        if(percent != 0){
            return percent;
        }
        if (open == 0) {
            return 0;
        }
        changeRatio = Arith.div(Arith.sub(close, open), open);
        changeRatio = Arith.mul(changeRatio, 100, 2);
        return changeRatio;
    }

    public double getNetChange() {
        if(chg !=0){
            return chg;
        }
        return Arith.div(Arith.mul(close, getChangeRatio()), 100, 4);
    }
}
