package com.yami.trading.bean.ipo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class UserPromiseListDto {

    @ApiModelProperty("扣除usdt")
    private BigDecimal deductUsdt;

    private Date createTime;

    private String userId;
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("产品代码（字母）")
    private  String productName;

    @ApiModelProperty("产品代码（数字）")
    private  String productCode;

    @ApiModelProperty("中签数量")
    private  BigDecimal winningNumber;

    @ApiModelProperty("申购需认缴")
    private  BigDecimal  requiredSubscribe;

    @ApiModelProperty("中签应认缴")
    private BigDecimal requiredNumber;

    @ApiModelProperty("订单号")
    private  String orderNo;
    @ApiModelProperty("申购价格")
    private BigDecimal subPrice;

    @ApiModelProperty(" 1。 待确认  2 已认缴")
    private  int status;

}
