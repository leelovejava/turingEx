package com.yami.trading.bean.ipo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class ApplyNewSharesOrderSharesDto {

    @ApiModelProperty("订单号")
    private  String orderNo;

    private Date createTime;

    @ApiModelProperty("申购状态  1 申购中 2已中签 3 未中签")
    private int status;

    private String userId;

    @ApiModelProperty("股票代码")
    private String symbolCode;

    @ApiModelProperty("股票名称")
    private String symbolName;

    @ApiModelProperty("申购价格")
    private BigDecimal subPrice;

    @ApiModelProperty("申购股数")
    private BigDecimal subNumber;



    @ApiModelProperty("申购需认缴")
    private BigDecimal  requiredSubscribe;
    @ApiModelProperty("中签数量")
    private BigDecimal winningNumber;
    @ApiModelProperty("中签应认缴")
    private BigDecimal requiredNumber;

    @ApiModelProperty("已认缴次数")
    private int subscribedCount;

    @ApiModelProperty("已认缴金额")
    private BigDecimal subscribedAmount;

    private String userCode;

    private String userName;

    @ApiModelProperty("认缴次数")
    private  int userPromiseCount=0;
    @ApiModelProperty("真实名称")
    private String realName;
}
