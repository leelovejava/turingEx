package com.yami.trading.bean.contract.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 非按金额订单Entity
 *
 * @author lucas
 * @version 2023-03-29
 */
@Data
public class ContractOrderProfit extends BaseEntity {
    /**
     * 收益
     */
    private BigDecimal profit;

    /**
     * 强平价格
     */
    private String forceClosePrice;

    /**
     * 平仓均价
     */
    private BigDecimal closeAvgPrice;

    public BigDecimal getProfit() {
        if(profit == null){
            return BigDecimal.ZERO;
        }
        return profit;
    }
}
