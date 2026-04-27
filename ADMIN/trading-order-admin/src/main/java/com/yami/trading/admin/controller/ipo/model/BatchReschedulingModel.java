package com.yami.trading.admin.controller.ipo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class BatchReschedulingModel
{

    @ApiModelProperty("订单号 多个 ,分开")
    private List<String> orderNo;
}
