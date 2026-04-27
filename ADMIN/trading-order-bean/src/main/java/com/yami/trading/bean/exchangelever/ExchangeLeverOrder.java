package com.yami.trading.bean.exchangelever;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_exchange_lever_order")
public class ExchangeLeverOrder  extends UUIDEntity {

    public final static String STATE_SUBMITTED = "submitted";
    public final static String STATE_CREATED = "created";
    /**
     * 多仓
     */
    public final static String DIRECTION_BUY = "buy";
    /**
     * 空仓
     */
    public final static String DIRECTION_SELL = "sell";

    private String partyId;

    private String symbol;
    /**
     * 订单 号
     */
    private String orderNo;
    /**
     * "buy":买(多) "sell":卖(空)
     */
    private String direction;

    /**
     * 每手金额
     */
    private double unitAmount;

    /**
     * 平仓退回金额
     */
    private double amountClose;

    /**
     * 手续费
     */
    private double fee;

    /**
     * 保证金(剩余)
     */
    private double deposit;

    /**
     * 保证金
     */
    private double depositOpen;

    /**
     * 收益
     */
    private double profit;

    /**
     * 成交均价(成本)
     */
    private Double tradeAvgPrice;

    /**
     * 平仓均价
     */
    private Double closeAvgPrice;

    /**
     * 止盈触发价格
     */
    private Double stopPriceProfit;
    /**
     * 止损触发价格
     */
    private Double stopPriceLoss;

    /**
     * 最小浮动
     */
    private double pips;
    /**
     * 最小浮动金额（以交易金额计算）
     */
    private double pipsAmount;

    /**
     * 状态。submitted 已提交（持仓）， created 完成（平仓）
     */
    private String state = "submitted";

    private Date createTime;
    /**
     * 平仓时间
     */
    private Date closeTime;

    /**
     * 杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
     */
    private Double leverRate;

    /**
     * 涨跌幅
     */
    @TableField(exist = false)
    private double changeRatio;

    /**
     * 委托数量(剩余)(张)
     */
    private Double volume;
    /**
     * 委托数量(张)
     */
    private Double volumeOpen;
    /**
     * 下单金额
     */
    @TableField(exist = false)
    private Double amount;

    /**
     * 保证金率
     */
    @TableField(exist = false)
    private  double riskRate;

    /**
     * 上次结息日期纪录，（如遇服务中途停止，可根据该字段判定是否需要重新计算）
     */
    private Date computeDay;
}
