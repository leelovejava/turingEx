package com.yami.trading.bean.ipo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_apply_new_shares_order")
public class ApplyNewSharesOrder extends BaseEntity{

    @ApiModelProperty("订单号")
    private  String orderNo;

    @ApiModelProperty("申购状态  1 申购中 2已中签 3 未中签")
    private  int status;

    private String userId;

    @ApiModelProperty("股票代码")
    private  String symbolCode;

    @ApiModelProperty("股票名称")
    private  String symbolName;

    @ApiModelProperty("申购价格")
    private BigDecimal subPrice;

    @ApiModelProperty("申购股数")
    private  BigDecimal subNumber;

    @ApiModelProperty("申购需认缴")
    private  BigDecimal  requiredSubscribe;
    @ApiModelProperty("中签数量")
    private  BigDecimal winningNumber;


    @ApiModelProperty("中签应认缴")
    private  BigDecimal requiredNumber;


    @ApiModelProperty("认缴次数")
    private  int userPromiseCount=0;
    @ApiModelProperty("已认缴次数")
    private  int subscribedCount;

    @ApiModelProperty("卖出状态 1 已卖出 0 未卖出")
    private int sell;

    @ApiModelProperty("已认缴金额")
    private  BigDecimal  subscribedAmount;


    @ApiModelProperty("库存损益")
    @TableField(exist = false)
    private  double inventoryGainsLosses;


    @ApiModelProperty("库存损益%")
    @TableField(exist = false)
    private  double inventoryGainsLossesValue;
    @ApiModelProperty("现价")
    @TableField(exist = false)
    private  BigDecimal closePrice=new BigDecimal(0);


    @ApiModelProperty("市场")
    @TableField(exist = false)
    private  String symbolType;

    @ApiModelProperty("市值")
    @TableField(exist = false)
    private  double marketValue;
}
