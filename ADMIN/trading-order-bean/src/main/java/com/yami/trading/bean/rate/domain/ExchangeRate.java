package com.yami.trading.bean.rate.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 汇率管理Entity
 *
 * @author lucas
 * @version 2023-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_exchange_rate")
public class ExchangeRate extends UUIDEntity {

    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    public static final String IN = "in";
    @TableField(exist = false)
    public static final String OUT = "out";

    /**
     * OUT_OR_IN
     */
    private String outOrIn;
    /**
     * RATA
     */
    private BigDecimal rata;
    /**
     * CURRENCY
     */
    private String currency;
    /**
     * NAME
     */
    private String name;
    /**
     * CURRENCY_SYMBOL
     */
    private String currencySymbol;
    /**
     * 股票类型
     */
    private String type;

}
