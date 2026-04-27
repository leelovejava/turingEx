package com.yami.trading.admin.controller.ipo.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class ApplyNewSharesOrderListModel  extends PageRequest {


    @ApiModelProperty("订单号")
    private  String orderNo;

    @ApiModelProperty("申购状态  1 申购中 2已中签 3 未中签")
    private  int status;

    @ApiModelProperty("股票代码")
    private  String symbolCode;

    @ApiModelProperty("股票名称")
    private  String symbolName;


    @ApiModelProperty("uuid")
    private String userName;


}
