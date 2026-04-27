package com.yami.trading.admin.controller.ipo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BatchPublishModel {

    @ApiModelProperty("订单号 多个 ,分开")
    private List<String> orderNo;


    @ApiModelProperty("申购状态  2已中签 3 未中签")
    private  int status;
}
