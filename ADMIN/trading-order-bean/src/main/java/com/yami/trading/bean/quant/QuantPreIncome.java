package com.yami.trading.bean.quant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_quant_pre_income")
public class QuantPreIncome {
    private static final long serialVersionUID = 1L;

    /**
     * 实体主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 机器人订单id
     */
    @TableField("quant_order_id")
    private String quantOrderId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 收益
     */
    private Double income;

    /**
     * 是否使用 0:未使用 1:已使用
     */
    private Integer status;

    /**
     * 下单数量(USDT)
     */
    private Double number;
}
