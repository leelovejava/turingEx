package com.yami.trading.bean.ipo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_new_shares_config")
public class NewSharesConfig extends BaseEntity {
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("产品代码（字母）")
    private  String productName;


    @ApiModelProperty("产品代码（数字）")
    private  String productCode;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;


    @ApiModelProperty("承销价")
    private BigDecimal underwritingPrice;


    @ApiModelProperty("总申购数")
    private  int  subscribeTotalNumber;

    @ApiModelProperty("已申购数")
    private int appliedSubscribeNumber;

    @ApiModelProperty("抽签日期")
    private Date drawDate;


    @ApiModelProperty("开放申购日期")
    private Date startSubscribeDate;


    @ApiModelProperty("截止申购日期")
    private Date endSubscribeDate;


    @ApiModelProperty("发劵日期")
    private Date issuanceDate;


    @ApiModelProperty("权重")
    private int weight;

    @ApiModelProperty("发行市场 1 待上市 2 已上市 ")
    private int ipoStatus;

    @ApiModelProperty("状态  1 未开始 2 开放中  3 已结束")
    private  int status;

    @ApiModelProperty("默认额度")
    private BigDecimal defaultLimit;


    @ApiModelProperty("锁定时间 天")
    private int lockDay;

    @ApiModelProperty("差价")
    @TableField(exist = false)
    private BigDecimal  priceDifference;

    @TableField(exist = false)
    @ApiModelProperty("溢价差")
    private double priceDifferenceValue;
    @TableField(exist = false)
    @ApiModelProperty("市值")
    private BigDecimal marketValue;
    @TableField(exist = false)
    @ApiModelProperty(" 1 抽签 2 认缴")
    private int shareStatus;
    @TableField(exist = false)
    private BigDecimal requiredNumber;

    @TableField(exist = false)
    @ApiModelProperty("认购时间")
    private Date subscribeTime=new Date();
    @TableField(exist = false)
    private String orderNo;


    @ApiModelProperty("中签数量")
    @TableField(exist = false)
    private BigDecimal winningNumber;
    /**
     * SZ-stocks 深市     SH-stocks 沪市
     */
    @TableField(exist = false)
    private  String type;

}
