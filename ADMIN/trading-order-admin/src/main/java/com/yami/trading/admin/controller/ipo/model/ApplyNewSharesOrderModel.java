package com.yami.trading.admin.controller.ipo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyNewSharesOrderModel {

    private  String  userCode;

    private  String productCode;

    /**
     * 申购价格
     */
    private BigDecimal subPrice;
    /**
     * 申购股数
     */
    private  BigDecimal subNumber;
    /**
     * 中签数量
     */
    private  BigDecimal winningNumber;
    /**
     * 申购需认缴
     */
    private  BigDecimal  requiredSubscribe;
    /**
     * 中签应认缴
     */
    private  BigDecimal requiredNumber;
    /**
     *  已认缴次数
     */
    private  int subscribedCount;
    /**
     * 已认缴金额
     */
    private  BigDecimal  subscribedAmount;


    private  int status;


}
