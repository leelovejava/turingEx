package com.yami.trading.api.controller.ipo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class    GetTopDataModel {

    @ApiModelProperty(" 1 抽签记录 2 新股库存  3 现股库存")
    private int type;
}
