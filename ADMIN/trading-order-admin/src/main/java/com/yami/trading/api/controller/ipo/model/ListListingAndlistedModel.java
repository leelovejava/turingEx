package com.yami.trading.api.controller.ipo.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ListListingAndlistedModel extends PageRequest {

    @ApiModelProperty("发行市场 1 待上市 2 已上市")
    private  String ipoStatus;
    private  String type;
}
