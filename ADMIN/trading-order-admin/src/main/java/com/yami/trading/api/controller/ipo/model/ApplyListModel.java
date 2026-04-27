package com.yami.trading.api.controller.ipo.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ApplyListModel  extends PageRequest {
    @ApiModelProperty(" 1 抽签记录 2 新股库存  3 现股库存")
    private int type;

    private String symbolType;
}
