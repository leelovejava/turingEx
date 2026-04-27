package com.yami.trading.bean.ipo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class UserListDto {

    private  String uuid;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("产品代码（字母）")
    private  String productName;


    @ApiModelProperty("产品代码（数字）")
    private  String productCode;

    /**
     * 账户类型
     */
    @ApiModelProperty("账户类型")
    private String rolename;
    @ApiModelProperty("认缴股数")
    private BigDecimal deductNumber;

    @ApiModelProperty("认缴金额")
    private BigDecimal deductUsdt;
    private  String createTime;


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



    private String userCode;



    private String userName;

    @ApiModelProperty("真实名称")
    private String realName;
}
