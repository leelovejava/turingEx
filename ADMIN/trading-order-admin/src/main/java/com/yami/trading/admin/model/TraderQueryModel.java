package com.yami.trading.admin.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TraderQueryModel extends PageRequest {

    @ApiModelProperty("交易员ID")
    private String id;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("币种")
    private String symbol;
}
