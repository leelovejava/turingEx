package com.yami.trading.admin.model;

import com.yami.trading.common.domain.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class RiskConfigListModel extends PageRequest {

    @ApiModelProperty("风控类型")
    private String type;

    @ApiModelProperty("数据类型")
    private String clientType;

    @ApiModelProperty("关键词")
    private String keyword;

    @ApiModelProperty("配置状态")
    private int status = -1;


}
