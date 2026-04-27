package com.yami.trading.bean.exchange;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;

import java.util.Date;

@Data

@TableName("t_exchange_lever_apply_order")
public class ExchangeLeverApplyOrder  extends UUIDEntity {


    public final static String STATE_SUBMITTED = "submitted";
    public final static String STATE_CANCELED = "canceled";
    public final static String STATE_CREATED = "created";
    /**
     * 多仓
     */
    public final static String DIRECTION_BUY = "buy";
    /**
     * 空仓
     */
    public final static String DIRECTION_SELL = "sell";
    /**
     * 开仓
     */
    public final static String OFFSET_OPEN = "open";

    /**
     * 平仓
     */
    public final static String OFFSET_CLOSE = "close";

    /**
     * 限价单
     */
    public final static String ORDER_PRICE_TYPE_LIMIT = "limit";

    /**
     * 对手价（市价）
     */
    public final static String ORDER_PRICE_TYPE_OPPONENT = "opponent";

    private static final long serialVersionUID = 3005514385287413248L;

    private String partyId;

    /**
     * 订单 号
     */
    private String orderNo;

    private String symbol;
    /**
     * "buy":多 "sell":空
     */
    private String direction;

    /**
     * "open":开 "close":平
     */
    private String offset;
    /**
     * 委托数量(剩余)(张)
     */
    private Double volume;
    /**
     * 委托数量(张)
     */
    private Double volumeOpen;
    /**
     * 杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
     */
    private Double leverRate;

    /**
     * limit order的交易价格
     */
    private Double price;
    /**
     * 止盈触发价格
     */
    private Double stop_price_profit;
    /**
     * 止损触发价格
     */
    private Double stop_price_loss;
    /**
     * 订单报价类型。 "limit":限价 "opponent":对手价（市价）
     */
    private String order_price_type;
    /**
     * 状态。submitted 已提交，canceled 已撤销， created 委托完成
     */
    private String state = "submitted";

    private Date createTime;

    /**
     * 手续费
     */
    private double fee;

    /**
     * 保证金
     */
    private double deposit;

    /**
     * 每手金额
     */
    private double unitAmount;
}
