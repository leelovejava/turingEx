package com.yami.trading.api.controller.ipo.dto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProspectusDto {
    @ApiModelProperty("募集资金")
    private BigDecimal raisedFunds;


    @ApiModelProperty("产品代码）")
    private  String productName;


    @ApiModelProperty("产品代码（")
    private  String productCode;


    private Date createTime;


}
