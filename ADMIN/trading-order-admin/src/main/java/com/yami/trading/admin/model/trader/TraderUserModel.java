package com.yami.trading.admin.model.trader;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TraderUserModel {
    @ApiModelProperty("修改传id  新增不传")
    private String uuid;

    @ApiModelProperty("用户编码")
    private String usercode;

    @ApiModelProperty("用户名称")
    private String name;

    @ApiModelProperty("累计金额")
    private String amountSum;

    @ApiModelProperty("累计收益")
    private String profit;

    @ApiModelProperty("创建时间")
    private String createTime;

}
