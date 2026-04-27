package com.yami.trading.bean.ipo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class NewSharesConfigDto {


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
    private    Date startSubscribeDate;


    @ApiModelProperty("截止申购日期")
    private Date endSubscribeDate;


    @ApiModelProperty("发劵日期")
    private  Date issuanceDate;


    @ApiModelProperty("权重")
    private  int weight;

    @ApiModelProperty("发行市场 1 待上市 2 已上市 ")
    private  int ipoStatus;

    @ApiModelProperty("状态  1 未开始 2 开放中  3 已结束")
    private  int status;

    @ApiModelProperty("默认额度")
    private  BigDecimal defaultLimit;

    @ApiModelProperty("差价")
    private  BigDecimal  priceDifference;

    @ApiModelProperty("溢价差")
    private  double  priceDifferenceValue;

    @ApiModelProperty("市值")
    private  BigDecimal  marketValue;

    @ApiModelProperty(" 1 抽签 2 认缴")
    private  int   shareStatus;

    private  BigDecimal requiredNumber;

    @ApiModelProperty("认购时间")
    private  Date subscribeTime=new Date();

    private String orderNo;


    @ApiModelProperty("中签数量")
    private  BigDecimal winningNumber;

    @ApiModelProperty("认缴次数")
    private  int userPromiseCount=0;
    @ApiModelProperty("剩余认缴股数")
    private int  residuePromiseNumber;

}
